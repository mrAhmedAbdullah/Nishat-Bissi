import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('bissi_user');
    return saved ? JSON.parse(saved) : null;
  });

  const loginUser = (authData) => {
    localStorage.setItem('bissi_token', authData.token);
    localStorage.setItem('bissi_user', JSON.stringify(authData));
    setUser(authData);
  };

  const logoutUser = () => {
    localStorage.removeItem('bissi_token');
    localStorage.removeItem('bissi_user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loginUser, logoutUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);