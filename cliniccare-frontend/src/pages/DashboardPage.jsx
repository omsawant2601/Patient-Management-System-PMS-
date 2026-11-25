import { useEffect, useMemo, useState } from 'react';
import { Users, CalendarDays, CalendarCheck, BellRing } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import { appointmentApi, patientApi, reminderApi } from '../api/modules';
import { Loading,ErrorState,Badge } from '../components/common';
import { apiError } from '../utils/errors';

export default function DashboardPage(){
 const [loading,setLoading]=useState(true);const [error,setError]=useState('');
 const [patients,setPatients]=useState([]);const [appointments,setAppointments]=useState([]);const [reminders,setReminders]=useState([]);
 useEffect(()=>{let active=true;(async()=>{try{
  const [p,a,r]=await Promise.all([patientApi.list({page:0,size:5,sort:'createdAt,desc'}),appointmentApi.list({page:0,size:100}),reminderApi.list()]);
  if(active){setPatients(p.data.data.content||[]);setAppointments(a.data.data.content||[]);setReminders(r.data.data||[]);}
 }catch(e){if(active)setError(apiError(e));}finally{if(active)setLoading(false)}})();return()=>{active=false}},[]);
 const today=new Date().toISOString().slice(0,10);
 const todays=appointments.filter(a=>a.scheduledAt?.startsWith(today));
 const chart=useMemo(()=>{const counts={};appointments.forEach(a=>{const d=a.scheduledAt?.slice(0,10);if(d)counts[d]=(counts[d]||0)+1});return Object.entries(counts).slice(-7).map(([date,count])=>({date,count}))},[appointments]);
 if(loading)return <Loading/>;if(error)return <ErrorState message={error}/>;
 const cards=[['Registered patients',patients.length,Users],["Today's appointments",todays.length,CalendarDays],['Upcoming appointments',appointments.filter(a=>a.scheduledAt>new Date().toISOString()).length,CalendarCheck],['Reminder activity',reminders.length,BellRing]];
 return <>
 <div className="card-grid">{cards.map(([label,value,Icon])=><div className="card stat-card" key={label}><div className="stat-icon"><Icon size={21}/></div><div><strong>{value}</strong><span>{label}</span></div></div>)}</div>
 <div style={{display:'grid',gridTemplateColumns:'1.3fr 1fr',gap:16,marginTop:16}}>
  <div className="card"><h3 style={{marginTop:0}}>Appointment statistics</h3><div style={{height:260}}><ResponsiveContainer width="100%" height="100%"><BarChart data={chart}><CartesianGrid stroke="#edf2f5" strokeDasharray="3 3"/><XAxis dataKey="date" fontSize={12}/><YAxis allowDecimals={false}/><Tooltip/><Bar dataKey="count" fill="#2878b9" radius={[5,5,0,0]}/></BarChart></ResponsiveContainer></div></div>
  <div className="card"><h3 style={{marginTop:0}}>Today's appointments</h3>{todays.length?<div>{todays.slice(0,6).map(a=><div key={a.id} style={{display:'flex',justifyContent:'space-between',padding:'10px 0',borderBottom:'1px solid #edf2f5'}}><span>{a.appointmentNumber}</span><Badge status={a.status}/></div>)}</div>:<div className="empty">No appointments today</div>}</div>
 </div>
 <div className="card" style={{marginTop:16}}><h3 style={{marginTop:0}}>Recent patients</h3>{patients.length?<div className="table-wrap"><table className="table"><thead><tr><th>Registration</th><th>Name</th><th>Mobile</th></tr></thead><tbody>{patients.map(p=><tr key={p.id}><td>{p.patientCode}</td><td>{p.fullName}</td><td>{p.mobile}</td></tr>)}</tbody></table></div>:<div className="empty">No patients yet</div>}</div>
 </>;
}
