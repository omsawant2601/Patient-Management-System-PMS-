import { useEffect,useState } from 'react';
import { BarChart,Bar,XAxis,YAxis,Tooltip,ResponsiveContainer,CartesianGrid,PieChart,Pie,Cell } from 'recharts';
import { appointmentApi,patientApi } from '../api/modules';
import { Loading,ErrorState } from '../components/common';
import { apiError } from '../utils/errors';

export default function ReportsPage(){
 const [loading,setLoading]=useState(true),[error,setError]=useState(''),[patients,setPatients]=useState([]),[appointments,setAppointments]=useState([]);
 useEffect(()=>{Promise.all([patientApi.list({page:0,size:100}),appointmentApi.list({page:0,size:100})]).then(([p,a])=>{setPatients(p.data.data.content||[]);setAppointments(a.data.data.content||[])}).catch(e=>setError(apiError(e))).finally(()=>setLoading(false))},[]);
 if(loading)return <Loading/>;if(error)return <ErrorState message={error}/>;
 const statuses=['COMPLETED','CANCELLED','NO_SHOW','SCHEDULED','CONFIRMED'].map(status=>({name:status, value:appointments.filter(a=>a.status===status).length}));
 const today=new Date().toISOString().slice(0,10);
 const data=[['Registered patients',patients.length],["Today's appointments",appointments.filter(a=>a.scheduledAt?.startsWith(today)).length],['Completed appointments',statuses.find(x=>x.name==='COMPLETED')?.value||0],['Cancelled appointments',statuses.find(x=>x.name==='CANCELLED')?.value||0]];
 const colors=['#2878b9','#b9524c','#d59d35','#65b28b','#7f9fb5'];
 return <><div className="card-grid">{data.map(([label,value])=><div className="card stat-card" key={label}><div><strong>{value}</strong><span>{label}</span></div></div>)}</div>
 <div style={{display:'grid',gridTemplateColumns:'1fr 1fr',gap:16,marginTop:16}}><div className="card"><h3 style={{marginTop:0}}>Appointments by status</h3><div style={{height:280}}><ResponsiveContainer><BarChart data={statuses}><CartesianGrid strokeDasharray="3 3" stroke="#edf2f5"/><XAxis dataKey="name" fontSize={11}/><YAxis allowDecimals={false}/><Tooltip/><Bar dataKey="value" fill="#2878b9" radius={[5,5,0,0]}/></BarChart></ResponsiveContainer></div></div>
 <div className="card"><h3 style={{marginTop:0}}>Status distribution</h3><div style={{height:280}}><ResponsiveContainer><PieChart><Pie data={statuses} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label>{statuses.map((_,i)=><Cell key={i} fill={colors[i]}/>)}</Pie><Tooltip/></PieChart></ResponsiveContainer></div></div></div></>;
}
