import apiClient from '../../../shared/services/apiClient';

const sensorService = {
  async getAll() {
    return apiClient.get('/sensors');
  },

  async getByEnvironment(environmentId) {
    return apiClient.get(`/sensors?environmentId=${environmentId}`);
  },

  async create(sensor) {
    return apiClient.post('/sensors', sensor);
  },

  async update(id, updates) {
    return apiClient.patch(`/sensors/${id}`, updates);
  },

  async delete(id) {
    return apiClient.delete(`/sensors/${id}`);
  },

  async toggleActive(id) {
    const sensor = await apiClient.get(`/sensors/${id}`);
    return apiClient.patch(`/sensors/${id}`, { active: !sensor.active });
  },
};

export default sensorService;
