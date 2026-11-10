import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { orderService } from '../services/orderService';
import { paymentService } from '../services/paymentService';
import { authService } from '../services/authService';
import { addressService } from '../services/addressService';
import { wishlistService } from '../services/wishlistService';
import { OrderSummary, Order, OrderItem, OrderStatus } from '../types/order.types';
import { Address, AddressRequest } from '../types/address.types';
import { WishlistItem } from '../types/cart.types';
import {
  User,
  Package,
  MapPin,
  Heart,
  Plus,
  Sparkles,
  ChevronRight,
  X,
  RefreshCw,
  Gem,
} from 'lucide-react';

const PasswordForm: React.FC = () => {
  const { success, error } = useToast();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  return (
    <form
      onSubmit={async (event) => {
        event.preventDefault();
        try {
          await authService.changePassword(currentPassword, newPassword);
          success('Đã đổi mật khẩu.', 'Tài khoản');
          setCurrentPassword('');
          setNewPassword('');
        } catch {
          error('Mật khẩu hiện tại không đúng.');
        }
      }}
      style={{ marginTop: '8px', display: 'grid', gap: '12px', paddingTop: '20px', borderTop: '1px solid rgba(255,255,255,0.12)' }}
    >
      <div style={{ color: '#fff', fontWeight: 700, fontSize: '1.05rem' }}>Đổi mật khẩu</div>
      <input type="password" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} placeholder="Mật khẩu hiện tại" required style={{ padding: '12px 14px', borderRadius: '10px', background: '#1a1d27', color: '#fff', border: '1px solid rgba(255,255,255,0.28)', fontSize: '0.95rem' }} />
      <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} placeholder="Mật khẩu mới" required minLength={6} style={{ padding: '12px 14px', borderRadius: '10px', background: '#1a1d27', color: '#fff', border: '1px solid rgba(255,255,255,0.28)', fontSize: '0.95rem' }} />
      <button className="btn-pill-sakura" style={{ padding: '12px' }}>Lưu mật khẩu mới</button>
    </form>
  );
};

export const CustomerPortalPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const currentTab = searchParams.get('tab') || 'profile';

  const { user, isAuthenticated, openAuthModal, logout } = useAuth();
  const { success, error, info } = useToast();

  // Orders State
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [loadingOrders, setLoadingOrders] = useState(false);
  const [selectedOrderDetails, setSelectedOrderDetails] = useState<Order | null>(null);
  const [orderFilterStatus, setOrderFilterStatus] = useState<OrderStatus | undefined>(undefined);

  // Addresses State
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [showAddAddressModal, setShowAddAddressModal] = useState(false);
  const [newAddressForm, setNewAddressForm] = useState<AddressRequest>({
    receiverName: user?.fullName || '',
    phone: user?.phone || '',
    streetAddress: '',
    ward: 'Phường Bến Nghé',
    district: 'Quận 1',
    province: 'TP. Hồ Chí Minh',
    isDefault: false,
  });

  // Wishlist State
  const [wishlist, setWishlist] = useState<WishlistItem[]>([]);

  // Tab switcher
  const setTab = (tab: string) => {
    const next = new URLSearchParams(searchParams);
    next.set('tab', tab);
    setSearchParams(next);
  };

  // Load data based on tab
  useEffect(() => {
    if (!isAuthenticated) return;

    if (currentTab === 'orders') {
      setLoadingOrders(true);
      orderService
        .getUserOrders(orderFilterStatus)
        .then((res: any) => setOrders(res.content))
        .catch(() => setOrders([]))
        .finally(() => setLoadingOrders(false));
    } else if (currentTab === 'addresses') {
      addressService
        .getAddresses()
        .then((res: Address[]) => setAddresses(res))
        .catch(() => {});
    } else if (currentTab === 'wishlist') {
      wishlistService
        .getWishlist()
        .then((res: WishlistItem[]) => setWishlist(res))
        .catch(() => {});
    }
  }, [isAuthenticated, currentTab, orderFilterStatus, user]);

  if (!isAuthenticated) {
    return (
      <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '150px', textAlign: 'center' }}>
        <div className="container" style={{ maxWidth: '520px' }}>
          <User size={56} color="#ff6584" style={{ marginBottom: '16px' }} />
          <h2 style={{ fontSize: '2rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '12px' }}>
            Tài khoản
          </h2>
          <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.95rem', lineHeight: 1.6, marginBottom: '28px' }}>
            Đăng nhập để xem đơn hàng, địa chỉ và sản phẩm yêu thích.
          </p>
          <button onClick={() => openAuthModal('login')} className="btn-pill-sakura" style={{ padding: '12px 32px' }}>
            Đăng nhập để xem tài khoản
          </button>
        </div>
      </div>
    );
  }

  // Handle Order Details View
  const handleViewOrder = async (orderCode: string) => {
    try {
      const details = await orderService.getOrderDetails(orderCode);
      setSelectedOrderDetails(details);
    } catch {
      error('Không tải được chi tiết đơn hàng.', 'Đơn hàng');
    }
  };

  // Handle Cancel Order
  const handleCancelOrder = async (orderCode: string) => {
    if (!window.confirm(`Quý khách có chắc chắn muốn hủy đơn hàng #${orderCode}?`)) return;
    try {
      await orderService.cancelOrder(orderCode, 'Khách hàng yêu cầu hủy đơn');
      success(`Đơn hàng #${orderCode} đã được hủy thành công.`, 'Hủy Đơn');
      // Reload orders
      orderService.getUserOrders(orderFilterStatus).then((res: any) => setOrders(res.content));
      setSelectedOrderDetails(null);
    } catch {
      error('Không hủy được đơn hàng lúc này.', 'Đơn hàng');
    }
  };

  // Handle Add Address
  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const created = await addressService.createAddress(newAddressForm);
      setAddresses((prev) => [...prev, created]);
      success('Đã thêm địa chỉ giao hàng.', 'Địa chỉ');
      setShowAddAddressModal(false);
      setNewAddressForm({
        receiverName: user?.fullName || '',
        phone: user?.phone || '',
        streetAddress: '',
        ward: '',
        district: '',
        province: '',
        isDefault: false,
      });
    } catch {
      error('Không thêm được địa chỉ.', 'Địa chỉ');
    }
  };

  // Handle Delete Address
  const handleDeleteAddress = async (id: number) => {
    try {
      await addressService.deleteAddress(id);
    } catch {
      // Ignore
    }
    setAddresses((prev) => prev.filter((a) => a.id !== id));
    info('Đã xóa địa chỉ khỏi sổ tay.');
  };

  // Handle Set Default Address
  const handleSetDefaultAddress = async (id: number) => {
    try {
      await addressService.setDefaultAddress(id);
    } catch {
      // Ignore
    }
    setAddresses((prev) =>
      prev.map((a) => ({
        ...a,
        isDefault: a.id === id,
      }))
    );
    success('Đã đặt làm địa chỉ giao hàng mặc định.', 'Cập Nhật');
  };

  const getOrderStatusBadge = (status: OrderStatus) => {
    switch (status) {
      case 'PENDING':
        return { label: 'Chờ Xác Nhận', color: '#F59E0B', bg: 'rgba(245, 158, 11, 0.15)' };
      case 'CONFIRMED':
        return { label: 'Đang Chế Tác', color: '#ff6584', bg: 'rgba(255, 101, 132, 0.15)' };
      case 'SHIPPING':
        return { label: 'Bảo An Vận Chuyển', color: '#3B82F6', bg: 'rgba(59, 130, 246, 0.15)' };
      case 'DELIVERED':
      case 'COMPLETED':
        return { label: 'Đã giao', color: '#10B981', bg: 'rgba(16, 185, 129, 0.15)' };
      case 'CANCELLED':
        return { label: 'Đã Hủy', color: '#EF4444', bg: 'rgba(239, 68, 68, 0.15)' };
      default:
        return { label: status, color: '#9ea3b0', bg: 'rgba(255, 255, 255, 0.1)' };
    }
  };

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '110px', paddingBottom: '90px' }}>
      <div className="container">
        {/* Header Title */}
        <div style={{ marginBottom: '36px' }}>
          <div
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '6px',
              fontSize: '0.78rem',
              letterSpacing: '0.15em',
              color: 'var(--color-sakura-300)',
              textTransform: 'uppercase',
              fontWeight: 600,
              marginBottom: '8px',
            }}
          >
            <Sparkles size={14} /> Tài khoản
          </div>
          <h1 style={{ fontSize: '2.4rem', fontWeight: 700, color: '#FFFFFF' }}>
            Hồ sơ
          </h1>
        </div>

        {/* 2-Column Dashboard Layout */}
        <div style={{ display: 'grid', gridTemplateColumns: '260px 1fr', gap: '36px', alignItems: 'start' }}>
          {/* Navigation Sidebar */}
          <aside
            style={{
              backgroundColor: 'rgba(14, 16, 23, 0.7)',
              backdropFilter: 'blur(16px)',
              border: '1px solid rgba(255, 255, 255, 0.08)',
              borderRadius: '16px',
              padding: '24px 16px',
            }}
          >
            {/* User Avatar Card */}
            <div style={{ textAlign: 'center', paddingBottom: '20px', borderBottom: '1px solid rgba(255, 255, 255, 0.08)', marginBottom: '18px' }}>
              <div
                style={{
                  width: '64px',
                  height: '64px',
                  borderRadius: '50%',
                  backgroundColor: 'rgba(255, 101, 132, 0.2)',
                  border: '2px solid rgba(255, 101, 132, 0.5)',
                  color: 'var(--color-sakura-300)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '1.4rem',
                  fontWeight: 700,
                  margin: '0 auto 12px',
                }}
              >
                {user?.firstName ? user.firstName.charAt(0).toUpperCase() : user?.username.charAt(0).toUpperCase()}
              </div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600, color: '#FFFFFF' }}>
                {user?.fullName || user?.username}
              </h3>
              <div style={{ fontSize: '0.78rem', color: 'var(--color-gold-400)', marginTop: '4px', fontWeight: 500 }}>
                Tài khoản của bạn
              </div>
            </div>

            {/* Menu Items */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button
                onClick={() => setTab('profile')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '12px 14px',
                  borderRadius: '10px',
                  border: 'none',
                  fontSize: '0.86rem',
                  fontWeight: currentTab === 'profile' ? 600 : 400,
                  backgroundColor: currentTab === 'profile' ? 'rgba(255, 101, 132, 0.15)' : 'transparent',
                  color: currentTab === 'profile' ? 'var(--color-sakura-300)' : 'var(--color-text-secondary)',
                  cursor: 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.2s ease',
                }}
              >
                <User size={16} /> Thông Tin Cá Nhân
              </button>

              <button
                onClick={() => setTab('orders')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '12px 14px',
                  borderRadius: '10px',
                  border: 'none',
                  fontSize: '0.86rem',
                  fontWeight: currentTab === 'orders' ? 600 : 400,
                  backgroundColor: currentTab === 'orders' ? 'rgba(255, 101, 132, 0.15)' : 'transparent',
                  color: currentTab === 'orders' ? 'var(--color-sakura-300)' : 'var(--color-text-secondary)',
                  cursor: 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.2s ease',
                }}
              >
                <Package size={16} /> Lịch Sử Đơn Hàng
              </button>

              <button
                onClick={() => setTab('addresses')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '12px 14px',
                  borderRadius: '10px',
                  border: 'none',
                  fontSize: '0.86rem',
                  fontWeight: currentTab === 'addresses' ? 600 : 400,
                  backgroundColor: currentTab === 'addresses' ? 'rgba(255, 101, 132, 0.15)' : 'transparent',
                  color: currentTab === 'addresses' ? 'var(--color-sakura-300)' : 'var(--color-text-secondary)',
                  cursor: 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.2s ease',
                }}
              >
                <MapPin size={16} /> Sổ Địa Chỉ Bảo Hiểm
              </button>

              <button
                onClick={() => setTab('wishlist')}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '12px 14px',
                  borderRadius: '10px',
                  border: 'none',
                  fontSize: '0.86rem',
                  fontWeight: currentTab === 'wishlist' ? 600 : 400,
                  backgroundColor: currentTab === 'wishlist' ? 'rgba(255, 101, 132, 0.15)' : 'transparent',
                  color: currentTab === 'wishlist' ? 'var(--color-sakura-300)' : 'var(--color-text-secondary)',
                  cursor: 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.2s ease',
                }}
              >
                <Heart size={16} /> Yêu thích
              </button>

              <button
                onClick={logout}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '12px 14px',
                  borderRadius: '10px',
                  border: 'none',
                  fontSize: '0.86rem',
                  color: '#EF4444',
                  backgroundColor: 'transparent',
                  cursor: 'pointer',
                  textAlign: 'left',
                  marginTop: '12px',
                  borderTop: '1px solid rgba(255, 255, 255, 0.06)',
                  paddingTop: '16px',
                }}
              >
                Đăng Xuất Khỏi Thiết Bị
              </button>
            </div>
          </aside>

          {/* Main Display Pane */}
          <div>
            {/* TAB 1: PROFILE */}
            {currentTab === 'profile' && (
              <div
                style={{
                  backgroundColor: 'rgba(14, 16, 23, 0.7)',
                  backdropFilter: 'blur(16px)',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                  borderRadius: '16px',
                  padding: '32px',
                }}
              >
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '24px' }}>
                  Thông tin khách hàng
                </h3>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '28px' }}>
                  {[
                    ['Họ và Tên', user?.fullName || 'Chưa cập nhật', '#FFFFFF'],
                    ['Tên tài khoản', user?.username || 'Chưa cập nhật', '#ffb3c4'],
                    ['Email', user?.email || 'Chưa cập nhật', '#FFFFFF'],
                    ['Số điện thoại', user?.phone || 'Chưa cập nhật', '#FFFFFF'],
                  ].map(([label, value, color]) => (
                    <div key={label} style={{ padding: '16px 18px', backgroundColor: '#1a1d27', border: '1px solid rgba(255,255,255,0.16)', borderRadius: '12px' }}>
                      <div style={{ fontSize: '0.78rem', color: '#b7becc', marginBottom: '6px', letterSpacing: '0.04em' }}>{label}</div>
                      <div style={{ fontSize: '1.05rem', color, fontWeight: 650 }}>{value}</div>
                    </div>
                  ))}
                </div>
                <PasswordForm />

              </div>
            )}

            {/* TAB 2: ORDERS */}
            {currentTab === 'orders' && (
              <div>
                {/* Orders Toolbar / Filter */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF' }}>
                    Lịch Sử Đơn Hàng ({orders.length})
                  </h3>

                  <select
                    value={orderFilterStatus || ''}
                    onChange={(e) => setOrderFilterStatus(e.target.value ? (e.target.value as OrderStatus) : undefined)}
                    style={{
                      backgroundColor: 'rgba(255, 255, 255, 0.06)',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                      color: '#FFFFFF',
                      borderRadius: '8px',
                      padding: '8px 14px',
                      fontSize: '0.82rem',
                      outline: 'none',
                    }}
                  >
                    <option value="" style={{ backgroundColor: '#0e1017' }}>Tất cả trạng thái</option>
                    <option value="PENDING" style={{ backgroundColor: '#0e1017' }}>Chờ xác nhận</option>
                    <option value="CONFIRMED" style={{ backgroundColor: '#0e1017' }}>Đang chế tác</option>
                    <option value="SHIPPING" style={{ backgroundColor: '#0e1017' }}>Đang vận chuyển</option>
                    <option value="DELIVERED" style={{ backgroundColor: '#0e1017' }}>Đã hoàn tất</option>
                    <option value="CANCELLED" style={{ backgroundColor: '#0e1017' }}>Đã hủy</option>
                  </select>
                </div>

                {loadingOrders ? (
                  <div style={{ textAlign: 'center', padding: '60px 0' }}>
                    <RefreshCw size={28} color="#ff6584" style={{ animation: 'spin 1.5s linear infinite' }} />
                  </div>
                ) : orders.length > 0 ? (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                    {orders.map((o) => {
                      const badge = getOrderStatusBadge(o.orderStatus);
                      return (
                        <div
                          key={o.id}
                          style={{
                            backgroundColor: 'rgba(14, 16, 23, 0.7)',
                            border: '1px solid rgba(255, 255, 255, 0.08)',
                            borderRadius: '14px',
                            padding: '20px 24px',
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            flexWrap: 'wrap',
                            gap: '16px',
                          }}
                        >
                          <div>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '6px' }}>
                              <span style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--color-sakura-300)' }}>
                                #{o.orderCode}
                              </span>
                              <span
                                style={{
                                  fontSize: '0.72rem',
                                  fontWeight: 600,
                                  color: badge.color,
                                  backgroundColor: badge.bg,
                                  padding: '3px 8px',
                                  borderRadius: '6px',
                                }}
                              >
                                {badge.label}
                              </span>
                            </div>
                            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>
                              Ngày đặt: {new Date(o.createdAt).toLocaleDateString('vi-VN')} • Phương thức: {o.paymentMethod}
                            </div>
                            {o.firstItemName && (
                              <div style={{ fontSize: '0.88rem', color: 'var(--color-text-secondary)', marginTop: '4px' }}>
                                {o.firstItemName} {o.itemCount > 1 ? `và ${o.itemCount - 1} món khác` : ''}
                              </div>
                            )}
                          </div>

                          <div style={{ textAlign: 'right' }}>
                            <div style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--color-gold-400)', marginBottom: '8px' }}>
                              {o.totalAmount.toLocaleString('vi-VN')}₫
                            </div>
                            <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end', flexWrap: 'wrap' }}>
                            <button
                              onClick={() => handleViewOrder(o.orderCode)}
                              className="btn-outline-pill"
                              style={{ padding: '6px 16px', fontSize: '0.78rem' }}
                            >
                              Chi tiết <ChevronRight size={13} />
                            </button>
                            {o.paymentMethod === 'VNPAY' && o.paymentStatus === 'PENDING' && o.orderStatus !== 'CANCELLED' && (
                              <button
                                className="btn-pill-sakura"
                                style={{ padding: '6px 16px', fontSize: '0.78rem' }}
                                onClick={async () => {
                                  try {
                                    const detail = await orderService.getOrderDetails(o.orderCode);
                                    sessionStorage.setItem('aura_vnpay_pending', JSON.stringify({
                                      orderCode: detail.orderCode,
                                      accessToken: detail.guestAccessToken,
                                      items: (detail.items || []).map((item) => ({
                                        id: item.id,
                                        variantId: item.variantId,
                                        productId: item.productId,
                                        productName: item.productName,
                                        productSlug: item.productSlug,
                                        sku: item.sku,
                                        unitPrice: item.unitPrice,
                                        originalPrice: item.unitPrice,
                                        quantity: item.quantity,
                                        subtotal: item.subtotal,
                                        thumbnailUrl: item.thumbnailUrl,
                                        availableStock: item.quantity,
                                        inStock: true,
                                      })),
                                    }));
                                    const payment = await paymentService.initiatePayment(detail.orderCode, detail.guestAccessToken);
                                    if (payment.paymentUrl) window.location.href = payment.paymentUrl;
                                  } catch {
                                    error('Không thanh toán lại được đơn này.');
                                  }
                                }}
                              >
                                Thanh toán lại
                              </button>
                            )}
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <div style={{ textAlign: 'center', padding: '60px 0', backgroundColor: 'rgba(14, 16, 23, 0.4)', borderRadius: '16px' }}>
                    <Package size={44} color="#ff6584" style={{ marginBottom: '12px', opacity: 0.7 }} />
                    <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                      Quý khách chưa có đơn hàng nào trong mục này.
                    </p>
                  </div>
                )}
              </div>
            )}

            {/* TAB 3: ADDRESSES */}
            {currentTab === 'addresses' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF' }}>
                    Sổ Địa Chỉ Nhận Hàng Bảo Hiểm
                  </h3>
                  <button
                    onClick={() => setShowAddAddressModal(true)}
                    className="btn-pill-sakura"
                    style={{ padding: '8px 18px', fontSize: '0.82rem' }}
                  >
                    <Plus size={15} /> Thêm Địa Chỉ Mới
                  </button>
                </div>

                {addresses.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '64px 24px', backgroundColor: '#12141c', border: '1px solid rgba(255,255,255,0.14)', borderRadius: '16px' }}>
                    <MapPin size={42} color="#ff9bb2" style={{ marginBottom: '14px' }} />
                    <p style={{ color: '#d5dbe6', fontSize: '1rem', marginBottom: '8px', fontWeight: 600 }}>Chưa có địa chỉ nhận hàng</p>
                    <p style={{ color: '#9ea3b0', fontSize: '0.9rem' }}>Bấm Thêm địa chỉ mới để lưu nơi giao hàng.</p>
                  </div>
                ) : (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px' }}>
                  {addresses.map((addr) => (
                    <div
                      key={addr.id}
                      style={{
                        backgroundColor: '#1a1d27',
                        border: addr.isDefault ? '1px solid #ff6584' : '1px solid rgba(255, 255, 255, 0.18)',
                        borderRadius: '14px',
                        padding: '20px',
                        position: 'relative',
                      }}
                    >
                      {addr.isDefault && (
                        <span
                          style={{
                            position: 'absolute',
                            top: '14px',
                            right: '14px',
                            fontSize: '0.68rem',
                            fontWeight: 700,
                            color: '#ff6584',
                            backgroundColor: 'rgba(255, 101, 132, 0.15)',
                            padding: '3px 8px',
                            borderRadius: '4px',
                          }}
                        >
                          MẶC ĐỊNH
                        </span>
                      )}

                      <h4 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '8px' }}>
                        {addr.receiverName}
                      </h4>
                      <p style={{ fontSize: '0.95rem', color: '#FFFFFF', marginBottom: '8px' }}>
                        {addr.phone}
                      </p>
                      <p style={{ fontSize: '0.95rem', color: '#e4e8f0', lineHeight: 1.55, marginBottom: '16px' }}>
                        {[addr.streetAddress, addr.ward, addr.district, addr.province].filter(Boolean).join(', ')}
                      </p>

                      <div style={{ display: 'flex', gap: '12px', alignItems: 'center', paddingTop: '12px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                        {!addr.isDefault && (
                          <button
                            onClick={() => handleSetDefaultAddress(addr.id)}
                            style={{
                              backgroundColor: 'transparent',
                              border: 'none',
                              color: '#ffb3c4',
                              fontSize: '0.86rem',
                              fontWeight: 600,
                              cursor: 'pointer',
                              textDecoration: 'underline',
                            }}
                          >
                            Đặt làm mặc định
                          </button>
                        )}
                        <button
                          onClick={() => handleDeleteAddress(addr.id)}
                          style={{
                            backgroundColor: 'transparent',
                            border: 'none',
                            color: '#f0b4b4',
                            fontSize: '0.86rem',
                            fontWeight: 600,
                            cursor: 'pointer',
                            marginLeft: 'auto',
                          }}
                          onMouseEnter={(e) => (e.currentTarget.style.color = '#ff6b6b')}
                          onMouseLeave={(e) => (e.currentTarget.style.color = '#f0b4b4')}
                        >
                          Xóa
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
                )}
              </div>
            )}

            {/* TAB 4: WISHLIST */}
            {currentTab === 'wishlist' && (
              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '20px' }}>
                  Sản phẩm yêu thích
                </h3>

                {wishlist.length > 0 ? (
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '20px' }}>
                    {wishlist.map((item) => (
                      <div
                        key={item.id}
                        style={{
                          backgroundColor: 'rgba(14, 16, 23, 0.7)',
                          border: '1px solid rgba(255, 255, 255, 0.08)',
                          borderRadius: '14px',
                          padding: '16px',
                        }}
                      >
                        <div style={{ height: '160px', borderRadius: '8px', backgroundColor: '#0a0c12', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '12px' }}>
                          <Gem size={44} color="#ff9bb2" />
                        </div>
                        <h4 style={{ fontSize: '0.92rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '6px' }}>
                          {item.productName}
                        </h4>
                        <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--color-gold-400)', marginBottom: '14px' }}>
                          {item.minPrice.toLocaleString('vi-VN')}₫
                        </div>
                        <Link to={`/products/${item.productSlug}`} className="btn-pill-sakura" style={{ display: 'block', textAlign: 'center', padding: '8px', fontSize: '0.8rem' }}>
                          Xem Tác Phẩm
                        </Link>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div style={{ textAlign: 'center', padding: '60px 0', backgroundColor: 'rgba(14, 16, 23, 0.4)', borderRadius: '16px' }}>
                    <Heart size={44} color="#ff6584" style={{ marginBottom: '12px', opacity: 0.7 }} />
                    <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem', marginBottom: '18px' }}>
                      Quý khách chưa lưu tác phẩm nào vào danh sách yêu thích.
                    </p>
                    <Link to="/products" className="btn-pill-sakura" style={{ padding: '10px 24px' }}>
                      Khám Phá Bộ Sưu Tập
                    </Link>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* ORDER DETAILS MODAL */}
      {selectedOrderDetails && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            zIndex: 9998,
            backgroundColor: 'rgba(5, 5, 8, 0.85)',
            backdropFilter: 'blur(16px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
          }}
          onClick={() => setSelectedOrderDetails(null)}
        >
          <div
            style={{
              width: '100%',
              maxWidth: '560px',
              backgroundColor: '#0e1017',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              borderRadius: '16px',
              padding: '32px',
              maxHeight: '90vh',
              overflowY: 'auto',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <div>
                <div style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>CHI TIẾT ĐƠN HÀNG</div>
                <h3 style={{ fontSize: '1.3rem', fontWeight: 700, color: 'var(--color-sakura-300)' }}>
                  #{selectedOrderDetails.orderCode}
                </h3>
              </div>
              <button
                onClick={() => setSelectedOrderDetails(null)}
                style={{ backgroundColor: 'transparent', border: 'none', color: 'var(--color-text-muted)', cursor: 'pointer' }}
              >
                <X size={20} />
              </button>
            </div>

            <div style={{ marginBottom: '20px', padding: '14px', backgroundColor: 'rgba(255, 255, 255, 0.03)', borderRadius: '10px' }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)', marginBottom: '4px' }}>Địa chỉ nhận hàng:</div>
              <div style={{ fontSize: '0.88rem', color: '#FFFFFF' }}>{selectedOrderDetails.shippingAddressSnapshot}</div>
            </div>

            {/* Items */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '24px' }}>
              {selectedOrderDetails.items?.map((item: OrderItem) => (
                <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingBottom: '10px', borderBottom: '1px solid rgba(255, 255, 255, 0.05)' }}>
                  <div>
                    <div style={{ fontSize: '0.88rem', color: '#FFFFFF', fontWeight: 600 }}>{item.productName}</div>
                    <div style={{ fontSize: '0.76rem', color: 'var(--color-text-muted)' }}>
                      {item.variantAttributes || item.sku} × {item.quantity}
                    </div>
                  </div>
                  <div style={{ fontSize: '0.92rem', color: 'var(--color-gold-400)', fontWeight: 700 }}>
                    {item.subtotal.toLocaleString('vi-VN')}₫
                  </div>
                </div>
              ))}
            </div>

            {/* Total */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', paddingTop: '14px', borderTop: '1px solid rgba(255, 255, 255, 0.1)', marginBottom: '28px' }}>
              <span style={{ fontSize: '1rem', color: '#FFFFFF', fontWeight: 600 }}>Tổng thanh toán:</span>
              <span style={{ fontSize: '1.4rem', color: 'var(--color-gold-400)', fontWeight: 800 }}>
                {selectedOrderDetails.totalAmount.toLocaleString('vi-VN')}₫
              </span>
            </div>

            {/* Cancel Button if PENDING */}
            {selectedOrderDetails.orderStatus === 'PENDING' && (
              <button
                onClick={() => handleCancelOrder(selectedOrderDetails.orderCode)}
                style={{
                  width: '100%',
                  padding: '12px',
                  borderRadius: '9999px',
                  backgroundColor: 'rgba(239, 68, 68, 0.15)',
                  border: '1px solid rgba(239, 68, 68, 0.4)',
                  color: '#EF4444',
                  fontSize: '0.86rem',
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                Yêu Cầu Hủy Đơn Hàng Này
              </button>
            )}
          </div>
        </div>
      )}

      {/* ADD ADDRESS MODAL */}
      {showAddAddressModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            zIndex: 9998,
            backgroundColor: 'rgba(5, 5, 8, 0.85)',
            backdropFilter: 'blur(16px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
          }}
          onClick={() => setShowAddAddressModal(false)}
        >
          <div
            style={{
              width: '100%',
              maxWidth: '480px',
              backgroundColor: '#0e1017',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              borderRadius: '16px',
              padding: '32px',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '20px' }}>
              Thêm Địa Chỉ Giao Hàng Bảo Hiểm
            </h3>

            <form onSubmit={handleAddAddress}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px', marginBottom: '14px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Họ Tên</label>
                  <input
                    type="text"
                    required
                    value={newAddressForm.receiverName}
                    onChange={(e) => setNewAddressForm({ ...newAddressForm, receiverName: e.target.value })}
                    style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Điện Thoại</label>
                  <input
                    type="tel"
                    required
                    value={newAddressForm.phone}
                    onChange={(e) => setNewAddressForm({ ...newAddressForm, phone: e.target.value })}
                    style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                  />
                </div>
              </div>

              <div style={{ marginBottom: '14px' }}>
                <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Địa Chỉ Chi Tiết</label>
                <input
                  type="text"
                  required
                  placeholder="Số nhà, Tòa nhà, Đường..."
                  value={newAddressForm.streetAddress}
                  onChange={(e) => setNewAddressForm({ ...newAddressForm, streetAddress: e.target.value })}
                  style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '10px', marginBottom: '20px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.74rem', color: 'var(--color-text-secondary)', marginBottom: '4px' }}>Tỉnh/TP</label>
                  <input
                    type="text"
                    value={newAddressForm.province}
                    onChange={(e) => setNewAddressForm({ ...newAddressForm, province: e.target.value })}
                    style={{ width: '100%', padding: '8px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none', fontSize: '0.8rem' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.74rem', color: 'var(--color-text-secondary)', marginBottom: '4px' }}>Quận/Huyện</label>
                  <input
                    type="text"
                    value={newAddressForm.district}
                    onChange={(e) => setNewAddressForm({ ...newAddressForm, district: e.target.value })}
                    style={{ width: '100%', padding: '8px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none', fontSize: '0.8rem' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.74rem', color: 'var(--color-text-secondary)', marginBottom: '4px' }}>Phường/Xã</label>
                  <input
                    type="text"
                    value={newAddressForm.ward}
                    onChange={(e) => setNewAddressForm({ ...newAddressForm, ward: e.target.value })}
                    style={{ width: '100%', padding: '8px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none', fontSize: '0.8rem' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => setShowAddAddressModal(false)}
                  style={{ padding: '10px 18px', borderRadius: '9999px', backgroundColor: 'transparent', border: '1px solid rgba(255,255,255,0.1)', color: 'var(--color-text-secondary)', cursor: 'pointer' }}
                >
                  Đóng
                </button>
                <button type="submit" className="btn-pill-sakura" style={{ padding: '10px 22px' }}>
                  Lưu Địa Chỉ
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
