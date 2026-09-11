import { createContext, useContext, useState } from 'react';
import environmentData from '../modules/environment/data/environmentData';

const EnvironmentContext = createContext();

export function EnvironmentProvider({ children }) {
  const [environments, setEnvironments] = useState(environmentData);

  const toggleFavorite = (id, favorite) => {
    setEnvironments((prev) =>
      prev.map((env) =>
        env.id === id
          ? {
              ...env,
              isFavorite: favorite,
            }
          : env
      )
    );
  };

  const addEnvironment = (data) => {
    setEnvironments((prev) => {
      const nextId = prev.length ? Math.max(...prev.map((env) => Number(env.id) || 0)) + 1 : 1;

      return [
        ...prev,
        {
          id: nextId,
          name: data.name || `Ambiente ${nextId}`,
          building: data.building || 'Bloque A',
          floor: Number(data.floor) || 1,
          capacity: Number(data.capacity) || 0,
          statusKey: data.statusKey || 'dashboard.statusNormal',
          temp: Number(data.temp) || 22,
          humidity: Number(data.humidity) || 50,
          co2: Number(data.co2) || 600,
          noise: Number(data.noise) || 40,
          isFavorite: false,
          lastUpdate: 'Ahora',
        },
      ];
    });
  };

  const editEnvironment = (id, data) => {
    setEnvironments((prev) =>
      prev.map((env) =>
        env.id === id
          ? {
              ...env,
              ...data,
            }
          : env
      )
    );
  };

  const deleteEnvironment = (id) => {
    setEnvironments((prev) => prev.filter((env) => env.id !== id));
  };

  return (
    <EnvironmentContext.Provider
      value={{
        environments,
        toggleFavorite,
        addEnvironment,
        editEnvironment,
        deleteEnvironment,
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
