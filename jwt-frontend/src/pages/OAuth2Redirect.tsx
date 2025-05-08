import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import API from '../api/axios';

const OAuth2Redirect: React.FC = () => {
  const navigate = useNavigate();
  const { search } = useLocation();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const params = new URLSearchParams(search);
    const code = params.get('code');
    if (code) {
      API.post('/oauth/google/login', { code })
        .then(res => {
          const { accessToken, refreshToken } = res.data.data;
          localStorage.setItem('accessToken', accessToken);
          localStorage.setItem('refreshToken', refreshToken);
          navigate('/profile');
        })
        .catch(err => {
          setError(err.response?.data?.message || 'OAuth Login failed');
        });
    } else {
      setError('Authorization code not found in URL');
    }
  }, [search]);

  if (error) return <div>Error: {error}</div>;
  return <div>Logging you in...</div>;
};

export default OAuth2Redirect;