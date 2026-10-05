import { useState, useEffect, useCallback } from 'react';
import { EnvironmentContext } from './useEnvironment';
import environmentService from '../modules/environment/services/environmentService';
import authService from '../modules/auth/services/authService';
import { useToast } from '../shared/hooks/useToast';

export function EnvironmentProvider({ children }) {
  const [environments, setEnvironments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const toast = useToast();

  const loadEnvironments = useCallback(async () => {
    if (!authService.isAuthenticated()) {
      setEnvironments([]);
      setError(null);
      setLoading(false);
      return;
    }
    try {
      const data = await environmentService.getAll();
      setEnvironments(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadEnvironments();
    const onVisibility = () => {
      if (document.visibilityState === 'visible') loadEnvironments();
    };
    window.addEventListener('eduaircontrol:auth', loadEnvironments);
    document.addEventListener('visibilitychange', onVisibility);
    return () => {
      window.removeEventListener('eduaircontrol:auth', loadEnvironments);
      document.removeEventListener('visibilitychange', onVisibility);
    };
  }, [loadEnvironments]);

  const toggleFavorite = async (id, favorite) => {
    const snapshot = environments;
    setEnvironments((prev) =>
      prev.map((env) => (env.id === id ? { ...env, isFavorite: favorite } : env))
    );
    try {
      const isFavorite = await environmentService.toggleFavorite(id);
      setEnvironments((prev) =>
        prev.map((env) => (env.id === id ? { ...env, isFavorite } : env))
      );
      setError(null);
    } catch (err) {
      setEnvironments(snapshot);
      setError(err.message);
      toast.error(err.message);
    }
  };

  const addEnvironment = async (data) => {
    try {
      const newEnv = await environmentService.create(data);
      setEnvironments((prev) => [...prev, newEnv]);
      setError(null);
      return newEnv;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const editEnvironment = async (id, data) => {
    const snapshot = environments;
    setEnvironments((prev) =>
      prev.map((env) => (env.id === id ? { ...env, ...data } : env))
    );
    try {
      const updated = await environmentService.update(id, data);
      setEnvironments((prev) =>
        prev.map((env) => (env.id === id ? { ...env, ...updated } : env))
      );
      setError(null);
      return updated;
    } catch (err) {
      setEnvironments(snapshot);
      setError(err.message);
      throw err;
    }
  };

  const deleteEnvironment = async (id) => {
    const snapshot = environments;
    setEnvironments((prev) => prev.filter((env) => env.id !== id));
    try {
      await environmentService.delete(id);
      setError(null);
    } catch (err) {
      setEnvironments(snapshot);
      setError(err.message);
      throw err;
    }
  };

  const refreshEnvironments = () => loadEnvironments();

  return (
    <EnvironmentContext.Provider
      value={{
        environments,
        loading,
        error,
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
