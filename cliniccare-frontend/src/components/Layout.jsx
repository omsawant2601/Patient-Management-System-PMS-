import { useState } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Users, CalendarDays, Stethoscope, Pill, BellRing, UserCog, ChartNoAxesCombined, Settings, LogOut, Menu, CircleUserRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const titles = { '/dashboard':'Dashboard','/patients':'Patients','/appointments':'Appointments','/consultations':'Consultations','/prescriptions':'Prescriptions','/reminders':'WhatsApp Reminders','/users':'Users','/reports':'Reports','/settings':'Settings','/profile':'Profile' };
export default function Layout() {
  const [open,setOpen]=useState(false);
  const {user,logout}=useAuth();
  const location=useLocation(); const navigate=useNavigate();
  const doctor=user?.role==='DOCTOR';
  const links=[
    ['/dashboard','Dashboard',LayoutDashboard],
    ['/patients','Patients',Users],
    ['/appointments','Appointments',CalendarDays],
    ...(doctor?[['/consultations','Consultations',Stethoscope],['/prescriptions','Prescriptions',Pill]]:[]),
    ['/reminders','WhatsApp Reminders',BellRing],
    ...(doctor?[['/users','Users',UserCog]]:[]),
    ['/reports','Reports',ChartNoAxesCombined],
    ['/settings','Settings',Settings],
  ];
  const title=titles[location.pathname] || 'ClinicCare';
  const doLogout=async()=>{await logout();navigate('/login',{replace:true});};
  return <div className="app-shell">
    {open&&<div onClick={()=>setOpen(false)} style={{position:'fixed',inset:0,zIndex:30,background:'rgba(0,0,0,.2)'}}/>}
    <aside className={`sidebar ${open?'open':''}`}>
      <div className="brand"><div className="brand-mark">C</div><span>ClinicCare</span></div>
      <nav>{links.map(([to,label,Icon])=> <NavLink key={to} to={to} onClick={()=>setOpen(false)} className={({isActive})=>`nav-link ${isActive?'active':''}`}><Icon size={18}/><span>{label}</span></NavLink>)}</nav>
      <button className="nav-link sidebar-footer" onClick={doLogout} style={{background:'transparent',border:0,width:'100%',cursor:'pointer'}}><LogOut size={18}/><span>Logout</span></button>
    </aside>
    <main>
      <header className="topbar">
        <div style={{display:'flex',alignItems:'center',gap:10}}><button className="btn btn-secondary mobile-menu" onClick={()=>setOpen(true)}><Menu size={18}/></button><h1>{title}</h1></div>
        <button className="btn btn-secondary" onClick={()=>navigate('/profile')}><CircleUserRound size={18}/><span>{user?.fullName} · {user?.role}</span></button>
      </header>
      <div className="content"><Outlet/></div>
    </main>
  </div>;
}
