import { Navigate, useLocation } from 'react-router-dom';
import authService from '../../../modules/auth/services/authService';

function SuperAdminRoute({ children }) {
  const location = useLocation();

  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!authService.isSuperAdmin()) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export default SuperAdminRoute;
