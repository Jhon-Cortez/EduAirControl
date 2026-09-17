import apiClient from '../../../shared/services/apiClient';

const environmentService = {
  async getAll() {
    return apiClient.get('/environments');
  },

  async getById(id) {
    return apiClient.get(`/environments/${id}`);
  },

  async getFavorites() {
    const all = await apiClient.get('/environments?isFavorite=true');
    return all;
  },

  async create(environment) {
    return apiClient.post('/environments', {
      ...environment,
      temp: 22,
      humidity: 50,
      co2: 600,
      noise: 40,
      isFavorite: false,
      lastUpdate: 'Ahora',
    });
  },

  async update(id, updates) {
    return apiClient.patch(`/environments/${id}`, updates);
  },

  async delete(id) {
    return apiClient.delete(`/environments/${id}`);
  },

  async toggleFavorite(id) {
    const env = await apiClient.get(`/environments/${id}`);
    return apiClient.patch(`/environments/${id}`, { isFavorite: !env.isFavorite });
  },
};

export default environmentService;
