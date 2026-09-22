import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { IoClose } from 'react-icons/io5';
import './EditModal.css';

const DATE_FORMATS = [
  { label: 'DD/MM/YYYY', example: (d) => `${d.dd}/${d.mm}/${d.yyyy}` },
  { label: 'MM/DD/YYYY', example: (d) => `${d.mm}/${d.dd}/${d.yyyy}` },
  { label: 'YYYY-MM-DD', example: (d) => `${d.yyyy}-${d.mm}-${d.dd}` },
  { label: 'DD-MM-YYYY', example: (d) => `${d.dd}-${d.mm}-${d.yyyy}` },
  { label: 'DD.MM.YYYY', example: (d) => `${d.dd}.${d.mm}.${d.yyyy}` },
  { label: 'YYYY/MM/DD', example: (d) => `${d.yyyy}/${d.mm}/${d.dd}` },
];

function getTodayParts() {
  const now = new Date();
  const dd = String(now.getDate()).padStart(2, '0');
  const mm = String(now.getMonth() + 1).padStart(2, '0');
  const yyyy = now.getFullYear();
  return { dd, mm, yyyy };
}

function EditModal({ field, value, onSave, onClose }) {
  const { t } = useTranslation();
  const isDateFormat = field === 'dateFormat';
  const [newValue, setNewValue] = useState(value);
  const today = getTodayParts();

  const handleSave = () => {
    onSave(field, newValue);
    onClose();
  };

  return (
    <div className="edit-modal-overlay" onClick={onClose}>
      <div className="edit-modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="edit-modal-close" onClick={onClose} aria-label="Cerrar">
          <IoClose />
        </button>

        <h2>
          {isDateFormat ? t('editModal.dateFormatTitle') : `${t('editModal.updateTitle')} ${field}`}
        </h2>

        {isDateFormat ? (
          <>
            <p className="modal-subtitle">{t('editModal.dateFormatSubtitle')}</p>
            <div className="format-chips">
              {DATE_FORMATS.map(({ label, example }) => (
                <button
                  key={label}
                  className={`format-chip ${newValue === label ? 'format-chip--active' : ''}`}
                  onClick={() => setNewValue(label)}
                >
                  <span className="chip-label">{label}</span>
                  <span className="chip-example">{example(today)}</span>
                </button>
              ))}
            </div>
            <div className="format-preview">
              <span className="preview-label">{t('editModal.preview')}</span>
              <span className="preview-value">
                {DATE_FORMATS.find((f) => f.label === newValue)?.example(today) ?? newValue}
              </span>
            </div>
          </>
        ) : (
          <input
            type="text"
            aria-label={field || 'Valor'}
            value={newValue}
            onChange={(e) => setNewValue(e.target.value)}
            className="modal-input"
          />
        )}

        <div className="modal-buttons">
          <button className="btn-cancel" onClick={onClose}>
            {t('editModal.cancel')}
          </button>
          <button className="btn-save" onClick={handleSave}>
            {t('editModal.save')}
          </button>
        </div>
      </div>
    </div>
  );
}

export default EditModal;
