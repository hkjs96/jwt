import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

interface PrivateRouteProps { children: React.ReactElement; }

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const token = localStorage.getItem('accessToken');
  const location = useLocation();
  return token ? children : <Navigate to="/login" state={{ from: location }} replace />;
};

export default PrivateRoute;