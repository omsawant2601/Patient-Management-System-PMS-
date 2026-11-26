import api from './axios';

export const authApi = {
  login: async (payload) => (await api.post('/auth/login', payload)).data.data,
  refresh: async () => (await api.post('/auth/refresh', {
    refreshToken: localStorage.getItem('cliniccare_refresh'),
  })).data.data,
  logout: async () => {
    const refreshToken = localStorage.getItem('cliniccare_refresh');
    if (refreshToken) await api.post('/auth/logout', { refreshToken }).catch(() => {});
  },
  forgotPassword: (username) => api.post('/auth/forgot-password', { username }),
  resetPassword: (payload) => api.post('/auth/reset-password', payload),
  changePassword: (payload) => api.post('/auth/change-password', payload),
};
export const userApi = {
  me: () => api.get('/users/me'),
  list: () => api.get('/users'),
  create: (payload) => api.post('/users', payload),
  get: (id) => api.get(`/users/${id}`),
  update: (id, payload) => api.put(`/users/${id}`, payload),
  setStatus: (id, active) => api.patch(`/users/${id}/status`, { active }),
};
export const patientApi = {
  list: (params) => api.get('/patients', { params }),
  search: (params) => api.get('/patients/search', { params }),
  get: (id) => api.get(`/patients/${id}`),
  create: (payload) => api.post('/patients', payload),
  update: (id, payload) => api.put(`/patients/${id}`, payload),
  history: (id) => api.get(`/patients/${id}/history`),
  photo: (id) => api.get(`/patients/${id}/photo`, { responseType: 'blob' }),
  uploadPhoto: (id, file) => {
    const body = new FormData();
    body.append('photo', file);
    return api.post(`/patients/${id}/photo`, body);
  },
  removePhoto: (id) => api.delete(`/patients/${id}/photo`),
  gallery: (id) => api.get(`/patients/${id}/gallery`),
  galleryImage: (id, comparisonId, side) => api.get(`/patients/${id}/gallery/${comparisonId}/${side}`, { responseType: 'blob' }),
  addComparison: (id, before, after, caption) => {
    const body = new FormData();
    body.append('before', before);
    body.append('after', after);
    if (caption) body.append('caption', caption);
    return api.post(`/patients/${id}/gallery`, body);
  },
  removeComparison: (id, comparisonId) => api.delete(`/patients/${id}/gallery/${comparisonId}`),
};
export const appointmentApi = {
  list: (params) => api.get('/appointments', { params }),
  get: (id) => api.get(`/appointments/${id}`),
  create: (payload) => api.post('/appointments', payload),
  reschedule: (id, payload) => api.put(`/appointments/${id}`, payload),
  cancel: (id) => api.patch(`/appointments/${id}/cancel`),
  setStatus: (id, status) => api.patch(`/appointments/${id}/status`, { status }),
};
export const consultationApi = {
  get: (id) => api.get(`/consultations/${id}`),
  create: (payload) => api.post('/consultations', payload),
  update: (id, payload) => api.put(`/consultations/${id}`, payload),
  history: (patientId) => api.get(`/patients/${patientId}/consultations`),
};
export const prescriptionApi = {
  get: (id) => api.get(`/prescriptions/${id}`),
  create: (payload) => api.post('/prescriptions', payload),
  update: (id, payload) => api.put(`/prescriptions/${id}`, payload),
  finalize: (id) => api.post(`/prescriptions/${id}/finalize`),
  history: (patientId) => api.get(`/patients/${patientId}/prescriptions`),
  pdf: (id) => api.get(`/prescriptions/${id}/pdf`, { responseType: 'blob' }),
};
export const reminderApi = {
  list: () => api.get('/reminders'),
  forAppointment: (id) => api.get(`/appointments/${id}/reminders`),
};
export const whatsappApi = {
  inbox: (params) => api.get('/whatsapp/inbox', { params }),
};
