import { forwardRef } from 'react';
import './Input.css';

const Input = forwardRef(function Input(
  { label, type = 'text', placeholder, error, icon, disabled, className = '', ...props },
  ref
) {
  const errorId = error ? `${props.name || 'input'}-error` : undefined;

  return (
    <div className={`ds-field ${disabled ? 'ds-field--disabled' : ''} ${className}`}>
      {label && (
        <label className="ds-field__label" htmlFor={props.name}>
          {label}
        </label>
      )}
      <div className={`ds-field__wrapper ${error ? 'ds-field__wrapper--error' : ''}`}>
        {icon && <span className="ds-field__icon">{icon}</span>}
        <input
          ref={ref}
          id={props.name}
          type={type}
          placeholder={placeholder}
          disabled={disabled}
          className="ds-field__input"
          aria-invalid={error ? 'true' : undefined}
          aria-describedby={errorId}
          {...props}
        />
      </div>
      {error && (
        <p className="ds-field__error" id={errorId} role="alert">
          {error}
        </p>
      )}
    </div>
  );
});

export default Input;
