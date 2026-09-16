import { useEffect, useRef, useCallback } from 'react';
import { IoClose } from 'react-icons/io5';
import './Modal.css';

function Modal({ isOpen, onClose, title, children, size = 'md' }) {
  const overlayRef = useRef(null);
  const contentRef = useRef(null);
  const previousFocusRef = useRef(null);

  const handleKeyDown = useCallback(
    (e) => {
      if (e.key === 'Escape') {
        onClose();
        return;
      }

      if (e.key === 'Tab') {
        const focusable = contentRef.current?.querySelectorAll(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        if (!focusable?.length) return;

        const first = focusable[0];
        const last = focusable[focusable.length - 1];

        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    },
    [onClose]
  );

  useEffect(() => {
    if (isOpen) {
      previousFocusRef.current = document.activeElement;
      document.body.style.overflow = 'hidden';
      contentRef.current?.focus();
    }

    return () => {
      document.body.style.overflow = '';
      previousFocusRef.current?.focus();
    };
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, handleKeyDown]);

  if (!isOpen) return null;

  const sizeClass = size === 'sm' ? 'ds-modal--sm' : size === 'lg' ? 'ds-modal--lg' : '';

  return (
    <div
      className="ds-overlay"
      ref={overlayRef}
      onClick={(e) => {
        if (e.target === overlayRef.current) onClose();
      }}
      role="dialog"
      aria-modal="true"
      aria-labelledby={title ? 'modal-title' : undefined}
    >
      <div className={`ds-modal ${sizeClass}`} ref={contentRef} tabIndex={-1}>
        <button className="ds-modal__close" onClick={onClose} aria-label="Cerrar">
          <IoClose />
        </button>

        {title && (
          <h2 id="modal-title" className="ds-modal__title">
            {title}
          </h2>
        )}

        <div className="ds-modal__body">{children}</div>
      </div>
    </div>
  );
}

export default Modal;
