import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authApi, userApi } from '../api/modules';
import { setAccessToken, clearAccessToken } from '../api/axios';

const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('cliniccare_user') || 'null'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const restore = async () => {
      if (localStorage.getItem('cliniccare_refresh')) {
        try {
          const tokens = await authApi.refresh();
          setAccessToken(tokens.accessToken);
          localStorage.setItem('cliniccare_refresh', tokens.refreshToken);
          const me = (await userApi.me()).data.data;
          setUser(me); localStorage.setItem('cliniccare_user', JSON.stringify(me));
        } catch {
          clearAccessToken(); localStorage.removeItem('cliniccare_refresh'); localStorage.removeItem('cliniccare_user'); setUser(null);
        }
      }
      setLoading(false);
    };
    restore();
  }, []);

  useEffect(() => {
    const logout = () => {
      clearAccessToken(); localStorage.removeItem('cliniccare_refresh'); localStorage.removeItem('cliniccare_user'); setUser(null);
    };
    window.addEventListener('cliniccare:logout', logout);
    return () => window.removeEventListener('cliniccare:logout', logout);
  }, []);

  const login = async (credentials) => {
    const tokens = await authApi.login(credentials);
    setAccessToken(tokens.accessToken); localStorage.setItem('cliniccare_refresh', tokens.refreshToken);
    const me = (await userApi.me()).data.data;
    setUser(me); localStorage.setItem('cliniccare_user', JSON.stringify(me));
    return me;
  };
  const logout = async () => {
    await authApi.logout(); clearAccessToken(); localStorage.removeItem('cliniccare_refresh'); localStorage.removeItem('cliniccare_user'); setUser(null);
  };
  const value = useMemo(() => ({ user, loading, login, logout }), [user, loading]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export const useAuth = () => useContext(AuthContext);
