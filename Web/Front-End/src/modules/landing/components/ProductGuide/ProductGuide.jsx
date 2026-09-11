import './ProductGuide.css';
import {
  Activity,
  ArrowLeft,
  ArrowRight,
  BarChart3,
  BellRing,
  CheckCircle2,
  CloudSun,
  Gauge,
  Info,
  LayoutDashboard,
  Lightbulb,
  MousePointerClick,
  RadioTower,
  ShieldCheck,
  SlidersHorizontal,
  Thermometer,
  Users,
  Wind,
  Wrench,
} from 'lucide-react';
import { Link } from 'react-router-dom';

const steps = [
  {
    number: '01',
    icon: Users,
    title: 'Ingresa a tu cuenta',
    description:
      'Inicia sesión para acceder a los ambientes y a las funciones habilitadas para tu perfil institucional.',
  },
  {
    number: '02',
    icon: LayoutDashboard,
    title: 'Lee el resumen',
    description:
      'El dashboard reúne el estado general, las lecturas recientes y los avisos que requieren atención.',
  },
  {
    number: '03',
    icon: MousePointerClick,
    title: 'Elige un ambiente',
    description:
      'Abre un salón o espacio para revisar sus indicadores, comparar su comportamiento y consultar su detalle.',
  },
  {
    number: '04',
    icon: Lightbulb,
    title: 'Decide y verifica',
    description:
      'Usa la tendencia como guía para actuar, registra el cambio y vuelve a revisar si el ambiente mejora.',
  },
];

const variables = [
  {
    icon: Thermometer,
    name: 'Temperatura',
    symbol: '°C',
    description:
      'Indica qué tan cálido o frío está el espacio. Una variación sostenida puede afectar el confort y la concentración.',
    reading: 'Confort térmico',
    tone: 'mint',
  },
  {
    icon: CloudSun,
    name: 'Humedad relativa',
    symbol: '%',
    description:
      'Representa el vapor de agua en el aire. Permite reconocer ambientes muy secos, cargados o con sensación de bochorno.',
    reading: 'Sensación ambiental',
    tone: 'blue',
  },
  {
    icon: Wind,
    name: 'Dióxido de carbono',
    symbol: 'ppm',
    description:
      'Es un indicador útil de ventilación. Un aumento persistente sugiere revisar la renovación del aire y la ocupación del espacio.',
    reading: 'Calidad del aire',
    tone: 'amber',
  },
  {
    icon: Activity,
    name: 'Nivel de ruido',
    symbol: 'dB',
    description:
      'Mide la intensidad sonora. Sirve para identificar momentos o espacios que pueden generar distracción o incomodidad.',
    reading: 'Confort acústico',
    tone: 'purple',
  },
];

const modules = [
  {
    icon: LayoutDashboard,
    title: 'Dashboard',
    description: 'Vista general con indicadores, resumen de ambientes y prioridades.',
  },
  {
    icon: RadioTower,
    title: 'Ambientes',
    description: 'Listado y ficha detallada de los espacios monitoreados.',
  },
  {
    icon: BarChart3,
    title: 'Ranking',
    description: 'Comparación para encontrar ambientes estables y oportunidades de mejora.',
  },
  {
    icon: BellRing,
    title: 'Alertas',
    description: 'Avisos para detectar lecturas que merecen revisión o seguimiento.',
  },
  {
    icon: ShieldCheck,
    title: 'Favoritos',
    description: 'Acceso rápido a los ambientes más consultados por cada usuario.',
  },
  {
    icon: SlidersHorizontal,
    title: 'Configuración',
    description: 'Preferencias, idioma, accesibilidad y datos de la cuenta.',
  },
];

const actions = [
  {
    label: 'Temperatura',
    action: 'Compara con el confort percibido y revisa ventilación o climatización del espacio.',
  },
  {
    label: 'Humedad',
    action:
      'Observa si la lectura se mantiene en el tiempo; revisa filtraciones, ventilación o condiciones del lugar.',
  },
  {
    label: 'CO₂',
    action:
      'Verifica ocupación y renovación del aire; prioriza ventilar antes de sacar conclusiones.',
  },
  {
    label: 'Ruido',
    action:
      'Identifica la hora y la actividad asociada para reducir fuentes de distracción o reorganizar el espacio.',
  },
];

function ProductGuide() {
  return (
    <main className="product-guide" aria-labelledby="product-guide-title">
      <div className="product-guide__shell">
        <div className="product-guide__back-row">
          <Link to="/landing" className="product-guide__back">
            <ArrowLeft size={16} /> Volver al inicio
          </Link>
          <span className="product-guide__status">
            <span /> Centro de demostraciones
          </span>
        </div>

        <header className="product-guide__intro">
          <span className="product-guide__eyebrow">Guía completa de EduAirControl</span>
          <h1 id="product-guide-title">
            Todo lo que necesitas para <span>leer tu ambiente.</span>
          </h1>
          <p>
            EduAirControl convierte las mediciones de sensores IoT en información visual para cuidar
            el bienestar, la concentración y la calidad de los espacios educativos.
          </p>
          <div className="product-guide__intro-meta">
            <span>
              <CheckCircle2 size={16} /> Monitoreo organizado
            </span>
            <span>
              <CheckCircle2 size={16} /> Decisiones basadas en datos
            </span>
            <span>
              <CheckCircle2 size={16} /> Seguimiento continuo
            </span>
          </div>
        </header>

        <section className="product-guide__mission" aria-labelledby="mission-title">
          <div className="product-guide__mission-icon">
            <Gauge size={27} />
          </div>
          <div>
            <span className="product-guide__kicker">El propósito</span>
            <h2 id="mission-title">Del sensor a una decisión útil</h2>
            <p>
              La aplicación recibe datos de los dispositivos instalados en cada ambiente, los agrupa
              por espacio y los muestra con indicadores que cualquier usuario puede entender. Así
              puedes pasar de “algo se siente diferente” a “qué variable cambió, dónde ocurrió y qué
              debo revisar”.
            </p>
          </div>
        </section>

        <section className="product-guide__block" aria-labelledby="steps-title">
          <div className="product-guide__section-heading">
            <div>
              <span className="product-guide__kicker">Paso a paso</span>
              <h2 id="steps-title">Así se usa la app</h2>
            </div>
            <span className="product-guide__heading-note">De la lectura a la acción</span>
          </div>
          <div className="product-guide__steps">
            {steps.map(({ number, icon: Icon, title, description }) => (
              <article className="product-guide__step" key={number}>
                <div className="product-guide__step-top">
                  <span>{number}</span>
                  <Icon size={22} />
                </div>
                <h3>{title}</h3>
                <p>{description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="product-guide__block" aria-labelledby="variables-title">
          <div className="product-guide__section-heading">
            <div>
              <span className="product-guide__kicker">Diccionario ambiental</span>
              <h2 id="variables-title">¿Qué significa cada variable?</h2>
            </div>
            <span className="product-guide__heading-note">Lee la tendencia, no solo el número</span>
          </div>
          <div className="product-guide__variables">
            {variables.map(({ icon: Icon, name, symbol, description, reading, tone }) => (
              <article
                className={`product-guide__variable product-guide__variable--${tone}`}
                key={name}
              >
                <div className="product-guide__variable-icon">
                  <Icon size={22} />
                </div>
                <div className="product-guide__variable-title">
                  <h3>{name}</h3>
                  <span>{symbol}</span>
                </div>
                <p>{description}</p>
                <div className="product-guide__variable-reading">
                  <span>Te ayuda a leer</span>
                  <strong>{reading}</strong>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="product-guide__interpretation" aria-labelledby="interpretation-title">
          <div className="product-guide__section-heading">
            <div>
              <span className="product-guide__kicker">Cómo interpretar la información</span>
              <h2 id="interpretation-title">Mira el contexto completo</h2>
            </div>
          </div>
          <div className="product-guide__interpretation-grid">
            <article>
              <span className="product-guide__signal product-guide__signal--good">Estable</span>
              <h3>El ambiente se mantiene</h3>
              <p>
                Las lecturas presentan un comportamiento consistente. Continúa el seguimiento y
                registra buenas prácticas.
              </p>
            </article>
            <article>
              <span className="product-guide__signal product-guide__signal--watch">Revisar</span>
              <h3>Hay una variación</h3>
              <p>
                Observa la tendencia, la hora y la actividad del espacio antes de tomar una medida
                correctiva.
              </p>
            </article>
            <article>
              <span className="product-guide__signal product-guide__signal--alert">Atención</span>
              <h3>La alerta persiste</h3>
              <p>
                Prioriza una verificación en sitio, revisa el sensor y aplica el protocolo definido
                por tu institución.
              </p>
            </article>
          </div>
        </section>

        <section className="product-guide__block" aria-labelledby="actions-title">
          <div className="product-guide__section-heading">
            <div>
              <span className="product-guide__kicker">Guía práctica</span>
              <h2 id="actions-title">¿Qué puedo hacer con cada lectura?</h2>
            </div>
          </div>
          <div className="product-guide__actions">
            {actions.map(({ label, action }) => (
              <div className="product-guide__action" key={label}>
                <div className="product-guide__action-check">
                  <CheckCircle2 size={18} />
                </div>
                <div>
                  <strong>{label}</strong>
                  <p>{action}</p>
                </div>
              </div>
            ))}
          </div>
        </section>

        <section className="product-guide__block" aria-labelledby="modules-title">
          <div className="product-guide__section-heading">
            <div>
              <span className="product-guide__kicker">Dentro de tu cuenta</span>
              <h2 id="modules-title">Encuentra todo en un solo lugar</h2>
            </div>
          </div>
          <div className="product-guide__modules">
            {modules.map(({ icon: Icon, title, description }) => (
              <article className="product-guide__module" key={title}>
                <Icon size={20} />
                <div>
                  <h3>{title}</h3>
                  <p>{description}</p>
                </div>
                <ArrowRight size={17} aria-hidden="true" />
              </article>
            ))}
          </div>
        </section>

        <section className="product-guide__roles" aria-labelledby="roles-title">
          <div className="product-guide__roles-copy">
            <span className="product-guide__kicker">Trabajo colaborativo</span>
            <h2 id="roles-title">Cada perfil tiene una misión</h2>
            <p>
              La plataforma puede ser usada por estudiantes, docentes, personal administrativo y
              responsables de la gestión ambiental. Todos consultan la misma realidad, pero actúan
              desde necesidades distintas.
            </p>
          </div>
          <div className="product-guide__role-list">
            <div>
              <Users size={20} />
              <span>
                <strong>Usuario</strong>
                <small>Consulta ambientes, favoritos y tendencias.</small>
              </span>
            </div>
            <div>
              <Wrench size={20} />
              <span>
                <strong>Administrador</strong>
                <small>Gestiona espacios, usuarios y seguimiento operativo.</small>
              </span>
            </div>
          </div>
        </section>

        <section className="product-guide__notice">
          <div className="product-guide__notice-icon">
            <Info size={22} />
          </div>
          <div>
            <strong>Importante: una lectura es una señal, no un diagnóstico.</strong>
            <p>
              Los sensores pueden presentar variaciones, errores o interrupciones por conectividad y
              factores externos. Usa los datos como apoyo, verifica el contexto y acude a una
              evaluación técnica cuando sea necesario.
            </p>
          </div>
        </section>

        <section className="product-guide__faq" aria-labelledby="faq-title">
          <span className="product-guide__kicker">Preguntas frecuentes</span>
          <h2 id="faq-title">Antes de comenzar</h2>
          <div className="product-guide__faq-grid">
            <details>
              <summary>¿Los datos se actualizan todo el tiempo?</summary>
              <p>
                La frecuencia depende del sensor y de la conectividad disponible. Si una lectura no
                cambia, revisa primero la hora de actualización y el estado del dispositivo.
              </p>
            </details>
            <details>
              <summary>¿Una alerta significa que hay una emergencia?</summary>
              <p>
                No necesariamente. Es una señal para revisar el contexto, confirmar la lectura y
                seguir el protocolo de tu institución.
              </p>
            </details>
            <details>
              <summary>¿Puedo comparar dos ambientes?</summary>
              <p>
                Sí. Usa el ranking y el detalle de cada ambiente para observar diferencias,
                tendencias y oportunidades de mejora.
              </p>
            </details>
          </div>
        </section>
      </div>
    </main>
  );
}

export default ProductGuide;
