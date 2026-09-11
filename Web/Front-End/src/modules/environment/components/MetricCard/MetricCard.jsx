import './MetricCard.css';

function MetricCard({ icon, label, value, status }) {
  return (
    <div className="metric-card">
      <div className="metric-icon">{icon}</div>

      <div className="metric-info">
        <span className="metric-label">{label}</span>

        <span className="metric-value">{value}</span>

        <small className="metric-status" style={{ color: status.color }}>
          {status.text}
        </small>
      </div>
    </div>
  );
}

export default MetricCard;
