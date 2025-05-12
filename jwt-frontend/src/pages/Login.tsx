import React from 'react';
import AuthForm from '../components/AuthForm';
import SocialLoginButton from '../components/SocialLoginButton';

const Login: React.FC = () => (
  <div>
    <h2>Login</h2>
    <AuthForm mode="login" />
    <SocialLoginButton />
  </div>
);

export default Login;