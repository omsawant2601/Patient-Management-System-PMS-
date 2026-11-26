import { useEffect,useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Plus, CalendarX, Pencil } from 'lucide-react';
import { appointmentApi,patientApi,userApi } from '../api/modules';
import { Loading,ErrorState,EmptyState,Modal } from '../components/common';
import { apiError } from '../utils/errors';

const schema=yup.object({patientId:yup.string().required('Patient is required'),doctorId:yup.string().required('Doctor is required'),scheduledAt:yup.string().required('Date and time are required'),durationMinutes:yup.number().min(5).max(240).required(),appointmentType:yup.string().required(),notes:yup.string().max(1000)});
export default function AppointmentsPage(){
 const [rows,setRows]=useState([]),[patients,setPatients]=useState([]),[doctors,setDoctors]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(''),[modal,setModal]=useState(false),[edit,setEdit]=useState(null);
 const [searchParams,setSearchParams]=useSearchParams();
 const {register,handleSubmit,reset,formState:{errors,isSubmitting}}=useForm({resolver:yupResolver(schema)});
 const load=async()=>{setLoading(true);setError('');try{const [a,p,u]=await Promise.all([appointmentApi.list({page:0,size:100}),patientApi.list({page:0,size:100}),userApi.list().catch(()=>({data:{data:[]}}))]);setRows(a.data.data.content||[]);setPatients(p.data.data.content||[]);setDoctors(u.data.data.filter(x=>x.role==='DOCTOR'))}catch(e){setError(apiError(e))}finally{setLoading(false)}};
 useEffect(()=>{load()},[]);
 useEffect(()=>{
  const patientId=searchParams.get('patientId');
  if(searchParams.get('action')!=='create'||!patientId)return;
  const openForPatient=async()=>{
   try{
    const response=await patientApi.get(patientId);
    setPatients(current=>current.some(patient=>patient.id===patientId)?current:[response.data.data,...current]);
    setEdit(null);
    reset({patientId,doctorId:'',scheduledAt:'',durationMinutes:15,appointmentType:'CONSULTATION',notes:''});
    setModal(true);
   }catch(e){setError(apiError(e))}
  };
  openForPatient();
  setSearchParams({}, {replace:true});
 },[searchParams,setSearchParams,reset]);
 const open=()=>{setEdit(null);reset({patientId:'',doctorId:'',scheduledAt:'',durationMinutes:15,appointmentType:'CONSULTATION',notes:''});setModal(true)};
 const openEdit=a=>{setEdit(a);reset({scheduledAt:a.scheduledAt?.slice(0,16),durationMinutes:a.durationMinutes,notes:a.notes||''});setModal(true)};
 const save=async v=>{setError('');try{if(edit){await appointmentApi.reschedule(edit.id,{scheduledAt:new Date(v.scheduledAt).toISOString(),durationMinutes:Number(v.durationMinutes),notes:v.notes});}else{await appointmentApi.create({...v,durationMinutes:Number(v.durationMinutes),scheduledAt:new Date(v.scheduledAt).toISOString()});}setModal(false);await load()}catch(e){setError(apiError(e))}};
 const cancel=async a=>{if(!confirm(`Cancel appointment ${a.appointmentNumber}?`))return;try{await appointmentApi.cancel(a.id);await load()}catch(e){setError(apiError(e))}};
 const changeStatus=async(a,status)=>{try{await appointmentApi.setStatus(a.id,status);await load()}catch(e){setError(apiError(e))}};
 return <>
 <div className="toolbar"><h2 style={{margin:0}}>Appointments</h2><button className="btn btn-primary" onClick={open}><Plus size={17}/> Book appointment</button></div>
 {loading?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap">{rows.length?<table className="table"><thead><tr><th>Appointment</th><th>Date & time</th><th>Duration</th><th>Type</th><th>Status</th><th>Actions</th></tr></thead><tbody>{rows.map(a=><tr key={a.id}><td>{a.appointmentNumber}</td><td>{new Date(a.scheduledAt).toLocaleString()}</td><td>{a.durationMinutes} min</td><td>{a.appointmentType}</td><td><select className="select" aria-label={`Status for ${a.appointmentNumber}`} value={a.status} disabled={a.status==='CANCELLED'} onChange={e=>changeStatus(a,e.target.value)}><option value="SCHEDULED">SCHEDULED</option><option value="CONFIRMED">CONFIRMED</option><option value="COMPLETED">COMPLETED</option><option value="CANCELLED">CANCELLED</option><option value="NO_SHOW">NO_SHOW</option></select></td><td>{!['CANCELLED','COMPLETED'].includes(a.status)&&<><button className="btn btn-secondary" onClick={()=>openEdit(a)} title="Reschedule"><Pencil size={15}/></button>{' '}<button className="btn btn-danger" onClick={()=>cancel(a)} title="Cancel"><CalendarX size={15}/></button></>}</td></tr>)}</tbody></table>:<EmptyState message="No appointments found"/>}</div>}
 <Modal title={edit?'Reschedule appointment':'Book appointment'} open={modal} onClose={()=>setModal(false)}><form onSubmit={handleSubmit(save)}><div className="form-grid">
 {!edit&&<><div className="field"><label>Patient</label><select className="select" {...register('patientId')}><option value="">Select patient</option>{patients.map(p=><option key={p.id} value={p.id}>{p.fullName} · {p.patientCode}</option>)}</select><div className="error">{errors.patientId?.message}</div></div>
 <div className="field"><label>Doctor</label><select className="select" {...register('doctorId')}><option value="">Select doctor</option>{doctors.map(d=><option key={d.id} value={d.id}>{d.fullName}</option>)}</select><div className="error">{errors.doctorId?.message}</div></div></>}
 <div className="field"><label>Date and time</label><input className="input" type="datetime-local" {...register('scheduledAt')}/><div className="error">{errors.scheduledAt?.message}</div></div>
 <div className="field"><label>Duration (minutes)</label><input className="input" type="number" {...register('durationMinutes')}/></div>
 {!edit&&<div className="field"><label>Appointment type</label><input className="input" {...register('appointmentType')}/></div>}
 <div className="field full"><label>Notes</label><textarea className="textarea" {...register('notes')}/></div></div>{error&&<div className="error" style={{marginTop:10}}>{error}</div>}<button className="btn btn-primary" style={{marginTop:16}} disabled={isSubmitting}>Save appointment</button></form></Modal>
 </>;
}
