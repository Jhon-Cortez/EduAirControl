import { Navigate, useLocation } from 'react-router-dom';
import authService from '../../../modules/auth/services/authService';

function AdminRoute({ children }) {
  const location = useLocation();

  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!authService.isAdmin()) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export default AdminRoute;
