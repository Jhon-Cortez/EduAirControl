import { useEffect, useMemo, useState } from 'react';
import {
  FaArrowDown,
  FaArrowUp,
  FaCalendarAlt,
  FaChartBar,
  FaCheckCircle,
  FaChevronDown,
  FaClock,
  FaDownload,
  FaSyncAlt,
  FaTachometerAlt,
  FaThermometerHalf,
  FaTint,
  FaWind,
} from 'react-icons/fa';
import { IoStatsChart } from 'react-icons/io5';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Pie,
  PieChart,
  ReferenceLine,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';

import { useEnvironment } from '../context/EnvironmentContext';
import { getComputedA11yFontSizePx } from '../shared/accessibility/accessibilitySettings';
import Navbar from '../modules/dashboard/components/Navbar/Navbar';

import './DashboardScreen.css';

const PERIODS = [
  { id: 'day', label: 'Día', context: 'Últimas 24 horas' },
  { id: 'week', label: 'Semana', context: '18 — 24 agosto 2026' },
  { id: 'month', label: 'Mes', context: 'Agosto 2026' },
  { id: 'year', label: 'Año', context: 'Enero — agosto 2026' },
];

const PERIOD_LABELS = {
  day: ['00 h', '03 h', '06 h', '09 h', '12 h', '15 h', '18 h', '21 h'],
  week: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
  month: ['Sem 1', 'Sem 2', 'Sem 3', 'Sem 4', 'Actual'],
  year: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago'],
};

const CURVES = {
  day: [0.94, 0.9, 0.82, 1.03, 1.2, 1.09, 0.98, 0.86],
  week: [1.02, 0.96, 1.07, 0.91, 1.12, 0.82, 0.88],
  month: [1.08, 1.03, 0.98, 0.93, 0.88],
  year: [1.16, 1.09, 1.04, 1.02, 0.98, 0.93, 0.9, 0.86],
};

const METRICS = {
  co2: {
    label: 'CO₂',
    unit: 'ppm',
    color: 'var(--accent)',
    softColor: '#0B4D54',
    comfort: 800,
    domain: [350, 1300],
    icon: FaWind,
  },
  temperature: {
    label: 'Temperatura',
    unit: '°C',
    color: '#FF6873',
    softColor: '#542C38',
    comfort: 22,
    domain: [18, 34],
    icon: FaThermometerHalf,
  },
  humidity: {
    label: 'Humedad',
    unit: '%',
    color: '#58AFFF',
    softColor: '#173F5C',
    comfort: 50,
    domain: [25, 80],
    icon: FaTint,
  },
  noise: {
    label: 'Ruido',
    unit: 'dB',
    color: '#D98AFF',
    softColor: '#442C5D',
    comfort: 45,
    domain: [20, 85],
    icon: FaTachometerAlt,
  },
};

const STATUS = {
  normal: { label: 'Normal', className: 'normal', color: '#25E77C' },
  warning: { label: 'Advertencia', className: 'warning', color: '#FFB11A' },
  alert: { label: 'Alerta', className: 'alert', color: '#FF4D5B' },
};

const ENVIRONMENT_COLORS = ['var(--accent)', '#FFB11A', '#FF4D5B', '#9D73FF', '#49D17D', '#58AFFF'];

function normalizeStatus(statusKey) {
  if (statusKey?.toLowerCase().includes('alert')) return STATUS.alert;
  if (statusKey?.toLowerCase().includes('warning')) return STATUS.warning;
  return STATUS.normal;
}

function formatMetric(value, metric) {
  if (metric === 'temperature') return `${Number(value).toFixed(1)}°C`;
  return `${Math.round(value)} ${METRICS[metric].unit}`;
}

function getBaseValue(environment, metric) {
  return {
    co2: Number(environment.co2) || 0,
    temperature: Number(environment.temp) || 0,
    humidity: Number(environment.humidity) || 0,
    noise: Number(environment.noise) || 0,
  }[metric];
}

function getHistoricalValue(environment, metric, curveValue, index) {
  const base = getBaseValue(environment, metric);
  const variation =
    Math.sin((index + String(environment.id).length) * 1.37) *
    (metric === 'co2' ? 58 : metric === 'humidity' ? 3.8 : metric === 'noise' ? 4.5 : 0.55);
  const factor =
    metric === 'co2'
      ? curveValue
      : metric === 'temperature'
        ? 0.68 + curveValue * 0.25
        : metric === 'humidity'
          ? 0.76 + curveValue * 0.18
          : 0.8 + curveValue * 0.16;

  return metric === 'temperature'
    ? Number((base + variation * factor).toFixed(1))
    : Math.round(base * factor + variation);
}

function getPeriodValue(environment, metric, period) {
  const factor = period === 'day' ? 1 : period === 'week' ? 0.98 : period === 'month' ? 0.95 : 0.91;
  return getHistoricalValue(environment, metric, factor, 4);
}

function ChartTooltip({ active, payload, label, metric }) {
  if (!active || !payload?.length) return null;

  return (
    <div className="analysis-tooltip">
      <p>{label}</p>
      {payload.map((item) => (
        <div className="analysis-tooltip-row" key={item.dataKey || item.name}>
          <span className="analysis-tooltip-dot" style={{ background: item.color }} />
          <span>{item.name}</span>
          <strong>{formatMetric(Number(item.value), metric)}</strong>
        </div>
      ))}
    </div>
  );
}

function FilterSelect({ icon: Icon, value, options, onChange, isMetric = false, label }) {
  const [open, setOpen] = useState(false);
  const selected = options.find((option) => option.value === value) || options[0];

  useEffect(() => {
    if (!open) return;

    const handleOutsideClick = (event) => {
      if (!event.target.closest('.analysis-filter-select')) {
        setOpen(false);
      }
    };

    document.addEventListener('mousedown', handleOutsideClick);
    return () => document.removeEventListener('mousedown', handleOutsideClick);
  }, [open]);

  return (
    <div
      className={`analysis-filter-select ${isMetric ? 'metric-filter' : ''} ${open ? 'is-open' : ''}`}
    >
      <button
        type="button"
        className="analysis-filter-trigger"
        onClick={() => setOpen((prev) => !prev)}
        aria-label={label}
        aria-expanded={open}
      >
        <Icon size={15} />
        <span className="analysis-filter-value">{selected.label}</span>
        <FaChevronDown size={14} className="analysis-filter-chevron" />
      </button>

      {open && (
        <div className="analysis-filter-menu" role="listbox" aria-label={label}>
          {options.map((option) => (
            <button
              key={option.value}
              type="button"
              className={`analysis-filter-option ${option.value === value ? 'selected' : ''}`}
              onClick={() => {
                onChange(option.value);
                setOpen(false);
              }}
            >
              {option.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

// Tarjeta reutilizable para el resumen del hero: recibe su
// contenido por props en vez de tenerlo quemado en el JSX.
function HeroSummaryCard({ label, value, valueClassName, hint, highlight = false }) {
  return (
    <div className={`hero-summary-card ${highlight ? 'hero-summary-total' : ''}`}>
      <span className="hero-summary-label">{label}</span>
      <strong className={valueClassName}>{value}</strong>
      <small>{hint}</small>
    </div>
  );
}

// Tarjeta reutilizable para los indicadores (KPI): recibe ícono,
// etiqueta, valor y una nota/tendencia opcional por props.
function KpiCard({ className = '', icon, label, value, valueClassName, note }) {
  return (
    <article className={`analysis-kpi-card ${className}`}>
      <div className="kpi-card-header">
        <span>{label}</span>
        {icon}
      </div>
      <strong className={valueClassName}>{value}</strong>
      <small>{note}</small>
    </article>
  );
}

function DashboardScreen() {
  const { environments = [] } = useEnvironment();
  const [period, setPeriod] = useState('day');
  const [metric, setMetric] = useState('co2');
  const [environmentId, setEnvironmentId] = useState('all');
  const [lastUpdated, setLastUpdated] = useState('hace 3 min');

  useEffect(() => {
    document.body.classList.add('dashboard-analysis-mode');
    return () => document.body.classList.remove('dashboard-analysis-mode');
  }, []);

  const selectedEnvironments = useMemo(() => {
    if (environmentId === 'all') return environments;
    return environments.filter((environment) => String(environment.id) === String(environmentId));
  }, [environmentId, environments]);

  const visibleEnvironments = selectedEnvironments.length ? selectedEnvironments : environments;
  const metricInfo = METRICS[metric];
  const MetricIcon = metricInfo.icon;
  const periodInfo = PERIODS.find((item) => item.id === period) || PERIODS[0];
  const selectedLabel =
    environmentId === 'all'
      ? 'Todos los ambientes'
      : visibleEnvironments[0]?.name || 'Sin ambiente seleccionado';

  const chartData = useMemo(
    () =>
      PERIOD_LABELS[period].map((label, index) => {
        const row = { label };
        visibleEnvironments.forEach((environment, environmentIndex) => {
          row[`environment-${environment.id}`] = getHistoricalValue(
            environment,
            metric,
            CURVES[period][index],
            index + environmentIndex
          );
        });
        return row;
      }),
    [metric, period, visibleEnvironments]
  );

  const comparisonData = useMemo(
    () =>
      visibleEnvironments.map((environment, index) => ({
        name: environment.name.replace('Ambiente ', ''),
        value: getPeriodValue(environment, metric, period),
        color: ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length],
      })),
    [metric, period, visibleEnvironments]
  );

  const average = useMemo(() => {
    if (!visibleEnvironments.length) return 0;
    return (
      visibleEnvironments.reduce(
        (total, environment) => total + getPeriodValue(environment, metric, period),
        0
      ) / visibleEnvironments.length
    );
  }, [metric, period, visibleEnvironments]);

  const statusCounts = useMemo(
    () =>
      visibleEnvironments.reduce(
        (counts, environment) => {
          const status = normalizeStatus(environment.statusKey);
          counts[status.className] += 1;
          return counts;
        },
        { normal: 0, warning: 0, alert: 0 }
      ),
    [visibleEnvironments]
  );

  // Tamaños de fuente escalables para ticks y elementos que requieren números
  const baseA11yPx = typeof window !== 'undefined' ? getComputedA11yFontSizePx() : 16;
  const tickFont11 = Math.max(10, Math.round((baseA11yPx * 11) / 16));
  const tickFont10 = Math.max(9, Math.round((baseA11yPx * 10) / 16));

  const handleRefresh = () => {
    setLastUpdated('actualizado ahora');
    window.setTimeout(() => setLastUpdated('hace 3 min'), 2600);
  };

  const handleExport = () => {
    const headers = ['Ambiente', 'Métrica', 'Periodo', 'Promedio', 'Unidad'];
    const rows = visibleEnvironments.map((environment) => [
      environment.name,
      metricInfo.label,
      periodInfo.label,
      getPeriodValue(environment, metric, period),
      metricInfo.unit,
    ]);
    const csv = [headers, ...rows].map((row) => row.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `eduaircontrol-${metric}-${period}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  };

  // Config reutilizable de las tarjetas KPI.
  const kpiDefinitions = [
    {
      key: 'average',
      className: 'primary-kpi',
      icon: <MetricIcon size={17} />,
      label: `Promedio ${metricInfo.label}`,
      value: formatMetric(average, metric),
      note: (
        <>
          <FaArrowDown size={13} /> 7.4% vs. periodo anterior
        </>
      ),
    },
    {
      key: 'normal',
      icon: <FaCheckCircle size={17} />,
      label: 'Normal',
      value: statusCounts.normal,
      valueClassName: 'green-number',
      note: 'ambientes en rango',
    },
    {
      key: 'warning',
      icon: <IoStatsChart size={17} />,
      label: 'Advertencias',
      value: statusCounts.warning,
      valueClassName: 'amber-number',
      note: 'requieren seguimiento',
    },
    {
      key: 'alert',
      className: 'alert-kpi',
      icon: <FaTachometerAlt size={17} />,
      label: 'Alertas',
      value: statusCounts.alert,
      valueClassName: 'red-number',
      note: 'requieren atención',
    },
  ];

  // Ciclo forEach: recorre la config y arma cada tarjeta KPI con sus props.
  const kpiCards = [];
  kpiDefinitions.forEach((item) => {
    kpiCards.push(
      <KpiCard
        key={item.key}
        className={item.className}
        icon={item.icon}
        label={item.label}
        value={item.value}
        valueClassName={item.valueClassName}
        note={item.note}
      />
    );
  });

  return (
    <div className="dashboard-analysis-screen">
      <Navbar />

      <main className="analysis-page-content">
        <section className="analysis-hero">
          <div className="analysis-heading">
            <span className="analysis-eyebrow">
              <span /> DASHBOARD DE ANÁLISIS
            </span>
            <h1>
              La calidad ambiental
              <br />
              <em>también cuenta una historia.</em>
            </h1>
            <p>
              Compara la evolución de tus ambientes, identifica cambios y toma decisiones antes de
              que una alerta interrumpa el aprendizaje.
            </p>
          </div>
        </section>

        <div className="analysis-flow-mark" aria-hidden="true">
          <span />
          <span />
          <span />
          <span />
        </div>

        <section className="analysis-controls" aria-label="Controles del análisis">
          <div className="analysis-period-tabs" role="tablist" aria-label="Seleccionar periodo">
            {PERIODS.map((item) => (
              <button
                key={item.id}
                className={period === item.id ? 'active' : ''}
                type="button"
                role="tab"
                aria-selected={period === item.id}
                onClick={() => setPeriod(item.id)}
              >
                {item.label}
              </button>
            ))}
          </div>
          <div className="analysis-period-context">
            <FaCalendarAlt size={15} /> {periodInfo.context}
          </div>

          <FilterSelect
            icon={MetricIcon}
            label="Seleccionar métrica"
            value={metric}
            onChange={setMetric}
            isMetric
            options={Object.entries(METRICS).map(([key, info]) => ({
              value: key,
              label: info.label,
            }))}
          />
        </section>

        <section className="analysis-kpi-grid" aria-label="Indicadores del periodo">
          {kpiCards}
        </section>

        <div className="analysis-layout">
          <aside className="analysis-sidebar">
            <section className="analysis-panel environment-panel">
              <div className="analysis-panel-title">
                <div>
                  <span className="analysis-section-label">CONTEXTO</span>
                  <h2>Ambientes</h2>
                </div>
                <span className="analysis-count-badge">{environments.length}</span>
              </div>
              <p className="analysis-panel-description">
                Elige un espacio para revisar su evolución específica.
              </p>
              <div className="analysis-environment-list">
                <button
                  type="button"
                  className={`analysis-environment-row ${environmentId === 'all' ? 'selected' : ''}`}
                  onClick={() => setEnvironmentId('all')}
                >
                  <span className="analysis-environment-icon all">
                    <FaWind size={13} />
                  </span>
                  <span>
                    <strong>Todos los ambientes</strong>
                    <small>Vista consolidada</small>
                  </span>
                  <FaChevronDown size={13} />
                </button>
                {environments.map((environment, index) => {
                  const status = normalizeStatus(environment.statusKey);
                  return (
                    <button
                      type="button"
                      key={environment.id}
                      className={`analysis-environment-row ${String(environmentId) === String(environment.id) ? 'selected' : ''}`}
                      onClick={() => setEnvironmentId(String(environment.id))}
                    >
                      <span
                        className="analysis-environment-icon"
                        style={{
                          background: `${ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length]}24`,
                          color: ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length],
                        }}
                      >
                        {index + 1}
                      </span>
                      <span>
                        <strong>{environment.name}</strong>
                        <small>
                          {environment.building} · Piso {environment.floor}
                        </small>
                      </span>
                      <i
                        className={`analysis-status-dot ${status.className}`}
                        title={status.label}
                      />{' '}
                    </button>
                  );
                })}
              </div>
              <div className="analysis-sidebar-divider" />
              <div className="analysis-updated">
                <FaClock size={14} />
                <span>
                  Última lectura
                  <br />
                  <strong>{lastUpdated}</strong>
                </span>
                <button type="button" aria-label="Actualizar datos" onClick={handleRefresh}>
                  <FaSyncAlt size={13} />
                </button>
              </div>
            </section>
            <section className="analysis-panel reading-panel">
              <span className="analysis-section-label">LECTURA RÁPIDA</span>
              <h3>
                {statusCounts.alert > 0
                  ? 'Hay un ambiente que pide atención.'
                  : 'La red se mantiene estable.'}
              </h3>
              <p>
                {statusCounts.alert > 0
                  ? 'Revisa la tendencia del ambiente en alerta y compárala con sus horas de mayor ocupación.'
                  : 'La mayoría de los espacios se encuentran dentro del rango recomendado.'}
              </p>
              <button
                type="button"
                onClick={() =>
                  setEnvironmentId(
                    statusCounts.alert > 0
                      ? String(
                          environments.find(
                            (environment) =>
                              normalizeStatus(environment.statusKey).className === 'alert'
                          )?.id || 'all'
                        )
                      : 'all'
                  )
                }
              >
                Ver detalle <FaArrowUp size={14} />
              </button>
            </section>
          </aside>

          <section className="analysis-charts-column">
            <article className="analysis-panel main-analysis-chart">
              <div className="analysis-chart-header">
                <div>
                  <span className="analysis-section-label">EVOLUCIÓN TEMPORAL</span>
                  <h2>{metricInfo.label} a través del tiempo</h2>
                  <p>
                    {selectedLabel} · {periodInfo.context}
                  </p>
                </div>
                <span className="analysis-live-pill">
                  <span /> Datos sincronizados
                </span>
              </div>
              <div className="analysis-chart-legend">
                {visibleEnvironments.map((environment, index) => (
                  <span key={environment.id}>
                    <i
                      style={{ background: ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length] }}
                    />
                    {environment.name}
                  </span>
                ))}
                <span className="comfort-legend">
                  <i /> umbral de confort
                </span>
              </div>
              <div className="analysis-main-chart-wrap">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={chartData} margin={{ top: 15, right: 8, left: -17, bottom: 0 }}>
                    <defs>
                      {visibleEnvironments.map((environment, index) => (
                        <linearGradient
                          key={environment.id}
                          id={`analysis-fill-${environment.id}`}
                          x1="0"
                          y1="0"
                          x2="0"
                          y2="1"
                        >
                          <stop
                            offset="0%"
                            stopColor={ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length]}
                            stopOpacity={0.2}
                          />
                          <stop
                            offset="100%"
                            stopColor={ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length]}
                            stopOpacity={0}
                          />
                        </linearGradient>
                      ))}
                    </defs>
                    <CartesianGrid
                      stroke="rgba(167,188,208,.11)"
                      strokeDasharray="3 5"
                      vertical={false}
                    />
                    <XAxis
                      dataKey="label"
                      axisLine={false}
                      tickLine={false}
                      tick={{ fill: '#8394A8', fontSize: 11 }}
                      dy={10}
                    />
                    <YAxis
                      axisLine={false}
                      tickLine={false}
                      tick={{ fill: '#8394A8', fontSize: 11 }}
                      domain={metricInfo.domain}
                      width={45}
                    />
                    <Tooltip content={<ChartTooltip metric={metric} />} />
                    <ReferenceLine
                      y={metricInfo.comfort}
                      stroke="rgba(185,203,197,.5)"
                      strokeDasharray="5 5"
                    />
                    {visibleEnvironments.map((environment, index) => (
                      <Area
                        key={environment.id}
                        type="monotone"
                        dataKey={`environment-${environment.id}`}
                        name={environment.name}
                        stroke={ENVIRONMENT_COLORS[index % ENVIRONMENT_COLORS.length]}
                        fill={`url(#analysis-fill-${environment.id})`}
                        strokeWidth={2.5}
                        activeDot={{ r: 4, strokeWidth: 2, fill: '#0C1528' }}
                        isAnimationActive={false}
                      />
                    ))}
                  </AreaChart>
                </ResponsiveContainer>
              </div>
              <div className="analysis-chart-caption">
                <span>
                  <i /> Promedio recomendado: {formatMetric(metricInfo.comfort, metric)}
                </span>
                <span>{metric === 'co2' ? 'Menor a 800 ppm' : 'Rango de referencia'}</span>
              </div>
            </article>

            <div className="analysis-secondary-grid">
              <article className="analysis-panel comparison-chart">
                <div className="analysis-chart-header compact">
                  <div>
                    <span className="analysis-section-label">COMPARACIÓN</span>
                    <h2>Por ambiente</h2>
                    <p>Promedio de {metricInfo.label.toLowerCase()} en el periodo</p>
                  </div>
                  <FaChartBar size={18} />
                </div>
                <div className="analysis-bar-chart-wrap">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                      data={comparisonData}
                      margin={{ top: 12, right: 8, left: -18, bottom: 0 }}
                    >
                      <CartesianGrid
                        stroke="rgba(167,188,208,.11)"
                        strokeDasharray="3 5"
                        vertical={false}
                      />
                      <XAxis
                        dataKey="name"
                        axisLine={false}
                        tickLine={false}
                        tick={{ fill: '#8394A8', fontSize: 10 }}
                        interval={0}
                      />
                      <YAxis
                        axisLine={false}
                        tickLine={false}
                        tick={{ fill: '#8394A8', fontSize: 10 }}
                        width={38}
                      />
                      <Tooltip
                        content={<ChartTooltip metric={metric} />}
                        cursor={{ fill: 'rgb(var(--accent-rgb) / .05)' }}
                      />
                      <Bar dataKey="value" radius={[5, 5, 0, 0]} isAnimationActive={false}>
                        {comparisonData.map((item) => (
                          <Cell key={item.name} fill={item.color} />
                        ))}
                      </Bar>
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </article>
              <article className="analysis-panel network-status-chart">
                <div className="analysis-chart-header compact">
                  <div>
                    <span className="analysis-section-label">ESTADO ACTUAL</span>
                    <h2>Salud de la red</h2>
                  </div>
                  <span className="analysis-network-pill">
                    <span /> Monitoreando
                  </span>
                </div>
                <div className="analysis-network-body">
                  <div className="analysis-donut-wrap">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie
                          data={[
                            { name: 'Normal', value: statusCounts.normal },
                            { name: 'Advertencia', value: statusCounts.warning },
                            { name: 'Alerta', value: statusCounts.alert },
                          ].filter((item) => item.value > 0)}
                          dataKey="value"
                          innerRadius={48}
                          outerRadius={65}
                          startAngle={90}
                          endAngle={-270}
                          paddingAngle={4}
                          stroke="none"
                          isAnimationActive={false}
                        >
                          <Cell fill="#25E77C" />
                          <Cell fill="#FFB11A" />
                          <Cell fill="#FF4D5B" />
                        </Pie>
                      </PieChart>
                    </ResponsiveContainer>
                    <div className="analysis-donut-center">
                      <strong>
                        {visibleEnvironments.length
                          ? Math.round((statusCounts.normal / visibleEnvironments.length) * 100)
                          : 0}
                        %
                      </strong>
                      <span>en rango</span>
                    </div>
                  </div>
                  <div className="analysis-network-legend">
                    <div>
                      <i className="normal" />
                      <span>Normal</span>
                      <strong>{statusCounts.normal}</strong>
                    </div>
                    <div>
                      <i className="warning" />
                      <span>Advertencia</span>
                      <strong>{statusCounts.warning}</strong>
                    </div>
                    <div>
                      <i className="alert" />
                      <span>Alerta</span>
                      <strong>{statusCounts.alert}</strong>
                    </div>
                    <small>
                      <FaCheckCircle size={13} /> Datos actualizados
                    </small>
                  </div>
                </div>
              </article>
            </div>
          </section>
        </div>

        <footer className="analysis-footer">
          <span>EduAirControl · Smart Air Monitoring</span>
          <span>{environments.length} ambientes · Actualización automática cada 5 min</span>
        </footer>
      </main>
    </div>
  );
}

export default DashboardScreen;
