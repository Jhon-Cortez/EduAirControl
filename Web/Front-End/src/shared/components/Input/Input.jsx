import { forwardRef } from 'react';

const Input = forwardRef(({ label, type, placeholder, ...props }, ref) => {
  return (
    <div className="input-group">
      <label>{label}</label>
      <input ref={ref} type={type} placeholder={placeholder} {...props} />
    </div>
  );
});

export default Input;
