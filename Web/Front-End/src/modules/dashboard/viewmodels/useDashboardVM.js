/**
 * ViewModel: useDashboardVM
 * Lógica del ranking, filtros y score para la pantalla Dashboard.
 */

import { useState, useMemo } from 'react'
import { calcScore } from '../../environment/utils/environmentHelpers'
import { useEnvironments } from '../../../context/EnvironmentContext'

const STATUS_KEY_MAP = {
  normal:  'dashboard.statusNormal',
  warning: 'dashboard.statusWarning',
  alert:   'dashboard.statusAlert',
}

export function useDashboardVM() {
  const { environments, toggleFavorite } = useEnvironments()
  const [filter, setFilter] = useState('all')

  const ranked = useMemo(
    () =>
      environments
        .map((env) => ({ env, score: calcScore(env) }))
        .sort((a, b) => b.score - a.score),
    [environments]
  )

  const filtered = useMemo(() => {
    if (filter === 'all') return ranked
    return ranked.filter(({ env }) => env.statusKey === STATUS_KEY_MAP[filter])
  }, [ranked, filter])

  const statusCounts = useMemo(() => ({
    normal:  environments.filter((e) => e.statusKey === 'dashboard.statusNormal').length,
    warning: environments.filter((e) => e.statusKey === 'dashboard.statusWarning').length,
    alert:   environments.filter((e) => e.statusKey === 'dashboard.statusAlert').length,
    total:   environments.length,
  }), [environments])

  const top3 = filtered.slice(0, 3)
  const rest = filtered.slice(3)

  return {
    filter,
    setFilter,
    filtered,
    top3,
    rest,
    statusCounts,
    toggleFavorite,
  }
}
