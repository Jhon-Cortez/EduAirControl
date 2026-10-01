import apiClient from '../../../shared/services/apiClient';

const BASE = '/api/v1/sensors';

const sensorService = {
  async getAll(environmentId) {
    const query = environmentId ? `?environmentId=${environmentId}` : '';
    const data = await apiClient.get(`${BASE}${query}`);
    return Array.isArray(data) ? data : data.items || [];
  },

  async getByEnvironment(environmentId) {
    return this.getAll(environmentId);
  },

  async getById(serial) {
    return apiClient.get(`${BASE}/${serial}`);
  },

  async create(sensor) {
    return apiClient.post(BASE, sensor);
  },

  async update(serial, updates) {
    return apiClient.patch(`${BASE}/${serial}`, updates);
  },

  async delete(serial) {
    return apiClient.delete(`${BASE}/${serial}`);
  },

  /** Alterna activo/inactivo; el backend usa el estado ACTIVE/OFFLINE. */
  async toggleActive(serial) {
    const sensor = await this.getById(serial);
    return apiClient.patch(`${BASE}/${serial}`, { active: !sensor.active });
  },
};

export default sensorService;
