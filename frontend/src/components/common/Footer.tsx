import React from 'react';
import { Gem, ShieldCheck, Truck, RefreshCw, Award } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer style={{
      backgroundColor: 'var(--color-bg-surface)',
      borderTop: '1px solid var(--color-border)',
      marginTop: '80px',
      paddingTop: '60px',
      paddingBottom: '30px',
    }}>
      {/* Value Propositions / Brand Trust Badges */}
      <div className="container" style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
        gap: '24px',
        paddingBottom: '48px',
        borderBottom: '1px solid var(--color-border)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <Award size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Sản phẩm trong kho</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Giá và tồn kho theo từng món đang bán</p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <Truck size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Thanh toán</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>COD hoặc VNPay</p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <RefreshCw size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Nhận xét</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Chỉ hiện lời khách đã mua và được duyệt</p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <ShieldCheck size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Tài khoản</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Đăng nhập để xem đơn và yêu thích</p>
          </div>
        </div>
      </div>

      {/* Main Footer Links */}
      <div className="container" style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '40px',
        paddingTop: '48px',
        paddingBottom: '48px',
      }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px' }}>
            <Gem size={22} color="var(--color-gold-500)" />
            <span className="heading-serif gold-gradient-text" style={{ fontSize: '1.2rem', fontWeight: 700 }}>AURA JEWELRY</span>
          </div>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', lineHeight: 1.7 }}>
            Cửa hàng trang sức AURA: xem hàng, đặt hàng và thanh toán khi nhận hoặc qua VNPay.
          </p>
        </div>

        <div>
          <h4 className="heading-serif" style={{ fontSize: '0.9rem', color: 'var(--color-gold-400)', marginBottom: '16px' }}>Dòng Sản Phẩm</h4>
          <ul style={{ listStyle: 'none', fontSize: '0.85rem', color: 'var(--color-text-secondary)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <li><a href="/products?category=nhan-kim-cuong">Nhẫn Kim Cương</a></li>
            <li><a href="/products?category=bong-tai">Bông Tai Quý Phái</a></li>
            <li><a href="/products?category=trang-suc-vang-18k">Trang Sức Vàng 18K</a></li>
            <li><a href="/products?category=trang-suc-bac">Trang Sức Bạc Ý 925</a></li>
          </ul>
        </div>

        <div>
          <h4 className="heading-serif" style={{ fontSize: '0.9rem', color: 'var(--color-gold-400)', marginBottom: '16px' }}>Mua hàng</h4>
          <ul style={{ listStyle: 'none', fontSize: '0.85rem', color: 'var(--color-text-secondary)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <li><a href="/products">Tất cả sản phẩm</a></li>
            <li><a href="/compare">So sánh</a></li>
            <li><a href="/cart">Giỏ hàng</a></li>
            <li><a href="/account">Tài khoản</a></li>
          </ul>
        </div>

        <div>
          <h4 className="heading-serif" style={{ fontSize: '0.9rem', color: 'var(--color-gold-400)', marginBottom: '16px' }}>Thanh toán</h4>
          <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', lineHeight: 1.6 }}>
            COD hoặc VNPay. Phí vận chuyển hiện là 0 đ.
          </p>
        </div>
      </div>

      {/* Copyright */}
      <div className="container" style={{
        borderTop: '1px solid var(--color-border)',
        paddingTop: '24px',
        textAlign: 'center',
        fontSize: '0.78rem',
        color: 'var(--color-text-muted)',
      }}>
        © 2026 AURA. Trang sức vàng và đá quý.
      </div>
    </footer>
  );
};
