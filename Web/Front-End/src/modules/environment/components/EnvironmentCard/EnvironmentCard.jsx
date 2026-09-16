import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { FaHeart, FaRegHeart } from 'react-icons/fa';
import { WiThermometer, WiHumidity } from 'react-icons/wi';
import { MdCo2 } from 'react-icons/md';
import { HiSpeakerWave } from 'react-icons/hi2';
import ScoreCircle from '../ScoreCircle/ScoreCircle';
import MetricCard from '../MetricCard/MetricCard';
import EnvironmentModal from '../EnvironmentModal/EnvironmentModal';
import { calculateEnvironmentScore } from '../../utils/calculateEnvironmentScore';
import { getEnvironmentStatus } from '../../utils/getEnvironmentStatus';
import getMetricStatus from '../../utils/getMetricStatus';
import './EnvironmentCard.css';

// Configuración reutilizable: cada métrica sabe cómo leer su propio
// valor desde los datos del ambiente (los datos "de la API"/contexto)

const METRIC_DEFINITIONS = [
  {
    key: 'temp',
    statusType: 'temp',
    icon: <WiThermometer className="summary-icon temp" />,
    labelKey: 'dashboard.temperature',
    getValue: (env) => `${env.temp} °C`,
    getRaw: (env) => env.temp,
  },
  {
    key: 'humidity',
    statusType: 'humidity',
    icon: <WiHumidity className="summary-icon humidity" />,
    labelKey: 'dashboard.humidity',
    getValue: (env) => `${env.humidity}%`,
    getRaw: (env) => env.humidity,
  },
  {
    key: 'co2',
    statusType: 'co2',
    icon: <MdCo2 className="summary-icon co2" />,
    labelKey: 'allEnvironments.co2',
    getValue: (env) => `${env.co2} ppm`,
    getRaw: (env) => env.co2,
  },
  {
    key: 'noise',
    statusType: 'noise',
    icon: <HiSpeakerWave className="summary-icon noise" />,
    labelKey: 'dashboard.noise',
    getValue: (env) => `${env.noise} dB`,
    getRaw: (env) => env.noise,
  },
];

function EnvironmentCard({ environment, onToggleFavorite }) {
  const { t } = useTranslation();

  const [favorite, setFavorite] = useState(environment.isFavorite ?? false);
  const [open, setOpen] = useState(false);

  const score = calculateEnvironmentScore(environment);
  const status = getEnvironmentStatus(environment.statusKey, t);

  const handleFavorite = (e) => {
    e.stopPropagation();
    const value = !favorite;
    setFavorite(value);
    onToggleFavorite?.(environment.id, value);
  };

  // Ciclo forEach: recorre las definiciones de métricas y, por cada
  // una, va tomando el dato correspondiente de "environment" (el dato
  // que llega desde la API/contexto) para construir la tarjeta.
  const metricCards = [];
  METRIC_DEFINITIONS.forEach((metric) => {
    metricCards.push(
      <MetricCard
        key={metric.key}
        icon={metric.icon}
        label={t(metric.labelKey)}
        value={metric.getValue(environment)}
        status={getMetricStatus(metric.statusType, metric.getRaw(environment), t)}
      />
    );
  });

  return (
    <>
      <article className="environment-card" onClick={() => setOpen(true)}>
        <div className={`environment-status ${status.class}`} />

        <div className="environment-card-header">
          <div>
            <h3>{environment.name}</h3>
            <span className={`status-badge ${status.class}`}>{t(status.label)}</span>
          </div>

          <button className="card-favorite-btn" onClick={handleFavorite}>
            {favorite ? <FaHeart /> : <FaRegHeart />}
          </button>
        </div>

        <div className="environment-score">
          <ScoreCircle score={score} />
          <div>
            <h4>{t('allEnvironments.qualityTitle')}</h4>
            <p>{t('allEnvironments.qualityDesc')}</p>
          </div>
        </div>

        <div className="environment-metrics">{metricCards}</div>
      </article>

      <EnvironmentModal
        isOpen={open}
        onClose={() => setOpen(false)}
        environment={{
          ...environment,
          score,
          favorite,
        }}
        isFavorite={favorite}
        onToggleFavorite={() => {
          const value = !favorite;
          setFavorite(value);
          onToggleFavorite?.(environment.id, value);
        }}
      />
    </>
  );
}

export default EnvironmentCard;
