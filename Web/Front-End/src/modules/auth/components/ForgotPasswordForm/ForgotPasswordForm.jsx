import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Input } from '../../../../shared/components';

function ForgotPasswordForm() {
  const [email, setEmail] = useState('');
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleSubmit = (e) => {
    e.preventDefault();
    navigate('/verify-code');
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label={t('forgotPassword.emailLabel')}
        type="email"
        placeholder={t('login.placeholder')}
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />
      <button type="submit" className="btn-send-code">
        {t('forgotPassword.sendBtn')}
      </button>
      <p className="try-another">{t('forgotPassword.tryAnother')}</p>
    </form>
  );
}

export default ForgotPasswordForm;
