import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Input, Button } from '../../../../shared/components';

function ChangePasswordForm() {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      alert(t('changePassword.mismatch'));
      return;
    }
    navigate('/');
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label={t('changePassword.newPassword')}
        type="password"
        placeholder={t('login.placeholder')}
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
      />
      <Input
        label={t('changePassword.confirmPassword')}
        type="password"
        placeholder={t('login.placeholder')}
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
      />
      <Button text={t('changePassword.confirmBtn')} className="btn-confirm" type="submit" />
    </form>
  );
}

export default ChangePasswordForm;
