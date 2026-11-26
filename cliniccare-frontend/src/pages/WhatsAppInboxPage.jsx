import { useCallback,useEffect,useState } from 'react';
import { RefreshCw } from 'lucide-react';
import { whatsappApi } from '../api/modules';
import { Badge,EmptyState,ErrorState,Loading } from '../components/common';
import { apiError } from '../utils/errors';

export default function WhatsAppInboxPage(){
 const [messages,setMessages]=useState(null);
 const [error,setError]=useState('');

 const load=useCallback(async()=>{
  try{
   const response=await whatsappApi.inbox({page:0,size:50});
   setError('');
   setMessages(response.data.data.content||[]);
  }catch(e){setError(apiError(e))}
 },[]);

 useEffect(()=>{
  const initialLoad=setTimeout(load,0);
  const timer=setInterval(load,30000);
  return()=>{clearTimeout(initialLoad);clearInterval(timer)};
 },[load]);

 return <>
  <div className="toolbar">
   <div><h2 style={{margin:0}}>WhatsApp Inbox</h2><p style={{margin:'6px 0 0',color:'var(--text-muted)'}}>Incoming messages refresh automatically every 30 seconds.</p></div>
   <button className="btn btn-secondary" onClick={()=>load()}><RefreshCw size={16}/> Refresh</button>
  </div>
  {messages===null&&!error?<Loading/>:error?<ErrorState message={error}/>:<div className="card table-wrap">
   {messages.length?<table className="table"><thead><tr><th>Received</th><th>Sender</th><th>Type</th><th>Message</th></tr></thead><tbody>
    {messages.map(message=><tr key={message.id}>
     <td>{new Date(message.receivedAt).toLocaleString()}</td>
     <td>{message.senderName||'Unknown'}<div>{message.senderPhone}</div></td>
     <td><Badge status={message.messageType}/></td>
     <td style={{whiteSpace:'pre-wrap',minWidth:240}}>{message.content}</td>
    </tr>)}
   </tbody></table>:<EmptyState message="No incoming WhatsApp messages have been received."/>}
  </div>}
 </>;
}
