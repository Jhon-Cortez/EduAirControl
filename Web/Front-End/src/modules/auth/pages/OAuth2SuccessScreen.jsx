import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import authService from '../services/authService';

function OAuth2SuccessScreen() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get('token');

    if (token) {
      authService.setSession(token);
      navigate('/dashboard', { replace: true });
      return;
    }

    navigate('/', { replace: true });
  }, [navigate, searchParams]);

  return null;
}

export default OAuth2SuccessScreen;
