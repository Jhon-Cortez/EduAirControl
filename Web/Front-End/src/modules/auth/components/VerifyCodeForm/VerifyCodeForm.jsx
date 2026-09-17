import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Button } from '../../../../shared/components';

function VerifyCodeForm() {
  const [code, setCode] = useState(['', '', '', '', '']);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleChange = (index, value) => {
    if (value.length > 1) return;
    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);
    if (value && index < 4) document.getElementById(`code-${index + 1}`).focus();
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    navigate('/change-password');
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="code-inputs">
        {code.map((digit, index) => (
          <input
            key={index}
            id={`code-${index}`}
            type="text"
            maxLength="1"
            aria-label={`Código de verificación dígito ${index + 1}`}
            value={digit}
            onChange={(e) => handleChange(index, e.target.value)}
            className="code-input"
          />
        ))}
      </div>
      <a href="#" className="resend-code" onClick={(e) => e.preventDefault()}>
        {t('verifyCode.resend')}
      </a>
      <Button text={t('verifyCode.verifyBtn')} className="btn-login" type="submit" />
    </form>
  );
}

export default VerifyCodeForm;
