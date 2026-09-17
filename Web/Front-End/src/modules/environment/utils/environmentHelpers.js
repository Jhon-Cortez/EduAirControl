/**
 * Helpers compartidos de ambiente.
 * Lógica pura — sin hooks, sin estado React.
 */

import { STATUS, QUALITY } from '../constants/environments';
import calculateEnvironmentScore from './calculateEnvironmentScore';

export const calcScore = calculateEnvironmentScore;

export function getStatusColor(statusKey) {
  switch (statusKey) {
    case STATUS.NORMAL:
      return '#3fb950';
    case STATUS.WARNING:
      return '#d29922';
    case STATUS.ALERT:
      return '#f85149';
    default:
      return '#8b949e';
  }
}

export function getQualityColor(qualityKey) {
  switch (qualityKey) {
    case QUALITY.GOOD:
      return '#3fb950';
    case QUALITY.REGULAR:
      return '#d29922';
    case QUALITY.BAD:
      return '#f85149';
    default:
      return '#8b949e';
  }
}

export function getBadgeClass(statusKey, qualityKey) {
  const key = qualityKey || statusKey;
  if (key === 'dashboard.qualityGood' || key === STATUS.NORMAL) return 'mgmt-card__badge--good';
  if (key === 'dashboard.qualityBad' || key === STATUS.ALERT) return 'mgmt-card__badge--alert';
  if (key === 'dashboard.qualityRegular' || key === STATUS.WARNING)
    return 'mgmt-card__badge--regular';
  return 'mgmt-card__badge--normal';
}

export function getMetricColor(key, value) {
  if (key === 'temp') return value > 25 ? '#f85149' : value > 23 ? '#d29922' : '#3fb950';
  if (key === 'humidity') return value > 65 ? '#f85149' : value < 35 ? '#d29922' : '#3fb950';
  if (key === 'co2') return value > 1200 ? '#f85149' : value > 1000 ? '#d29922' : '#3fb950';
  if (key === 'noise') return value > 60 ? '#d29922' : '#3fb950';
  return '#3fb950';
}

export function getDisplayName(env, t) {
  return env.nameKey ? t(env.nameKey) : env.name || '';
}

export function getDisplayLocation(env, t) {
  return env.locationKey ? t(env.locationKey) : env.location || '';
}
