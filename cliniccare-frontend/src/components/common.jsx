import { AlertCircle } from 'lucide-react';

export function Loading({ label = 'Loading...' }) {
  return <div className="loading"><span className="spinner"></span><span style={{marginLeft:10}}>{label}</span></div>;
}
export function ErrorState({ message }) {
  return <div className="page-error"><AlertCircle size={20}/> {message || 'Unable to load data.'}</div>;
}
export function EmptyState({ message = 'No records found.' }) {
  return <div className="empty">{message}</div>;
}
export function Modal({ title, open, onClose, children }) {
  if (!open) return null;
  return <div className="modal-backdrop" role="dialog" aria-modal="true">
    <div className="modal"><div className="toolbar"><h2 style={{margin:0}}>{title}</h2><button className="btn btn-secondary" onClick={onClose}>Close</button></div>{children}</div>
  </div>;
}
export function Badge({ status }) {
  const value = status || '';
  const cls = ['COMPLETED','FINALIZED','DELIVERED','READ','GRANTED','ACTIVE','CONFIRMED'].includes(value) ? 'badge-green'
    : ['CANCELLED','FAILED','NO_SHOW','REVOKED'].includes(value) ? 'badge-red'
    : ['PENDING','PROCESSING','SCHEDULED','DRAFT'].includes(value) ? 'badge-yellow' : 'badge-blue';
  return <span className={`badge ${cls}`}>{value.replaceAll('_',' ')}</span>;
}
