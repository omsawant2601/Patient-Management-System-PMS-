import { useCallback,useEffect,useState } from 'react';
import { Link,useNavigate,useParams } from 'react-router-dom';
import { ArrowLeft,CalendarPlus,ClipboardPlus,FilePlus2 } from 'lucide-react';
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

 const load=useCallback(async()=>{
  setLoading(true);setError('');
  try{const response=await patientApi.history(id);setProfile(response.data.data)}
  catch(e){setError(apiError(e))}
  finally{setLoading(false)}
 },[id]);
 useEffect(()=>{load()},[load]);

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
