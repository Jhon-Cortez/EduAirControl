import { dbClient } from '../../../shared/services/apiClient';

const sensorService = {
  async getAll() {
    return dbClient.get('/sensors');
  },

  async getByEnvironment(environmentId) {
    return dbClient.get(`/sensors?environmentId=${environmentId}`);
  },

  async create(sensor) {
    return dbClient.post('/sensors', sensor);
  },

  async update(id, updates) {
    return dbClient.patch(`/sensors/${id}`, updates);
  },

  async delete(id) {
    return dbClient.delete(`/sensors/${id}`);
  },

  async toggleActive(id) {
    const sensor = await dbClient.get(`/sensors/${id}`);
    return dbClient.patch(`/sensors/${id}`, { active: !sensor.active });
  },
};

export default sensorService;
