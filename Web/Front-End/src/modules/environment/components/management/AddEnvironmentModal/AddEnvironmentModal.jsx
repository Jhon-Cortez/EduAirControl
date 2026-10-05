import { useState } from 'react'
import './AddEnvironmentModal.css'

// ── Opciones predefinidas ────────────────────────────────────
const LOCATION_OPTIONS = [
  { value: '',                    label: 'Seleccionar área…'   },
  { value: 'Área Administrativa', label: 'Área Administrativa' },
  { value: 'Área de Sistemas',    label: 'Área de Sistemas'    },
  { value: 'Área Técnica',        label: 'Área Técnica'        },
  { value: 'Laboratorio',         label: 'Laboratorio'         },
  { value: 'Taller',              label: 'Taller'              },
  { value: 'Biblioteca',          label: 'Biblioteca'          },
  { value: 'Auditorio',           label: 'Auditorio'           },
]

const TYPE_OPTIONS = [
  { value: '',             label: 'Seleccionar tipo…' },
  { value: 'Aula',         label: 'Aula'              },
  { value: 'Laboratorio',  label: 'Laboratorio'       },
  { value: 'Oficina',      label: 'Oficina'           },
  { value: 'Taller',       label: 'Taller'            },
  { value: 'Sala reunión', label: 'Sala de reunión'   },
  { value: 'Auditorio',    label: 'Auditorio'         },
]

const TEMP_PROFILES = {
  'Con A/C':    { min: 18, max: 24 },
  'Sin A/C':    { min: 18, max: 28 },
  Personalizar: null,
}

function AddEnvironmentModal({ onClose, onAdd }) {
  const [name,        setName]        = useState('')
  const [capacity,    setCapacity]    = useState('')
  const [location,    setLocation]    = useState('')
  const [floor,       setFloor]       = useState('')
  const [envType,     setEnvType]     = useState('')
  const [tempProfile, setTempProfile] = useState('Con A/C')
  const [tempMin,     setTempMin]     = useState(TEMP_PROFILES['Con A/C'].min)
  const [tempMax,     setTempMax]     = useState(TEMP_PROFILES['Con A/C'].max)

  const handleProfileChange = (profile) => {
    setTempProfile(profile)
    if (profile !== 'Personalizar') {
      setTempMin(TEMP_PROFILES[profile].min)
      setTempMax(TEMP_PROFILES[profile].max)
    }
  }

  const handleSubmit = () => {
    if (!name.trim() || !location) return
    onAdd({
      name: name.trim(),
      capacity: Number(capacity) || 0,
      location,
      floor: floor.trim(),
      envType,
      tempMin: Number(tempMin),
      tempMax: Number(tempMax),
    })
  }

  const isCustom = tempProfile === 'Personalizar'

  return (
    <div className="add-env-overlay" onClick={onClose}>
      <div className="add-env-modal add-env-modal--wide" onClick={(e) => e.stopPropagation()}>

        <h2 className="add-env-modal__title">Agregar Ambiente</h2>

        {/* Fila 1: Nombre + Capacidad */}
        <div className="add-env-modal__row">
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">
              Nombre / N° Ambiente <span className="add-env-modal__required">*</span>
            </label>
            <input
              className="add-env-modal__input"
              placeholder="Ej. 209-1"
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
              placeholder="Ej. 30"
              value={capacity}
              onChange={(e) => setCapacity(e.target.value)}
            />
          </div>
        </div>

        {/* Fila 2: Ubicación + Piso/Bloque */}
        <div className="add-env-modal__row">
          <div className="add-env-modal__field">
            <label className="add-env-modal__label">
              Área / Ubicación <span className="add-env-modal__required">*</span>
            </label>
            <select
              className="add-env-modal__input add-env-modal__select"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
            >
              {LOCATION_OPTIONS.map((o) => (
                <option key={o.value} value={o.value} disabled={o.value === ''}>
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

        {/* Tipo de ambiente */}
        <div className="add-env-modal__field">
          <label className="add-env-modal__label">Tipo de ambiente</label>
          <select
            className="add-env-modal__input add-env-modal__select"
            value={envType}
            onChange={(e) => setEnvType(e.target.value)}
          >
            {TYPE_OPTIONS.map((o) => (
              <option key={o.value} value={o.value} disabled={o.value === ''}>
                {o.label}
              </option>
            ))}
          </select>
        </div>

        {/* Umbrales de temperatura */}
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
            disabled={!name.trim() || !location}
          >
            Agregar Ambiente
          </button>
        </div>

      </div>
    </div>
  )
}

export default AddEnvironmentModal