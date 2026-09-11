import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FaArrowLeft } from 'react-icons/fa';
import LoginForm from '../loginForm/LoginForm';
import SignUpForm from '../signUpForm/SignUpForm';
import SocialLogin from '../SocialLogin/SocialLogin';
import { Divider } from '../../../../shared/components';
import './AuthSlider.css';

function AuthSlider({ initialRegister = false }) {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [isRegister, setIsRegister] = useState(initialRegister);

  return (
    <div className={`auth-slider ${isRegister ? 'right-panel-active' : ''}`}>
      <div className="auth-slider-forms">
        <section
          className="auth-slider-form auth-slider-login"
          aria-hidden={isRegister}
          inert={isRegister}
        >
          <button
            className="auth-slider-back"
            type="button"
            onClick={() => navigate('/landing')}
            aria-label={t('common.back', 'Volver')}
          >
            <FaArrowLeft />
          </button>
          <div className="auth-slider-heading">
            <h1>{t('login.title')}</h1>
            <p>{t('login.subtitle', 'Bienvenido de nuevo a EduAirControl')}</p>
          </div>
          <LoginForm />
          <Divider text={t('login.or')} className="divider-clean" />
          <SocialLogin />
        </section>

        <section
          className="auth-slider-form auth-slider-register"
          aria-hidden={!isRegister}
          inert={!isRegister}
        >
          <div className="auth-slider-heading">
            <h1>{t('signup.title')}</h1>
            <p>{t('signup.subtitle', 'Únete a la red de monitoreo inteligente')}</p>
          </div>
          <SignUpForm />
        </section>
      </div>

      <aside className="auth-slider-overlay" aria-live="polite">
        <div className="auth-slider-overlay-bg" aria-hidden="true" />
        <div className="auth-slider-overlay-panel auth-slider-overlay-login">
          <h2>{t('signup.sliderWelcome', 'Únete a EduAirControl')}</h2>
          <p>
            {t('signup.sliderPrompt', 'Crea tu cuenta para comenzar a monitorear tus espacios.')}
          </p>
          <button type="button" className="auth-slider-button" onClick={() => setIsRegister(true)}>
            {t('login.signUpBtn')}
          </button>
        </div>
        <div className="auth-slider-overlay-panel auth-slider-overlay-register">
          <h2>{t('login.sliderWelcome', 'Qué bueno verte de nuevo')}</h2>
          <p>
            {t('login.sliderPrompt', 'Inicia sesión para continuar con tu monitoreo ambiental.')}
          </p>
          <button type="button" className="auth-slider-button" onClick={() => setIsRegister(false)}>
            {t('login.title')}
          </button>
        </div>
      </aside>

      <div className="auth-slider-mobile-switch">
        <button type="button" onClick={() => setIsRegister(!isRegister)}>
          {isRegister ? t('login.title') : t('login.signUpBtn')}
        </button>
      </div>
    </div>
  );
}

export default AuthSlider;
