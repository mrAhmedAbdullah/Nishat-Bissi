import { useAuth } from './context/AuthContext';
import AuthPage from './pages/AuthPage';
import AdminDashboard from './pages/AdminDashboard';
import MemberDashboard from './pages/MemberDashboard';

export default function App() {
  const { user, logoutUser } = useAuth();

  if (!user) {
    return <AuthPage />;
  }

  return (
    <div className="min-h-screen bg-slate-100">
      <nav className="bg-white border-b border-slate-200 px-6 py-4 flex justify-between items-center">
        <div>
          <span className="font-bold text-emerald-700 text-lg">Tajdar Nishat Bissi</span>
          <span className="ml-3 text-xs bg-slate-100 px-2.5 py-1 rounded-full text-slate-600">
            {user.role === 'ROLE_ADMIN' ? 'Admin Portal' : 'Member Passbook'}
          </span>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm text-slate-600">{user.fullName}</span>
          <button
            onClick={logoutUser}
            className="text-sm text-red-600 hover:underline font-medium"
          >
            Logout
          </button>
        </div>
      </nav>

      {user.role === 'ROLE_ADMIN' ? <AdminDashboard /> : <MemberDashboard />}
    </div>
  );
}