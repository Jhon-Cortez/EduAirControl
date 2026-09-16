import { useState, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { FaHeart } from 'react-icons/fa';
import Navbar from '../dashboard/components/Navbar/Navbar';
import { useEnvironment } from '../../context/EnvironmentContext';
import { Modal, Button } from '../../shared/components';
import './Favorites.css';

const FAV_METRIC_DEFINITIONS = [
  {
    key: 'temp',
    icon: '🌡️',
    labelKey: 'dashboard.temperature',
    getValue: (fav) => `${fav.temp}°C`,
  },
  {
    key: 'humidity',
    icon: '💧',
    labelKey: 'dashboard.humidity',
    getValue: (fav) => `${fav.humidity}%`,
  },
  {
    key: 'co2',
    icon: '🌫️',
    labelKey: null,
    staticLabel: 'CO₂',
    getValue: (fav) => `${fav.co2}ppm`,
  },
  { key: 'noise', icon: '🔊', labelKey: 'dashboard.noise', getValue: (fav) => `${fav.noise} dB` },
];

// Tarjeta reutilizable: recibe icono, etiqueta y valor por props.
function FavMetricBox({ icon, label, value }) {
  return (
    <div className="fav-metric-box">
      <span className="fav-metric-icon">{icon}</span>
      <div className="fav-metric-info">
        <span className="fav-metric-label">{label}</span>
        <span className="fav-metric-value">{value}</span>
      </div>
    </div>
  );
}

function FavoritesScreen() {
  const { t } = useTranslation();
  const { environments, toggleFavorite } = useEnvironment();

  // favoritos memoizados
  const favorites = useMemo(() => environments.filter((e) => e.isFavorite), [environments]);

  // Modal state
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [selectedFav, setSelectedFav] = useState(null);

  const getStatusColor = (statusKey) => {
    switch (statusKey) {
      case 'dashboard.statusNormal':
        return 'var(--color-success)'
      case 'dashboard.statusWarning':
        return 'var(--color-warning)'
      case 'dashboard.statusAlert':
        return 'var(--color-danger)'
      default:
        return '#8b949e';
    }
  };

  const handleRemoveFavoriteClick = (fav) => {
    setSelectedFav(fav);
    setShowConfirmModal(true);
  };

  const handleConfirmRemove = () => {
    if (!selectedFav) return;

    toggleFavorite(selectedFav.id, false);

    setShowConfirmModal(false);
    setSelectedFav(null);
  };

  const handleCancelRemove = () => {
    setShowConfirmModal(false);
    setSelectedFav(null);
  };

  return (
    <div className="favorites-page">
      <Navbar />

      <div className="app-page-container">
        {/* HEADER */}
        <div className="favorites-header">
          <div className="favorites-header-content">
            <FaHeart size={32} color="var(--color-danger)" />
            <div>
              <h1>{t('favorites.title')}</h1>
              <p className="favorites-subtitle">{t('favorites.description')}</p>
            </div>
          </div>
        </div>

        {/* EMPTY STATE */}
        {favorites.length === 0 ? (
          <div className="favorites-empty">
            <FaHeart size={56} />
            <h2>{t('favorites.empty')}</h2>
            <p>{t('favorites.emptyHint')}</p>
          </div>
        ) : (
          /* LIST */
          <div className="favorites-list">
            {favorites.map((fav) => {
              const name = fav.nameKey ? t(fav.nameKey) : fav.name;

              // Ciclo forEach: recorre la config de métricas y, por cada
              // una, toma el dato correspondiente de "fav" para construir
              // la mini-tarjeta.
              const favMetricBoxes = [];
              FAV_METRIC_DEFINITIONS.forEach((metric) => {
                favMetricBoxes.push(
                  <FavMetricBox
                    key={metric.key}
                    icon={metric.icon}
                    label={metric.labelKey ? t(metric.labelKey) : metric.staticLabel}
                    value={metric.getValue(fav)}
                  />
                );
              });

              return (
                <div key={fav.id} className="fav-card-impact">
                  <div
                    className="fav-status-indicator"
                    style={{ backgroundColor: getStatusColor(fav.statusKey) }}
                  />

                  {/* CONTENT */}
                  <div className="fav-card-content">
                    {/* LEFT */}
                    <div className="fav-section-left">
                      <h3>{name}</h3>

                      <span
                        className="fav-status-badge"
                        style={{ color: getStatusColor(fav.statusKey) }}
                      >
                        {t(fav.statusKey)}
                      </span>
                    </div>

                    {/* METRICS */}
                    <div className="fav-section-metrics">{favMetricBoxes}</div>

                    {/* RIGHT */}
                    <div className="fav-section-right">
                      <div className="fav-quality-info">
                        <span className="fav-quality-label">{t('dashboard.airQuality')}</span>
                        <span className="fav-quality-value">{t(fav.qualityKey)}</span>
                      </div>

                      <button
                        className="fav-heart-btn-impact"
                        onClick={() => handleRemoveFavoriteClick(fav)}
                        title={t('favorites.removeFavorite')}
                      >
                        <FaHeart size={20} />
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* MODAL */}
      <Modal isOpen={showConfirmModal && !!selectedFav} onClose={handleCancelRemove} size="sm">
        <div className="modal-header modal-header-warning">
          <FaHeart size={24} color="#ff4d5b" />
          <h2>{t('favorites.removeFavorite')}</h2>
        </div>

        <div className="modal-body">
          <p>
            {t('favorites.confirmRemove', '¿Seguro que quieres eliminar')}{' '}
            <strong>{selectedFav?.nameKey ? t(selectedFav.nameKey) : selectedFav?.name}</strong>{' '}
            {t('favorites.fromFavorites', 'de favoritos?')}
          </p>
        </div>

        <div className="modal-footer">
          <Button variant="secondary" onClick={handleCancelRemove}>
            {t('profile.cancel', 'Cancelar')}
          </Button>

          <Button variant="danger-solid" onClick={handleConfirmRemove}>
            {t('favorites.remove', 'Eliminar')}
          </Button>
        </div>
      </Modal>
    </div>
  );
}

export default FavoritesScreen;
