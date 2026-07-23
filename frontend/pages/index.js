import { useEffect, useState } from 'react';
import Link from 'next/link';
import Layout from '../components/Layout';
import { apiFetch, getUser } from '../lib/api';

export default function Home() {
  const [user, setUser] = useState(null);
  const [forms, setForms] = useState([]);
  const [error, setError] = useState('');
  useEffect(() => {
    setUser(getUser());
  }, []);
  useEffect(() => {
    if (!user) return;
    apiFetch('/api/forms').then(setForms).catch(e => setError(e.message));
  }, [user]);
  return (
    <Layout title="Início">
      <div className="grid">
        <div className="card">
          <h2>Bem-vindo</h2>
          <p className="muted">Plataforma para responder os formulários de prontidão em IA.</p>
          {user ? <p><b>Logado como:</b> {user.name} ({user.roles?.join(', ')})</p> : <p className="muted">Faça login para começar.</p>}
          <div className="row">
            {!user && <Link className="btn" href="/auth/login">Login</Link>}
            {!user && <Link className="btn secondary" href="/auth/register">Cadastro</Link>}
          </div>
        </div>
        <div className="card">
          <h3>Formulários disponíveis</h3>
          {user ? error ? <p className="error">{error}</p> : null : <p className="muted">Entre para ver os formulários liberados.</p>}
          {forms.map(form => <div key={form.code} style={{marginBottom:12}}><b>{form.title}</b><br/><span className="muted">{form.description}</span><br/><Link href={`/forms/${form.code}`}>Abrir</Link></div>)}
        </div>
        <div className="card">
          <h3>Dashboard</h3>
          <p className="muted">Área administrativa para ADMIN.</p>
          <Link href="/dashboard">Ir para dashboard</Link>
        </div>
      </div>
    </Layout>
  );
}
