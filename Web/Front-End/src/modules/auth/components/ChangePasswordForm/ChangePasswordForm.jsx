import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Input, Button } from '../../../../shared/components';
import authService from '../../services/authService';

function ChangePasswordForm() {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [apiError, setApiError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const email = location.state?.email || authService.getResetEmail();
  const code = location.state?.code;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');

    if (!email || !code) {
      navigate('/forgot-password');
      return;
    }
    if (newPassword !== confirmPassword) {
      setApiError(t('changePassword.mismatch'));
      return;
    }
    if (newPassword.length < 8) {
      setApiError(t('errors.password_min_8'));
      return;
    }
    if (!/[A-Z]/.test(newPassword)) {
      setApiError(t('errors.uppercaseRequired:_password'));
      return;
    }

    setIsSubmitting(true);
    try {
      await authService.resetPassword(email, code, newPassword);
      authService.clearResetEmail();
      navigate('/login', { state: { passwordReset: true } });
    } catch (err) {
      setApiError(err.message || t('changePassword.error'));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label={t('changePassword.newPassword')}
        type="password"
        placeholder={t('login.placeholder')}
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        required
      />
      <Input
        label={t('changePassword.confirmPassword')}
        type="password"
        placeholder={t('login.placeholder')}
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        required
      />
      {apiError && <p className="error-text">⚠ {apiError}</p>}
      <Button
        className="btn-confirm"
        type="submit"
        loading={isSubmitting}
      >
        {t('changePassword.confirmBtn')}
      </Button>
    </form>
  );
}

export default ChangePasswordForm;
