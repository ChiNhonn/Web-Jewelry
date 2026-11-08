import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { paymentService } from '../services/paymentService';
import { orderService } from '../services/orderService';
import { VNPayReturnResponse } from '../types/payment.types';
import { useCart } from '../context/CartContext';
import { useToast } from '../context/ToastContext';
import { CartItem } from '../types/cart.types';
import { CheckCircle2, XCircle, ShieldCheck, RefreshCw } from 'lucide-react';

interface PendingPayment {
  orderCode: string;
  accessToken?: string;
  items: CartItem[];
}

const readPending = (): PendingPayment | null => {
  try {
    const raw = sessionStorage.getItem('aura_vnpay_pending');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

export const PaymentReturnPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { replaceItems } = useCart();
  const { success, error } = useToast();
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [askCod, setAskCod] = useState(false);
  const [result, setResult] = useState<VNPayReturnResponse | null>(null);
  const pending = readPending();

  useEffect(() => {
    const paramsObj: Record<string, string> = {};
    searchParams.forEach((val, key) => {
      paramsObj[key] = val;
    });
    if (Object.keys(paramsObj).length === 0) {
      setLoading(false);
      return;
    }
    paymentService
      .processVNPayReturn(paramsObj)
      .then(setResult)
      .catch(() => {
        const responseCode = searchParams.get('vnp_ResponseCode') || '99';
        const rawAmount = parseInt(searchParams.get('vnp_Amount') || '0', 10);
        setResult({
          orderCode: searchParams.get('vnp_TxnRef') || pending?.orderCode || '',
          transactionCode: searchParams.get('vnp_TransactionNo') || '',
          responseCode,
          isSuccess: responseCode === '00',
          amount: rawAmount > 0 ? rawAmount / 100 : 0,
          bankCode: searchParams.get('vnp_BankCode') || '',
          payDate: searchParams.get('vnp_PayDate') || '',
          message: responseCode === '00' ? 'Thanh toán VNPay thành công.' : 'Giao dịch bị hủy hoặc không thành công.',
        });
      })
      .finally(() => setLoading(false));
  }, [searchParams]);

  const orderCode = result?.orderCode || pending?.orderCode || '';
  const accessToken = pending?.accessToken || '';

  const retry = async () => {
    if (!orderCode) return;
    setBusy(true);
    try {
      const payment = await paymentService.initiatePayment(orderCode, accessToken);
      if (payment.paymentUrl) window.location.href = payment.paymentUrl;
      else error('Không tạo lại được liên kết VNPay.');
    } catch {
      error('Không thanh toán lại được.');
    } finally {
      setBusy(false);
    }
  };

  const confirmCod = async () => {
    if (!orderCode || !accessToken) {
      error('Thiếu mã đơn để đổi sang thanh toán khi nhận hàng.');
      return;
    }
    setBusy(true);
    try {
      await orderService.switchToCod(orderCode, accessToken);
      sessionStorage.removeItem('aura_vnpay_pending');
      success('Đơn đã chuyển sang thanh toán khi nhận hàng.', 'Đã xác nhận');
      navigate('/account?tab=orders');
    } catch {
      error('Không đổi được sang thanh toán khi nhận hàng.');
    } finally {
      setBusy(false);
    }
  };

  const leaveForLater = async () => {
    if (!orderCode || !accessToken) {
      error('Không tìm thấy đơn vừa tạo để hoàn giỏ.');
      return;
    }
    setBusy(true);
    try {
      await orderService.abandonOrder(orderCode, accessToken);
      if (pending?.items?.length) replaceItems(pending.items);
      sessionStorage.removeItem('aura_vnpay_pending');
      success('Đã hủy đơn vừa tạo và trả sản phẩm về giỏ.', 'Để sau');
      navigate('/cart');
    } catch {
      error('Không hoàn được đơn về giỏ hàng.');
    } finally {
      setBusy(false);
    }
  };

  if (loading) {
    return (
      <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '150px', textAlign: 'center' }}>
        <RefreshCw size={36} color="#ff6584" style={{ animation: 'spin 1.5s linear infinite' }} />
        <p style={{ marginTop: '16px', color: 'var(--color-text-secondary)' }}>Đang xác nhận kết quả VNPay sandbox...</p>
      </div>
    );
  }

  const isSuccess = !!result?.isSuccess;

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '120px', paddingBottom: '90px' }}>
      <div className="container" style={{ maxWidth: '640px' }}>
        <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.9)', border: '1px solid rgba(255,255,255,0.08)', borderRadius: '20px', padding: '40px 28px', textAlign: 'center' }}>
          {isSuccess ? <CheckCircle2 size={44} color="#10B981" /> : <XCircle size={44} color="#EF4444" />}
          <h1 style={{ color: '#fff', margin: '16px 0 8px' }}>{isSuccess ? 'Thanh toán thành công' : 'Thanh toán VNPay chưa hoàn tất'}</h1>
          <p style={{ color: 'var(--color-text-secondary)' }}>{result?.message || 'Giao dịch sandbox, không trừ tiền thật.'}</p>
          <div style={{ margin: '20px 0', color: '#fff' }}>
            <div>Mã đơn: {orderCode || '—'}</div>
            <div style={{ color: 'var(--color-gold-400)', fontWeight: 700, marginTop: '8px' }}>
              {(result?.amount || 0).toLocaleString('vi-VN')}₫
            </div>
          </div>
          {isSuccess ? (
            <Link to="/account?tab=orders" className="btn-pill-sakura" style={{ padding: '12px 24px' }}>Xem đơn hàng</Link>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <button className="btn-pill-sakura" disabled={busy} onClick={retry} style={{ padding: '12px' }}>Thanh toán lại</button>
              {!askCod ? (
                <button className="btn-outline-pill" disabled={busy} onClick={() => setAskCod(true)} style={{ padding: '12px' }}>Đổi sang thanh toán khi nhận hàng</button>
              ) : (
                <button className="btn-outline-pill" disabled={busy} onClick={confirmCod} style={{ padding: '12px' }}>Xác nhận đổi sang COD</button>
              )}
              <button disabled={busy} onClick={leaveForLater} style={{ background: 'transparent', color: 'var(--color-text-secondary)', border: 'none', cursor: 'pointer' }}>
                Để sau
              </button>
            </div>
          )}
          <div style={{ marginTop: '18px', fontSize: '0.78rem', color: 'var(--color-text-muted)', display: 'flex', justifyContent: 'center', gap: '6px' }}>
            <ShieldCheck size={14} /> VNPay sandbox, tiền ảo
          </div>
        </div>
      </div>
    </div>
  );
};
