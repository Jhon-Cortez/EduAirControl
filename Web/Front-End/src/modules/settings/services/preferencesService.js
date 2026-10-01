import apiClient from '../../../shared/services/apiClient';

/**
 * Preferencias de usuario sincronizadas con ux.user_preference.
 * Todas las operaciones son tolerantes a fallos: si el backend no está
 * disponible la app sigue funcionando con localStorage.
 */
const BASE = '/api/v1/preferences';

export const DEFAULT_REMINDERS = {
  alertas: true,
  advertencias: true,
  resumenDiario: false,
  sonido: true,
};

const preferencesService = {
  async get() {
    try {
      return await apiClient.get(BASE);
    } catch {
      return null;
    }
  },

  async save(preferences) {
    try {
      return await apiClient.put(BASE, preferences);
    } catch {
      return null;
    }
  },
};

export default preferencesService;
