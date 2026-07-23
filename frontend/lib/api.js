const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export function getToken() {
  if (typeof window === 'undefined') return null;
  return window.localStorage.getItem('token');
}

export function setAuth(data) {
  if (typeof window === 'undefined') return;
  window.localStorage.setItem('token', data.token);
  window.localStorage.setItem('user', JSON.stringify({
    userId: data.userId,
    name: data.name,
    email: data.email,
    roles: data.roles,
  }));
}

export function clearAuth() {
  if (typeof window === 'undefined') return;
  window.localStorage.removeItem('token');
  window.localStorage.removeItem('user');
}

export function getUser() {
  if (typeof window === 'undefined') return null;
  const raw = window.localStorage.getItem('user');
  return raw ? JSON.parse(raw) : null;
}

export async function apiFetch(path, options = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(`${API_URL}${path}`, { ...options, headers });
  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = { message: text }; }
  if (!res.ok) {
    throw new Error(data?.message || 'Erro na requisição');
  }
  return data;
}
