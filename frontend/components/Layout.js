import Link from 'next/link';
import { useEffect, useState } from 'react';
import { clearAuth, getUser } from '../lib/api';

export default function Layout({ title, children }) {
  const [user, setUser] = useState(null);
  useEffect(() => { setUser(getUser()); }, []);
  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <div className="brand">IA Assessment</div>
          <div className="subtitle">Diagnóstico de prontidão em IA</div>
        </div>
        <nav className="nav">
          <Link href="/">Início</Link>
          <Link href="/forms/FORM1">Formulário 1</Link>
          <Link href="/forms/FORM2">Formulário 2</Link>
          <Link href="/dashboard">Dashboard</Link>
          {!user ? <Link href="/auth/login">Login</Link> : <button className="linklike" onClick={() => { clearAuth(); window.location.href = '/auth/login'; }}>Sair</button>}
        </nav>
      </header>
      <main className="container">
        {title ? <h1 className="page-title">{title}</h1> : null}
        {children}
      </main>
    </div>
  );
}
