import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FaUser } from 'react-icons/fa';
import AuthLayout from '../../components/AuthLayout/AuthLayout';
import { BackButton } from '../../../../shared/components';
import './GoogleSignUp.css';

const MOCK_ACCOUNTS = [
  { name: 'Account 1', email: 'account1@gmail.com' },
  { name: 'Account 2', email: 'account2@gmail.com' },
  { name: 'Account 3', email: 'account3@gmail.com' },
];

function GoogleSignUpScreen() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <AuthLayout>
      <BackButton onClick={() => navigate(-1)} />
      <div className="google-signup-content">
        <img
          src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/768px-Google_%22G%22_logo.svg.png"
          alt="Google"
          className="google-logo"
        />
        <h3 className="choose-title">{t('google.chooseAccount')}</h3>

        <div className="account-list">
          {MOCK_ACCOUNTS.map((account, index) => (
            <div key={index} className="account-item" onClick={() => navigate('/')}>
              <div className="account-avatar">
                <FaUser />
              </div>
              <div className="account-info">
                <span className="account-name">{account.name}</span>
                <span className="account-email">{account.email}</span>
              </div>
            </div>
          ))}
        </div>

        <a href="#" className="other-account" onClick={(e) => e.preventDefault()}>
          {t('google.otherAccount')}
        </a>
      </div>
    </AuthLayout>
  );
}

export default GoogleSignUpScreen;
