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

  /**
   * Edición completa: el backend exige `environmentId` y `variable` en todo PATCH.
   */
  async update(serial, updates) {
    return apiClient.patch(`${BASE}/${serial}`, updates);
  },

  async delete(serial) {
    return apiClient.delete(`${BASE}/${serial}`);
  },

  /** Alterna activo/inactivo; el backend lo resuelve en POST /{id}/toggle. */
  async toggleActive(serial) {
    return apiClient.post(`${BASE}/${serial}/toggle`, {});
  },
};

export default sensorService;
