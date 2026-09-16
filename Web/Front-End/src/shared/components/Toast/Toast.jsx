import { createContext, useContext, useState, useCallback } from 'react';
import { IoCheckmarkCircle, IoAlertCircle, IoInformationCircle, IoClose } from 'react-icons/io5';
import './Toast.css';

const ToastContext = createContext(null);

const ICONS = {
  success: IoCheckmarkCircle,
  error: IoAlertCircle,
  info: IoInformationCircle,
};

let toastId = 0;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'info', duration = 4000) => {
    const id = ++toastId;
    setToasts((prev) => [...prev, { id, message, type }]);

    if (duration > 0) {
      setTimeout(() => {
        setToasts((prev) => prev.filter((t) => t.id !== id));
      }, duration);
    }

    return id;
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const toast = {
    success: (msg, dur) => addToast(msg, 'success', dur),
    error: (msg, dur) => addToast(msg, 'error', dur),
    info: (msg, dur) => addToast(msg, 'info', dur),
  };

  return (
    <ToastContext.Provider value={toast}>
      {children}

      <div className="ds-toast-container" aria-live="polite" aria-atomic="true">
        {toasts.map(({ id, message, type }) => {
          const Icon = ICONS[type];
          return (
            <div key={id} className={`ds-toast ds-toast--${type}`} role="alert">
              <Icon className="ds-toast__icon" />
              <span className="ds-toast__message">{message}</span>
              <button
                className="ds-toast__close"
                onClick={() => removeToast(id)}
                aria-label="Cerrar"
              >
                <IoClose />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within a ToastProvider');
  return context;
}
