import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './routes/ProtectedRoute';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import PatientsPage from './pages/PatientsPage';
import AppointmentsPage from './pages/AppointmentsPage';
import ConsultationsPage from './pages/ConsultationsPage';
import PrescriptionsPage from './pages/PrescriptionsPage';
import RemindersPage from './pages/RemindersPage';
import UsersPage from './pages/UsersPage';
import ReportsPage from './pages/ReportsPage';
import SettingsPage from './pages/SettingsPage';
import ProfilePage from './pages/ProfilePage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ResetPasswordPage from './pages/ResetPasswordPage';

export default function App(){
 return <AuthProvider><BrowserRouter><Routes>
  <Route path="/login" element={<LoginPage/>}/>
  <Route path="/forgot-password" element={<ForgotPasswordPage/>}/>
  <Route path="/reset-password" element={<ResetPasswordPage/>}/>
  <Route element={<ProtectedRoute><Layout/></ProtectedRoute>}>
   <Route path="/dashboard" element={<DashboardPage/>}/>
   <Route path="/patients" element={<PatientsPage/>}/>
   <Route path="/appointments" element={<AppointmentsPage/>}/>
   <Route path="/consultations" element={<ProtectedRoute roles={['DOCTOR']}><ConsultationsPage/></ProtectedRoute>}/>
   <Route path="/prescriptions" element={<ProtectedRoute roles={['DOCTOR']}><PrescriptionsPage/></ProtectedRoute>}/>
   <Route path="/reminders" element={<RemindersPage/>}/>
   <Route path="/users" element={<ProtectedRoute roles={['DOCTOR']}><UsersPage/></ProtectedRoute>}/>
   <Route path="/reports" element={<ReportsPage/>}/>
   <Route path="/settings" element={<SettingsPage/>}/>
   <Route path="/profile" element={<ProfilePage/>}/>
  </Route>
  <Route path="*" element={<Navigate to="/dashboard" replace/>}/>
 </Routes></BrowserRouter></AuthProvider>;
}
