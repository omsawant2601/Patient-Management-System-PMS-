import { useEffect,useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Plus } from 'lucide-react';
import { userApi } from '../api/modules';
import { Loading,ErrorState,Modal,Badge } from '../components/common';
import { apiError } from '../utils/errors';

const schema=yup.object({fullName:yup.string().required().max(150),username:yup.string().required().max(100),email:yup.string().email().nullable(),mobile:yup.string().required().matches(/^[6-9][0-9]{9}$/),password:yup.string().min(10).max(128).required()});
export default function UsersPage(){
 const [rows,setRows]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(''),[modal,setModal]=useState(false);
 const {register,handleSubmit,reset,formState:{errors,isSubmitting}}=useForm({resolver:yupResolver(schema)});
 const load=async()=>{setLoading(true);try{const r=await userApi.list();setRows(r.data.data||[])}catch(e){setError(apiError(e))}finally{setLoading(false)}};useEffect(()=>{load()},[]);
 const open=()=>{reset({fullName:'',username:'',email:'',mobile:'',password:'',role:'RECEPTIONIST'});setModal(true)};
 const save=async v=>{try{await userApi.create(v);setModal(false);await load()}catch(e){setError(apiError(e))}};
 const toggle=async u=>{if(!confirm(`${u.active?'Deactivate':'Activate'} ${u.fullName}?`))return;try{await userApi.setStatus(u.id,!u.active);await load()}catch(e){setError(apiError(e))}};
 return <><div className="toolbar"><h2 style={{margin:0}}>Clinic users</h2><button className="btn btn-primary" onClick={open}><Plus size={17}/> Add receptionist</button></div>
 {loading?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap"><table className="table"><thead><tr><th>Name</th><th>Username</th><th>Mobile</th><th>Role</th><th>Status</th><th>Action</th></tr></thead><tbody>{rows.map(u=><tr key={u.id}><td>{u.fullName}</td><td>{u.username}</td><td>{u.mobile}</td><td>{u.role}</td><td><Badge status={u.active?'ACTIVE':'INACTIVE'}/></td><td><button className={`btn ${u.active?'btn-danger':'btn-secondary'}`} onClick={()=>toggle(u)}>{u.active?'Deactivate':'Activate'}</button></td></tr>)}</tbody></table></div>}
 <Modal title="Add receptionist" open={modal} onClose={()=>setModal(false)}><form onSubmit={handleSubmit(save)}><div className="form-grid">
 <div className="field"><label>Full name</label><input className="input" {...register('fullName')}/><div className="error">{errors.fullName?.message}</div></div>
 <div className="field"><label>Username</label><input className="input" {...register('username')}/><div className="error">{errors.username?.message}</div></div>
 <div className="field"><label>Email for password recovery (optional)</label><input className="input" type="email" {...register('email')}/><div className="error">{errors.email?.message}</div></div>
 <div className="field"><label>Mobile</label><input className="input" {...register('mobile')}/><div className="error">{errors.mobile?.message}</div></div>
 <div className="field"><label>Temporary password</label><input className="input" type="password" {...register('password')}/><div className="error">{errors.password?.message}</div></div>
 <input type="hidden" value="RECEPTIONIST" {...register('role')}/>
 </div>{error&&<div className="error" style={{marginTop:10}}>{error}</div>}<button className="btn btn-primary" style={{marginTop:16}} disabled={isSubmitting}>Create user</button></form></Modal></>;
}
