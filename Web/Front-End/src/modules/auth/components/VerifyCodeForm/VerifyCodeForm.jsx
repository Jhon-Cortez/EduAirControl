import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Button } from '../../../../shared/components';
import authService from '../../services/authService';

const CODE_LENGTH = 6;

function VerifyCodeForm() {
  const [code, setCode] = useState(Array(CODE_LENGTH).fill(''));
  const [apiError, setApiError] = useState('');
  const [infoMessage, setInfoMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleChange = (index, value) => {
    if (!/^\d?$/.test(value)) return;
    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);
    setApiError('');
    if (value && index < CODE_LENGTH - 1) {
      document.getElementById(`code-${index + 1}`)?.focus();
    }
  };

  const handleKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !code[index] && index > 0) {
      document.getElementById(`code-${index - 1}`)?.focus();
    }
  };

  const joinedCode = code.join('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    if (joinedCode.length !== CODE_LENGTH) {
      setApiError(t('verifyCode.incomplete'));
      return;
    }
    const email = authService.getResetEmail();
    if (!email) {
      navigate('/forgot-password');
      return;
    }
    setIsSubmitting(true);
    try {
      await authService.verifyCode(email, joinedCode);
      navigate('/change-password', { state: { email, code: joinedCode } });
    } catch (err) {
      setApiError(err.message || t('verifyCode.error'));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleResend = async (e) => {
    e.preventDefault();
    setApiError('');
    setInfoMessage('');
    const email = authService.getResetEmail();
    if (!email) {
      navigate('/forgot-password');
      return;
    }
    setIsResending(true);
    try {
      await authService.resendCode(email);
      setCode(Array(CODE_LENGTH).fill(''));
      setInfoMessage(t('verifyCode.resent'));
    } catch (err) {
      setApiError(err.message || t('verifyCode.resendError'));
    } finally {
      setIsResending(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="code-inputs">
        {code.map((digit, index) => (
          <input
            key={index}
            id={`code-${index}`}
            type="text"
            inputMode="numeric"
            maxLength="1"
            aria-label={`Código de verificación dígito ${index + 1}`}
            value={digit}
            onChange={(e) => handleChange(index, e.target.value)}
            onKeyDown={(e) => handleKeyDown(index, e)}
            className="code-input"
          />
        ))}
      </div>
      {apiError && <p className="error-text">⚠ {apiError}</p>}
      {infoMessage && <p className="error-text">✓ {infoMessage}</p>}
      <a href="#" className="resend-code" onClick={handleResend}>
        {isResending ? '...' : t('verifyCode.resend')}
      </a>
      <Button className="btn-login" type="submit" loading={isSubmitting}>
        {t('verifyCode.verifyBtn')}
      </Button>
    </form>
  );
}

export default VerifyCodeForm;
