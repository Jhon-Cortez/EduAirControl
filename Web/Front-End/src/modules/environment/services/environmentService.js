import apiClient from '../../../shared/services/apiClient';
import environmentsData from '../data/environmentData';

const USE_API = false;

const environmentService = {
  async getAll() {
    if (USE_API) return apiClient.get('/api/environments');
    return [...environmentsData];
  },

  async getById(id) {
    if (USE_API) return apiClient.get(`/api/environments/${id}`);
    return environmentsData.find((e) => e.id === Number(id)) || null;
  },

  async getFavorites() {
    if (USE_API) return apiClient.get('/api/environments/favorites');
    return environmentsData.filter((e) => e.isFavorite);
  },

  async create(environment) {
    if (USE_API) return apiClient.post('/api/environments', environment);
    const newEnv = {
      ...environment,
      id: Math.max(...environmentsData.map((e) => e.id), 0) + 1,
      temp: 22,
      humidity: 50,
      co2: 600,
      noise: 40,
      lastUpdate: 'Ahora',
    };
    environmentsData.push(newEnv);
    return newEnv;
  },

  async update(id, updates) {
    if (USE_API) return apiClient.put(`/api/environments/${id}`, updates);
    const index = environmentsData.findIndex((e) => e.id === Number(id));
    if (index !== -1) {
      environmentsData[index] = { ...environmentsData[index], ...updates };
      return environmentsData[index];
    }
    return null;
  },

  async delete(id) {
    if (USE_API) return apiClient.delete(`/api/environments/${id}`);
    const index = environmentsData.findIndex((e) => e.id === Number(id));
    if (index !== -1) environmentsData.splice(index, 1);
  },

  async toggleFavorite(id) {
    const env = environmentsData.find((e) => e.id === Number(id));
    if (env) env.isFavorite = !env.isFavorite;
    return env;
  },
};

export default environmentService;
