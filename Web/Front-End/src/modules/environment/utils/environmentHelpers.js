/**
 * Helpers compartidos de ambiente.
 * Lógica pura — sin hooks, sin estado React.
 */

import { STATUS, QUALITY } from '../constants/environments';

export function calcScore(env) {
  const tempScore = Math.max(0, 100 - Math.abs(env.temp - 21) * 8);
  const humidityScore = Math.max(0, 100 - Math.abs(env.humidity - 50) * 3);
  const co2Score = Math.max(0, 100 - Math.max(0, env.co2 - 600) * 0.08);
  const noiseScore = Math.max(0, 100 - Math.max(0, env.noise - 30) * 2);
  return Math.round((tempScore + humidityScore + co2Score + noiseScore) / 4);
}

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
