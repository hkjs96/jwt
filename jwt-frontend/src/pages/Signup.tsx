import React from 'react';
import AuthForm from '../components/AuthForm';


const Signup: React.FC = () => (
  <div>
    <h2>Signup</h2>
    <AuthForm mode="signup" />
  </div>
);

export default Signup;