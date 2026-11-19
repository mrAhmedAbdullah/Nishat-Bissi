import { useEffect, useState } from 'react';
import axiosClient from '../api/axiosClient';

export default function MemberDashboard() {
  const [diary, setDiary] = useState(null);
  const [transactions, setTransactions] = useState([]);

  useEffect(() => {
    const loadPassbook = async () => {
      try {
        const { data: myDiary } = await axiosClient.get('/diaries/my');
        setDiary(myDiary);
        const { data: txns } = await axiosClient.get(`/transactions/diary/${myDiary.diaryNumber}`);
        setTransactions(txns);
      } catch (err) {
        console.error(err);
      }
    };
    loadPassbook();
  }, []);

  if (!diary) {
    return <div className="p-8 text-center text-slate-500">Loading your Bissi Diary...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      <div className="bg-gradient-to-r from-emerald-700 to-teal-700 text-white p-6 rounded-xl shadow">
        <div className="flex justify-between items-center">
          <div>
            <p className="text-xs uppercase tracking-wider text-emerald-100">Diary Number</p>
            <h2 className="text-2xl font-mono font-bold mt-1">{diary.diaryNumber}</h2>
          </div>
          <span className="px-3 py-1 rounded-full text-xs font-semibold bg-white/20">
            Status: {diary.status}
          </span>
        </div>
        <div className="mt-6">
          <p className="text-xs text-emerald-100">Available Interest-Free Balance</p>
          <p className="text-3xl font-bold mt-1">₹{Number(diary.currentBalance).toFixed(2)}</p>
        </div>
        {diary.status === 'REJECTED' && (
          <p className="mt-3 text-xs bg-red-900/40 p-2 rounded">
            Rejection Reason: {diary.rejectionReason}
          </p>
        )}
      </div>

      <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
        <h3 className="text-lg font-semibold text-slate-800 mb-4">Passbook Statement</h3>
        {transactions.length === 0 ? (
          <p className="text-sm text-slate-500">No deposits or withdrawals recorded yet.</p>
        ) : (
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600 border-b">
              <tr>
                <th className="p-3">Date</th>
                <th className="p-3">Ref No</th>
                <th className="p-3">Type</th>
                <th className="p-3">Amount</th>
                <th className="p-3">Balance</th>
                <th className="p-3">Remarks</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {transactions.map((t) => (
                <tr key={t.id}>
                  <td className="p-3 text-xs text-slate-500">
                    {new Date(t.transactionDate).toLocaleString()}
                  </td>
                  <td className="p-3 font-mono text-xs">{t.referenceNumber}</td>
                  <td className="p-3">
                    <span
                      className={`font-semibold text-xs ${
                        t.type === 'DEPOSIT' ? 'text-emerald-600' : 'text-rose-600'
                      }`}
                    >
                      {t.type}
                    </span>
                  </td>
                  <td className="p-3 font-medium">
                    {t.type === 'DEPOSIT' ? '+' : '-'}₹{Number(t.amount).toFixed(2)}
                  </td>
                  <td className="p-3 font-semibold">₹{Number(t.balanceAfter).toFixed(2)}</td>
                  <td className="p-3 text-slate-500">{t.remarks || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}