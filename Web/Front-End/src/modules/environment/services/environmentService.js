import apiClient from '../../../shared/services/apiClient';

/**
 * Ambientes educativos: el backend devuelve `favorite` y la UI usa `isFavorite`.
 */
function toUi(env) {
  if (!env) return env;
  return { ...env, isFavorite: Boolean(env.favorite) };
}

const environmentService = {
  async getAll() {
    const data = await apiClient.get('/api/v1/environments');
    return (Array.isArray(data) ? data : data.items || []).map(toUi);
  },

  async getById(id) {
    return toUi(await apiClient.get(`/api/v1/environments/${id}`));
  },

  async getFavorites() {
    const all = await this.getAll();
    return all.filter((env) => env.isFavorite);
  },

  async create(environment) {
    return toUi(await apiClient.post('/api/v1/environments', {
      name: environment.name,
      location: environment.location,
      floor: environment.floor,
      capacity: environment.capacity,
      envType: environment.envType,
      tempMin: environment.tempMin,
      tempMax: environment.tempMax,
    }));
  },

  async update(id, updates) {
    return toUi(await apiClient.patch(`/api/v1/environments/${id}`, updates));
  },

  async delete(id) {
    return apiClient.delete(`/api/v1/environments/${id}`);
  },

  async toggleFavorite(id) {
    const result = await apiClient.post(`/api/v1/environments/${id}/favorite`, {});
    return result?.isFavorite;
  },
};

export default environmentService;
