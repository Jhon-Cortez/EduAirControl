import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FaUser } from 'react-icons/fa';
import AuthLayout from '../../components/AuthLayout/AuthLayout';
import { BackButton } from '../../../../shared/components';
import './FacebookSignUp.css';

const MOCK_ACCOUNTS = [
  { name: 'Account 1', email: 'account1@facebook.com' },
  { name: 'Account 2', email: 'account2@facebook.com' },
  { name: 'Account 3', email: 'account3@facebook.com' },
];

function FacebookSignUpScreen() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <AuthLayout>
      <BackButton onClick={() => navigate(-1)} />
      <div className="facebook-signup-content">
        <div className="facebook-logo-wrapper">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 36 36"
            className="facebook-logo"
            aria-label="Facebook"
          >
            <path
              d="M36 18C36 8.059 27.941 0 18 0S0 8.059 0 18c0 8.984 6.584 16.425 15.188 17.787V23.203h-4.57V18h4.57v-3.961c0-4.511 2.688-7.004 6.797-7.004 1.97 0 4.031.352 4.031.352v4.43h-2.27c-2.238 0-2.934 1.389-2.934 2.813V18h4.992l-.798 5.203h-4.194v12.584C29.416 34.425 36 26.984 36 18z"
              fill="#1877F2"
            />
            <path
              d="M25.008 23.203L25.806 18h-4.992v-3.37c0-1.424.696-2.813 2.934-2.813h2.27v-4.43s-2.062-.352-4.031-.352c-4.109 0-6.797 2.493-6.797 7.004V18h-4.57v5.203h4.57v12.584a18.145 18.145 0 005.624 0V23.203h4.194z"
              fill="#fff"
            />
          </svg>
        </div>

        <h3 className="facebook-choose-title">{t('facebook.chooseAccount')}</h3>

        <div className="facebook-account-list">
          {MOCK_ACCOUNTS.map((account, index) => (
            <div key={index} className="facebook-account-item" onClick={() => navigate('/')}>
              <div className="facebook-account-avatar">
                <FaUser />
              </div>
              <div className="facebook-account-info">
                <span className="facebook-account-name">{account.name}</span>
                <span className="facebook-account-email">{account.email}</span>
              </div>
            </div>
          ))}
        </div>

        <a href="#" className="facebook-other-account" onClick={(e) => e.preventDefault()}>
          {t('facebook.otherAccount')}
        </a>
      </div>
    </AuthLayout>
  );
}

export default FacebookSignUpScreen;
