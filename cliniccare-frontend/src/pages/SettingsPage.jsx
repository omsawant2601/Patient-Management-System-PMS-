import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authApi } from '../api/modules';
import { apiError } from '../utils/errors';

const schema=yup.object({currentPassword:yup.string().required(),newPassword:yup.string().min(10).required(),confirmPassword:yup.string().oneOf([yup.ref('newPassword')],'Passwords must match').required()});
export default function SettingsPage(){
 const [message,setMessage]=useState(''),[error,setError]=useState('');
 const {register,handleSubmit,reset,formState:{errors,isSubmitting}}=useForm({resolver:yupResolver(schema)});
 const submit=async v=>{setError('');setMessage('');try{await authApi.changePassword({currentPassword:v.currentPassword,newPassword:v.newPassword});reset();setMessage('Password changed successfully')}catch(e){setError(apiError(e))}};
 return <div className="card" style={{maxWidth:620}}><h2 style={{marginTop:0}}>Change password</h2>{message&&<div className="badge badge-green" style={{marginBottom:14}}>{message}</div>}<form onSubmit={handleSubmit(submit)}>
 <div className="form-grid"><div className="field full"><label>Current password</label><input className="input" type="password" {...register('currentPassword')}/><div className="error">{errors.currentPassword?.message}</div></div>
 <div className="field"><label>New password</label><input className="input" type="password" {...register('newPassword')}/><div className="error">{errors.newPassword?.message}</div></div>
 <div className="field"><label>Confirm password</label><input className="input" type="password" {...register('confirmPassword')}/><div className="error">{errors.confirmPassword?.message}</div></div></div>
 {error&&<div className="error" style={{marginTop:12}}>{error}</div>}<button className="btn btn-primary" style={{marginTop:16}} disabled={isSubmitting}>Change password</button></form></div>;
}
