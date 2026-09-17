import apiClient from '../../../shared/services/apiClient';

const authService = {
  async login(email, password) {
    const data = await apiClient.post('/auth/login', { email, password });
    localStorage.setItem('token', data.token);
    return data;
  },

  async register(name, email, password) {
    const data = await apiClient.post('/auth/register', { name, email, password });
    localStorage.setItem('token', data.token);
    return data;
  },

  logout() {
    localStorage.removeItem('token');
  },

  getToken() {
    return localStorage.getItem('token');
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },
};

export default authService;
