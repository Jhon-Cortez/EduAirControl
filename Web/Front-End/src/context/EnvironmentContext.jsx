import { createContext, useContext, useState, useEffect } from 'react';
import environmentService from '../modules/environment/services/environmentService';

const EnvironmentContext = createContext();

export function EnvironmentProvider({ children }) {
  const [environments, setEnvironments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    environmentService.getAll().then((data) => {
      setEnvironments(data);
      setLoading(false);
    });
  }, []);

  const toggleFavorite = (id, favorite) => {
    environmentService.toggleFavorite(id);
    setEnvironments((prev) =>
      prev.map((env) =>
        env.id === id ? { ...env, isFavorite: favorite } : env
      )
    );
  };

  const addEnvironment = (data) => {
    environmentService.create(data).then((newEnv) => {
      setEnvironments((prev) => [...prev, newEnv]);
    });
  };

  const editEnvironment = (id, data) => {
    environmentService.update(id, data);
    setEnvironments((prev) =>
      prev.map((env) => (env.id === id ? { ...env, ...data } : env))
    );
  };

  const deleteEnvironment = (id) => {
    environmentService.delete(id);
    setEnvironments((prev) => prev.filter((env) => env.id !== id));
  };

  const refreshEnvironments = () => {
    environmentService.getAll().then(setEnvironments);
  };

  return (
    <EnvironmentContext.Provider
      value={{
        environments,
        loading,
        toggleFavorite,
        addEnvironment,
        editEnvironment,
        deleteEnvironment,
        refreshEnvironments,
      }}
    >
      {children}
    </EnvironmentContext.Provider>
  );
}

export function useEnvironment() {
  return useContext(EnvironmentContext);
}

export const useEnvironments = useEnvironment;
