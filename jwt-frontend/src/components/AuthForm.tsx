import React, { useState } from 'react';
import API from '../api/axios';
import type { SignupRequest, LoginRequest, TokenResponse } from '../types';
import { useNavigate, useLocation } from 'react-router-dom';

interface AuthFormProps {
  mode: 'login' | 'signup';
  onSuccess?: () => void;
}

const AuthForm: React.FC<AuthFormProps> = ({ mode, onSuccess }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as any)?.from?.pathname || '/profile';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      if (mode === 'signup') {
        await API.post('/api/auth/signup', { email, password } as SignupRequest);
        navigate('/login');
      } else {
        const { data } = await API.post('/api/auth/login', { email, password } as LoginRequest);
        const tokens = (data.data as TokenResponse);
        localStorage.setItem('accessToken', tokens.accessToken);
        localStorage.setItem('refreshToken', tokens.refreshToken);
        navigate(from);
      }
      onSuccess && onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error occurred');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div style={{ color: 'red' }}>{error}</div>}
      <div>
        <label>
          Email:
          <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />
        </label>
      </div>
      <div>
        <label>
          Password:
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
        </label>
      </div>
      <button type="submit">{mode === 'login' ? 'Login' : 'Signup'}</button>
    </form>
  );
};

export default AuthForm;