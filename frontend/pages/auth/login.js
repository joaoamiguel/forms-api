import { useState } from 'react';
import Link from 'next/link';
import Layout from '../../components/Layout';
import { apiFetch, setAuth } from '../../lib/api';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const submit = async e => {
    e.preventDefault();
    setError('');
    try {
      const data = await apiFetch('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) });
      setAuth(data);
      window.location.href = '/';
    } catch (err) { setError(err.message); }
  };
  return <Layout title="Login"><div className="card" style={{maxWidth:480}}><form onSubmit={submit}><div className="field"><label>Email</label><input value={email} onChange={e=>setEmail(e.target.value)} /></div><div className="field"><label>Senha</label><input type="password" value={password} onChange={e=>setPassword(e.target.value)} /></div>{error && <p className="error">{error}</p>}<button className="btn" type="submit">Entrar</button></form><p className="muted">Sem conta? <Link href="/auth/register">Cadastre-se</Link></p></div></Layout>;
}
