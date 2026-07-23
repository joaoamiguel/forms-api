import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/router';
import Layout from '../../components/Layout';
import { apiFetch, getUser } from '../../lib/api';

function isSelected(selected, value) {
  if (Array.isArray(selected)) return selected.includes(value);
  return selected === value;
}

export default function FormPage() {
  const router = useRouter();
  const { code } = router.query;
  const [user, setUser] = useState(null);
  const [form, setForm] = useState(null);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  useEffect(() => {
    const u = getUser();
    setUser(u);
    if (!u) { router.replace('/auth/login'); return; }
  }, [router]);

  useEffect(() => {
    if (!code || !getUser()) return;
    setLoading(true);
    apiFetch(`/api/forms/${code}`)
      .then(setForm)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, [code]);

  const answeredCount = useMemo(() => {
    if (!form) return 0;
    return form.questions.filter(q => {
      if (!q.required) return true;
      const v = answers[q.questionKey];
      return v !== undefined && v !== null && !(typeof v === 'string' && !v.trim()) && !(Array.isArray(v) && v.length === 0);
    }).length;
  }, [form, answers]);

  const setAnswer = (key, value) => setAnswers(prev => ({ ...prev, [key]: value }));

  const submit = async e => {
    e.preventDefault();
    setError('');
    try {
      const data = await apiFetch(`/api/submissions/${code}`, { method: 'POST', body: JSON.stringify({ answers }) });
      setResult(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const renderQuestion = (q) => {
    const v = answers[q.questionKey];
    if (q.sectionTitle) {
      // handled externally in grouping
    }
    return (
      <div className="question" key={q.questionKey}>
        <h3>{q.label} {q.reversed ? <span className="pill">R</span> : null}</h3>
        {q.helpText ? <p className="muted">{q.helpText}</p> : null}
        {(q.type === 'LIKERT' || q.type === 'LADDER') && (
          <div className="choices">
            {(q.type === 'LIKERT' ? [1,2,3,4,5].map(n => ({ value: n, label: String(n) })) : q.options.map((opt, idx) => ({ value: idx + 1, label: opt }))).map(item => (
              <label className="choice" key={item.value}>
                <input
                  type="radio"
                  checked={v === item.value}
                  onChange={() => setAnswer(q.questionKey, item.value)}
                />
                <span>{item.label}</span>
              </label>
            ))}
          </div>
        )}
        {q.type === 'OBJECTIVE' && (
          <div className="choices">
            {q.options.map((opt, idx) => (
              <label className="choice" key={idx}>
                <input type="radio" checked={v === idx} onChange={() => setAnswer(q.questionKey, idx)} />
                <span>{opt}</span>
              </label>
            ))}
          </div>
        )}
        {q.type === 'SINGLE_UNSCORED' && (
          <div className="choices">
            {q.options.map((opt, idx) => (
              <label className="choice" key={idx}>
                <input type="radio" checked={v === idx} onChange={() => setAnswer(q.questionKey, idx)} />
                <span>{opt}</span>
              </label>
            ))}
          </div>
        )}
        {q.type === 'MULTI_CHECK' && (
          <div className="choices">
            {q.options.map(opt => {
              const selected = Array.isArray(v) ? v.includes(opt) : false;
              return (
                <label className="choice" key={opt}>
                  <input
                    type="checkbox"
                    checked={selected}
                    onChange={(e) => {
                      const current = Array.isArray(v) ? [...v] : [];
                      if (e.target.checked) {
                        if (opt === 'Ainda não usei') setAnswer(q.questionKey, [opt]);
                        else {
                          const withoutNone = current.filter(x => x !== 'Ainda não usei');
                          setAnswer(q.questionKey, [...withoutNone, opt]);
                        }
                      } else {
                        setAnswer(q.questionKey, current.filter(x => x !== opt));
                      }
                    }}
                  />
                  <span>{opt}</span>
                </label>
              );
            })}
          </div>
        )}
        {q.type === 'OPEN' && (
          <textarea rows={4} value={v || ''} onChange={e => setAnswer(q.questionKey, e.target.value)} />
        )}
      </div>
    );
  };

  if (!user) return <Layout title="Formulário"><div className="card">Redirecionando...</div></Layout>;
  if (loading) return <Layout title="Formulário"><div className="card">Carregando...</div></Layout>;
  if (!form) return <Layout title="Formulário"><div className="card error">{error || 'Formulário não encontrado'}</div></Layout>;

  const grouped = form.questions.reduce((acc, q) => {
    acc[q.sectionTitle || 'Geral'] = acc[q.sectionTitle || 'Geral'] || [];
    acc[q.sectionTitle || 'Geral'].push(q);
    return acc;
  }, {});

  return (
    <Layout title={form.title}>
      {result ? (
        <div className="grid">
          <div className="card">
            <h2>Resultado enviado</h2>
            <p><b>Formulário:</b> {result.formCode}</p>
            {result.readinessScore !== null && <p><b>Prontidão:</b> {result.readinessScore}/100</p>}
            {result.literacyScore !== null && <p><b>Literacia:</b> {result.literacyScore}/100</p>}
            {result.opportunityScore !== null && <p><b>Oportunidade:</b> {result.opportunityScore}/100</p>}
            {result.quadrant && <p><b>Quadrante:</b> {result.quadrant}</p>}
            {result.technicalScore !== null && <p><b>Índice técnico:</b> {result.technicalScore}/100</p>}
            {result.technicalBand && <p><b>Classificação:</b> {result.technicalBand}</p>}
          </div>
          <div className="card">
            <h3>Resposta registrada</h3>
            <pre style={{whiteSpace:'pre-wrap'}}>{JSON.stringify(result.answers, null, 2)}</pre>
          </div>
        </div>
      ) : (
        <form onSubmit={submit}>
          <div className="card">
            <p className="muted">{form.description}</p>
            <p><b>Progresso:</b> {answeredCount} / {form.questions.length}</p>
          </div>
          {Object.entries(grouped).map(([section, questions]) => (
            <div key={section}>
              <div className="section">{section}</div>
              {questions.map(renderQuestion)}
            </div>
          ))}
          {error ? <p className="error">{error}</p> : null}
          <button className="btn" type="submit">Enviar respostas</button>
        </form>
      )}
    </Layout>
  );
}
