import './Spinner.css';

function Spinner({ size = 'md', label = 'Cargando...' }) {
  return (
    <div className="ds-spinner-wrapper" role="status" aria-label={label}>
      <div className={`ds-spinner ds-spinner--${size}`} />
      <span className="ds-spinner__sr-only">{label}</span>
    </div>
  );
}

export default Spinner;
