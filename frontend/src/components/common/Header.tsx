import React from 'react';
import { Gem, ShoppingBag, Heart, User, Search } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header style={{
      borderBottom: '1px solid var(--color-border)',
      backgroundColor: 'rgba(18, 20, 24, 0.95)',
      backdropFilter: 'blur(10px)',
      position: 'sticky',
      top: 0,
      zIndex: 100,
    }}>
      {/* Top promotional bar */}
      <div style={{
        backgroundColor: '#07080A',
        padding: '6px 0',
        fontSize: '0.75rem',
        textAlign: 'center',
        color: 'var(--color-gold-400)',
        letterSpacing: '0.12em',
        textTransform: 'uppercase',
        borderBottom: '1px solid rgba(212, 175, 55, 0.15)'
      }}>
        ✨ Miễn phí vận chuyển toàn quốc cho đơn hàng từ 10.000.000đ • Chứng nhận kiểm định GIA
      </div>

      <div className="container" style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        paddingTop: '16px',
        paddingBottom: '16px',
      }}>
        {/* Brand Logo */}
        <a href="/" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            width: '38px',
            height: '38px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #D4AF37, #8C6D18)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#0B0C0E',
            boxShadow: 'var(--shadow-gold)'
          }}>
            <Gem size={20} />
          </div>
          <div>
            <span className="heading-serif gold-gradient-text" style={{ fontSize: '1.4rem', fontWeight: 700, letterSpacing: '0.15em' }}>
              AURA
            </span>
            <span style={{ display: 'block', fontSize: '0.62rem', letterSpacing: '0.3em', color: 'var(--color-text-secondary)', textTransform: 'uppercase' }}>
              Fine Jewelry
            </span>
          </div>
        </a>

        {/* Navigation Categories */}
        <nav style={{ display: 'flex', gap: '28px', fontSize: '0.85rem', fontWeight: 500, letterSpacing: '0.06em', textTransform: 'uppercase' }}>
          <a href="#nhan" style={{ color: 'var(--color-text-primary)' }} className="nav-link">Nhẫn Kim Cương</a>
          <a href="#day-chuyen" style={{ color: 'var(--color-text-secondary)' }} className="nav-link">Dây Chuyền</a>
          <a href="#vong-tay" style={{ color: 'var(--color-text-secondary)' }} className="nav-link">Vòng Tay & Lắc</a>
          <a href="#bong-tai" style={{ color: 'var(--color-text-secondary)' }} className="nav-link">Bông Tai</a>
          <a href="#bo-suu-tap" style={{ color: 'var(--color-gold-400)' }} className="nav-link">Bộ Sưu Tập 2026</a>
        </nav>

        {/* Actions Icons */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '18px' }}>
          <button title="Tìm kiếm" style={{ color: 'var(--color-text-secondary)', display: 'flex' }}>
            <Search size={20} />
          </button>
          <button title="Yêu thích" style={{ color: 'var(--color-text-secondary)', display: 'flex' }}>
            <Heart size={20} />
          </button>
          <button title="Giỏ hàng" style={{ color: 'var(--color-gold-400)', display: 'flex', position: 'relative' }}>
            <ShoppingBag size={20} />
            <span style={{
              position: 'absolute',
              top: '-6px',
              right: '-8px',
              backgroundColor: 'var(--color-gold-500)',
              color: '#0B0C0E',
              fontSize: '0.65rem',
              fontWeight: 700,
              width: '16px',
              height: '16px',
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}>
              0
            </span>
          </button>
          <button title="Tài khoản" style={{ color: 'var(--color-text-secondary)', display: 'flex' }}>
            <User size={20} />
          </button>
        </div>
      </div>
    </header>
  );
};
