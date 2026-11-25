import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiError } from '../utils/errors';

const schema = yup.object({ username: yup.string().required('Username is required'), password: yup.string().required('Password is required') });
export default function LoginPage() {
    const { login } = useAuth(); const navigate = useNavigate(); const [error, setError] = useState('');
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({ resolver: yupResolver(schema) });
    const submit = async values => { setError(''); try { await login(values); navigate('/dashboard', { replace: true }); } catch (e) { setError(apiError(e)); } };
    return <div className="login-page"><section className="login-art"><div><div className="brand-mark" style={{ width: 52, height: 52 }}>C</div><h1 style={{ fontSize: 42 }}>ClinicCare</h1><p style={{ color: '#d9ebf6', lineHeight: 1.7 }}>A secure workspace for patient care, appointments and clinic operations.</p></div></section>
        <section className="login-box"><form className="login-form" onSubmit={handleSubmit(submit)}><h2 style={{ fontSize: 28, color: '#12344f', margin: '0 0 6px' }}>Welcome back</h2><p style={{ color: '#718298', marginBottom: 24 }}>Sign in to your ClinicCare account</p>
            {error && <div className="page-error" style={{ marginBottom: 14, padding: 12, textAlign: 'left' }}>{error}</div>}
            <div className="field" style={{ marginBottom: 14 }}><label>Username</label><input className="input" {...register('username')} autoComplete="username" />{errors.username && <div className="error">{errors.username.message}</div>}</div>
            <div className="field" style={{ marginBottom: 18 }}><label>Password</label><input className="input" type="password" {...register('password')} autoComplete="current-password" />{errors.password && <div className="error">{errors.password.message}</div>}</div>
            <button className="btn btn-primary" style={{ width: '100%' }} disabled={isSubmitting}>{isSubmitting ? 'Signing in...' : 'Sign in'}</button>
            <div style={{ textAlign: 'right', marginTop: 14 }}><Link to="/forgot-password" style={{ color: '#2878b9', fontSize: 14 }}>Forgot password?</Link></div>
        </form></section></div>;
}
