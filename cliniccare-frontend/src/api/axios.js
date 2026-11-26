import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

let accessToken = null;
let refreshPromise = null;
export const setAccessToken = (token) => { accessToken = token; };
export const clearAccessToken = () => { accessToken = null; };

api.interceptors.request.use((config) => {
  if (accessToken) config.headers.Authorization = `Bearer ${accessToken}`;
  if (typeof FormData !== 'undefined' && config.data instanceof FormData) {
    delete config.headers['Content-Type'];
    delete config.headers['content-type'];
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const isAuthEndpoint = original?.url?.startsWith('/auth/');
    if (error.response?.status === 401 && !original?._retry && !isAuthEndpoint) {
      original._retry = true;
      try {
        const refreshToken = localStorage.getItem('cliniccare_refresh');
        if (!refreshToken) throw new Error('Refresh token unavailable');
        if (!refreshPromise) {
          refreshPromise = api.post('/auth/refresh', { refreshToken }).then((r) => r.data.data);
        }
        const response = await refreshPromise;
        refreshPromise = null;
        setAccessToken(response.accessToken);
        localStorage.setItem('cliniccare_refresh', response.refreshToken);
        original.headers.Authorization = `Bearer ${response.accessToken}`;
        return api(original);
      } catch (refreshError) {
        refreshPromise = null;
        clearAccessToken();
        localStorage.removeItem('cliniccare_refresh');
        localStorage.removeItem('cliniccare_user');
        window.dispatchEvent(new Event('cliniccare:logout'));
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);
export default api;
