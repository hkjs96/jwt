import React from 'react';
import AuthForm from '../components/AuthForm';

const Login: React.FC = () => (
  <div>
    <h2>Login</h2>
    <AuthForm mode="login" />
  </div>
);

export default Login;