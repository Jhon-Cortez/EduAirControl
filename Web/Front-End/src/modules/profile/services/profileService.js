import { DEFAULT_PROFILE } from '../data/defaultProfile';

const PROFILE_KEY = 'profile';

const profileService = {
  get() {
    try {
      const raw = localStorage.getItem(PROFILE_KEY);
      return raw ? JSON.parse(raw) : DEFAULT_PROFILE;
    } catch {
      return DEFAULT_PROFILE;
    }
  },

  save(profile) {
    localStorage.setItem(PROFILE_KEY, JSON.stringify(profile));
    window.dispatchEvent(new CustomEvent('profile-updated'));
  },

  clear() {
    localStorage.removeItem(PROFILE_KEY);
  },
};

export default profileService;
