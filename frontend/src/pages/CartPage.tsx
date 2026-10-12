import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { CartItem } from '../types/cart.types';
import {
  ShoppingBag,
  Trash2,
  Minus,
  Plus,
  ArrowRight,
  Tag,
  ShieldCheck,
  Truck,
  Sparkles,
  Gem,
  X,
} from 'lucide-react';

export const CartPage: React.FC = () => {
  const {
    items,
    totalQuantity,
    subtotal,
    appliedCoupon,
    discountAmount,
    finalTotal,
    updateQuantity,
    removeItem,
    clearCart,
    applyCoupon,
    removeCoupon,
  } = useCart();

  const [couponInput, setCouponInput] = useState('');
  const [couponLoading, setCouponLoading] = useState(false);
  const navigate = useNavigate();

  const handleApplyCoupon = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!couponInput.trim()) return;
    setCouponLoading(true);
    await applyCoupon(couponInput);
    setCouponLoading(false);
    setCouponInput('');
  };

  if (items.length === 0) {
    return (
      <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '140px', paddingBottom: '80px' }}>
        <div className="container" style={{ maxWidth: '600px', textAlign: 'center' }}>
          <div
            style={{
              width: '100px',
              height: '100px',
              borderRadius: '50%',
              backgroundColor: 'rgba(255, 101, 132, 0.1)',
              border: '1px solid rgba(255, 101, 132, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 24px',
            }}
          >
            <ShoppingBag size={44} color="#ff6584" />
          </div>

          <h1 style={{ fontSize: '2rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '12px' }}>
            Túi Trang Sức Trống
          </h1>
          <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.95rem', lineHeight: 1.6, marginBottom: '32px' }}>
            Quý khách chưa lựa chọn tác phẩm nào. Hãy khám phá bộ sưu tập nhẫn kim cương và vàng Ý mới nhất của chúng tôi.
          </p>

          <Link to="/products" className="btn-pill-sakura" style={{ padding: '12px 32px' }}>
            Khám Phá Tuyệt Tác <ArrowRight size={16} />
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '110px', paddingBottom: '90px' }}>
      <div className="container">
        {/* Page Title */}
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
            <Sparkles size={14} /> TÚI TRANG SỨC QUÝ TỘC
          </div>
          <h1 style={{ fontSize: '2.4rem', fontWeight: 700, color: '#FFFFFF' }}>
            Giỏ Hàng Của Quý Khách ({totalQuantity} món)
          </h1>
        </div>

        {/* 2-Column Layout: Items List + Order Summary */}
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 1.8fr) minmax(0, 1fr)', gap: '40px', alignItems: 'start' }}>
          {/* LEFT: Items List */}
          <div>
            <div
              style={{
                backgroundColor: 'rgba(14, 16, 23, 0.7)',
                backdropFilter: 'blur(16px)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                borderRadius: '16px',
                padding: '24px',
                marginBottom: '24px',
              }}
            >
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  paddingBottom: '16px',
                  borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
                  marginBottom: '20px',
                }}
              >
                <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--color-text-secondary)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                  Danh Sách Món Trang Sức
                </span>
                <button
                  onClick={clearCart}
                  style={{
                    backgroundColor: 'transparent',
                    border: 'none',
                    color: 'var(--color-text-muted)',
                    fontSize: '0.78rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px',
                  }}
                  onMouseEnter={(e) => (e.currentTarget.style.color = '#EF4444')}
                  onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-muted)')}
                >
                  <Trash2 size={14} /> Xóa toàn bộ túi
                </button>
              </div>

              {/* Items Rows */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                {items.map((item: CartItem) => (
                  <div
                    key={item.variantId}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '20px',
                      paddingBottom: '20px',
                      borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
                    }}
                  >
                    {/* Thumbnail */}
                    <div
                      style={{
                        width: '90px',
                        height: '90px',
                        borderRadius: '10px',
                        backgroundColor: '#0a0c12',
                        border: '1px solid rgba(255, 255, 255, 0.08)',
                        overflow: 'hidden',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                      }}
                    >
                      {item.thumbnailUrl ? (
                        <img src={item.thumbnailUrl} alt={item.productName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      ) : (
                        <Gem size={32} color="#ff9bb2" />
                      )}
                    </div>

                    {/* Info */}
                    <div style={{ flex: 1 }}>
                      <Link to={`/products/${item.productSlug}`}>
                        <h4
                          style={{ fontSize: '1rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '4px', transition: 'color 0.2s ease' }}
                          onMouseEnter={(e) => (e.currentTarget.style.color = 'var(--color-sakura-300)')}
                          onMouseLeave={(e) => (e.currentTarget.style.color = '#FFFFFF')}
                        >
                          {item.productName}
                        </h4>
                      </Link>

                      <div style={{ display: 'flex', gap: '12px', fontSize: '0.78rem', color: 'var(--color-text-muted)', marginBottom: '8px' }}>
                        {item.sizeOption && <span>Size: {item.sizeOption}</span>}
                        {item.materialType && <span>• Chất liệu: {item.materialType}</span>}
                        <span>• SKU: {item.sku}</span>
                      </div>

                      <div style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--color-gold-400)' }}>
                        {item.unitPrice.toLocaleString('vi-VN')}₫
                      </div>
                    </div>

                    {/* Quantity Stepper */}
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        backgroundColor: 'rgba(255, 255, 255, 0.04)',
                        border: '1px solid rgba(255, 255, 255, 0.1)',
                        borderRadius: '9999px',
                        padding: '4px 6px',
                      }}
                    >
                      <button
                        onClick={() => updateQuantity(item.variantId, item.quantity - 1)}
                        style={{
                          width: '26px',
                          height: '26px',
                          borderRadius: '50%',
                          backgroundColor: 'transparent',
                          border: 'none',
                          color: '#FFFFFF',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}
                      >
                        <Minus size={12} />
                      </button>
                      <span style={{ width: '30px', textAlign: 'center', fontSize: '0.85rem', fontWeight: 600, color: '#FFFFFF' }}>
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => updateQuantity(item.variantId, item.quantity + 1)}
                        style={{
                          width: '26px',
                          height: '26px',
                          borderRadius: '50%',
                          backgroundColor: 'transparent',
                          border: 'none',
                          color: '#FFFFFF',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}
                      >
                        <Plus size={12} />
                      </button>
                    </div>

                    {/* Subtotal of item */}
                    <div style={{ textAlign: 'right', minWidth: '120px' }}>
                      <div style={{ fontSize: '1.05rem', fontWeight: 700, color: '#FFFFFF' }}>
                        {(item.unitPrice * item.quantity).toLocaleString('vi-VN')}₫
                      </div>
                      <button
                        onClick={() => removeItem(item.variantId)}
                        style={{
                          backgroundColor: 'transparent',
                          border: 'none',
                          color: 'var(--color-text-muted)',
                          fontSize: '0.74rem',
                          cursor: 'pointer',
                          marginTop: '4px',
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.color = '#EF4444')}
                        onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-muted)')}
                      >
                        Xóa
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Back to Shopping */}
            <Link
              to="/products"
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '8px',
                color: 'var(--color-sakura-300)',
                fontSize: '0.85rem',
                fontWeight: 500,
              }}
            >
              ← Tiếp tục ngắm nhìn bộ sưu tập
            </Link>
          </div>

          {/* RIGHT: Financial Summary & Checkout Card */}
          <div>
            <div
              style={{
                backgroundColor: 'rgba(14, 16, 23, 0.85)',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                borderRadius: '16px',
                padding: '28px',
                boxShadow: '0 20px 45px rgba(0, 0, 0, 0.7)',
                position: 'sticky',
                top: '110px',
              }}
            >
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '20px' }}>
                Tổng Kết Đơn Hàng VIP
              </h3>

              {/* Coupon Form */}
              <div style={{ marginBottom: '24px' }}>
                <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                  Mã Ưu Đãi Độc Quyền (Coupon)
                </label>
                {appliedCoupon ? (
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      backgroundColor: 'rgba(255, 101, 132, 0.12)',
                      border: '1px solid rgba(255, 101, 132, 0.35)',
                      borderRadius: '8px',
                      padding: '10px 14px',
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Tag size={16} color="#ff6584" />
                      <span style={{ fontSize: '0.88rem', fontWeight: 700, color: 'var(--color-sakura-300)' }}>
                        {appliedCoupon.code}
                      </span>
                      <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>
                        (-{discountAmount.toLocaleString('vi-VN')}₫)
                      </span>
                    </div>
                    <button
                      onClick={removeCoupon}
                      style={{ backgroundColor: 'transparent', border: 'none', color: 'var(--color-text-muted)', cursor: 'pointer', display: 'flex' }}
                    >
                      <X size={16} />
                    </button>
                  </div>
                ) : (
                  <form onSubmit={handleApplyCoupon} style={{ display: 'flex', gap: '8px' }}>
                    <input
                      type="text"
                      placeholder="Nhập AURA10 hoặc DIAMOND500K"
                      value={couponInput}
                      onChange={(e) => setCouponInput(e.target.value)}
                      style={{
                        flex: 1,
                        padding: '10px 12px',
                        borderRadius: '8px',
                        backgroundColor: 'rgba(255, 255, 255, 0.04)',
                        border: '1px solid rgba(255, 255, 255, 0.1)',
                        color: '#FFFFFF',
                        fontSize: '0.85rem',
                        outline: 'none',
                        textTransform: 'uppercase',
                      }}
                    />
                    <button
                      type="submit"
                      disabled={couponLoading}
                      className="btn-outline-pill"
                      style={{ padding: '0 16px', fontSize: '0.8rem' }}
                    >
                      {couponLoading ? '...' : 'Áp Dụng'}
                    </button>
                  </form>
                )}
              </div>

              {/* Price Breakdown */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', paddingBottom: '20px', borderBottom: '1px solid rgba(255, 255, 255, 0.08)', marginBottom: '20px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', color: 'var(--color-text-secondary)' }}>
                  <span>Tạm tính giá trị trang sức:</span>
                  <span style={{ color: '#FFFFFF', fontWeight: 600 }}>{subtotal.toLocaleString('vi-VN')}₫</span>
                </div>

                {appliedCoupon && (
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', color: '#ff6584' }}>
                    <span>Ưu đãi voucher ({appliedCoupon.code}):</span>
                    <span>-{discountAmount.toLocaleString('vi-VN')}₫</span>
                  </div>
                )}

                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.88rem', color: 'var(--color-text-secondary)' }}>
                  <span>Bảo hiểm & Vận chuyển đặc biệt:</span>
                  <span style={{ color: '#10B981', fontWeight: 600 }}>MIỄN PHÍ VIP</span>
                </div>
              </div>

              {/* Total Row */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '28px' }}>
                <span style={{ fontSize: '1.05rem', fontWeight: 600, color: '#FFFFFF' }}>Tổng thanh toán:</span>
                <span style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-gold-400)' }}>
                  {finalTotal.toLocaleString('vi-VN')}₫
                </span>
              </div>

              {/* Checkout Action */}
              <button
                onClick={() => navigate('/checkout')}
                className="btn-pill-sakura"
                style={{ width: '100%', padding: '15px', fontSize: '0.95rem', cursor: 'pointer' }}
              >
                Tiến Hành Đặt Hàng VIP <ArrowRight size={18} />
              </button>

              {/* Assurance Trust Points */}
              <div style={{ marginTop: '24px', paddingTop: '18px', borderTop: '1px solid rgba(255, 255, 255, 0.06)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>
                  <ShieldCheck size={16} color="#ff6584" /> Cam kết 100% kim cương tự nhiên & vàng chuẩn tuổi
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>
                  <Truck size={16} color="#ff6584" /> Nhân viên AURA giao hàng tận tay có bảo an hộ tống
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
