import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Profile from './pages/Profile';
import Home from './pages/Home';
import OAuth2Redirect from './pages/OAuth2Redirect';
import PrivateRoute from './components/PrivateRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/profile"
          element={<PrivateRoute><Profile /></PrivateRoute>}
        />
        <Route path="*" element={<Navigate to="/" />} />
        <Route path="/oauth2/redirect" element={<OAuth2Redirect />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;