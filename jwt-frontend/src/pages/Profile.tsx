import React, { useEffect, useState } from 'react';
import API from '../api/axios';
import type { UserResponse } from '../types';
import { useNavigate } from 'react-router-dom';

const Profile: React.FC = () => {
  const [user, setUser] = useState<UserResponse | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    API.get<{ data: UserResponse }>('/api/users/me')
      .then(res => setUser(res.data.data))
      .catch(() => navigate('/login'));
  }, []);

  const handleLogout = () => {
    API.post('/api/auth/logout', null, { params: { email: user?.email } })
      .then(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        navigate('/login');
      });
  };

  if (!user) return <div>Loading...</div>;

  return (
    <div>
      <h2>Profile</h2>
      <p>Email: {user.email}</p>
      <p>Roles: {user.roles.join(', ')}</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default Profile;