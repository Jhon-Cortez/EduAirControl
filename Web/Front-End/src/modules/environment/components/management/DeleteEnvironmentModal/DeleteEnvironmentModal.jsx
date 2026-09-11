import { useTranslation } from 'react-i18next';
import { IoTrashOutline, IoWarningOutline } from 'react-icons/io5';

function DeleteEnvironmentModal({ environment, onClose, onConfirm }) {
  const { t } = useTranslation();

  const name = environment.nameKey ? t(environment.nameKey) : environment.name || '';

  return (
    <div className="add-env-overlay" onClick={onClose}>
      <div className="add-env-modal add-env-modal--delete" onClick={(e) => e.stopPropagation()}>
        <div className="add-env-modal__delete-icon">
          <IoWarningOutline size={28} />
        </div>

        <h2 className="add-env-modal__title">{t('management.deleteTitle', 'Eliminar Ambiente')}</h2>

        <p className="add-env-modal__delete-desc">
          {t('management.deleteDesc', '¿Estás seguro de que quieres eliminar')}{' '}
          <strong>{name}</strong>?{' '}
          {t('management.deleteWarning', 'Esta acción no se puede deshacer.')}
        </p>

        <div className="add-env-modal__actions">
          <button className="add-env-modal__btn-cancel" onClick={onClose}>
            {t('management.cancelBtn', 'Cancelar')}
          </button>
          <button className="add-env-modal__btn-delete" onClick={() => onConfirm(environment.id)}>
            <IoTrashOutline size={15} />
            {t('management.deleteConfirmBtn', 'Eliminar')}
          </button>
        </div>
      </div>
    </div>
  );
}

export default DeleteEnvironmentModal;
