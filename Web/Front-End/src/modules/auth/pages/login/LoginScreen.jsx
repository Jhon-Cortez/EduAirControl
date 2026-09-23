import AuthLayout from '../../components/AuthLayout/AuthLayout'
import AuthSlider from '../../components/AuthSlider/AuthSlider'
import { useLocation, useSearchParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'

function LoginScreen() {
  const [searchParams] = useSearchParams()
  const location = useLocation()
  const { t } = useTranslation()
  const initialRegister = searchParams.get('panel') === 'register'
  const passwordReset = location.state?.passwordReset

  return (
    <AuthLayout className="auth-login-background">
      {passwordReset && (
        <p className="error-text" role="status" style={{ textAlign: 'center', marginBottom: 12 }}>
          ✓ {t('changePassword.success', 'Contraseña actualizada. Inicia sesión.')}
        </p>
      )}
      <AuthSlider initialRegister={initialRegister} />
    </AuthLayout>
  )
}

export default LoginScreen
