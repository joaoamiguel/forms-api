import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import { apiFetch, getUser } from '../../lib/api';

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const u = getUser();
    setUser(u);
    if (!u) return;
    apiFetch('/api/auth/me').catch(() => {});
    apiFetch('/api/admin/dashboard').then(setData).catch(e => setError(e.message));
  }, []);

  if (!user) return <Layout title="Dashboard"><div className="card">Faça login para acessar.</div></Layout>;
  if (!user.roles?.includes('ADMIN')) return <Layout title="Dashboard"><div className="card error">Acesso restrito ao perfil ADMIN.</div></Layout>;

  return (
    <Layout title="Dashboard Administrativo">
      {error && <p className="error">{error}</p>}
      {!data ? <div className="card">Carregando...</div> : (
        <>
          <div className="grid">
            <div className="card"><h3>Total de usuários</h3><p style={{fontSize:32, margin:0}}>{data.summary.totalUsers}</p></div>
            <div className="card"><h3>Total de respostas</h3><p style={{fontSize:32, margin:0}}>{data.summary.totalSubmissions}</p></div>
            <div className="card"><h3>Respostas por formulário</h3>{Object.entries(data.summary.submissionsByForm).map(([k,v]) => <div key={k}><b>{k}</b>: {v}</div>)}</div>
            <div className="card"><h3>Usuários por perfil</h3>{Object.entries(data.summary.usersByRole).map(([k,v]) => <div key={k}><b>{k}</b>: {v}</div>)}</div>
          </div>
          <div className="card" style={{marginTop:16}}>
            <h3>Submissões</h3>
            <table className="table">
              <thead><tr><th>Usuário</th><th>Form</th><th>Scores</th><th>Data</th></tr></thead>
              <tbody>
                {data.submissions.map(s => <tr key={s.submissionId}><td>{s.userName}<br/><span className="muted">{s.userEmail}</span></td><td>{s.formCode}</td><td>{s.readinessScore ?? '-'} | {s.literacyScore ?? '-'} | {s.opportunityScore ?? '-'} | {s.technicalScore ?? '-'}</td><td>{new Date(s.submittedAt).toLocaleString()}</td></tr>)}
              </tbody>
            </table>
          </div>
        </>
      )}
    </Layout>
  );
}
