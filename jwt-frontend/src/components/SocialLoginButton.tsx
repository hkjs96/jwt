import React from 'react';

const GoogleIcon = () => (
    <svg width="18" height="18" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48">
      <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
      <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
      <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
      <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
      <path fill="none" d="M0 0h48v48H0z"/>
    </svg>
  );

const SocialLoginButton: React.FC = () => {
    const clientId   = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    const redirectUri= `${import.meta.env.VITE_FRONTEND_URL}/oauth2/redirect`;
    const scope      = encodeURIComponent('openid profile email');
    const authUrl = [
      'https://accounts.google.com/o/oauth2/v2/auth?',
      `client_id=${clientId}`,
      `&redirect_uri=${encodeURIComponent(redirectUri)}`,
      '&response_type=code',
      `&scope=${scope}`
    ].join('');

    const handleLogin = (e: React.MouseEvent) => {
        e.preventDefault();
        window.location.href = authUrl;
      };
  
    return (
        <button 
      className="gsi-material-button"
      onClick={handleLogin}
      type="button"
      aria-label="Sign in with Google"
      style={{
        userSelect: 'none',
        WebkitUserSelect: 'none',
        appearance: 'none',
        backgroundColor: 'white',
        backgroundImage: 'none',
        border: '1px solid #747775',
        borderRadius: '4px',
        boxSizing: 'border-box',
        color: '#1f1f1f',
        cursor: 'pointer',
        fontFamily: "'Roboto', arial, sans-serif",
        fontSize: '14px',
        height: '40px',
        letterSpacing: '0.25px',
        outline: 'none',
        overflow: 'hidden',
        padding: '0 12px',
        position: 'relative',
        textAlign: 'center',
        transition: 'background-color .218s, border-color .218s, box-shadow .218s',
        verticalAlign: 'middle',
        whiteSpace: 'nowrap',
        width: 'auto',
        maxWidth: '400px',
        minWidth: 'min-content'
      }}
    >
      <div 
        className="gsi-material-button-state"
        style={{
          transition: 'opacity .218s',
          bottom: 0,
          left: 0,
          opacity: 0,
          position: 'absolute',
          right: 0,
          top: 0
        }}
      ></div>
      <div 
        className="gsi-material-button-content-wrapper"
        style={{
          alignItems: 'center',
          display: 'flex',
          flexDirection: 'row',
          flexWrap: 'nowrap',
          height: '100%',
          justifyContent: 'space-between',
          position: 'relative',
          width: '100%'
        }}
      >
        <div 
          className="gsi-material-button-icon"
          style={{
            height: '20px',
            marginRight: '12px',
            minWidth: '20px',
            width: '20px',
            opacity: '100%'
          }}
        >
            <GoogleIcon />
        </div>
        <span 
          className="gsi-material-button-contents"
          style={{
            flexGrow: 1,
            fontFamily: "'Roboto', arial, sans-serif",
            fontWeight: 500,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            verticalAlign: 'top',
            opacity: '100%'
          }}
        >
          Sign in with Google
        </span>
        <span style={{ display: 'none' }}>Sign in with Google</span>
      </div>
      <style jsx>{`
        .gsi-material-button:not(:disabled):active .gsi-material-button-state, 
        .gsi-material-button:not(:disabled):focus .gsi-material-button-state {
          background-color: #303030;
          opacity: 12%;
        }
        .gsi-material-button:not(:disabled):hover {
          box-shadow: 0 1px 2px 0 rgba(60, 64, 67, .30), 0 1px 3px 1px rgba(60, 64, 67, .15);
        }
        .gsi-material-button:not(:disabled):hover .gsi-material-button-state {
          background-color: #303030;
          opacity: 8%;
        }
        .gsi-material-button:disabled {
          background-color: #ffffff61;
          border-color: #1f1f1f1f;
        }
      `}</style>
    </button>
    );
  };
  
  export default SocialLoginButton;