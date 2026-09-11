import { FaFacebookF } from 'react-icons/fa';
import { FcGoogle } from 'react-icons/fc';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function SocialLogin() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  const handleGoogleLogin = () => {
    window.location.href = `${apiUrl}/oauth2/authorization/google`;
  };
  const handleFacebookLogin = () => {
    window.location.href = `${apiUrl}`;
  };

  return (
    <div>
      <p className="social-text">{t('login.orWith')}</p>
      <div className="social-buttons">
        <button className="btn-facebook" onClick={() => navigate('/facebook-signup')}>
          <FaFacebookF /> Facebook
        </button>
        <button className="btn-google" onClick={handleGoogleLogin}>
          <FcGoogle /> Google
        </button>
      </div>
    </div>
  );
}

export default SocialLogin;
