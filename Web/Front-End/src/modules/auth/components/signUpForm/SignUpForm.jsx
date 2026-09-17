import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FaUser, FaEnvelope, FaLock, FaBuilding } from 'react-icons/fa';
import { HiOutlineDocumentText, HiCheckCircle } from 'react-icons/hi2';
import { ChevronDown } from 'lucide-react';
import SocialLogin from '../SocialLogin/SocialLogin';
import authService from '../../services/authService';
import { Divider } from '../../../../shared/components';
import '../../pages/signUp/SignUp.css';

function SignUpForm() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const localizedTerms = t('terms', { returnObjects: true });
  const nestedTerms = t('signup.terms', { returnObjects: true });
  const fullTerms = Array.isArray(localizedTerms?.sections) ? localizedTerms : nestedTerms;
  const termSections = Array.isArray(fullTerms?.sections) ? fullTerms.sections : [];
  const [hasReadFullTerms] = useState(
    () => sessionStorage.getItem('eduaircontrol-terms-read') === 'true'
  );
  const [formData, setFormData] = useState({
    companyCode: '',
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    acceptTerms: false,
  });
  const [showTerms, setShowTerms] = useState(false);
  const [openTerm, setOpenTerm] = useState(-1);
  const passwordRequirements = {
    minLength: formData.password.length >= 8,
    hasUppercase: /[A-Z]/.test(formData.password),
    hasLowercase: /[a-z]/.test(formData.password),
    hasNumber: /[0-9]/.test(formData.password),
    hasSpecialChar: /[^A-Za-z0-9]/.test(formData.password),
  };
  const passwordStrength = Object.values(passwordRequirements).filter(Boolean).length;

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const [apiError, setApiError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    if (!hasReadFullTerms) {
      setShowTerms(true);
      return;
    }
    if (!formData.acceptTerms) {
      alert(t('signup.errorTerms', 'Debes aceptar los términos'));
      return;
    }
    if (formData.password !== formData.confirmPassword) {
      alert(t('signup.errorPassword', 'Las contraseñas no coinciden'));
      return;
    }
    try {
      await authService.register(formData.name, formData.email, formData.password);
      navigate('/dashboard');
    } catch (err) {
      setApiError(err.message || t('signup.error', 'Error al registrarse'));
    }
  };

  return (
    <>
      <form className="signup-form-modern" onSubmit={handleSubmit}>
        <div className="input-group-modern">
          <label htmlFor="signup-company-code">
            {t('signup.companyCode', 'Código de Empresa')}
          </label>
          <div className="input-wrapper">
            <FaBuilding className="input-icon" />
            <input
              id="signup-company-code"
              type="text"
              name="companyCode"
              placeholder={t('signup.placeholderCompany', 'Ej: EDU-2024')}
              value={formData.companyCode}
              onChange={handleChange}
              required
            />
          </div>
        </div>
        <div className="input-group-modern">
          <label htmlFor="signup-name">{t('signup.fullName', 'Nombre completo')}</label>
          <div className="input-wrapper">
            <FaUser className="input-icon" />
            <input
              id="signup-name"
              type="text"
              name="name"
              placeholder={t('signup.placeholderName')}
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
        </div>
        <div className="input-group-modern">
          <label htmlFor="signup-email">{t('signup.email')}</label>
          <div className="input-wrapper">
            <FaEnvelope className="input-icon" />
            <input
              id="signup-email"
              type="email"
              name="email"
              placeholder={t('signup.placeholderEmail')}
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
        </div>
        <div className="form-row-modern">
          <div className="input-group-modern">
            <label htmlFor="signup-password">{t('signup.password')}</label>
            <div className="input-wrapper">
              <FaLock className="input-icon" />
              <input
                id="signup-password"
                type="password"
                name="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>
            {formData.password && (
              <div className="password-strength" aria-live="polite">
                <div className="password-strength-bar" aria-hidden="true">
                  <div className={`password-strength-fill strength-${passwordStrength}`} />
                </div>
                <span>
                  {passwordStrength <= 2 && t('signup.passwordStrength.weak')}
                  {passwordStrength === 3 && t('signup.passwordStrength.medium')}
                  {passwordStrength === 4 && t('signup.passwordStrength.good')}
                  {passwordStrength === 5 && t('signup.passwordStrength.strong')}
                </span>
              </div>
            )}
          </div>
          <div className="input-group-modern">
            <label htmlFor="signup-confirm-password">
              {t('signup.confirmPassword', 'Confirmar contraseña')}
            </label>
            <div className="input-wrapper">
              <FaLock className="input-icon" />
              <input
                id="signup-confirm-password"
                type="password"
                name="confirmPassword"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>
          </div>
        </div>
        <div className="terms-container-modern">
          <label className="custom-toggle-modern" htmlFor="signup-accept-terms">
            <input
              id="signup-accept-terms"
              type="checkbox"
              name="acceptTerms"
              checked={formData.acceptTerms}
              onChange={handleChange}
              disabled={!hasReadFullTerms}
            />
            <span className="slider-modern"></span>
          </label>
          <p className="terms-text-modern">
            {t('signup.termsModal.acceptPrefix')}{' '}
            <button type="button" className="terms-link-btn" onClick={() => setShowTerms(true)}>
              {t('signup.termsModal.link')}
            </button>
          </p>
          {!hasReadFullTerms && (
            <p className="terms-read-required">{t('signup.termsModal.readRequired')}</p>
          )}
        </div>
        {apiError && <p className="error-text">⚠ {apiError}</p>}
        <button type="submit" className="btn-signup-premium">
          {t('signup.signUpBtn')}
        </button>
      </form>
      <Divider text="OR" className="divider-clean" />
      <SocialLogin />

      {showTerms && (
        <div className="modal-overlay-modern" onClick={() => setShowTerms(false)}>
          <div className="modal-content-modern" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div className="modal-icon">
                <HiOutlineDocumentText />
              </div>
              <h2>{t('signup.termsModal.title')}</h2>
              <p>{t('signup.termsModal.subtitle')}</p>
            </div>
            <div className="terms-scroll-area" key={showTerms}>
              <p>{fullTerms.intro || t('signup.termsModal.intro')}</p>
              <div className="terms-accordion">
                {termSections.map((section, index) => {
                  const isOpen = openTerm === index;
                  return (
                    <section
                      className={`term-module ${isOpen ? 'is-open' : ''}`}
                      key={section.title}
                    >
                      <button
                        type="button"
                        className="term-module-trigger"
                        aria-expanded={isOpen}
                        onClick={() => setOpenTerm(isOpen ? -1 : index)}
                      >
                        <span>{section.title}</span>
                        <ChevronDown size={22} aria-hidden="true" />
                      </button>
                      {isOpen && (
                        <div className="term-module-content">
                          {section.paragraphs?.map((paragraph) => (
                            <p key={paragraph}>{paragraph}</p>
                          ))}
                          {section.list && (
                            <ul>
                              {section.list.map((item) => (
                                <li key={item}>
                                  <HiCheckCircle />
                                  <span>{item}</span>
                                </li>
                              ))}
                            </ul>
                          )}
                          {section.note && <p className="term-module-note">{section.note}</p>}
                        </div>
                      )}
                    </section>
                  );
                })}
              </div>
              <p className="terms-full-document">
                {t('signup.termsModal.fullDocumentPrefix')}{' '}
                <button type="button" className="terms-link-btn" onClick={() => navigate('/terms')}>
                  {t('signup.termsModal.fullDocumentLink')}
                </button>
              </p>
            </div>
            <button className="btn-close-modal" onClick={() => setShowTerms(false)}>
              {t('common.close')}
            </button>
          </div>
        </div>
      )}
    </>
  );
}

export default SignUpForm;
