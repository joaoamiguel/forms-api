import { useState } from 'react';
import Link from 'next/link';
import Layout from '../../components/Layout';
import { apiFetch, setAuth } from '../../lib/api';

export default function Register() {
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');
  const submit = async e => {
    e.preventDefault();
    setError('');
    try {
      const data = await apiFetch('/api/auth/register', { method: 'POST', body: JSON.stringify(form) });
      setAuth(data);
      window.location.href = '/';
    } catch (err) { setError(err.message); }
  };
  return <Layout title="Cadastro"><div className="card" style={{maxWidth:520}}><form onSubmit={submit}><div className="field"><label>Nome</label><input value={form.name} onChange={e=>setForm({...form,name:e.target.value})} /></div><div className="field"><label>Email</label><input value={form.email} onChange={e=>setForm({...form,email:e.target.value})} /></div><div className="field"><label>Senha</label><input type="password" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} /></div><p className="muted">Sua conta será criada com acesso ao Formulário 1. Acesso administrativo (Formulário 2 e Dashboard) é concedido apenas a usuários com a role ADMIN.</p>{error && <p className="error">{error}</p>}<button className="btn" type="submit">Criar conta</button></form><p className="muted">Já tem conta? <Link href="/auth/login">Entrar</Link></p></div></Layout>;
}
