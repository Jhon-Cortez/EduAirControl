import './Button.css';

function Button({
  children,
  onClick,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  icon,
  iconPosition = 'left',
  type = 'button',
  className = '',
  ...props
}) {
  const classes = [
    'ds-btn',
    `ds-btn--${variant}`,
    `ds-btn--${size}`,
    loading && 'ds-btn--loading',
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button
      className={classes}
      onClick={onClick}
      type={type}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <span className="ds-btn__spinner" />}
      {!loading && icon && iconPosition === 'left' && (
        <span className="ds-btn__icon">{icon}</span>
      )}
      {children && <span className="ds-btn__text">{children}</span>}
      {!loading && icon && iconPosition === 'right' && (
        <span className="ds-btn__icon">{icon}</span>
      )}
    </button>
  );
}

export default Button;
