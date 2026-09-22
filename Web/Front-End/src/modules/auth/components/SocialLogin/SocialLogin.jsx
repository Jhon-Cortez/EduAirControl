import { FaFacebookF } from 'react-icons/fa';
import { FcGoogle } from 'react-icons/fc';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function SocialLogin() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <div>
      <p className="social-text">{t('login.orWith')}</p>
      <div className="social-buttons">
        <button className="btn-facebook" onClick={() => navigate('/facebook-signup')}>
          <FaFacebookF /> Facebook
        </button>
      </div>
    </div>
  );
}

export default SocialLogin;
