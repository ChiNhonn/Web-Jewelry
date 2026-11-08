import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { orderService } from '../services/orderService';
import { paymentService } from '../services/paymentService';
import { addressService } from '../services/addressService';
import { Address } from '../types/address.types';
import { PaymentMethodType } from '../types/order.types';
import { BankTransferDetails } from '../types/payment.types';
import { CartItem } from '../types/cart.types';
import {
  CreditCard,
  QrCode,
  Truck,
  CheckCircle2,
  Lock,
  ArrowRight,
  Copy,
  ChevronLeft,
} from 'lucide-react';

export const CheckoutPage: React.FC = () => {
  const { items, subtotal, shippingFee, appliedCoupon, discountAmount, finalTotal, clearCart } = useCart();
  const { user, isAuthenticated, openAuthModal } = useAuth();
  const { success, error, info } = useToast();
  const navigate = useNavigate();

  // Address State
  const [savedAddresses, setSavedAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);

  const [shippingForm, setShippingForm] = useState({
    receiverName: user?.fullName || '',
    phone: user?.phone || '',
    streetAddress: '',
    ward: '',
    district: '',
    province: '',
    customerNote: '',
  });

  // Payment Selection
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethodType>('COD');
  const [submitting, setSubmitting] = useState(false);

  // VietQR Modal State
  const [vietQrData] = useState<BankTransferDetails | null>(null);
  const [orderCreatedCode, setOrderCreatedCode] = useState<string | null>(null);

  // Load Saved Addresses if logged in
  useEffect(() => {
    if (isAuthenticated) {
      addressService
        .getAddresses()
        .then((list: Address[]) => {
          setSavedAddresses(list);
          const defaultAddr = list.find((a: Address) => a.isDefault) || list[0];
          if (defaultAddr) {
            setSelectedAddressId(defaultAddr.id);
            setShippingForm((prev) => ({
              ...prev,
              receiverName: defaultAddr.receiverName,
              phone: defaultAddr.phone,
              streetAddress: defaultAddr.streetAddress,
              ward: defaultAddr.ward,
              district: defaultAddr.district,
              province: defaultAddr.province,
            }));
          }
        })
        .catch(() => {});
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (localStorage.getItem('aura_geo_prompted')) return;
    localStorage.setItem('aura_geo_prompted', '1');
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition(async (position) => {
      try {
        const url = `https://nominatim.openstreetmap.org/reverse?format=json&accept-language=vi&lat=${position.coords.latitude}&lon=${position.coords.longitude}`;
        const response = await fetch(url, { headers: { 'Accept-Language': 'vi' } });
        const data = await response.json();
        const address = data.address || {};
        setShippingForm((prev) => ({
          ...prev,
          streetAddress: prev.streetAddress || [address.house_number, address.road].filter(Boolean).join(' ') || data.display_name || '',
          ward: prev.ward || address.suburb || address.quarter || address.neighbourhood || '',
          district: prev.district || address.city_district || address.county || address.town || '',
          province: prev.province || address.city || address.state || '',
        }));
      } catch {
        /* địa chỉ vẫn nhập tay */
      }
    });
  }, []);

  if (items.length === 0 && !orderCreatedCode) {
    return (
      <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '140px', textAlign: 'center' }}>
        <h2 style={{ color: '#FFFFFF', marginBottom: '16px' }}>Túi trang sức của quý khách đang trống</h2>
        <Link to="/products" className="btn-pill-sakura">
          Khám Phá Tuyệt Tác Trang Sức
        </Link>
      </div>
    );
  }

  const handleAddressSelect = (addr: Address) => {
    setSelectedAddressId(addr.id);
    setShippingForm({
      receiverName: addr.receiverName,
      phone: addr.phone,
      streetAddress: addr.streetAddress,
      ward: addr.ward,
      district: addr.district,
      province: addr.province,
      customerNote: shippingForm.customerNote,
    });
  };

  const handlePlaceOrder = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!shippingForm.receiverName || !shippingForm.phone || !shippingForm.streetAddress || !shippingForm.ward || !shippingForm.district || !shippingForm.province) {
      error('Vui lòng điền tên người nhận, số điện thoại và địa chỉ đầy đủ.', 'Thiếu thông tin');
      return;
    }
    if (!/^(0|\+84)[35789][0-9]{8}$/.test(shippingForm.phone.replace(/\s/g, ''))) {
      error('Số điện thoại chưa đúng định dạng Việt Nam.', 'Số điện thoại');
      return;
    }

    setSubmitting(true);
    try {
      const address = {
        receiverName: shippingForm.receiverName,
        phone: shippingForm.phone.replace(/\s/g, ''),
        streetAddress: shippingForm.streetAddress,
        ward: shippingForm.ward,
        district: shippingForm.district,
        province: shippingForm.province,
      };
      const order = isAuthenticated
        ? await orderService.checkout({
            shippingAddressId: selectedAddressId || undefined,
            shippingAddress: address,
            paymentMethod,
            couponCode: appliedCoupon?.code,
            customerNote: shippingForm.customerNote,
          })
        : await orderService.guestCheckout({
            shippingAddress: address,
            paymentMethod,
            couponCode: appliedCoupon?.code,
            customerNote: shippingForm.customerNote,
            items: items.map((item) => ({ variantId: item.variantId, quantity: item.quantity })),
          });

      setOrderCreatedCode(order.orderCode);

      if (paymentMethod === 'VNPAY') {
        info('Đang chuyển sang VNPay sandbox. Đây là tiền ảo, không trừ tiền thật.', 'Thanh toán');
        const paymentRes = await paymentService.initiatePayment(order.orderCode, order.guestAccessToken);
        if (paymentRes.paymentUrl) {
          sessionStorage.setItem('aura_vnpay_pending', JSON.stringify({
            orderCode: order.orderCode,
            accessToken: order.guestAccessToken,
            items,
          }));
          clearCart();
          window.location.href = paymentRes.paymentUrl;
          return;
        }
        error('Không tạo được liên kết VNPay.', 'Thanh toán');
        return;
      }

      clearCart();
      success(`Đơn ${order.orderCode} đã được tạo. Thanh toán khi nhận hàng.`, 'Đặt hàng thành công');
    } catch (err: unknown) {
      let message = 'Không đặt được hàng. Vui lòng kiểm tra lại thông tin và tồn kho.';
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { data?: { message?: string } } };
        if (axiosErr.response?.data?.message) message = axiosErr.response.data.message;
      }
      error(message, 'Đặt hàng thất bại');
    } finally {
      setSubmitting(false);
    }
  };

  const copyToClipboard = (text: string, label: string) => {
    navigator.clipboard.writeText(text);
    success(`Đã sao chép ${label}: ${text}`, 'Sao Chép Thành Công');
  };

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '100px', paddingBottom: '90px' }}>
      <div className="container" style={{ maxWidth: '1100px' }}>
        {/* Back Link */}
        <Link
          to="/cart"
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            color: 'var(--color-text-secondary)',
            fontSize: '0.85rem',
            marginBottom: '24px',
          }}
        >
          <ChevronLeft size={16} /> Quay lại giỏ hàng
        </Link>

        {/* ORDER SUCCESS DISPLAY (IF PLACED) */}
        {orderCreatedCode && !vietQrData && (
          <div
            style={{
              backgroundColor: 'rgba(14, 16, 23, 0.9)',
              border: '1px solid rgba(255, 101, 132, 0.3)',
              borderRadius: '20px',
              padding: '48px 32px',
              textAlign: 'center',
              boxShadow: '0 25px 60px rgba(0, 0, 0, 0.9), 0 0 50px rgba(255, 101, 132, 0.2)',
            }}
          >
            <div
              style={{
                width: '76px',
                height: '76px',
                borderRadius: '50%',
                backgroundColor: 'rgba(16, 185, 129, 0.15)',
                border: '2px solid #10B981',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 20px',
              }}
            >
              <CheckCircle2 size={40} color="#10B981" />
            </div>

            <h1 style={{ fontSize: '2.2rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '8px' }}>
              Đặt Hàng Hoàng Gia Thành Công!
            </h1>
            <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.98rem', maxWidth: '520px', margin: '0 auto 20px', lineHeight: 1.6 }}>
              Đơn hàng đã được ghi nhận. Bạn có thể theo dõi trạng thái trong mục đơn hàng.
            </p>

            <div
              style={{
                display: 'inline-block',
                backgroundColor: 'rgba(255, 255, 255, 0.05)',
                border: '1px solid rgba(255, 255, 255, 0.12)',
                borderRadius: '12px',
                padding: '16px 28px',
                marginBottom: '32px',
              }}
            >
              <div style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                MÃ ĐƠN HÀNG ĐẶC QUYỀN
              </div>
              <div style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--color-sakura-300)', letterSpacing: '0.05em', marginTop: '4px' }}>
                {orderCreatedCode}
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '16px' }}>
              <Link to="/account?tab=orders" className="btn-outline-pill" style={{ padding: '12px 28px' }}>
                Xem Tiến Độ Đơn Hàng
              </Link>
              <Link to="/products" className="btn-pill-sakura" style={{ padding: '12px 28px' }}>
                Tiếp Tục Chiêm Ngưỡng Tuyệt Tác
              </Link>
            </div>
          </div>
        )}

        {/* VIETQR PAYMENT DISPLAY MODAL (IF VIETQR SELECTED) */}
        {vietQrData && (
          <div
            style={{
              backgroundColor: 'rgba(14, 16, 23, 0.9)',
              border: '1px solid rgba(212, 175, 55, 0.35)',
              borderRadius: '20px',
              padding: '40px 32px',
              maxWidth: '680px',
              margin: '0 auto',
              textAlign: 'center',
              boxShadow: '0 25px 60px rgba(0, 0, 0, 0.9), 0 0 50px rgba(212, 175, 55, 0.2)',
            }}
          >
            <div style={{ fontSize: '0.8rem', letterSpacing: '0.15em', color: 'var(--color-gold-400)', textTransform: 'uppercase', fontWeight: 600, marginBottom: '8px' }}>
              THANH TOÁN TỰ ĐỘNG NAPAS 24/7
            </div>
            <h2 style={{ fontSize: '1.8rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '8px' }}>
              Quét Mã VietQR Hoàn Tất Đơn Hàng
            </h2>
            <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.86rem', marginBottom: '24px' }}>
              Đơn hàng <strong style={{ color: '#FFFFFF' }}>#{vietQrData.orderCode}</strong> đang chờ thanh toán số tiền{' '}
              <strong style={{ color: 'var(--color-gold-400)' }}>{vietQrData.amount.toLocaleString('vi-VN')}₫</strong>
            </p>

            {/* QR Code Image */}
            <div
              style={{
                width: '240px',
                height: '240px',
                backgroundColor: '#FFFFFF',
                borderRadius: '16px',
                padding: '12px',
                margin: '0 auto 24px',
                boxShadow: '0 10px 30px rgba(0, 0, 0, 0.8)',
              }}
            >
              <img
                src={vietQrData.qrCodeUrl}
                alt="VietQR AURA"
                style={{ width: '100%', height: '100%', objectFit: 'contain' }}
              />
            </div>

            {/* Bank details copy box */}
            <div
              style={{
                backgroundColor: 'rgba(255, 255, 255, 0.03)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                borderRadius: '12px',
                padding: '18px',
                textAlign: 'left',
                marginBottom: '28px',
                display: 'flex',
                flexDirection: 'column',
                gap: '12px',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Ngân hàng thụ hưởng:</span>
                <span style={{ fontSize: '0.9rem', color: '#FFFFFF', fontWeight: 600 }}>MB Bank (Quân Đội)</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Số tài khoản:</span>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '0.95rem', color: 'var(--color-gold-400)', fontWeight: 700 }}>
                    {vietQrData.accountNo}
                  </span>
                  <button
                    onClick={() => copyToClipboard(vietQrData.accountNo, 'Số tài khoản')}
                    style={{ backgroundColor: 'transparent', border: 'none', color: 'var(--color-text-secondary)', cursor: 'pointer' }}
                  >
                    <Copy size={14} />
                  </button>
                </div>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Chủ tài khoản:</span>
                <span style={{ fontSize: '0.9rem', color: '#FFFFFF', fontWeight: 600 }}>{vietQrData.accountName}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Nội dung chuyển khoản:</span>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '0.95rem', color: 'var(--color-sakura-300)', fontWeight: 700 }}>
                    {vietQrData.transferContent}
                  </span>
                  <button
                    onClick={() => copyToClipboard(vietQrData.transferContent, 'Nội dung chuyển khoản')}
                    style={{ backgroundColor: 'transparent', border: 'none', color: 'var(--color-text-secondary)', cursor: 'pointer' }}
                  >
                    <Copy size={14} />
                  </button>
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '16px' }}>
              <button
                onClick={() => {
                  success('Hệ thống đã ghi nhận yêu cầu chuyển khoản. Đang xác thực số dư.', 'Xác Nhận');
                  navigate('/account?tab=orders');
                }}
                className="btn-pill-sakura"
                style={{ padding: '12px 32px' }}
              >
                Tôi Đã Chuyển Khoản Xong
              </button>
            </div>
          </div>
        )}

        {/* REGULAR CHECKOUT FORM */}
        {!orderCreatedCode && !vietQrData && (
          <form onSubmit={handlePlaceOrder}>
            <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 1.7fr) minmax(0, 1fr)', gap: '40px', alignItems: 'start' }}>
              {/* LEFT: Shipping & Payment Info */}
              <div>
                {/* Section 1: Customer Auth Reminder */}
                {!isAuthenticated && (
                  <div
                    style={{
                      backgroundColor: 'rgba(255, 101, 132, 0.08)',
                      border: '1px solid rgba(255, 101, 132, 0.25)',
                      borderRadius: '12px',
                      padding: '16px 20px',
                      marginBottom: '28px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      flexWrap: 'wrap',
                      gap: '12px',
                    }}
                  >
                    <div style={{ fontSize: '0.86rem', color: 'var(--color-sakura-300)' }}>
                      Đã có tài khoản?
                    </div>
                    <button
                      type="button"
                      onClick={() => openAuthModal('login')}
                      className="btn-pill-white"
                      style={{ padding: '6px 16px', fontSize: '0.78rem' }}
                    >
                      Đăng Nhập Để Chọn Sổ Địa Chỉ
                    </button>
                  </div>
                )}

                {/* Section 2: Address Selection (If has saved addresses) */}
                {isAuthenticated && savedAddresses.length > 0 && (
                  <div style={{ marginBottom: '28px' }}>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '14px' }}>
                      Chọn Sổ Địa Chỉ Giao Hàng Đã Lưu
                    </h3>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '12px' }}>
                      {savedAddresses.map((addr) => {
                        const isSelected = selectedAddressId === addr.id;
                        return (
                          <div
                            key={addr.id}
                            onClick={() => handleAddressSelect(addr)}
                            style={{
                              padding: '14px',
                              borderRadius: '10px',
                              border: isSelected ? '1px solid #ff6584' : '1px solid rgba(255, 255, 255, 0.08)',
                              backgroundColor: isSelected ? 'rgba(255, 101, 132, 0.1)' : 'rgba(255, 255, 255, 0.03)',
                              cursor: 'pointer',
                              transition: 'all 0.2s ease',
                            }}
                          >
                            <div style={{ fontSize: '0.88rem', fontWeight: 600, color: isSelected ? '#FFFFFF' : 'var(--color-text-secondary)', marginBottom: '4px' }}>
                              {addr.receiverName} • {addr.phone}
                            </div>
                            <div style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)', lineHeight: 1.4 }}>
                              {addr.streetAddress}, {addr.ward}, {addr.district}, {addr.province}
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}

                {/* Section 3: Delivery Address Form */}
                <div
                  style={{
                    backgroundColor: 'rgba(14, 16, 23, 0.7)',
                    backdropFilter: 'blur(16px)',
                    border: '1px solid rgba(255, 255, 255, 0.08)',
                    borderRadius: '16px',
                    padding: '28px',
                    marginBottom: '28px',
                  }}
                >
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '20px' }}>
                    1. Thông Tin Nhận Hàng Bảo Hiểm
                  </h3>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '16px' }}>
                    <div>
                      <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                        Họ và Tên Quý Khách *
                      </label>
                      <input
                        type="text"
                        required
                        placeholder="Nguyễn Văn A"
                        value={shippingForm.receiverName}
                        onChange={(e) => setShippingForm({ ...shippingForm, receiverName: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '12px 14px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(255, 255, 255, 0.04)',
                          border: '1px solid rgba(255, 255, 255, 0.1)',
                          color: '#FFFFFF',
                          fontSize: '0.88rem',
                          outline: 'none',
                        }}
                      />
                    </div>
                    <div>
                      <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                        Số Điện Thoại Liên Hệ *
                      </label>
                      <input
                        type="tel"
                        required
                        placeholder="0912 345 678"
                        value={shippingForm.phone}
                        onChange={(e) => setShippingForm({ ...shippingForm, phone: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '12px 14px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(255, 255, 255, 0.04)',
                          border: '1px solid rgba(255, 255, 255, 0.1)',
                          color: '#FFFFFF',
                          fontSize: '0.88rem',
                          outline: 'none',
                        }}
                      />
                    </div>
                  </div>

                  <div style={{ marginBottom: '16px' }}>
                    <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                      Địa Chỉ Nhà / Tòa Nhà Chi Tiết *
                    </label>
                    <input
                      type="text"
                      required
                      placeholder="Số 88 Đồng Khởi, Căn hộ Penthouse B12..."
                      value={shippingForm.streetAddress}
                      onChange={(e) => setShippingForm({ ...shippingForm, streetAddress: e.target.value })}
                      style={{
                        width: '100%',
                        padding: '12px 14px',
                        borderRadius: '8px',
                        backgroundColor: 'rgba(255, 255, 255, 0.04)',
                        border: '1px solid rgba(255, 255, 255, 0.1)',
                        color: '#FFFFFF',
                        fontSize: '0.88rem',
                        outline: 'none',
                      }}
                    />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px', marginBottom: '16px' }}>
                    <div>
                      <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                        Tỉnh / Thành Phố
                      </label>
                      <input
                        type="text"
                        required
                        value={shippingForm.province}
                        onChange={(e) => setShippingForm({ ...shippingForm, province: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '10px 12px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(255, 255, 255, 0.04)',
                          border: '1px solid rgba(255, 255, 255, 0.1)',
                          color: '#FFFFFF',
                          fontSize: '0.82rem',
                          outline: 'none',
                        }}
                      />
                    </div>
                    <div>
                      <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                        Quận / Huyện
                      </label>
                      <input
                        type="text"
                        required
                        value={shippingForm.district}
                        onChange={(e) => setShippingForm({ ...shippingForm, district: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '10px 12px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(255, 255, 255, 0.04)',
                          border: '1px solid rgba(255, 255, 255, 0.1)',
                          color: '#FFFFFF',
                          fontSize: '0.82rem',
                          outline: 'none',
                        }}
                      />
                    </div>
                    <div>
                      <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                        Phường / Xã
                      </label>
                      <input
                        type="text"
                        required
                        value={shippingForm.ward}
                        onChange={(e) => setShippingForm({ ...shippingForm, ward: e.target.value })}
                        style={{
                          width: '100%',
                          padding: '10px 12px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(255, 255, 255, 0.04)',
                          border: '1px solid rgba(255, 255, 255, 0.1)',
                          color: '#FFFFFF',
                          fontSize: '0.82rem',
                          outline: 'none',
                        }}
                      />
                    </div>
                  </div>

                  <div>
                    <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                      Ghi Chú Cho Nghệ Nhân / Chuyên Viên Vận Chuyển
                    </label>
                    <textarea
                      rows={2}
                      placeholder="Ví dụ: Giao vào khung giờ chiều, khắc tên 'Forever Love' lên lòng nhẫn..."
                      value={shippingForm.customerNote}
                      onChange={(e) => setShippingForm({ ...shippingForm, customerNote: e.target.value })}
                      style={{
                        width: '100%',
                        padding: '12px 14px',
                        borderRadius: '8px',
                        backgroundColor: 'rgba(255, 255, 255, 0.04)',
                        border: '1px solid rgba(255, 255, 255, 0.1)',
                        color: '#FFFFFF',
                        fontSize: '0.88rem',
                        outline: 'none',
                        resize: 'none',
                      }}
                    />
                  </div>
                </div>

                {/* Section 4: Payment Method Strategy */}
                <div
                  style={{
                    backgroundColor: 'rgba(14, 16, 23, 0.7)',
                    backdropFilter: 'blur(16px)',
                    border: '1px solid rgba(255, 255, 255, 0.08)',
                    borderRadius: '16px',
                    padding: '28px',
                  }}
                >
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '20px' }}>
                    2. Phương Thức Thanh Toán Bảo Mật
                  </h3>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                    {/* Method 1: VNPAY */}
                    <label
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px',
                        padding: '16px 20px',
                        borderRadius: '12px',
                        border: paymentMethod === 'VNPAY' ? '1px solid #ff6584' : '1px solid rgba(255, 255, 255, 0.08)',
                        backgroundColor: paymentMethod === 'VNPAY' ? 'rgba(255, 101, 132, 0.12)' : 'rgba(255, 255, 255, 0.02)',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                      }}
                    >
                      <input
                        type="radio"
                        name="paymentMethod"
                        value="VNPAY"
                        checked={paymentMethod === 'VNPAY'}
                        onChange={() => setPaymentMethod('VNPAY')}
                        style={{ accentColor: '#ff6584', cursor: 'pointer' }}
                      />
                      <CreditCard size={24} color="#ff6584" />
                      <div style={{ flex: 1 }}>
                        <div style={{ fontSize: '0.92rem', fontWeight: 600, color: '#FFFFFF' }}>
                          Cổng Thanh Toán VNPAY (Thẻ ATM, Visa, QR Code)
                        </div>
                        <div style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginTop: '2px' }}>
                          Thanh toán bảo mật trực tiếp qua cổng sandbox VNPAY chính thức.
                        </div>
                      </div>
                    </label>

                    {/* Method 2: VietQR */}
                    <label
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px',
                        padding: '16px 20px',
                        borderRadius: '12px',
                        border: paymentMethod === 'BANK_TRANSFER' ? '1px solid #ff6584' : '1px solid rgba(255, 255, 255, 0.08)',
                        backgroundColor: paymentMethod === 'BANK_TRANSFER' ? 'rgba(255, 101, 132, 0.12)' : 'rgba(255, 255, 255, 0.02)',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                      }}
                    >
                      <input
                        type="radio"
                        name="paymentMethod"
                        value="BANK_TRANSFER"
                        checked={paymentMethod === 'BANK_TRANSFER'}
                        onChange={() => setPaymentMethod('BANK_TRANSFER')}
                        style={{ accentColor: '#ff6584', cursor: 'pointer' }}
                      />
                      <QrCode size={24} color="#E6CA65" />
                      <div style={{ flex: 1 }}>
                        <div style={{ fontSize: '0.92rem', fontWeight: 600, color: '#FFFFFF' }}>
                          Chuyển Khoản Ngân Hàng Tự Động (VietQR NAPAS 24/7)
                        </div>
                        <div style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginTop: '2px' }}>
                          Quét mã QR qua app ngân hàng MB Bank, tự động xác nhận số dư tức thì.
                        </div>
                      </div>
                    </label>

                    {/* Method 3: COD */}
                    <label
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '16px',
                        padding: '16px 20px',
                        borderRadius: '12px',
                        border: paymentMethod === 'COD' ? '1px solid #ff6584' : '1px solid rgba(255, 255, 255, 0.08)',
                        backgroundColor: paymentMethod === 'COD' ? 'rgba(255, 101, 132, 0.12)' : 'rgba(255, 255, 255, 0.02)',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease',
                      }}
                    >
                      <input
                        type="radio"
                        name="paymentMethod"
                        value="COD"
                        checked={paymentMethod === 'COD'}
                        onChange={() => setPaymentMethod('COD')}
                        style={{ accentColor: '#ff6584', cursor: 'pointer' }}
                      />
                      <Truck size={24} color="#10B981" />
                      <div style={{ flex: 1 }}>
                        <div style={{ fontSize: '0.92rem', fontWeight: 600, color: '#FFFFFF' }}>
                          Thanh Toán Khi Nhận Hàng (COD Đồng Kiểm Tận Tay)
                        </div>
                        <div style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginTop: '2px' }}>
                          Kiểm tra soi mã số kim cương GIA trước khi thanh toán cho nhân viên bảo an.
                        </div>
                      </div>
                    </label>
                  </div>
                </div>
              </div>

              {/* RIGHT: Order Confirmation Summary */}
              <div>
                <div
                  style={{
                    backgroundColor: 'rgba(14, 16, 23, 0.85)',
                    backdropFilter: 'blur(20px)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    borderRadius: '16px',
                    padding: '28px',
                    position: 'sticky',
                    top: '110px',
                  }}
                >
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '20px' }}>
                    Tuyệt Tác Trong Đơn Hàng
                  </h3>

                  {/* Item snippets */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginBottom: '20px', maxHeight: '240px', overflowY: 'auto' }}>
                    {items.map((item: CartItem) => (
                      <div key={item.variantId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.84rem' }}>
                        <div>
                          <div style={{ color: '#FFFFFF', fontWeight: 500 }}>
                            {item.productName} × {item.quantity}
                          </div>
                          <div style={{ color: 'var(--color-text-muted)', fontSize: '0.74rem' }}>
                            {item.sizeOption} • {item.materialType}
                          </div>
                        </div>
                        <div style={{ color: 'var(--color-gold-400)', fontWeight: 600 }}>
                          {(item.unitPrice * item.quantity).toLocaleString('vi-VN')}₫
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Financial breakdown */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.08)', marginBottom: '20px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                      <span>Tạm tính:</span>
                      <span style={{ color: '#FFFFFF' }}>{subtotal.toLocaleString('vi-VN')}₫</span>
                    </div>
                    {appliedCoupon && (
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', color: '#ff6584' }}>
                        <span>Ưu đãi ({appliedCoupon.code}):</span>
                        <span>-{discountAmount.toLocaleString('vi-VN')}₫</span>
                      </div>
                    )}
                    <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                      <span>Bảo hiểm & Vận chuyển:</span>
                      <span style={{ color: '#FFFFFF', fontWeight: 600 }}>{shippingFee === 0 ? 'Miễn phí' : `${shippingFee.toLocaleString('vi-VN')}₫`}</span>
                    </div>
                  </div>

                  {/* Total */}
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '28px' }}>
                    <span style={{ fontSize: '1.05rem', fontWeight: 600, color: '#FFFFFF' }}>Tổng thanh toán:</span>
                    <span style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-gold-400)' }}>
                      {finalTotal.toLocaleString('vi-VN')}₫
                    </span>
                  </div>

                  {/* Submit Button */}
                  <button
                    type="submit"
                    disabled={submitting}
                    className="btn-pill-sakura"
                    style={{ width: '100%', padding: '15px', fontSize: '0.95rem', cursor: submitting ? 'wait' : 'pointer' }}
                  >
                    {submitting ? 'Đang tạo đơn hàng...' : 'Xác nhận đặt hàng'} <ArrowRight size={18} />
                  </button>

                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px', fontSize: '0.75rem', color: 'var(--color-text-muted)', marginTop: '16px' }}>
                    <Lock size={13} /> Mã hóa giao dịch 256-bit SSL chuẩn PCI-DSS
                  </div>
                </div>
              </div>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
