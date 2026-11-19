import { useState } from 'react';
import axiosClient from '../api/axiosClient';
import { useAuth } from '../context/AuthContext';

export default function AuthPage() {
  const { loginUser } = useAuth();
  const [isLogin, setIsLogin] = useState(true);
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    phoneNumber: '',
    password: '',
    role: 'ROLE_MEMBER',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const endpoint = isLogin ? '/auth/login' : '/auth/register';
      const payload = isLogin
        ? { email: form.email, password: form.password }
        : form;
      const { data } = await axiosClient.post(endpoint, payload);
      loginUser(data);
    } catch (err) {
      setError(err.response?.data?.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="bg-white shadow-lg rounded-xl max-w-md w-full p-8 border border-slate-200">
        <h1 className="text-2xl font-bold text-emerald-700 text-center">Tajdar Nishat Bissi</h1>
        <p className="text-sm text-slate-500 text-center mt-1">
          Community Interest-Free Deposit Ledger
        </p>

        <div className="flex mt-6 bg-slate-100 p-1 rounded-lg">
          <button
            type="button"
            onClick={() => setIsLogin(true)}
            className={`flex-1 py-2 text-sm font-medium rounded-md ${
              isLogin ? 'bg-white shadow text-emerald-700' : 'text-slate-600'
            }`}
          >
            Login
          </button>
          <button
            type="button"
            onClick={() => setIsLogin(false)}
            className={`flex-1 py-2 text-sm font-medium rounded-md ${
              !isLogin ? 'bg-white shadow text-emerald-700' : 'text-slate-600'
            }`}
          >
            Register
          </button>
        </div>

        {error && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="mt-5 space-y-4">
          {!isLogin && (
            <>
              <input
                type="text"
                placeholder="Full Name"
                required
                className="w-full px-3 py-2 border rounded-lg text-sm"
                value={form.fullName}
                onChange={(e) => setForm({ ...form, fullName: e.target.value })}
              />
              <input
                type="text"
                placeholder="10-digit Mobile Number"
                required
                className="w-full px-3 py-2 border rounded-lg text-sm"
                value={form.phoneNumber}
                onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
              />
            </>
          )}
          <input
            type="email"
            placeholder="Email Address"
            required
            className="w-full px-3 py-2 border rounded-lg text-sm"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <input
            type="password"
            placeholder="Password (min 6 chars)"
            required
            className="w-full px-3 py-2 border rounded-lg text-sm"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
          {!isLogin && (
            <select
              className="w-full px-3 py-2 border rounded-lg text-sm bg-white"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
            >
              <option value="ROLE_MEMBER">Member (Auto-creates Diary)</option>
              <option value="ROLE_ADMIN">Admin</option>
            </select>
          )}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-medium py-2.5 rounded-lg text-sm transition"
          >
            {loading ? 'Please wait...' : isLogin ? 'Sign In' : 'Create Account'}
          </button>
        </form>
      </div>
    </div>
  );
}