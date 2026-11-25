import { Link, useSearchParams } from 'react-router-dom';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authApi } from '../api/modules';
import { apiError } from '../utils/errors';

const schema = yup.object({
  newPassword: yup.string().min(10, 'Use at least 10 characters').max(128).required('Enter a new password'),
  confirmPassword: yup.string().oneOf([yup.ref('newPassword')], 'Passwords must match').required('Confirm your password'),
});

export default function ResetPasswordPage() {
  const [params] = useSearchParams();
  const token = params.get('token') || '';
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({ resolver: yupResolver(schema) });

  const submit = async ({ newPassword }) => {
    setError('');
    setMessage('');
    try {
      await authApi.resetPassword({ token, newPassword });
      setMessage('Password updated. You can now sign in.');
    } catch (requestError) {
      setError(apiError(requestError));
    }
  };

  return <div className="login-page"><section className="login-art"><div><div className="brand-mark" style={{ width: 52, height: 52 }}>C</div><h1 style={{ fontSize: 42 }}>ClinicCare</h1><p style={{ color: '#d9ebf6', lineHeight: 1.7 }}>Choose a new password for your account.</p></div></section>
    <section className="login-box"><form className="login-form" onSubmit={handleSubmit(submit)}>
      <h2 style={{ fontSize: 28, color: '#12344f', margin: '0 0 6px' }}>Choose a new password</h2>
      {!token && <div className="page-error" style={{ margin: '16px 0', padding: 12, textAlign: 'left' }}>This reset link is missing its token. Request another link.</div>}
      {message && <div className="badge badge-green" style={{ display: 'block', padding: 12, margin: '16px 0' }}>{message}</div>}
      {error && <div className="page-error" style={{ margin: '16px 0', padding: 12, textAlign: 'left' }}>{error}</div>}
      <div className="field" style={{ margin: '16px 0' }}><label>New password</label><input className="input" type="password" autoComplete="new-password" {...register('newPassword')} />{errors.newPassword && <div className="error">{errors.newPassword.message}</div>}</div>
      <div className="field" style={{ marginBottom: 18 }}><label>Confirm password</label><input className="input" type="password" autoComplete="new-password" {...register('confirmPassword')} />{errors.confirmPassword && <div className="error">{errors.confirmPassword.message}</div>}</div>
      <button className="btn btn-primary" style={{ width: '100%' }} disabled={!token || isSubmitting || !!message}>{isSubmitting ? 'Updating...' : 'Reset password'}</button>
      <div style={{ textAlign: 'center', marginTop: 16 }}><Link to="/login" style={{ color: '#2878b9' }}>Back to sign in</Link></div>
    </form></section></div>;
}
