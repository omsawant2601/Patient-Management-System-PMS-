import { useCallback,useEffect,useState } from 'react';
import { Link,useNavigate,useParams } from 'react-router-dom';
import { ArrowLeft,CalendarPlus,ClipboardPlus,FilePlus2,ImagePlus,Trash2,Upload } from 'lucide-react';
import { patientApi } from '../api/modules';
import { Badge,EmptyState,ErrorState,Loading } from '../components/common';
import { apiError } from '../utils/errors';

const dateTime=value=>value?new Date(value).toLocaleString():'-';
const Field=({label,value})=><div><strong>{label}: </strong>{value??'-'}</div>;

export default function PatientProfilePage(){
 const {id}=useParams();
 const navigate=useNavigate();
 const [profile,setProfile]=useState(null);
 const [loading,setLoading]=useState(true);
 const [error,setError]=useState('');
 const [gallery,setGallery]=useState([]);
 const [profilePhotoUrl,setProfilePhotoUrl]=useState('');
 const [galleryPhotos,setGalleryPhotos]=useState({});
 const [imageError,setImageError]=useState('');
 const [imageBusy,setImageBusy]=useState(false);
 const [photoVersion,setPhotoVersion]=useState(0);
 const [comparison,setComparison]=useState({before:null,after:null,caption:''});

 const load=useCallback(async()=>{
  setLoading(true);setError('');
  try{
   const [historyResponse,galleryResponse]=await Promise.all([patientApi.history(id),patientApi.gallery(id)]);
   setProfile(historyResponse.data.data);
   setGallery(galleryResponse.data.data||[]);
  }
  catch(e){setError(apiError(e))}
  finally{setLoading(false)}
 },[id]);
 useEffect(()=>{load()},[load]);
 useEffect(()=>{
  if(!profile?.patient?.hasProfilePhoto){setProfilePhotoUrl('');return undefined}
  let active=true;let objectUrl;
  patientApi.photo(id).then(response=>{
   if(!active)return;
   objectUrl=URL.createObjectURL(response.data);
   setProfilePhotoUrl(objectUrl);
  }).catch(e=>{if(active)setImageError(apiError(e))});
  return()=>{active=false;if(objectUrl)URL.revokeObjectURL(objectUrl)};
 },[id,profile?.patient?.hasProfilePhoto,photoVersion]);
 useEffect(()=>{
  let active=true;const objectUrls=[];
  Promise.all(gallery.map(async item=>{
   const [before,after]=await Promise.all([
    patientApi.galleryImage(id,item.id,'before'),
    patientApi.galleryImage(id,item.id,'after'),
   ]);
   const beforeUrl=URL.createObjectURL(before.data),afterUrl=URL.createObjectURL(after.data);
   if(!active){URL.revokeObjectURL(beforeUrl);URL.revokeObjectURL(afterUrl);return [item.id,null]}
   objectUrls.push(beforeUrl,afterUrl);
   return [item.id,{before:beforeUrl,after:afterUrl}];
  })).then(entries=>{if(active)setGalleryPhotos(Object.fromEntries(entries))})
   .catch(e=>{if(active)setImageError(apiError(e))});
  return()=>{active=false;objectUrls.forEach(URL.revokeObjectURL)};
 },[gallery,id]);

 const uploadProfilePhoto=async event=>{
  const file=event.target.files?.[0];event.target.value='';
  if(!file)return;
  setImageError('');setImageBusy(true);
  try{
   const response=await patientApi.uploadPhoto(id,file);
   setProfile(current=>current?{...current,patient:response.data.data}:current);
   setPhotoVersion(version=>version+1);
  }catch(e){setImageError(apiError(e))}
  finally{setImageBusy(false)}
 };
 const removeProfilePhoto=async()=>{
  if(!confirm('Remove this profile picture?'))return;
  setImageError('');setImageBusy(true);
  try{
   await patientApi.removePhoto(id);
   setProfile(current=>current?{...current,patient:{...current.patient,hasProfilePhoto:false}}:current);
   setProfilePhotoUrl('');
  }catch(e){setImageError(apiError(e))}
  finally{setImageBusy(false)}
 };
 const addComparison=async()=>{
  if(!comparison.before||!comparison.after){setImageError('Select both the before and after images.');return}
  setImageError('');setImageBusy(true);
  try{
   const response=await patientApi.addComparison(id,comparison.before,comparison.after,comparison.caption);
   setGallery(current=>[response.data.data,...current]);
   setComparison({before:null,after:null,caption:''});
  }catch(e){setImageError(apiError(e))}
  finally{setImageBusy(false)}
 };
 const removeComparison=async item=>{
  if(!confirm('Remove this before-and-after comparison?'))return;
  setImageError('');setImageBusy(true);
  try{
   await patientApi.removeComparison(id,item.id);
   setGallery(current=>current.filter(entry=>entry.id!==item.id));
  }catch(e){setImageError(apiError(e))}
  finally{setImageBusy(false)}
 };

 if(loading)return <Loading label="Loading patient profile..."/>;
 if(error)return <ErrorState message={error}/>;
 if(!profile)return <EmptyState message="Patient profile not found."/>;

 const {patient,appointments=[],consultations=[],prescriptions=[]}=profile;
 return <>
  <div className="toolbar">
   <div><Link className="btn btn-secondary" to="/patients"><ArrowLeft size={16}/> Patients</Link><h2 style={{margin:'12px 0 0'}}>{patient.fullName}</h2></div>
   <div style={{display:'flex',gap:8,flexWrap:'wrap'}}>
    <button className="btn btn-primary" onClick={()=>navigate(`/appointments?patientId=${id}&action=create`)}><CalendarPlus size={16}/> Book appointment</button>
    <button className="btn btn-secondary" onClick={()=>navigate(`/consultations?patientId=${id}&action=create`)}><ClipboardPlus size={16}/> New consultation</button>
    <button className="btn btn-secondary" onClick={()=>navigate(`/prescriptions?patientId=${id}&action=create`)}><FilePlus2 size={16}/> Create prescription</button>
   </div>
  </div>

  <section className="card" style={{marginBottom:20}}>
   <h3 style={{marginTop:0}}>Patient details</h3>
   <div style={{display:'flex',alignItems:'center',gap:18,flexWrap:'wrap',marginBottom:20}}>
    {profilePhotoUrl?<img src={profilePhotoUrl} alt={`${patient.fullName} profile`} style={{width:112,height:112,borderRadius:'50%',objectFit:'cover',border:'1px solid var(--border)'}}/>:
     <div aria-label="No profile picture" style={{width:112,height:112,borderRadius:'50%',display:'grid',placeItems:'center',background:'var(--accent-bg)',color:'var(--accent)',fontSize:36,fontWeight:700}}>{patient.fullName?.charAt(0)?.toUpperCase()||'?'}</div>}
    <div style={{display:'flex',gap:8,flexWrap:'wrap'}}>
     <label className="btn btn-secondary" style={{cursor:imageBusy?'wait':'pointer'}}>
      <Upload size={16}/>{patient.hasProfilePhoto?'Change profile picture':'Upload profile picture'}
      <input type="file" accept="image/jpeg,image/png" hidden disabled={imageBusy} onChange={uploadProfilePhoto}/>
     </label>
     {patient.hasProfilePhoto&&<button className="btn btn-danger" onClick={removeProfilePhoto} disabled={imageBusy}><Trash2 size={16}/> Remove picture</button>}
    </div>
   </div>
   <div className="form-grid">
    <Field label="Registration no." value={patient.patientCode}/>
    <Field label="Mobile" value={patient.mobile}/>
    <Field label="Alternate contact" value={patient.alternateContactNumber}/>
    <Field label="Date of birth" value={patient.dateOfBirth}/>
    <Field label="Age" value={patient.age}/>
    <Field label="Gender" value={patient.gender}/>
    <Field label="Registered on" value={patient.registrationDate}/>
    <Field label="Address" value={patient.address}/>
   </div>
   <div style={{borderTop:'1px solid var(--border)',marginTop:20,paddingTop:16}}>
    <h3>Before-and-after gallery</h3>
    <div className="form-grid">
     <div className="field"><label>Before image</label><input className="input" type="file" accept="image/jpeg,image/png" disabled={imageBusy} onChange={e=>setComparison(current=>({...current,before:e.target.files?.[0]||null}))}/></div>
     <div className="field"><label>After image</label><input className="input" type="file" accept="image/jpeg,image/png" disabled={imageBusy} onChange={e=>setComparison(current=>({...current,after:e.target.files?.[0]||null}))}/></div>
     <div className="field full"><label>Caption (optional)</label><input className="input" maxLength={500} value={comparison.caption} disabled={imageBusy} onChange={e=>setComparison(current=>({...current,caption:e.target.value}))}/></div>
    </div>
    <button className="btn btn-primary" style={{marginTop:12}} onClick={addComparison} disabled={imageBusy||!comparison.before||!comparison.after}><ImagePlus size={16}/> Add comparison</button>
    {gallery.length?gallery.map(item=><article key={item.id} className="card" style={{marginTop:14,padding:14}}>
     <div className="toolbar"><div><strong>{item.caption||'Photo comparison'}</strong><div>{dateTime(item.createdAt)}</div></div><button className="btn btn-danger" onClick={()=>removeComparison(item)} disabled={imageBusy} title="Remove comparison"><Trash2 size={15}/></button></div>
     <div style={{display:'grid',gridTemplateColumns:'repeat(auto-fit,minmax(220px,1fr))',gap:14}}>
      <figure style={{margin:0}}><figcaption>Before</figcaption>{galleryPhotos[item.id]?.before?<img src={galleryPhotos[item.id].before} alt={`${item.caption||'Comparison'} before`} style={{width:'100%',maxHeight:320,objectFit:'contain',background:'#f7f9fb'}}/>:<div className="empty">Loading image...</div>}</figure>
      <figure style={{margin:0}}><figcaption>After</figcaption>{galleryPhotos[item.id]?.after?<img src={galleryPhotos[item.id].after} alt={`${item.caption||'Comparison'} after`} style={{width:'100%',maxHeight:320,objectFit:'contain',background:'#f7f9fb'}}/>:<div className="empty">Loading image...</div>}</figure>
     </div>
    </article>):<div className="empty">No before-and-after comparisons have been uploaded.</div>}
    {imageError&&<div className="error" role="alert" style={{marginTop:10}}>{imageError}</div>}
   </div>
  </section>

  <section className="card" style={{marginBottom:20}}>
   <h3 style={{marginTop:0}}>Appointments ({appointments.length})</h3>
   {appointments.length?appointments.map(a=><article key={a.id} style={{padding:'14px 0',borderTop:'1px solid var(--border)'}}>
    <div className="toolbar"><strong>{a.appointmentNumber}</strong><Badge status={a.status}/></div>
    <div className="form-grid">
     <Field label="Date & time" value={dateTime(a.scheduledAt)}/>
     <Field label="Doctor" value={a.doctorName}/>
     <Field label="Type" value={a.appointmentType}/>
     <Field label="Duration" value={`${a.durationMinutes} minutes`}/>
     <Field label="Notes" value={a.notes}/>
    </div>
   </article>):<EmptyState message="No appointment history for this patient."/>}
  </section>

  <section className="card" style={{marginBottom:20}}>
   <h3 style={{marginTop:0}}>Consultations ({consultations.length})</h3>
   {consultations.length?consultations.map(c=><article key={c.id} style={{padding:'14px 0',borderTop:'1px solid var(--border)'}}>
    <div className="toolbar"><strong>{dateTime(c.consultationDate)}</strong><Badge status={c.status}/></div>
    <div className="form-grid">
     <Field label="Symptoms" value={c.symptoms}/>
     <Field label="Diagnosis" value={c.diagnosis}/>
     <Field label="Doctor" value={c.doctorName}/>
     <Field label="Clinical notes" value={c.clinicalNotes}/>
     <Field label="Follow-up recommendations" value={c.followUpRecommendations}/>
    </div>
   </article>):<EmptyState message="No consultation history for this patient."/>}
  </section>

  <section className="card">
   <h3 style={{marginTop:0}}>Prescriptions ({prescriptions.length})</h3>
   {prescriptions.length?prescriptions.map(p=><article key={p.id} style={{padding:'14px 0',borderTop:'1px solid var(--border)'}}>
    <div className="toolbar"><strong>{p.prescriptionNumber} · {dateTime(p.prescriptionDate)}</strong><Badge status={p.status}/></div>
    <Field label="Doctor" value={p.doctorName}/>
    <Field label="Instructions" value={p.instructions}/>
    <h4>Medicines</h4>
    {p.medicines?.length?<div className="table-wrap"><table className="table"><thead><tr><th>Medicine</th><th>Dosage</th><th>Frequency</th><th>Duration</th><th>Quantity</th><th>Instructions</th></tr></thead><tbody>{p.medicines.map((m,index)=><tr key={`${m.medicineName}-${index}`}><td>{m.medicineName}</td><td>{m.dosage||'-'}</td><td>{m.frequency||'-'}</td><td>{m.duration||'-'}</td><td>{m.quantity||'-'}</td><td>{m.instructions||'-'}</td></tr>)}</tbody></table></div>:<EmptyState message="No medicine details recorded."/>}
   </article>):<EmptyState message="No prescription history for this patient."/>}
  </section>
 </>;
}
