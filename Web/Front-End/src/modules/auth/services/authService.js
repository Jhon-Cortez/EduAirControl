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

const RESET_EMAIL_KEY = 'resetEmail';

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

  async forgotPassword(email) {
    return apiClient.post('/auth/forgot-password', { email });
  },

  async verifyCode(email, code) {
    return apiClient.post('/auth/verify-code', { email, code });
  },

  async resetPassword(email, code, newPassword) {
    return apiClient.post('/auth/reset-password', { email, code, newPassword });
  },

  async resendCode(email) {
    return apiClient.post('/auth/resend-code', { email });
  },

  async changePassword(currentPassword, newPassword) {
    return apiClient.post('/auth/change-password', { currentPassword, newPassword });
  },

  async deleteAccount(password) {
    return apiClient.deleteWithBody('/auth/account', { password });
  },

  setResetEmail(email) {
    sessionStorage.setItem(RESET_EMAIL_KEY, email);
  },

  getResetEmail() {
    return sessionStorage.getItem(RESET_EMAIL_KEY) || '';
  },

  clearResetEmail() {
    sessionStorage.removeItem(RESET_EMAIL_KEY);
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
