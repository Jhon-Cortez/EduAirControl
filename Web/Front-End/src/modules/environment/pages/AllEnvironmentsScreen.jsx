import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import Navbar from '../../dashboard/components/Navbar/Navbar';
import EnvironmentFilters from '../components/EnvironmentFilters/EnvironmentFilters';
import EnvironmentSummaryCard from '../components/EnvironmentSummaryCard/EnvironmentSummaryCard';
import EnvironmentModal from '../components/EnvironmentModal/EnvironmentModal';
import calculateEnvironmentScore from '../utils/calculateEnvironmentScore';
import { useAllEnvironmentsVM } from '../../../viewmodels/useAllEnvironmentsVM';
import { useEnvironment } from '../../../context/EnvironmentContext';
import './AllEnvironments.css';

function AllEnvironmentsScreen() {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();

  const { environments, toggleFavorite } = useEnvironment();

  const { filtered, filters, setFilters, counts, suggestions } = useAllEnvironmentsVM(environments);

  const selectedEnvironmentId = searchParams.get('environment');
  const selectedEnvironment = environments.find(
    (environment) => String(environment.id) === String(selectedEnvironmentId)
  );

  const closeSelectedEnvironment = () => {
    const nextParams = new URLSearchParams(searchParams);
    nextParams.delete('environment');
    setSearchParams(nextParams, { replace: true });
  };

  return (
    <div className="all-env-page">
      <Navbar />

      <div className="app-page-container">
        <header className="all-env-header">
          <div>
            <h1>{t('allEnvironments.title')}</h1>

            <p>{t('allEnvironments.subtitle')}</p>
          </div>

          <div className="all-env-stats">
            <div className="ds-card ds-card--stat">
              <span>{counts.total}</span>

              <small>{t('filters.all')}</small>
            </div>

            <div className="ds-card ds-card--stat normal">
              <span>{counts.normal}</span>

              <small>{t('dashboard.statusNormal')}</small>
            </div>

            <div className="ds-card ds-card--stat warning">
              <span>{counts.warning}</span>

              <small>{t('dashboard.statusWarning')}</small>
            </div>

            <div className="ds-card ds-card--stat danger">
              <span>{counts.alert}</span>

              <small>{t('dashboard.statusAlert')}</small>
            </div>

            <div className="ds-card ds-card--stat favorite">
              <span>{counts.favorites}</span>

              <small>{t('favorites.title')}</small>
            </div>
          </div>
        </header>

        <EnvironmentFilters filters={filters} setFilters={setFilters} suggestions={suggestions} />

        {filtered.length === 0 ? (
          <div className="empty-state">
            <h2>{t('allEnvironments.noResults')}</h2>
          </div>
        ) : (
          <div className="all-env-grid">
            {filtered.map((environment) => (
              <EnvironmentSummaryCard
                key={environment.id}
                environment={environment}
                onToggleFavorite={toggleFavorite}
              />
            ))}
          </div>
        )}
      </div>

      <EnvironmentModal
        isOpen={Boolean(selectedEnvironment)}
        onClose={closeSelectedEnvironment}
        environment={
          selectedEnvironment
            ? {
                ...selectedEnvironment,
                score: calculateEnvironmentScore(selectedEnvironment),
              }
            : null
        }
        isFavorite={selectedEnvironment?.isFavorite ?? false}
        onToggleFavorite={() => {
          if (!selectedEnvironment) return;
          toggleFavorite(selectedEnvironment.id, !selectedEnvironment.isFavorite);
        }}
      />
    </div>
  );
}

export default AllEnvironmentsScreen;
