import { useState } from 'react';
import { useTranslation } from 'react-i18next';

const LOCATION_OPTIONS = [
  { value: 'Área Administrativa', label: 'Área Administrativa' },
  { value: 'Área de Sistemas', label: 'Área de Sistemas' },
  { value: 'Área Técnica', label: 'Área Técnica' },
  { value: 'Laboratorio', label: 'Laboratorio' },
  { value: 'Taller', label: 'Taller' },
  { value: 'Biblioteca', label: 'Biblioteca' },
  { value: 'Auditorio', label: 'Auditorio' },
];

const TYPE_OPTIONS = [
  { value: 'Aula', label: 'Aula' },
  { value: 'Laboratorio', label: 'Laboratorio' },
  { value: 'Oficina', label: 'Oficina' },
  { value: 'Taller', label: 'Taller' },
  { value: 'Sala reunión', label: 'Sala de reunión' },
  { value: 'Auditorio', label: 'Auditorio' },
];

const STATUS_OPTIONS = [
  { value: 'dashboard.statusNormal', label: 'Normal' },
  { value: 'dashboard.statusWarning', label: 'Advertencia' },
  { value: 'dashboard.statusAlert', label: 'Alerta' },
];

const TEMP_PROFILES = {
  'Con A/C': { min: 18, max: 24 },
  'Sin A/C': { min: 18, max: 28 },
  Personalizar: null,
};

function EditEnvironmentModal({ environment, onClose, onSave }) {
  const { t } = useTranslation();

  const defaultName = environment.nameKey ? t(environment.nameKey) : environment.name || '';
  const defaultLocation = environment.locationKey
    ? t(environment.locationKey)
    : environment.location || '';

  const [name, setName] = useState(defaultName);
  const [capacity, setCapacity] = useState(environment.capacity || '');
  const [location, setLocation] = useState(defaultLocation);
  const [floor, setFloor] = useState(environment.floor || '');
  const [envType, setEnvType] = useState(environment.envType || '');
  const [statusKey, setStatusKey] = useState(environment.statusKey || 'dashboard.statusNormal');
  const [tempMin, setTempMin] = useState(environment.tempMin ?? 18);
  const [tempMax, setTempMax] = useState(environment.tempMax ?? 28);
  const [tempProfile, setTempProfile] = useState('Personalizar');

  const handleProfileChange = (profile) => {
    setTempProfile(profile);
    if (profile !== 'Personalizar') {
      setTempMin(TEMP_PROFILES[profile].min);
      setTempMax(TEMP_PROFILES[profile].max);
    }
  };

  const handleSubmit = () => {
    if (!name.trim()) return;
    onSave(environment.id, {
      name,
      nameKey: null,
      capacity: Number(capacity),
      location,
      locationKey: null,
      floor,
      envType,
      statusKey,
      qualityKey: null,
      tempMin: Number(tempMin),
      tempMax: Number(tempMax),
    });
    onClose();
  };

  const isCustom = tempProfile === 'Personalizar';

  return (
    <div className="add-env-overlay" onClick={onClose}>
      <div className="add-env-modal add-env-modal--wide" onClick={(e) => e.stopPropagation()}>
        <h2 className="add-env-modal__title">Editar Ambiente</h2>

        {/* Fila 1: Nombre + Capacidad */}
        <div className="add-env-modal__row">
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">Nombre / N° Ambiente</label>
            <input
              className="add-env-modal__input"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">Capacidad</label>
            <input
              className="add-env-modal__input"
              type="number"
              min={0}
              value={capacity}
              onChange={(e) => setCapacity(e.target.value)}
            />
          </div>
        </div>

        {/* Fila 2: Ubicación + Piso */}
        <div className="add-env-modal__row">
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">Área / Ubicación</label>
            <select
              className="add-env-modal__input add-env-modal__select"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
            >
              <option value="">Seleccionar área…</option>
              {LOCATION_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </div>
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">Piso / Bloque</label>
            <input
              className="add-env-modal__input"
              placeholder="Ej. Piso 2, Bloque B"
              value={floor}
              onChange={(e) => setFloor(e.target.value)}
            />
          </div>
        </div>

        {/* Fila 3: Tipo + Estado */}
        <div className="add-env-modal__row">
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">Tipo de ambiente</label>
            <select
              className="add-env-modal__input add-env-modal__select"
              value={envType}
              onChange={(e) => setEnvType(e.target.value)}
            >
              <option value="">Seleccionar tipo…</option>
              {TYPE_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </div>
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">Estado</label>
            <select
              className="add-env-modal__input add-env-modal__select"
              value={statusKey}
              onChange={(e) => setStatusKey(e.target.value)}
            >
              {STATUS_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Umbrales temperatura */}
        <div className="add-env-modal__section">
          <label className="add-env-modal__label add-env-modal__label--section">
            🌡️ Umbrales de Temperatura
          </label>
          <div className="add-env-modal__profiles">
            {Object.keys(TEMP_PROFILES).map((p) => (
              <button
                key={p}
                type="button"
                className={`add-env-modal__profile-btn ${tempProfile === p ? 'active' : ''}`}
                onClick={() => handleProfileChange(p)}
              >
                {p === 'Con A/C' && '❄️ '}
                {p === 'Sin A/C' && '☀️ '}
                {p === 'Personalizar' && '✏️ '}
                {p}
              </button>
            ))}
          </div>
          <div className="add-env-modal__row add-env-modal__row--sm">
            <div className="add-env-modal__field">
              <label className="add-env-modal__label">Mín (°C)</label>
              <input
                className="add-env-modal__input"
                type="number"
                value={tempMin}
                disabled={!isCustom}
                onChange={(e) => setTempMin(e.target.value)}
              />
            </div>
            <div className="add-env-modal__field">
              <label className="add-env-modal__label">Máx (°C)</label>
              <input
                className="add-env-modal__input"
                type="number"
                value={tempMax}
                disabled={!isCustom}
                onChange={(e) => setTempMax(e.target.value)}
              />
            </div>
          </div>
        </div>

        <div className="add-env-modal__actions">
          <button className="add-env-modal__btn-cancel" onClick={onClose}>
            Cancelar
          </button>
          <button
            className="add-env-modal__btn-save"
            onClick={handleSubmit}
            disabled={!name.trim()}
          >
            Guardar Cambios
          </button>
        </div>
      </div>
    </div>
  );
}

export default EditEnvironmentModal;
