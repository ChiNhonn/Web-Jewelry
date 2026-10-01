import React, { useState, useEffect } from 'react';
import { ShoppingBag, Search, Calendar, ChevronRight } from 'lucide-react';

export const Header: React.FC = () => {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 40) {
        setIsScrolled(true);
      } else {
        setIsScrolled(false);
      }
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <header style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      zIndex: 1000,
      transition: 'all 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
      backgroundColor: isScrolled ? 'rgba(5, 5, 8, 0.85)' : 'rgba(5, 5, 8, 0.4)',
      backdropFilter: 'blur(20px)',
      WebkitBackdropFilter: 'blur(20px)',
      borderBottom: isScrolled ? '1px solid rgba(255, 255, 255, 0.08)' : '1px solid rgba(255, 255, 255, 0.03)',
      padding: isScrolled ? '14px 0' : '20px 0',
    }}>
      <div className="container" style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        {/* Brand Logo matching the reference picture style: LOGO • */}
        <a href="/" style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
          <span style={{
            fontSize: '1.25rem',
            fontWeight: 800,
            letterSpacing: '0.12em',
            color: '#FFFFFF',
            textTransform: 'uppercase',
          }}>
            AURAJEWELRY
          </span>
          <span style={{
            width: '7px',
            height: '7px',
            borderRadius: '50%',
            backgroundColor: '#ff6584',
            boxShadow: '0 0 12px #ff6584',
            display: 'inline-block',
          }} />
        </a>

        {/* Minimalist Center Navigation Links */}
        <nav style={{
          display: 'flex',
          alignItems: 'center',
          gap: '32px',
          fontSize: '0.85rem',
          fontWeight: 500,
          color: 'var(--color-text-secondary)',
        }}>
          <a href="#danh-muc" style={{ transition: 'color 0.2s ease' }} onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')} onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}>
            Danh mục
          </a>
          <a href="#quy-trinh" style={{ transition: 'color 0.2s ease' }} onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')} onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}>
            Quy trình
          </a>
          <a href="#giai-phap" style={{ transition: 'color 0.2s ease' }} onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')} onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}>
            Giải pháp
          </a>
          <a href="#tuyet-tac" style={{ transition: 'color 0.2s ease' }} onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')} onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}>
            Tuyệt tác
          </a>
          <a href="#cam-ket" style={{ transition: 'color 0.2s ease' }} onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')} onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}>
            Cam kết
          </a>
        </nav>

        {/* Right CTA Actions */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <button
            title="Tìm kiếm"
            style={{
              color: 'var(--color-text-secondary)',
              display: 'flex',
              padding: '6px',
              borderRadius: '50%',
              transition: 'color 0.2s ease',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
            onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
          >
            <Search size={18} />
          </button>

          <a
            href="#gio-hang"
            style={{
              color: 'var(--color-text-secondary)',
              fontSize: '0.85rem',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '6px',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
            onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
          >
            <ShoppingBag size={18} />
            <span style={{ fontSize: '0.8rem' }}>Giỏ hàng</span>
          </a>

          {/* Pill Button matching reference image white pill button */}
          <a
            href="#tu-van"
            className="btn-pill-white"
            style={{ padding: '8px 20px', fontSize: '0.82rem' }}
          >
            <Calendar size={14} /> Tư vấn VIP <ChevronRight size={14} />
          </a>
        </div>
      </div>
    </header>
  );
};
