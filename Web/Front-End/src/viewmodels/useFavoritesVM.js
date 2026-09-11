/**
 * ViewModel: useFavoritesVM
 * Lógica del modal de confirmación y acciones de favoritos.
 */

import { useState, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { getStatusColor, getDisplayName } from '../modules/environment/utils/environmentHelpers';
import { useEnvironments } from '../context/EnvironmentContext';

export function useFavoritesVM() {
  const { t } = useTranslation();
  const { environments, toggleFavorite } = useEnvironments();

  const [confirmId, setConfirmId] = useState(null);
  const [confirmName, setConfirmName] = useState('');

  const favorites = useMemo(() => environments.filter((e) => e.isFavorite), [environments]);

  const openConfirm = (env) => {
    setConfirmId(env.id);
    setConfirmName(getDisplayName(env, t));
  };

  const confirmRemove = () => {
    if (confirmId) {
      toggleFavorite(confirmId, false);
      setConfirmId(null);
      setConfirmName('');
    }
  };

  const cancelRemove = () => {
    setConfirmId(null);
    setConfirmName('');
  };

  return {
    favorites,
    confirmId,
    confirmName,
    openConfirm,
    confirmRemove,
    cancelRemove,
    getStatusColor,
    toggleFavorite,
  };
}
