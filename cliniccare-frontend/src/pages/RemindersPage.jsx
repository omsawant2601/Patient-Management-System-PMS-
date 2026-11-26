import { useEffect,useState } from 'react';
import { Link } from 'react-router-dom';
import { reminderApi } from '../api/modules';
import { Loading,ErrorState,EmptyState,Badge } from '../components/common';
import { apiError } from '../utils/errors';

export default function RemindersPage(){
 const [rows,setRows]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState('');
 useEffect(()=>{let active=true;reminderApi.list().then(r=>active&&setRows(r.data.data||[])).catch(e=>active&&setError(apiError(e))).finally(()=>active&&setLoading(false));return()=>{active=false}},[]);
 return <><div className="toolbar"><h2 style={{margin:0}}>WhatsApp reminders</h2></div>{loading?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap">{rows.length?<table className="table"><thead><tr><th>Patient / appointment</th><th>Type</th><th>Scheduled</th><th>Sent</th><th>Status</th><th>Retries</th><th>Error</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td><Link to={`/patients/${r.patientId}`}>{r.patientName} · Patient #{r.patientCode}</Link><div>{r.appointmentNumber}</div></td><td>{r.reminderType?.replace('_','-')}</td><td>{new Date(r.scheduledAt).toLocaleString()}</td><td>{r.sentAt?new Date(r.sentAt).toLocaleString():'-'}</td><td><Badge status={r.status}/></td><td>{r.retryCount}</td><td style={{maxWidth:220}}>{r.errorMessage||'-'}</td></tr>)}</tbody></table>:<EmptyState/>}</div>}</>;
}
