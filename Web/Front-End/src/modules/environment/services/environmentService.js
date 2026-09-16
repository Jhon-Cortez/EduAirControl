import environmentsData from '../data/environmentData';

const environmentService = {
  getAll() {
    return [...environmentsData];
  },

  getById(id) {
    return environmentsData.find((environment) => environment.id === id);
  },

  getFavorites() {
    return environmentsData.filter((environment) => environment.isFavorite);
  },

  getByStatus(statusKey) {
    return environmentsData.filter((environment) => environment.statusKey === statusKey);
  },
};

export default environmentService;
