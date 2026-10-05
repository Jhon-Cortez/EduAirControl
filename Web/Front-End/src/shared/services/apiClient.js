const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

function getToken() {
  return localStorage.getItem('token');
}

function isAuthEndpoint(endpoint) {
  return endpoint.startsWith('/auth/');
}

const PUBLIC_PATHS = ['/landing', '/guide', '/terms', '/login', '/forgot-password', '/verify-code', '/change-password'];

function handleUnauthorized() {
  const hadSession = Boolean(getToken());
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.dispatchEvent(new Event('eduaircontrol:auth'));
  if (!hadSession) return;
  if (PUBLIC_PATHS.some((path) => window.location.pathname.startsWith(path))) return;
  window.location.assign('/login');
}

function messageFor(status, body) {
  if (body?.message) return body.message;
  switch (status) {
    case 400:
      return 'Datos inválidos';
    case 401:
      return 'Sesión expirada o credenciales inválidas';
    case 403:
      return 'No tienes permiso para esta acción';
    case 404:
      return 'Recurso no encontrado';
    case 409:
      return 'El correo ya está registrado';
    case 500:
      return 'Error del servidor';
    default:
      return `Error ${status}`;
  }
}

async function request(endpoint, options = {}) {
  const token = getToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    if (response.status === 401 && !isAuthEndpoint(endpoint)) {
      handleUnauthorized();
    }
    throw new Error(messageFor(response.status, body));
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
  deleteWithBody: (endpoint, body) =>
    request(endpoint, { method: 'DELETE', body: JSON.stringify(body) }),
  request,
};

export default apiClient;
