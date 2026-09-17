const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const DB_BASE = import.meta.env.VITE_DB_URL || 'http://localhost:3001';

function getToken() {
  return localStorage.getItem('token');
}

async function request(endpoint, options = {}, baseUrl = API_BASE) {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${baseUrl}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.message || `Error ${response.status}`);
  }

  if (response.status === 204) return null;
  return response.json();
}

const apiClient = {
  get: (endpoint) => request(endpoint),
  post: (endpoint, body) => request(endpoint, { method: 'POST', body: JSON.stringify(body) }),
  put: (endpoint, body) => request(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
  patch: (endpoint, body) => request(endpoint, { method: 'PATCH', body: JSON.stringify(body) }),
  delete: (endpoint) => request(endpoint, { method: 'DELETE' }),
};

export const dbClient = {
  get: (endpoint) => request(endpoint, {}, DB_BASE),
  post: (endpoint, body) => request(endpoint, { method: 'POST', body: JSON.stringify(body) }, DB_BASE),
  put: (endpoint, body) => request(endpoint, { method: 'PUT', body: JSON.stringify(body) }, DB_BASE),
  patch: (endpoint, body) => request(endpoint, { method: 'PATCH', body: JSON.stringify(body) }, DB_BASE),
  delete: (endpoint) => request(endpoint, { method: 'DELETE' }, DB_BASE),
};

export default apiClient;
