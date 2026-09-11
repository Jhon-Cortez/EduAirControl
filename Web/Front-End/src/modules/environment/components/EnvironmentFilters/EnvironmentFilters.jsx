import { useTranslation } from 'react-i18next';
import './EnvironmentFilters.css';

function EnvironmentFilters({ filters, setFilters, suggestions }) {
  const { t } = useTranslation();

  const update = (field, value) => {
    setFilters((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  return (
    <section className="environment-filters">
      <div className="filter-group">
        <label>{t('filters.name')}</label>

        <input
          type="text"
          value={filters.name}
          placeholder={t('filters.searchEnvironment')}
          onChange={(e) => update('name', e.target.value)}
        />
      </div>

      <div className="filter-group">
        <label>{t('filters.building')}</label>

        <select value={filters.building} onChange={(e) => update('building', e.target.value)}>
          <option value="">{t('filters.all')}</option>

          {suggestions.buildings?.map((building) => (
            <option key={building} value={building}>
              {building}
            </option>
          ))}
        </select>
      </div>

      <div className="filter-group">
        <label>{t('filters.floor')}</label>

        <select value={filters.floor} onChange={(e) => update('floor', Number(e.target.value))}>
          <option value="">{t('filters.all')}</option>

          {suggestions.floors?.map((floor) => (
            <option key={floor} value={floor}>
              {floor}
            </option>
          ))}
        </select>
      </div>

      <div className="filter-group checkbox">
        <label>{t('filters.favorites')}</label>

        <input
          type="checkbox"
          checked={filters.favorite}
          onChange={(e) => update('favorite', e.target.checked)}
        />
      </div>
    </section>
  );
}

export default EnvironmentFilters;
