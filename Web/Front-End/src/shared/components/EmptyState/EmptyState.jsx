import './EmptyState.css';

function EmptyState({ icon, title, description, action }) {
  return (
    <div className="ds-empty-state">
      {icon && <div className="ds-empty-state__icon">{icon}</div>}
      {title && <h3 className="ds-empty-state__title">{title}</h3>}
      {description && <p className="ds-empty-state__description">{description}</p>}
      {action && <div className="ds-empty-state__action">{action}</div>}
    </div>
  );
}

export default EmptyState;
