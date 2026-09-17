import { dbClient } from '../../../shared/services/apiClient';
import authService from '../../auth/services/authService';

const profileService = {
  async get() {
    const jwtUser = authService.getUser();
    if (!jwtUser) return { fullName: '', email: '', title: '', phone: '', location: '', avatar: null };
    try {
      const remote = await dbClient.get('/profile/1');
      return { ...remote, email: jwtUser.email, fullName: jwtUser.name || remote.fullName };
    } catch {
      return {
        fullName: jwtUser.name || '',
        email: jwtUser.email || '',
        title: '',
        phone: '',
        location: '',
        avatar: null,
      };
    }
  },

  async save(profile) {
    return dbClient.put('/profile/1', profile);
  },

  async clear() {
    return dbClient.delete('/profile/1');
  },
};

export default profileService;
