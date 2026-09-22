import { Navigate, Routes, Route } from 'react-router-dom';

import ProtectedRoute from './shared/components/routes/ProtectedRoute';
import GuestRoute from './shared/components/routes/GuestRoute';

// ======================
// AUTH
// ======================
import LoginScreen from './modules/auth/pages/login/LoginScreen';
import ForgotPasswordScreen from './modules/auth/pages/forgotPassword/ForgotPasswordScreen';
import VerifyCodeScreen from './modules/auth/pages/verifyCode/VerifyCodeScreen';
import ChangePasswordScreen from './modules/auth/pages/ChangePassword/ChangePasswordScreen';
import TermsScreen from './modules/auth/pages/terms/TermsScreen';
import GoogleSignUpScreen from './modules/auth/pages/GoogleSignUp/GoogleSignUpScreen';
import FacebookSignUpScreen from './modules/auth/pages/FacebookSignUp/FacebookSignUpScreen';
import OAuth2SuccessScreen from './modules/auth/pages/OAuth2SuccessScreen';

// ======================
// LANDING
// ======================
import Landing from './modules/landing/pages/Landing';
import GuideScreen from './modules/landing/pages/GuideScreen';

// ======================
// DASHBOARD DE ANÁLISIS
// ======================
import DashboardScreen from './modules/dashboard/pages/DashboardScreen';

// ======================
// ENVIRONMENTS
// ======================
import AllEnvironmentsScreen from './modules/environment/pages/AllEnvironmentsScreen';
import EnvironmentDetailScreen from './modules/environment/pages/EnvironmentDetailScreen';
import EnvironmentManagement from './modules/environment/pages/EnvironmentManagement';

// ======================
// PROFILE
// ======================
import ProfileScreen from './modules/profile/pages/ProfileScreen';

// ======================
// SETTINGS
// ======================
import SettingsScreen from './modules/settings/pages/SettingsScreen';

// ======================
// FAVORITES
// ======================
import FavoritesScreen from './modules/favorites/FavoritesScreen';

function App() {
  return (
    <Routes>
      {/* ---------- Landing ---------- */}
      <Route path="/landing" element={<Landing />} />
      <Route path="/guide" element={<GuideScreen />} />

      {/* ---------- Authentication ---------- */}
      <Route path="/" element={<Navigate to="/landing" replace />} />
      <Route
        path="/login"
        element={
          <GuestRoute>
            <LoginScreen />
          </GuestRoute>
        }
      />
      <Route path="/forgot-password" element={<ForgotPasswordScreen />} />
      <Route path="/verify-code" element={<VerifyCodeScreen />} />
      <Route path="/change-password" element={<ChangePasswordScreen />} />
      <Route path="/terms" element={<TermsScreen />} />
      <Route path="/google-signup" element={<GoogleSignUpScreen />} />
      <Route path="/facebook-signup" element={<FacebookSignUpScreen />} />
      <Route path="/oauth2/success" element={<OAuth2SuccessScreen />} />

      {/* ---------- Dashboard de análisis ---------- */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardScreen />
          </ProtectedRoute>
        }
      />
      <Route path="/ranking" element={<Navigate to="/dashboard" replace />} />

      {/* ---------- Environments ---------- */}
      <Route
        path="/all-environments"
        element={
          <ProtectedRoute>
            <AllEnvironmentsScreen />
          </ProtectedRoute>
        }
      />
      <Route
        path="/environment/:id"
        element={
          <ProtectedRoute>
            <EnvironmentDetailScreen />
          </ProtectedRoute>
        }
      />
      <Route
        path="/management"
        element={
          <ProtectedRoute>
            <EnvironmentManagement />
          </ProtectedRoute>
        }
      />

      {/* ---------- User ---------- */}
      <Route
        path="/favorites"
        element={
          <ProtectedRoute>
            <FavoritesScreen />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <ProfileScreen />
          </ProtectedRoute>
        }
      />
      <Route
        path="/settings"
        element={
          <ProtectedRoute>
            <SettingsScreen />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;
