import { useEffect,useState } from 'react';
import { Plus, Trash2, Download } from 'lucide-react';
import { prescriptionApi,patientApi,consultationApi } from '../api/modules';
import { Loading,ErrorState,Modal,Badge,EmptyState } from '../components/common';
import { apiError } from '../utils/errors';

const emptyMedicine={medicineName:'',dosage:'',frequency:'',duration:'',quantity:'',instructions:''};
export default function PrescriptionsPage(){
 const [patients,setPatients]=useState([]),[consultations,setConsultations]=useState([]),[rows,setRows]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(''),[modal,setModal]=useState(false);
 const [patientId,setPatientId]=useState(''),[consultationId,setConsultationId]=useState(''),[instructions,setInstructions]=useState(''),[medicines,setMedicines]=useState([{...emptyMedicine}]);
 useEffect(()=>{patientApi.list({page:0,size:100}).then(r=>setPatients(r.data.data.content||[])).catch(e=>setError(apiError(e))).finally(()=>setLoading(false))},[]);
 const choosePatient=async id=>{setPatientId(id);setRows([]);if(!id)return;try{setLoading(true);const [p,c]=await Promise.all([prescriptionApi.history(id),consultationApi.history(id)]);setRows(p.data.data||[]);setConsultations(c.data.data||[])}catch(e){setError(apiError(e))}finally{setLoading(false)}};
 const open=()=>{setConsultationId('');setInstructions('');setMedicines([{...emptyMedicine}]);setModal(true)};
 const updateMed=(i,key,value)=>setMedicines(x=>x.map((m,n)=>n===i?{...m,[key]:value}:m));
 const save=async()=>{if(!consultationId||medicines.some(m=>!m.medicineName.trim()))return setError('Select a consultation and enter every medicine name');try{await prescriptionApi.create({consultationId,instructions,medicines});setModal(false);await choosePatient(patientId)}catch(e){setError(apiError(e))}};
 const finalize=async r=>{if(!confirm(`Finalize prescription ${r.prescriptionNumber}? It cannot be silently edited afterward.`))return;try{await prescriptionApi.finalize(r.id);await choosePatient(patientId)}catch(e){setError(apiError(e))}};
 const download=async r=>{try{const response=await prescriptionApi.pdf(r.id);const url=URL.createObjectURL(response.data);const a=document.createElement('a');a.href=url;a.download=`prescription-${r.prescriptionNumber}.pdf`;a.click();URL.revokeObjectURL(url)}catch(e){setError(apiError(e))}};
 return <><div className="toolbar"><h2 style={{margin:0}}>Prescriptions</h2><button className="btn btn-primary" onClick={open} disabled={!patientId}><Plus size={17}/> Create prescription</button></div>
 <div className="card" style={{marginBottom:16}}><label style={{fontWeight:700,display:'block',marginBottom:6}}>Select patient</label><select className="select" value={patientId} onChange={e=>choosePatient(e.target.value)}><option value="">Select patient</option>{patients.map(p=><option key={p.id} value={p.id}>{p.fullName} · {p.patientCode}</option>)}</select></div>
 {loading?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap">{rows.length?<table className="table"><thead><tr><th>Prescription</th><th>Date</th><th>Status</th><th>Actions</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td>{r.prescriptionNumber}</td><td>{new Date(r.prescriptionDate).toLocaleString()}</td><td><Badge status={r.status}/></td><td><button className="btn btn-secondary" onClick={()=>download(r)} title="Download PDF"><Download size={15}/></button>{r.status==='DRAFT'&&<> <button className="btn btn-primary" onClick={()=>finalize(r)}>Finalize</button></>}</td></tr>)}</tbody></table>:<EmptyState/>}</div>}
 <Modal title="Create prescription" open={modal} onClose={()=>setModal(false)}><div className="form-grid">
 <div className="field full"><label>Consultation</label><select className="select" value={consultationId} onChange={e=>setConsultationId(e.target.value)}><option value="">Select consultation</option>{consultations.map(c=><option key={c.id} value={c.id}>{new Date(c.consultationDate).toLocaleString()} · {c.symptoms}</option>)}</select></div>
 <div className="field full"><label>General instructions</label><textarea className="textarea" value={instructions} onChange={e=>setInstructions(e.target.value)}/></div></div>
 <h3>Medicines</h3>{medicines.map((m,i)=><div className="card" key={i} style={{marginBottom:10,padding:12}}><div className="form-grid">
 {Object.entries({medicineName:'Medicine name',dosage:'Dosage',frequency:'Frequency',duration:'Duration',quantity:'Quantity',instructions:'Instructions'}).map(([key,label])=><div className="field" key={key}><label>{label}</label><input className="input" value={m[key]} onChange={e=>updateMed(i,key,e.target.value)}/></div>)}
 </div>{medicines.length>1&&<button className="btn btn-danger" style={{marginTop:10}} onClick={()=>setMedicines(x=>x.filter((_,n)=>n!==i))}><Trash2 size={15}/> Remove</button>}</div>)}
 {error&&<div className="error">{error}</div>}<div style={{display:'flex',gap:8,marginTop:12}}><button className="btn btn-secondary" onClick={()=>setMedicines(x=>[...x,{...emptyMedicine}])}>Add medicine</button><button className="btn btn-primary" onClick={save}>Save draft</button></div></Modal></>;
}
