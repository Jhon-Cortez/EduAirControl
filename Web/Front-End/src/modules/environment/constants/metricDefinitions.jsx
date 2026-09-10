import { WiThermometer, WiHumidity } from "react-icons/wi";
import { MdCo2 } from "react-icons/md";
import { HiSpeakerWave } from "react-icons/hi2";

/**
 * Lista de métricas de un ambiente.
 * Cada una define su ícono, texto y cómo leer el valor
 * desde el objeto "environment".
 *
 * Así cualquier componente puede mostrar temperatura,
 * humedad, CO2 y ruido recorriendo este arreglo,
 * sin repetir el mismo JSX.
 */

export const METRIC_DEFINITIONS = [
  {
    key: "temp",
    statusType: "temp",
    icon: <WiThermometer className="summary-icon temp" />,
    labelKey: "dashboard.temperature",
    getValue: (env) => `${env.temp} °C`,
    getRaw: (env) => env.temp,
  },
  {
    key: "humidity",
    statusType: "humidity",
    icon: <WiHumidity className="summary-icon humidity" />,
    labelKey: "dashboard.humidity",
    getValue: (env) => `${env.humidity}%`,
    getRaw: (env) => env.humidity,
  },
  {
    key: "co2",
    statusType: "co2",
    icon: <MdCo2 className="summary-icon co2" />,
    labelKey: "allEnvironments.co2",
    getValue: (env) => `${env.co2} ppm`,
    getRaw: (env) => env.co2,
  },
  {
    key: "noise",
    statusType: "noise",
    icon: <HiSpeakerWave className="summary-icon noise" />,
    labelKey: "dashboard.noise",
    getValue: (env) => `${env.noise} dB`,
    getRaw: (env) => env.noise,
  },
];

export default METRIC_DEFINITIONS;
