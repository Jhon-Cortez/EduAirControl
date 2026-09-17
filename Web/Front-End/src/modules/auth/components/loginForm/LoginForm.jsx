import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema } from '../../schemas/loginSchema';
import authService from '../../services/authService';
import { FaEnvelope, FaLock, FaBuilding } from 'react-icons/fa';
import '../../pages/login/Login.css';

function LoginForm() {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(loginSchema),
  });

  const [apiError, setApiError] = useState('');

  const onSubmit = async (data) => {
    setApiError('');
    try {
      await authService.login(data.email, data.password);
      navigate('/dashboard');
    } catch (err) {
      setApiError(err.message || t('login.error', 'Error al iniciar sesión'));
    }
  };

  return (
    <form className="login-form-modern" onSubmit={handleSubmit(onSubmit)}>
      {/* Campo de Empresa*/}
      <div className="input-group-modern">
        <label htmlFor="companyCode">{t('login.companyCode', 'Código de Empresa')}</label>
        <div className="input-wrapper">
          <FaBuilding className="input-icon" />
          <input
            {...register('companyCode')}
            type="text"
            id="companyCode"
            placeholder={t('login.placeholderCompany', 'Ej: EDU-2024')}
            className={errors.companyCode ? 'input-error shake' : ''}
          />
        </div>
        {errors.companyCode && <p className="error-text">⚠ {t(errors.companyCode.message)}</p>}
      </div>

      <div className="input-group-modern">
        <label htmlFor="email">{t('login.email')}</label>
        <div className="input-wrapper">
          <FaEnvelope className="input-icon" />
          <input
            {...register('email')}
            type="email"
            id="email"
            placeholder={t('login.placeholderEmail')}
            className={errors.email ? 'input-error shake' : ''}
          />
        </div>
        {errors.email && <p className="error-text">⚠ {t(errors.email.message)}</p>}
      </div>

      <div className="input-group-modern">
        <label htmlFor="password">{t('login.password')}</label>
        <div className="input-wrapper">
          <FaLock className="input-icon" />
          <input
            {...register('password')}
            type="password"
            id="password"
            placeholder={t('login.placeholderPassword')}
            className={errors.password ? 'input-error shake' : ''}
          />
        </div>
        {errors.password && <p className="error-text">⚠ {t(errors.password.message)}</p>}
      </div>

      <div className="login-options-modern">
        <label className="custom-checkbox-modern">
          <input type="checkbox" {...register('rememberMe')} />
          <span className="checkmark"></span>
          {t('login.rememberMe')}
        </label>
        <button
          type="button"
          className="forgot-password-link"
          onClick={() => navigate('/forgot-password')}
        >
          {t('login.forgotPassword')}
        </button>
      </div>

      {apiError && <p className="error-text">⚠ {apiError}</p>}

      <button type="submit" className="btn-login-premium" disabled={isSubmitting}>
        {isSubmitting ? '...' : t('login.title')}
      </button>

      {isSubmitting && <p className="loading-text">Validando credenciales...</p>}
    </form>
  );
}

export default LoginForm;
