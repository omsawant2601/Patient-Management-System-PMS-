import { useAuth } from '../context/AuthContext';
export default function ProfilePage(){
 const {user}=useAuth();
 return <div className="card" style={{maxWidth:620}}><h2 style={{marginTop:0}}>My profile</h2><table className="table"><tbody><tr><th>Full name</th><td>{user?.fullName}</td></tr><tr><th>Username</th><td>{user?.username}</td></tr><tr><th>Email</th><td>{user?.email||'Not configured'}</td></tr><tr><th>Role</th><td>{user?.role}</td></tr><tr><th>Mobile</th><td>{user?.mobile}</td></tr></tbody></table></div>;
}
