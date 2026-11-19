import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';

export default function AdminDashboard() {
  const [diaries, setDiaries] = useState([]);
  const [audits, setAudits] = useState([]);
  const [txnForm, setTxnForm] = useState({
    diaryNumber: '',
    type: 'DEPOSIT',
    amount: '',
    remarks: '',
  });
  const [message, setMessage] = useState({ type: '', text: '' });

  const fetchData = async () => {
    try {
      const [diaryRes, auditRes] = await Promise.all([
        axiosClient.get('/diaries'),
        axiosClient.get('/audits'),
      ]);
      setDiaries(diaryRes.data);
      setAudits(auditRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleReview = async (diaryNumber, status) => {
    const rejectionReason =
      status === 'REJECTED' ? prompt('Enter rejection reason:') : null;
    if (status === 'REJECTED' && !rejectionReason) return;

    try {
      await axiosClient.patch(`/diaries/${diaryNumber}/review`, {
        status,
        rejectionReason,
      });
      setMessage({ type: 'success', text: `Diary ${diaryNumber} marked as ${status}` });
      fetchData();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Review failed' });
    }
  };

  const handleTransaction = async (e) => {
    e.preventDefault();
    try {
      await axiosClient.post('/transactions', {
        ...txnForm,
        amount: parseFloat(txnForm.amount),
      });
      setMessage({ type: 'success', text: `${txnForm.type} recorded for ${txnForm.diaryNumber}` });
      setTxnForm({ diaryNumber: '', type: 'DEPOSIT', amount: '', remarks: '' });
      fetchData();
    } catch (err) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Transaction failed' });
    }
  };

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      {message.text && (
        <div
          className={`p-3 rounded-lg text-sm border ${
            message.type === 'success'
              ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
              : 'bg-red-50 border-red-200 text-red-700'
          }`}
        >
          {message.text}
        </div>
      )}

      {/* Record Deposit / Withdrawal */}
      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
        <h2 className="text-lg font-semibold text-slate-800 mb-4">Record Deposit / Withdrawal</h2>
        <form onSubmit={handleTransaction} className="grid grid-cols-1 md:grid-cols-5 gap-3">
          <select
            required
            className="border rounded-lg px-3 py-2 text-sm bg-white"
            value={txnForm.diaryNumber}
            onChange={(e) => setTxnForm({ ...txnForm, diaryNumber: e.target.value })}
          >
            <option value="">Select Approved Diary</option>
            {diaries
              .filter((d) => d.status === 'APPROVED')
              .map((d) => (
                <option key={d.diaryNumber} value={d.diaryNumber}>
                  {d.diaryNumber} ({d.memberName})
                </option>
              ))}
          </select>

          <select
            className="border rounded-lg px-3 py-2 text-sm bg-white"
            value={txnForm.type}
            onChange={(e) => setTxnForm({ ...txnForm, type: e.target.value })}
          >
            <option value="DEPOSIT">DEPOSIT (+)</option>
            <option value="WITHDRAWAL">WITHDRAWAL (-)</option>
          </select>

          <input
            type="number"
            step="0.01"
            min="1"
            required
            placeholder="Amount (₹)"
            className="border rounded-lg px-3 py-2 text-sm"
            value={txnForm.amount}
            onChange={(e) => setTxnForm({ ...txnForm, amount: e.target.value })}
          />

          <input
            type="text"
            placeholder="Remarks (Optional)"
            className="border rounded-lg px-3 py-2 text-sm"
            value={txnForm.remarks}
            onChange={(e) => setTxnForm({ ...txnForm, remarks: e.target.value })}
          />

          <button
            type="submit"
            className="bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg px-4 py-2 text-sm font-medium"
          >
            Submit Entry
          </button>
        </form>
      </div>

      {/* Member Diaries & Approval Table */}
      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 overflow-x-auto">
        <h2 className="text-lg font-semibold text-slate-800 mb-4">Member Diaries</h2>
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-50 text-slate-600 border-b">
            <tr>
              <th className="p-3">Diary No</th>
              <th className="p-3">Member</th>
              <th className="p-3">Status</th>
              <th className="p-3">Balance</th>
              <th className="p-3">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {diaries.map((d) => (
              <tr key={d.id}>
                <td className="p-3 font-mono font-medium">{d.diaryNumber}</td>
                <td className="p-3">
                  <div>{d.memberName}</div>
                  <div className="text-xs text-slate-500">{d.memberEmail}</div>
                </td>
                <td className="p-3">
                  <span
                    className={`px-2.5 py-1 rounded-full text-xs font-medium ${
                      d.status === 'APPROVED'
                        ? 'bg-emerald-100 text-emerald-700'
                        : d.status === 'PENDING'
                        ? 'bg-amber-100 text-amber-700'
                        : 'bg-red-100 text-red-700'
                    }`}
                  >
                    {d.status}
                  </span>
                </td>
                <td className="p-3 font-semibold">₹{Number(d.currentBalance).toFixed(2)}</td>
                <td className="p-3 space-x-2">
                  {d.status === 'PENDING' ? (
                    <>
                      <button
                        onClick={() => handleReview(d.diaryNumber, 'APPROVED')}
                        className="bg-emerald-600 text-white px-3 py-1 rounded text-xs"
                      >
                        Approve
                      </button>
                      <button
                        onClick={() => handleReview(d.diaryNumber, 'REJECTED')}
                        className="bg-red-600 text-white px-3 py-1 rounded text-xs"
                      >
                        Reject
                      </button>
                    </>
                  ) : (
                    <span className="text-xs text-slate-400">Reviewed</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Live Audit Logs (From bissi-audit-service) */}
      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 overflow-x-auto">
        <h2 className="text-lg font-semibold text-slate-800 mb-4">Immutable Audit Logs</h2>
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-600 border-b">
            <tr>
              <th className="p-2.5">Timestamp</th>
              <th className="p-2.5">Action</th>
              <th className="p-2.5">Entity</th>
              <th className="p-2.5">Performed By</th>
              <th className="p-2.5">Details</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {audits.map((log) => (
              <tr key={log.id}>
                <td className="p-2.5 text-slate-500">
                  {new Date(log.timestamp).toLocaleString()}
                </td>
                <td className="p-2.5 font-semibold text-slate-700">{log.action}</td>
                <td className="p-2.5 font-mono">{log.entityId}</td>
                <td className="p-2.5">{log.performedBy}</td>
                <td className="p-2.5 text-slate-600">{log.details}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}