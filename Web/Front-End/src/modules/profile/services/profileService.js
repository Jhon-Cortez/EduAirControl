import apiClient from '../../../shared/services/apiClient';

const profileService = {
  async get() {
    return apiClient.get('/profile/1');
  },

  async save(profile) {
    return apiClient.put('/profile/1', profile);
  },

  async clear() {
    return apiClient.delete('/profile/1');
  },
};

export default profileService;
