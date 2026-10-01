import { createContext, useContext } from 'react';

export const EnvironmentContext = createContext();

export function useEnvironment() {
  return useContext(EnvironmentContext);
}

export const useEnvironments = useEnvironment;
