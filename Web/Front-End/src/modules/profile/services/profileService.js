import apiClient from '../../../shared/services/apiClient';
import { DEFAULT_PROFILE } from '../data/defaultProfile';

const USE_API = false;

const profileService = {
  async get() {
    if (USE_API) return apiClient.get('/api/user/profile');
    try {
      const raw = localStorage.getItem('profile');
      return raw ? JSON.parse(raw) : DEFAULT_PROFILE;
    } catch {
      return DEFAULT_PROFILE;
    }
  },

  async save(profile) {
    if (USE_API) return apiClient.put('/api/user/profile', profile);
    localStorage.setItem('profile', JSON.stringify(profile));
    window.dispatchEvent(new CustomEvent('profile-updated'));
  },

  async clear() {
    localStorage.removeItem('profile');
  },
};

export default profileService;
