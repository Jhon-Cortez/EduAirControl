import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { FaBuilding } from 'react-icons/fa';
import { HiOutlineBuildingOffice2 } from 'react-icons/hi2';
import authService from '../../services/authService';

function SocialOnboardingModal({ user, onComplete }) {
  const { t } = useTranslation();
  const [companyCode, setCompanyCode] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await authService.completeSocialOnboarding(user.userId, companyCode);
      onComplete();
    } catch (err) {
      setError(err.message || t('signup.onboardingError', 'Error al asignar la empresa'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay-modern">
      <div className="modal-content-modern" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div className="modal-icon">
            <HiOutlineBuildingOffice2 />
          </div>
          <h2>{t('signup.onboardingTitle', 'Último paso')}</h2>
          <p>{t('signup.onboardingSubtitle', 'Ingresa el código de tu empresa para continuar')}</p>
        </div>
        <form onSubmit={handleSubmit} className="social-onboarding-form">
          <div className="input-group-modern">
            <label htmlFor="onboarding-company">{t('signup.companyCode', 'Código de Empresa')}</label>
            <div className="input-wrapper">
              <FaBuilding className="input-icon" />
              <input
                id="onboarding-company"
                type="text"
                placeholder={t('signup.placeholderCompany', 'Ej: EDU-2024')}
                value={companyCode}
                onChange={(e) => setCompanyCode(e.target.value)}
                required
              />
            </div>
          </div>
          {error && <p className="error-text">⚠ {error}</p>}
          <button type="submit" className="btn-signup-premium" disabled={loading}>
            {loading ? '...' : t('signup.onboardingBtn', 'Continuar')}
          </button>
        </form>
      </div>
    </div>
  );
}

export default SocialOnboardingModal;
