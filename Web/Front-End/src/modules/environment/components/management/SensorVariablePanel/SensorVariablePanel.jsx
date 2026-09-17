import { useMemo, useState } from 'react';
import {
  IoAddOutline,
  IoCreateOutline,
  IoPowerOutline,
  IoSearchOutline,
  IoCloseOutline,
} from 'react-icons/io5';
import { MdCo2 } from 'react-icons/md';
import { WiThermometer, WiHumidity } from 'react-icons/wi';
import { HiSpeakerWave } from 'react-icons/hi2';
import './SensorVariablePanel.css';

const VARIABLE_META = {
  temperature: {
    label: 'Temperatura',
    unit: '°C',
    icon: WiThermometer,
    color: 'mint',
    defaultMin: 18,
    defaultMax: 28,
  },
  humidity: {
    label: 'Humedad relativa',
    unit: '%',
    icon: WiHumidity,
    color: 'blue',
    defaultMin: 35,
    defaultMax: 65,
  },
  co2: {
    label: 'CO₂',
    unit: 'ppm',
    icon: MdCo2,
    color: 'amber',
    defaultMin: 400,
    defaultMax: 1000,
  },
  noise: {
    label: 'Ruido',
    unit: 'dB',
    icon: HiSpeakerWave,
    color: 'purple',
    defaultMin: 30,
    defaultMax: 65,
  },
};

const INITIAL_SENSORS = [
  {
    id: 'EA-2091-T',
    environmentId: 1,
    variable: 'temperature',
    status: 'active',
    lastSync: 'Hace 2 min',
    min: 18,
    max: 24,
  },
  {
    id: 'EA-2091-H',
    environmentId: 1,
    variable: 'humidity',
    status: 'active',
    lastSync: 'Hace 2 min',
    min: 35,
    max: 65,
  },
  {
    id: 'EA-2091-C',
    environmentId: 1,
    variable: 'co2',
    status: 'active',
    lastSync: 'Hace 2 min',
    min: 400,
    max: 1000,
  },
  {
    id: 'EA-2092-T',
    environmentId: 2,
    variable: 'temperature',
    status: 'active',
    lastSync: 'Hace 4 min',
    min: 18,
    max: 28,
  },
  {
    id: 'EA-2092-C',
    environmentId: 2,
    variable: 'co2',
    status: 'warning',
    lastSync: 'Hace 12 min',
    min: 400,
    max: 1000,
  },
  {
    id: 'EA-2093-R',
    environmentId: 3,
    variable: 'noise',
    status: 'offline',
    lastSync: 'Hace 38 min',
    min: 30,
    max: 65,
  },
];

function SensorVariablePanel({ environments }) {
  const [sensors, setSensors] = useState(INITIAL_SENSORS);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [variableFilter, setVariableFilter] = useState('all');
  const [editingSensor, setEditingSensor] = useState(null);
  const [showAdd, setShowAdd] = useState(false);
  const [newSensor, setNewSensor] = useState({
    id: '',
    environmentId: environments[0]?.id || '',
    variable: 'temperature',
  });

  const getEnvironmentName = (id) =>
    environments.find((environment) => environment.id === Number(id))?.name || 'Sin asignar';
  const getVariableValue = (environment, variable) =>
    variable === 'temperature' ? environment?.temp : environment?.[variable];

  const filteredSensors = useMemo(
    () =>
      sensors.filter((sensor) => {
        const meta = VARIABLE_META[sensor.variable];
        const matchesSearch =
          `${sensor.id} ${getEnvironmentName(sensor.environmentId)} ${meta.label}`
            .toLowerCase()
            .includes(search.toLowerCase());
        return (
          matchesSearch &&
          (statusFilter === 'all' || sensor.status === statusFilter) &&
          (variableFilter === 'all' || sensor.variable === variableFilter)
        );
      }),
    [sensors, search, statusFilter, variableFilter, environments]
  );

  const summary = useMemo(
    () => ({
      total: sensors.length,
      active: sensors.filter((sensor) => sensor.status === 'active').length,
      warning: sensors.filter((sensor) => sensor.status === 'warning').length,
      offline: sensors.filter((sensor) => sensor.status === 'offline').length,
    }),
    [sensors]
  );

  const toggleSensor = (id) =>
    setSensors((current) =>
      current.map((sensor) =>
        sensor.id === id
          ? { ...sensor, status: sensor.status === 'offline' ? 'active' : 'offline' }
          : sensor
      )
    );
  const saveSensor = (event) => {
    event.preventDefault();
    setSensors((current) =>
      current.map((sensor) => (sensor.id === editingSensor.id ? editingSensor : sensor))
    );
    setEditingSensor(null);
  };
  const addSensor = (event) => {
    event.preventDefault();
    if (!newSensor.id.trim() || !newSensor.environmentId) return;
    const meta = VARIABLE_META[newSensor.variable];
    setSensors((current) => [
      ...current,
      {
        ...newSensor,
        id: newSensor.id.trim(),
        environmentId: Number(newSensor.environmentId),
        status: 'active',
        lastSync: 'Ahora',
        min: meta.defaultMin,
        max: meta.defaultMax,
      },
    ]);
    setNewSensor({ id: '', environmentId: environments[0]?.id || '', variable: 'temperature' });
    setShowAdd(false);
  };

  return (
    <section className="sensor-panel" aria-labelledby="sensor-panel-title">
      <div className="sensor-panel__intro">
        <div>
          <span className="sensor-panel__eyebrow">Gestión técnica</span>
          <h2 id="sensor-panel-title">Sensores y variables</h2>
          <p>
            Administra qué dispositivo mide cada variable, a qué ambiente pertenece y cuáles son sus
            rangos de referencia.
          </p>
        </div>
        <button className="sensor-panel__add" type="button" onClick={() => setShowAdd(true)}>
          <IoAddOutline size={17} /> Agregar sensor
        </button>
      </div>

      <div className="sensor-panel__summary">
        <div>
          <span>Total sensores</span>
          <strong>{summary.total}</strong>
        </div>
        <div className="is-active">
          <span>Activos</span>
          <strong>{summary.active}</strong>
        </div>
        <div className="is-warning">
          <span>Con aviso</span>
          <strong>{summary.warning}</strong>
        </div>
        <div className="is-offline">
          <span>Sin conexión</span>
          <strong>{summary.offline}</strong>
        </div>
      </div>

      <div className="sensor-panel__toolbar">
        <label className="sensor-panel__search">
          <IoSearchOutline size={16} />
          <input
            aria-label="Buscar sensores"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar ID, ambiente o variable…"
          />
        </label>
        <select value={variableFilter} onChange={(event) => setVariableFilter(event.target.value)}>
          <option value="all">Todas las variables</option>
          {Object.entries(VARIABLE_META).map(([key, meta]) => (
            <option key={key} value={key}>
              {meta.label}
            </option>
          ))}
        </select>
        <select value={statusFilter} onChange={(event) => setStatusFilter(event.target.value)}>
          <option value="all">Todos los estados</option>
          <option value="active">Activos</option>
          <option value="warning">Con aviso</option>
          <option value="offline">Sin conexión</option>
        </select>
      </div>

      <div className="sensor-panel__table-wrap">
        <table className="sensor-panel__table">
          <thead>
            <tr>
              <th>Sensor</th>
              <th>Variable</th>
              <th>Ambiente</th>
              <th>Lectura actual</th>
              <th>Rango configurado</th>
              <th>Estado</th>
              <th aria-label="Acciones" />
            </tr>
          </thead>
          <tbody>
            {filteredSensors.map((sensor) => {
              const meta = VARIABLE_META[sensor.variable];
              const Icon = meta.icon;
              const environment = environments.find(
                (item) => item.id === Number(sensor.environmentId)
              );
              return (
                <tr key={sensor.id}>
                  <td>
                    <div className="sensor-panel__sensor-id">
                      <span
                        className={`sensor-panel__sensor-icon sensor-panel__sensor-icon--${meta.color}`}
                      >
                        <Icon size={19} />
                      </span>
                      <span>
                        <strong>{sensor.id}</strong>
                        <small>Sincronizado {sensor.lastSync}</small>
                      </span>
                    </div>
                  </td>
                  <td>
                    {meta.label}
                    <small className="sensor-panel__unit">Unidad: {meta.unit}</small>
                  </td>
                  <td>{getEnvironmentName(sensor.environmentId)}</td>
                  <td>
                    <strong>
                      {getVariableValue(environment, sensor.variable) ?? '—'} {meta.unit}
                    </strong>
                  </td>
                  <td>
                    {sensor.min} – {sensor.max} {meta.unit}
                  </td>
                  <td>
                    <span className={`sensor-panel__status sensor-panel__status--${sensor.status}`}>
                      <i />
                      {sensor.status === 'active'
                        ? 'Activo'
                        : sensor.status === 'warning'
                          ? 'Revisar'
                          : 'Sin conexión'}
                    </span>
                  </td>
                  <td>
                    <div className="sensor-panel__actions">
                      <button
                        type="button"
                        title="Editar sensor"
                        onClick={() => setEditingSensor({ ...sensor })}
                      >
                        <IoCreateOutline size={16} />
                      </button>
                      <button
                        type="button"
                        title={sensor.status === 'offline' ? 'Activar sensor' : 'Desactivar sensor'}
                        onClick={() => toggleSensor(sensor.id)}
                      >
                        <IoPowerOutline size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {filteredSensors.length === 0 && (
          <div className="sensor-panel__empty">No hay sensores que coincidan con los filtros.</div>
        )}
      </div>

      <div className="sensor-panel__variables">
        <div>
          <span className="sensor-panel__eyebrow">Variables monitoreadas</span>
          <h3>Qué mide cada sensor</h3>
        </div>
        <div className="sensor-panel__variable-chips">
          {Object.entries(VARIABLE_META).map(([key, meta]) => {
            const Icon = meta.icon;
            return (
              <div
                key={key}
                className={`sensor-panel__variable-chip sensor-panel__variable-chip--${meta.color}`}
              >
                <Icon size={19} />
                <span>
                  <strong>{meta.label}</strong>
                  <small>{meta.unit} · rango de referencia editable</small>
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {showAdd && (
        <div className="sensor-panel__modal-backdrop" onClick={() => setShowAdd(false)}>
          <form
            className="sensor-panel__modal"
            onSubmit={addSensor}
            onClick={(event) => event.stopPropagation()}
          >
            <button
              type="button"
              className="sensor-panel__modal-close"
              onClick={() => setShowAdd(false)}
            >
              <IoCloseOutline size={22} />
            </button>
            <span className="sensor-panel__eyebrow">Nuevo dispositivo</span>
            <h3>Agregar sensor</h3>
            <p>Asigna una variable y un ambiente para comenzar a monitorearlo.</p>
            <label>
              ID del sensor
              <input
                required
                value={newSensor.id}
                onChange={(event) => setNewSensor({ ...newSensor, id: event.target.value })}
                placeholder="Ej. EA-2091-H"
              />
            </label>
            <label>
              Ambiente
              <select
                value={newSensor.environmentId}
                onChange={(event) =>
                  setNewSensor({ ...newSensor, environmentId: event.target.value })
                }
              >
                {environments.map((environment) => (
                  <option key={environment.id} value={environment.id}>
                    {environment.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Variable
              <select
                value={newSensor.variable}
                onChange={(event) => setNewSensor({ ...newSensor, variable: event.target.value })}
              >
                {Object.entries(VARIABLE_META).map(([key, meta]) => (
                  <option key={key} value={key}>
                    {meta.label} ({meta.unit})
                  </option>
                ))}
              </select>
            </label>
            <div className="sensor-panel__modal-buttons">
              <button type="button" onClick={() => setShowAdd(false)}>
                Cancelar
              </button>
              <button type="submit">Guardar sensor</button>
            </div>
          </form>
        </div>
      )}
      {editingSensor && (
        <div className="sensor-panel__modal-backdrop" onClick={() => setEditingSensor(null)}>
          <form
            className="sensor-panel__modal"
            onSubmit={saveSensor}
            onClick={(event) => event.stopPropagation()}
          >
            <button
              type="button"
              className="sensor-panel__modal-close"
              onClick={() => setEditingSensor(null)}
            >
              <IoCloseOutline size={22} />
            </button>
            <span className="sensor-panel__eyebrow">Configuración del sensor</span>
            <h3>{editingSensor.id}</h3>
            <p>Modifica los rangos que usarás para generar avisos.</p>
            <label>
              Variable
              <select
                value={editingSensor.variable}
                onChange={(event) =>
                  setEditingSensor({ ...editingSensor, variable: event.target.value })
                }
              >
                {Object.entries(VARIABLE_META).map(([key, meta]) => (
                  <option key={key} value={key}>
                    {meta.label} ({meta.unit})
                  </option>
                ))}
              </select>
            </label>
            <div className="sensor-panel__range-row">
              <label>
                Mínimo
                <input
                  type="number"
                  value={editingSensor.min}
                  onChange={(event) =>
                    setEditingSensor({ ...editingSensor, min: event.target.value })
                  }
                />
              </label>
              <label>
                Máximo
                <input
                  type="number"
                  value={editingSensor.max}
                  onChange={(event) =>
                    setEditingSensor({ ...editingSensor, max: event.target.value })
                  }
                />
              </label>
            </div>
            <div className="sensor-panel__modal-buttons">
              <button type="button" onClick={() => setEditingSensor(null)}>
                Cancelar
              </button>
              <button type="submit">Guardar cambios</button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default SensorVariablePanel;
