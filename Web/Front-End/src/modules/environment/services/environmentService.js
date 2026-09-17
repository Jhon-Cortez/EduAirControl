import { dbClient } from '../../../shared/services/apiClient';

const environmentService = {
  async getAll() {
    return dbClient.get('/environments');
  },

  async getById(id) {
    return dbClient.get(`/environments/${id}`);
  },

  async getFavorites() {
    return dbClient.get('/environments?isFavorite=true');
  },

  async create(environment) {
    return dbClient.post('/environments', {
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
    return dbClient.patch(`/environments/${id}`, updates);
  },

  async delete(id) {
    return dbClient.delete(`/environments/${id}`);
  },

  async toggleFavorite(id) {
    const env = await dbClient.get(`/environments/${id}`);
    return dbClient.patch(`/environments/${id}`, { isFavorite: !env.isFavorite });
  },
};

export default environmentService;
