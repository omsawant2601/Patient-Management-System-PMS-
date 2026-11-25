import { useEffect,useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Plus } from 'lucide-react';
import { consultationApi,patientApi } from '../api/modules';
import { Loading,ErrorState,Modal,Badge,EmptyState } from '../components/common';
import { apiError } from '../utils/errors';

const schema=yup.object({patientId:yup.string().required(),appointmentId:yup.string().nullable(),consultationDate:yup.string().required(),symptoms:yup.string().required().max(3000),diagnosis:yup.string().max(3000),clinicalNotes:yup.string().max(5000),followUpRecommendations:yup.string().max(3000)});
export default function ConsultationsPage(){
 const [rows,setRows]=useState([]),[patients,setPatients]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(''),[modal,setModal]=useState(false),[edit,setEdit]=useState(null);
 const {register,handleSubmit,reset,formState:{errors,isSubmitting}}=useForm({resolver:yupResolver(schema)});
 useEffect(()=>{patientApi.list({page:0,size:100}).then(r=>setPatients(r.data.data.content||[])).catch(e=>setError(apiError(e))).finally(()=>setLoading(false))},[]);
 const open=p=>{setEdit(null);reset({patientId:p||'',appointmentId:'',consultationDate:'',symptoms:'',diagnosis:'',clinicalNotes:'',followUpRecommendations:''});setModal(true)};
 const save=async v=>{try{const payload={...v,appointmentId:v.appointmentId||null,consultationDate:v.consultationDate?new Date(v.consultationDate).toISOString():null};if(edit)await consultationApi.update(edit.id,payload);else await consultationApi.create(payload);setModal(false);if(v.patientId){const r=await consultationApi.history(v.patientId);setRows(r.data.data||[])}}catch(e){setError(apiError(e))}};
 const choosePatient=async id=>{if(!id)return setRows([]);try{setLoading(true);const r=await consultationApi.history(id);setRows(r.data.data||[])}catch(e){setError(apiError(e))}finally{setLoading(false)}};
 return <><div className="toolbar"><h2 style={{margin:0}}>Consultations</h2><button className="btn btn-primary" onClick={()=>open('')}><Plus size={17}/> New consultation</button></div>
 <div className="card" style={{marginBottom:16}}><label style={{fontWeight:700,display:'block',marginBottom:6}}>Select patient</label><select className="select" defaultValue="" onChange={e=>choosePatient(e.target.value)}><option value="">Select patient</option>{patients.map(p=><option key={p.id} value={p.id}>{p.fullName} · {p.patientCode}</option>)}</select></div>
 {loading?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap">{rows.length?<table className="table"><thead><tr><th>Date</th><th>Symptoms</th><th>Diagnosis</th><th>Status</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td>{new Date(r.consultationDate).toLocaleString()}</td><td>{r.symptoms}</td><td>{r.diagnosis||'-'}</td><td><Badge status={r.status}/></td></tr>)}</tbody></table>:<EmptyState/>}</div>}
 <Modal title="New consultation" open={modal} onClose={()=>setModal(false)}><form onSubmit={handleSubmit(save)}><div className="form-grid">
 <div className="field"><label>Patient</label><select className="select" {...register('patientId')}><option value="">Select patient</option>{patients.map(p=><option key={p.id} value={p.id}>{p.fullName}</option>)}</select></div>
 <div className="field"><label>Consultation date & time</label><input className="input" type="datetime-local" {...register('consultationDate')}/></div>
 <div className="field full"><label>Symptoms</label><textarea className="textarea" {...register('symptoms')}/><div className="error">{errors.symptoms?.message}</div></div>
 <div className="field full"><label>Diagnosis</label><textarea className="textarea" {...register('diagnosis')}/></div>
 <div className="field full"><label>Clinical notes</label><textarea className="textarea" {...register('clinicalNotes')}/></div>
 <div className="field full"><label>Follow-up recommendations</label><textarea className="textarea" {...register('followUpRecommendations')}/></div>
 </div>{error&&<div className="error">{error}</div>}<button className="btn btn-primary" style={{marginTop:16}} disabled={isSubmitting}>Save consultation</button></form></Modal></>;
}
