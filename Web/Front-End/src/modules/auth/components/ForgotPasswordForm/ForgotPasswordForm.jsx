import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Input } from '../../../../shared/components';
import authService from '../../services/authService';

function ForgotPasswordForm() {
  const [email, setEmail] = useState('');
  const [apiError, setApiError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    setIsSubmitting(true);
    try {
      await authService.forgotPassword(email.trim());
      authService.setResetEmail(email.trim());
      navigate('/verify-code');
    } catch (err) {
      setApiError(err.message || t('forgotPassword.error'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label={t('forgotPassword.emailLabel')}
        type="email"
        placeholder={t('login.placeholder')}
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      {apiError && <p className="error-text">⚠ {apiError}</p>}
      <button type="submit" className="btn-send-code" disabled={isSubmitting}>
        {isSubmitting ? '...' : t('forgotPassword.sendBtn')}
      </button>
      <p className="try-another">{t('forgotPassword.tryAnother')}</p>
    </form>
  );
}

export default ForgotPasswordForm;
