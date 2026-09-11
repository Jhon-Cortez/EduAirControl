import './ScoreCircle.css';

function ScoreCircle({ score = 0, size = 110 }) {
  const radius = 48;
  const circumference = 2 * Math.PI * radius;
  const progress = circumference - (score / 100) * circumference;

  const getColor = () => {
    if (score >= 80) return '#22c55e';
    if (score >= 60) return '#f59e0b';
    return '#ef4444';
  };

  const getLabel = () => {
    if (score >= 80) return 'Excelente';
    if (score >= 60) return 'Aceptable';
    return 'Crítico';
  };

  return (
    <div className="score-circle" style={{ width: size, height: size }}>
      <svg width={size} height={size} viewBox="0 0 120 120">
        <circle className="score-bg" cx="60" cy="60" r={radius} />

        <circle
          className="score-progress"
          cx="60"
          cy="60"
          r={radius}
          stroke={getColor()}
          strokeDasharray={circumference}
          strokeDashoffset={progress}
        />
      </svg>

      <div className="score-content">
        <h2>{score}</h2>
        <span>{getLabel()}</span>
      </div>
    </div>
  );
}

export default ScoreCircle;
