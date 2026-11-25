import { useEffect,useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Plus, Search, Eye, Pencil } from 'lucide-react';
import { patientApi } from '../api/modules';
import { Loading,ErrorState,Modal } from '../components/common';
import { apiError } from '../utils/errors';

const schema=yup.object({fullName:yup.string().required().max(150),mobile:yup.string().required().matches(/^[6-9][0-9]{9}$/,'Enter a valid 10-digit Indian mobile number'),alternateContactNumber:yup.string().transform(v=>v||'').matches(/^($|[6-9][0-9]{9}$)/,'Enter a valid alternate mobile'),address:yup.string().max(1000),dateOfBirth:yup.date().nullable().typeError('Enter a valid date'),gender:yup.string().nullable()});
export default function PatientsPage(){
 const [rows,setRows]=useState([]);const [loading,setLoading]=useState(true);const [error,setError]=useState('');const [query,setQuery]=useState('');const [modal,setModal]=useState(null);const [detail,setDetail]=useState(null);const [history,setHistory]=useState(null);
 const {register,handleSubmit,reset,formState:{errors,isSubmitting}}=useForm({resolver:yupResolver(schema)});
 const load=async()=>{setLoading(true);setError('');try{const r=await patientApi.list({page:0,size:20,sort:'createdAt,desc'});setRows(r.data.data.content||[])}catch(e){setError(apiError(e))}finally{setLoading(false)}};
 useEffect(()=>{load()},[]);
 const search=async()=>{setLoading(true);try{const r=await patientApi.search(query.includes('@')?{}:{name:query,mobile:query});setRows(r.data.data||[])}catch(e){setError(apiError(e))}finally{setLoading(false)}};
 const openCreate=()=>{reset({fullName:'',mobile:'',alternateContactNumber:'',address:'',dateOfBirth:'',gender:''});setDetail(null);setModal('form')};
 const openEdit=async p=>{reset({...p,dateOfBirth:p.dateOfBirth||''});setDetail(p);setModal('form')};
 const save=async v=>{try{if(detail)await patientApi.update(detail.id,v);else await patientApi.create(v);setModal(null);await load()}catch(e){setError(apiError(e))}};
 const view=async p=>{try{const r=await patientApi.history(p.id);setHistory(r.data.data);setModal('history')}catch(e){setError(apiError(e))}};
 return <>
 <div className="toolbar"><div className="search-row"><input className="input" style={{width:260}} placeholder="Search name or mobile" value={query} onChange={e=>setQuery(e.target.value)} onKeyDown={e=>e.key==='Enter'&&search()}/><button className="btn btn-secondary" onClick={search}><Search size={16}/></button></div><button className="btn btn-primary" onClick={openCreate}><Plus size={17}/> Register patient</button></div>
 {loading?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap">{rows.length?<table className="table"><thead><tr><th>Registration no.</th><th>Name</th><th>Mobile</th><th>Age</th><th>Actions</th></tr></thead><tbody>{rows.map(p=><tr key={p.id}><td>{p.patientCode}</td><td>{p.fullName}</td><td>{p.mobile}</td><td>{p.age??'-'}</td><td><button className="btn btn-secondary" onClick={()=>view(p)}><Eye size={15}/></button> <button className="btn btn-secondary" onClick={()=>openEdit(p)}><Pencil size={15}/></button></td></tr>)}</tbody></table>:<div className="empty">No patients found</div>}</div>}
 <Modal title={detail?'Edit patient':'Register patient'} open={modal==='form'} onClose={()=>setModal(null)}>
  <form onSubmit={handleSubmit(save)}><div className="form-grid">
   <div className="field"><label>Full name</label><input className="input" {...register('fullName')}/><div className="error">{errors.fullName?.message}</div></div>
   <div className="field"><label>Mobile number</label><input className="input" {...register('mobile')}/><div className="error">{errors.mobile?.message}</div></div>
   <div className="field"><label>Alternate number</label><input className="input" {...register('alternateContactNumber')}/><div className="error">{errors.alternateContactNumber?.message}</div></div>
   <div className="field"><label>Date of birth</label><input className="input" type="date" {...register('dateOfBirth')}/><div className="error">{errors.dateOfBirth?.message}</div></div>
   <div className="field"><label>Gender</label><select className="select" {...register('gender')}><option value="">Select</option><option>MALE</option><option>FEMALE</option><option>OTHER</option></select></div>
   <div className="field"><label>Address</label><textarea className="textarea" {...register('address')}/></div>
  </div><button className="btn btn-primary" style={{marginTop:16}} disabled={isSubmitting}>Save patient</button></form>
 </Modal>
 <Modal title="Patient history" open={modal==='history'} onClose={()=>setModal(null)}>
  {history?<><h3>{history.patient.fullName} · {history.patient.patientCode}</h3><p><strong>Appointments:</strong> {(history.appointments||[]).length}</p><p><strong>Consultations:</strong> {(history.consultations||[]).length}</p><p><strong>Prescriptions:</strong> {(history.prescriptions||[]).length}</p></>:<Loading/>}
 </Modal>
 </>;
}
