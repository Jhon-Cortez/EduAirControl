import { Navigate, useLocation } from 'react-router-dom';
import authService from '../../../modules/auth/services/authService';

function ProtectedRoute({ children }) {
  const location = useLocation();

  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}

export default ProtectedRoute;
