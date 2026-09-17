import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { WiThermometer, WiHumidity } from 'react-icons/wi';
import { MdCo2 } from 'react-icons/md';
import { HiSpeakerWave } from 'react-icons/hi2';
import { FaHeart, FaRegHeart, FaUser, FaMapMarkerAlt } from 'react-icons/fa';
import { IoCheckmarkCircle, IoWarning, IoAlertCircle } from 'react-icons/io5';
import Navbar from '../../dashboard/components/Navbar/Navbar';
import { BackButton } from '../../../shared/components';
import { useEnvironment } from '../../../context/EnvironmentContext';
import { STATUS_COLORS, QUALITY_COLORS, IDEAL_RANGES } from '../constants/environments';
import './EnvironmentDetail.css';

function MetricBar({ value, min, max }) {
  const pct = Math.min(100, Math.max(0, ((value - min) / (max - min)) * 100));
  const color = pct < 60 ? '#4CAF50' : pct < 80 ? '#FFC107' : '#F44336';
  return (
    <div className="metric-bar-track">
      <div className="metric-bar-fill" style={{ width: `${pct}%`, background: color }} />
    </div>
  );
}

function StatusIcon({ statusKey }) {
  if (statusKey === 'dashboard.statusNormal')
    return <IoCheckmarkCircle className="detail-status-icon normal" />;
  if (statusKey === 'dashboard.statusWarning')
    return <IoWarning className="detail-status-icon warning" />;
  return <IoAlertCircle className="detail-status-icon alert" />;
}

function EnvironmentDetailScreen() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { environments, toggleFavorite } = useEnvironment();

  const env = environments.find((e) => String(e.id) === String(id));

  if (!env) {
    return (
      <div className="detail-page">
        <Navbar />
        <main id="main-content" className="detail-container">
          <BackButton onClick={() => navigate('/all-environments')} />
          <div className="detail-not-found">
            <p>{t('detail.notFound') || 'Ambiente no encontrado'}</p>
          </div>
        </main>
      </div>
    );
  }

  const name = env.nameKey ? t(env.nameKey) : env.name;
  const statusColor = STATUS_COLORS[env.statusKey];
  const qualityKey =
    env.qualityKey ||
    (env.statusKey === 'dashboard.statusAlert'
      ? 'dashboard.qualityBad'
      : env.statusKey === 'dashboard.statusWarning'
        ? 'dashboard.qualityRegular'
        : 'dashboard.qualityGood');
  const qualityColor = QUALITY_COLORS[qualityKey];

  const metrics = [
    {
      icon: <WiThermometer className="detail-metric-icon temp" />,
      label: t('dashboard.temperature'),
      value: `${env.temp}°C`,
      rawValue: env.temp,
      ideal: IDEAL_RANGES.temp,
      min: 0,
      max: 50,
      key: 'temp',
    },
    {
      icon: <WiHumidity className="detail-metric-icon humidity" />,
      label: t('dashboard.humidity'),
      value: `${env.humidity}%`,
      rawValue: env.humidity,
      ideal: IDEAL_RANGES.humidity,
      min: 0,
      max: 100,
      key: 'humidity',
    },
    {
      icon: <MdCo2 className="detail-metric-icon co2" />,
      label: 'CO₂',
      value: `${env.co2} ppm`,
      rawValue: env.co2,
      ideal: IDEAL_RANGES.co2,
      min: 0,
      max: 2000,
      key: 'co2',
    },
    {
      icon: <HiSpeakerWave className="detail-metric-icon noise" />,
      label: t('dashboard.noise'),
      value: `${env.noise} dB`,
      rawValue: env.noise,
      ideal: IDEAL_RANGES.noise,
      min: 0,
      max: 120,
      key: 'noise',
    },
  ];

  return (
      <div className="detail-page">
      <Navbar />
      <main id="main-content" className="detail-container">
        <BackButton onClick={() => navigate('/all-environments')} />

        {/* Hero */}
        <div className="detail-hero">
          <div className="detail-hero-left">
            <h1 className="detail-name">{name}</h1>

            <div className="detail-meta">
              {env.location && (
                <span className="detail-meta-item">
                  <FaMapMarkerAlt /> {env.location}
                </span>
              )}

              {env.capacity && (
                <span className="detail-meta-item">
                  <FaUser /> {t('management.capacity')}: {env.capacity}
                </span>
              )}
            </div>
          </div>

          <div className="detail-hero-right">
            <div
              className="detail-status-badge"
              style={{ borderColor: statusColor, color: statusColor }}
            >
              <StatusIcon statusKey={env.statusKey} />
              <span>{t(env.statusKey)}</span>
            </div>

            <button
              className={`detail-fav-btn ${env.isFavorite ? 'active' : ''}`}
              onClick={() => toggleFavorite(env.id, !env.isFavorite)}
            >
              {env.isFavorite ? <FaHeart /> : <FaRegHeart />}
              <span>
                {env.isFavorite
                  ? t('detail.removeFav') || 'Quitar favorito'
                  : t('detail.addFav') || 'Agregar favorito'}
              </span>
            </button>
          </div>
        </div>

        {/* Air Quality */}
        <div className="detail-quality-banner" style={{ borderLeftColor: qualityColor }}>
          <div>
            <p className="detail-quality-label">{t('dashboard.airQuality')}</p>
            <p className="detail-quality-value" style={{ color: qualityColor }}>
              {t(qualityKey)}
            </p>
          </div>

          <div className="detail-quality-desc">
            <p>
              {t('detail.qualityDesc') ||
                'Basado en los niveles de CO₂, temperatura, humedad y ruido del ambiente.'}
            </p>
          </div>
        </div>

        {/* Metrics */}
        <h2 className="detail-section-title">
          {t('detail.metricsTitle') || 'Métricas en tiempo real'}
        </h2>

        <div className="detail-metrics-grid">
          {metrics.map((m) => (
            <div className="detail-metric-card" key={m.key}>
              <div className="detail-metric-header">
                {m.icon}

                <div>
                  <p className="detail-metric-label">{m.label}</p>
                  <p className="detail-metric-value">{m.value}</p>
                </div>
              </div>

              <MetricBar value={m.rawValue} min={m.min} max={m.max} />

              <div className="detail-metric-footer">
                <span>{t('dashboard.idealRange')}</span>
                <span className="detail-metric-ideal">{m.ideal}</span>
              </div>
            </div>
          ))}
        </div>

        {/* Info Cards Row */}
        <div className="detail-info-row">
          <div className="detail-info-card">
            <h3>📍 {t('detail.locationTitle') || 'Ubicación'}</h3>
            <p>{env.location || '—'}</p>
          </div>

          <div className="detail-info-card">
            <h3>👥 {t('detail.capacityTitle') || 'Capacidad'}</h3>
            <p>{env.capacity ? `${env.capacity} ${t('detail.people') || 'personas'}` : '—'}</p>
          </div>

          <div className="detail-info-card">
            <h3>🌡️ {t('detail.conditionsTitle') || 'Condiciones'}</h3>
            <p style={{ color: statusColor, fontWeight: 600 }}>{t(env.statusKey)}</p>
          </div>
        </div>

        <br />
      </main>
    </div>
  );
}

export default EnvironmentDetailScreen;
