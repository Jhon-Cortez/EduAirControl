import apiClient from '../../../shared/services/apiClient';

function decodeJWT(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

const authService = {
  async login(email, password) {
    const data = await apiClient.post('/auth/login', { email, password });
    localStorage.setItem('token', data.token);
    const claims = decodeJWT(data.token);
    const user = { email: claims?.sub || email, role: claims?.role || 'USER', name: email.split('@')[0] };
    localStorage.setItem('user', JSON.stringify(user));
    return data;
  },

  async register(name, email, password) {
    const data = await apiClient.post('/auth/register', { name, email, password });
    localStorage.setItem('token', data.token);
    const claims = decodeJWT(data.token);
    const user = { email: claims?.sub || email, role: claims?.role || 'USER', name };
    localStorage.setItem('user', JSON.stringify(user));
    return data;
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getToken() {
    return localStorage.getItem('token');
  },

  getUser() {
    try {
      return JSON.parse(localStorage.getItem('user'));
    } catch {
      return null;
    }
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },
};

export default authService;
