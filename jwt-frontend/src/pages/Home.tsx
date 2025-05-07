import React from 'react';
import { Link } from 'react-router-dom';

const Home: React.FC = () => (
  <div>
    <h1>Welcome to JWT Auth App</h1>
    <nav>
      <Link to="/login">Login</Link> |{' '}
      <Link to="/signup">Signup</Link> |{' '}
      <Link to="/profile">Profile</Link>
    </nav>
  </div>
);

export default Home;