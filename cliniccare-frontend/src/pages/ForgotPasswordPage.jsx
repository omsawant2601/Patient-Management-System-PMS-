import { Link } from 'react-router-dom';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authApi } from '../api/modules';
import { apiError } from '../utils/errors';

const schema = yup.object({ username: yup.string().trim().required('Enter your username') });

export default function ForgotPasswordPage() {
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({ resolver: yupResolver(schema) });

  const submit = async ({ username }) => {
    setError('');
    setMessage('');
    try {
      await authApi.forgotPassword(username);
      setMessage('If the account exists and has a recovery email, reset instructions will be sent.');
    } catch (requestError) {
      setError(apiError(requestError));
    }
  };

  return <div className="login-page"><section className="login-art"><div><div className="brand-mark" style={{ width: 52, height: 52 }}>C</div><h1 style={{ fontSize: 42 }}>ClinicCare</h1><p style={{ color: '#d9ebf6', lineHeight: 1.7 }}>Secure account recovery for your clinic workspace.</p></div></section>
    <section className="login-box"><form className="login-form" onSubmit={handleSubmit(submit)}>
      <h2 style={{ fontSize: 28, color: '#12344f', margin: '0 0 6px' }}>Reset your password</h2>
      <p style={{ color: '#718298', marginBottom: 24 }}>Enter your account username. If recovery is available, we’ll email a one-time link.</p>
      {message && <div className="badge badge-green" style={{ display: 'block', padding: 12, marginBottom: 14 }}>{message}</div>}
      {error && <div className="page-error" style={{ marginBottom: 14, padding: 12, textAlign: 'left' }}>{error}</div>}
      <div className="field" style={{ marginBottom: 18 }}><label>Username</label><input className="input" autoComplete="username" {...register('username')} />{errors.username && <div className="error">{errors.username.message}</div>}</div>
      <button className="btn btn-primary" style={{ width: '100%' }} disabled={isSubmitting}>{isSubmitting ? 'Sending...' : 'Send reset link'}</button>
      <div style={{ textAlign: 'center', marginTop: 16 }}><Link to="/login" style={{ color: '#2878b9' }}>Back to sign in</Link></div>
    </form></section></div>;
}
