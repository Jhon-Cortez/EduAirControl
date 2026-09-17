import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  IoCreateOutline,
  IoTrashOutline,
  IoPeopleOutline,
  IoLocationOutline,
} from 'react-icons/io5';
import { WiThermometer, WiHumidity } from 'react-icons/wi';
import { MdCo2 } from 'react-icons/md';
import { HiSpeakerWave } from 'react-icons/hi2';
import {
  getBadgeClass,
  getMetricColor,
  getDisplayName,
  getDisplayLocation,
} from '../../../utils/environmentHelpers';
import './ManagementCard.css';

const STATUS_DATA_ATTR = {
  'dashboard.statusNormal': 'normal',
  'dashboard.statusWarning': 'warning',
  'dashboard.statusAlert': 'alert',
};

const getBadgeLabel = (qualityKey, statusKey, t) => {
  const key = qualityKey || statusKey;
  if (key === 'dashboard.qualityGood' || key === 'dashboard.statusNormal')
    return t('dashboard.qualityGood', 'Good');
  if (key === 'dashboard.qualityBad' || key === 'dashboard.statusAlert')
    return t('dashboard.statusAlert', 'Alert');
  if (key === 'dashboard.qualityRegular' || key === 'dashboard.statusWarning')
    return t('dashboard.qualityRegular', 'Regular');
  return 'Normal';
};

/* ── component ──────────────────────────────────────────────── */

function ManagementCard({ environment, onEdit, onDelete }) {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const name = getDisplayName(environment, t);
  const location = getDisplayLocation(environment, t);
  const statusAttr = STATUS_DATA_ATTR[environment.statusKey] || 'normal';

  const metrics = [
    {
      key: 'temp',
      icon: <WiThermometer size={18} />,
      label: t('dashboard.temp', 'Temp'),
      value: `${environment.temp}°C`,
      raw: environment.temp,
    },
    {
      key: 'humidity',
      icon: <WiHumidity size={18} />,
      label: t('dashboard.humidity', 'Humedad'),
      value: `${environment.humidity}%`,
      raw: environment.humidity,
    },
    {
      key: 'co2',
      icon: <MdCo2 size={16} />,
      label: 'CO₂',
      value: `${environment.co2} ppm`,
      raw: environment.co2,
    },
    {
      key: 'noise',
      icon: <HiSpeakerWave size={14} />,
      label: t('dashboard.noise', 'Ruido'),
      value: `${environment.noise} dB`,
      raw: environment.noise,
    },
  ];

  return (
    <div className="ds-card ds-card--hoverable mgmt-card" data-status={statusAttr}>
      {/* Header */}
      <div className="mgmt-card__header">
        <div className="mgmt-card__header-left">
          <h3 className="mgmt-card__name">{name}</h3>
          <div className="mgmt-card__meta">
            {environment.capacity > 0 && (
              <span className="mgmt-card__meta-pill">
                <IoPeopleOutline size={13} />
                {t('management.capacity', 'Cap.')}: {environment.capacity}
              </span>
            )}
            {location && (
              <span className="mgmt-card__meta-pill">
                <IoLocationOutline size={13} />
                {location}
              </span>
            )}
          </div>
        </div>

        <div className="mgmt-card__header-right">
          <span
            className={`mgmt-card__badge ${getBadgeClass(environment.statusKey, environment.qualityKey)}`}
          >
            {getBadgeLabel(environment.qualityKey, environment.statusKey, t)}
          </span>

          <button
            className="mgmt-card__btn"
            onClick={(e) => {
              e.stopPropagation();
              onEdit(environment);
            }}
            title={t('management.editBtn', 'Editar')}
          >
            <IoCreateOutline size={16} />
          </button>

          <button
            className="mgmt-card__btn mgmt-card__btn--danger"
            onClick={(e) => {
              e.stopPropagation();
              onDelete(environment.id);
            }}
            title={t('management.deleteBtn', 'Eliminar')}
          >
            <IoTrashOutline size={16} />
          </button>
        </div>
      </div>

      {/* Divider */}
      <div className="mgmt-card__divider" />

      {/* Metrics */}
      <div className="mgmt-card__metrics">
        {metrics.map((m) => (
          <div key={m.key} className="mgmt-card__metric">
            <div className="mgmt-card__metric-header">
              <span className="mgmt-card__metric-icon">{m.icon}</span>
              <span className="mgmt-card__metric-label">{m.label}</span>
            </div>
            <span
              className="mgmt-card__metric-value"
              style={{ color: getMetricColor(m.key, m.raw) }}
            >
              {m.value}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ManagementCard;
