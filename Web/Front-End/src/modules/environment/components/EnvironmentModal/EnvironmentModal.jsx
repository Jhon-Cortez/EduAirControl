import { FaTimes, FaHeart, FaRegHeart, FaTemperatureHigh } from 'react-icons/fa';
import { WiHumidity } from 'react-icons/wi';
import { MdCo2 } from 'react-icons/md';
import { HiSpeakerWave } from 'react-icons/hi2';
import { useTranslation } from 'react-i18next';

import ScoreCircle from '../ScoreCircle/ScoreCircle';
import SensorInfo from '../SensorInfo/SensorInfo';
import RecommendationBox from '../RecommendationBox/RecommendationBox';
import EnvironmentExplanation from '../EnvironmentExplanation/EnvironmentExplanation';
import InfoTooltip from '../InfoTooltip/InfoTooltip';

import { getRecommendations } from '../../utils/getRecommendations';

import './EnvironmentModal.css';

function EnvironmentModal({ isOpen, onClose, environment, isFavorite, onToggleFavorite }) {
  const { t } = useTranslation();

  if (!isOpen || !environment) return null;

  const statusText = environment.statusKey ? t(environment.statusKey) : environment.status;

  const statusClass =
    environment.statusKey === 'dashboard.statusNormal'
      ? 'normal'
      : environment.statusKey === 'dashboard.statusWarning'
        ? 'warning'
        : 'danger';

  return (
    <div className="environment-modal-overlay">
      <div className="environment-modal">
        <button className="modal-close" onClick={onClose}>
          <FaTimes />
        </button>

        <header className="modal-header">
          <h2>{environment.nameKey ? t(environment.nameKey) : environment.name}</h2>

          <span className={`modal-status ${statusClass}`}>{statusText}</span>
        </header>

        <div className="environment-score" style={{ position: 'relative', paddingRight: 44 }}>
          <InfoTooltip text={t('tooltips.score')} />

          <ScoreCircle score={environment.score} size={130} />

          <div className="environment-score-info">
            <h3>{t('allEnvironments.qualityTitle')}</h3>

            <p>{t('allEnvironments.qualityDesc')}</p>
          </div>
        </div>

        <div className="modal-sensors">
          <div style={{ position: 'relative' }}>
            <InfoTooltip text={t('tooltips.temperature')} />
            <SensorInfo
              icon={<FaTemperatureHigh />}
              title={t('dashboard.temperature')}
              value={`${environment.temp} °C`}
              description={t('allEnvironments.tempDescription')}
            />
          </div>

          <div style={{ position: 'relative' }}>
            <InfoTooltip text={t('tooltips.humidity')} />
            <SensorInfo
              icon={<WiHumidity />}
              title={t('dashboard.humidity')}
              value={`${environment.humidity}%`}
              description={t('allEnvironments.humidityDescription')}
            />
          </div>

          <div style={{ position: 'relative' }}>
            <InfoTooltip text={t('tooltips.co2')} />
            <SensorInfo
              icon={<MdCo2 />}
              title={t('allEnvironments.co2')}
              value={`${environment.co2} ppm`}
              description={t('allEnvironments.co2Description')}
            />
          </div>

          <div style={{ position: 'relative' }}>
            <InfoTooltip text={t('tooltips.noise')} />
            <SensorInfo
              icon={<HiSpeakerWave />}
              title={t('dashboard.noise')}
              value={`${environment.noise} dB`}
              description={t('allEnvironments.noiseDescription')}
            />
          </div>
        </div>

        <div style={{ position: 'relative', paddingRight: 44 }}>
          <InfoTooltip text={t('tooltips.recommendations')} />
          <RecommendationBox recommendations={getRecommendations(environment, t)} />
        </div>

        <EnvironmentExplanation />

        <div className="modal-actions">
          <button
            className={`modal-favorite-btn ${isFavorite ? 'active' : ''}`}
            onClick={onToggleFavorite}
          >
            {isFavorite ? <FaHeart /> : <FaRegHeart />}
            {isFavorite ? t('allEnvironments.removeFavorite') : t('allEnvironments.favorite')}
          </button>

          <button className="close-btn" onClick={onClose}>
            {t('common.close')}
          </button>
        </div>
      </div>
    </div>
  );
}

export default EnvironmentModal;
