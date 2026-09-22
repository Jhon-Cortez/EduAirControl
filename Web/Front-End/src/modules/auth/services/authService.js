import apiClient from '../../../shared/services/apiClient';

function base64UrlDecode(str) {
  const normalized = str.replace(/-/g, '+').replace(/_/g, '/');
  const padded = normalized + '='.repeat((4 - (normalized.length % 4)) % 4);
  return atob(padded);
}

function decodeJWT(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(base64UrlDecode(payload));
  } catch {
    return null;
  }
}

const authService = {
  setSession(token, extraUser = {}) {
    localStorage.setItem('token', token);
    const claims = decodeJWT(token);
    const email = claims?.sub || extraUser.email || '';
    const user = {
      email,
      role: claims?.role || 'USER',
      name: extraUser.name || (email ? email.split('@')[0] : ''),
      companyCode: extraUser.companyCode || null,
    };
    localStorage.setItem('user', JSON.stringify(user));
    return user;
  },

  async login(email, password, companyCode) {
    const data = await apiClient.post('/auth/login', { email, password, companyCode });
    this.setSession(data.token, { email });
    return data;
  },

  async register(name, email, password, companyCode) {
    const data = await apiClient.post('/auth/register', { name, email, password, companyCode });
    this.setSession(data.token, { name, email, companyCode });
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
    const token = this.getToken();
    if (!token) return false;

    const claims = decodeJWT(token);
    if (!claims) {
      this.logout();
      return false;
    }

    if (typeof claims.exp === 'number' && claims.exp * 1000 <= Date.now()) {
      this.logout();
      return false;
    }

    return true;
  },
};

export default authService;
