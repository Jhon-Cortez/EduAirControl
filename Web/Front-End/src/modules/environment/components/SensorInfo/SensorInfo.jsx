import './SensorInfo.css';

function SensorInfo({ icon, title, value, description }) {
  return (
    <div className="sensor-card">
      <div className="sensor-icon">{icon}</div>

      <div className="sensor-content">
        <h4>{title}</h4>

        <span className="sensor-value">{value}</span>

        <p>{description}</p>
      </div>
    </div>
  );
}

export default SensorInfo;
