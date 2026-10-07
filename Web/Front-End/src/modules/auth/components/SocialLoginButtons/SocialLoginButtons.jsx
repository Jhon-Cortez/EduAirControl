import { useTranslation } from 'react-i18next';
import { FcGoogle } from 'react-icons/fc';
import { FaFacebook } from 'react-icons/fa';
import authService from '../../services/authService';

function SocialLoginButtons() {
  const { t } = useTranslation();

  return (
    <>
      <div className="social-text">
        <span>{t('login.socialDivider', 'o continúa con')}</span>
      </div>
      <div className="social-buttons">
        <button
          type="button"
          className="btn-google"
          onClick={() => authService.loginWithSocial('google')}
        >
          <FcGoogle size={20} />
          <span>{t('login.googleBtn', 'Google')}</span>
        </button>
        <button
          type="button"
          className="btn-facebook"
          onClick={() => authService.loginWithSocial('facebook')}
        >
          <FaFacebook size={20} />
          <span>{t('login.facebookBtn', 'Facebook')}</span>
        </button>
      </div>
    </>
  );
}

export default SocialLoginButtons;
