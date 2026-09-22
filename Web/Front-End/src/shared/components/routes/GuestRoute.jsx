import { Navigate } from 'react-router-dom';
import authService from '../../../modules/auth/services/authService';

function GuestRoute({ children }) {
  if (authService.isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export default GuestRoute;
