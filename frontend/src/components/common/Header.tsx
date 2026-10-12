import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingBag, Search, User, LogOut, ShieldCheck, Package, Heart, ChevronDown } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';

export const Header: React.FC = () => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [userDropdownOpen, setUserDropdownOpen] = useState(false);
  const [searchOpen, setSearchOpen] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');
  const { user, isAuthenticated, isAdmin, isStaff, openAuthModal, logout } = useAuth();
  const { totalQuantity } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 30) {
        setIsScrolled(true);
      } else {
        setIsScrolled(false);
      }
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/products?search=${encodeURIComponent(searchKeyword.trim())}`);
      setSearchOpen(false);
    }
  };

  return (
    <>
      <header
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          zIndex: 1000,
          transition: 'all 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
          backgroundColor: isScrolled ? 'rgba(5, 5, 8, 0.92)' : 'rgba(5, 5, 8, 0.5)',
          backdropFilter: 'blur(20px)',
          WebkitBackdropFilter: 'blur(20px)',
          borderBottom: isScrolled ? '1px solid rgba(255, 255, 255, 0.08)' : '1px solid rgba(255, 255, 255, 0.03)',
          padding: isScrolled ? '12px 0' : '18px 0',
        }}
      >
        <div
          className="container"
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          {/* Brand Logo */}
          <Link to="/" style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
            <span
              style={{
                fontSize: '1.25rem',
                fontWeight: 800,
                letterSpacing: '0.12em',
                color: '#FFFFFF',
                textTransform: 'uppercase',
              }}
            >
              AURAJEWELRY
            </span>
            <span
              style={{
                width: '7px',
                height: '7px',
                borderRadius: '50%',
                backgroundColor: '#ff6584',
                boxShadow: '0 0 12px #ff6584',
                display: 'inline-block',
              }}
            />
          </Link>

          {/* Navigation Links */}
          <nav
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '28px',
              fontSize: '0.85rem',
              fontWeight: 500,
              color: 'var(--color-text-secondary)',
            }}
          >
            <Link
              to="/"
              style={{ transition: 'color 0.2s ease' }}
              onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
              onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
            >
              Trang Chủ
            </Link>
            <Link
              to="/products"
              style={{ transition: 'color 0.2s ease' }}
              onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
              onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
            >
              Bộ Sưu Tập
            </Link>
            <Link
              to="/products?category=nhan-kim-cuong"
              style={{ transition: 'color 0.2s ease' }}
              onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
              onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
            >
              Nhẫn Kim Cương
            </Link>
            <Link
              to="/products?category=day-chuyen-mat-day"
              style={{ transition: 'color 0.2s ease' }}
              onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
              onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
            >
              Dây Chuyền
            </Link>
            <Link
              to="/products?category=vong-tay-lac"
              style={{ transition: 'color 0.2s ease' }}
              onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
              onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
            >
              Vòng Tay & Lắc
            </Link>
            {(isAdmin || isStaff) && (
              <Link
                to="/admin"
                style={{
                  color: 'var(--color-gold-400)',
                  fontWeight: 600,
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '4px',
                }}
              >
                <ShieldCheck size={14} /> Quản Trị CMS
              </Link>
            )}
          </nav>

          {/* Right Action Icons */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '18px' }}>
            {/* Search Trigger */}
            <button
              onClick={() => setSearchOpen(!searchOpen)}
              title="Tìm kiếm trang sức"
              style={{
                color: 'var(--color-text-secondary)',
                backgroundColor: 'transparent',
                border: 'none',
                cursor: 'pointer',
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

            {/* Shopping Cart Link */}
            <Link
              to="/cart"
              style={{
                color: 'var(--color-text-secondary)',
                fontSize: '0.85rem',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '8px',
                position: 'relative',
              }}
              onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
              onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-secondary)')}
            >
              <div style={{ position: 'relative', display: 'flex' }}>
                <ShoppingBag size={20} />
                {totalQuantity > 0 && (
                  <span
                    style={{
                      position: 'absolute',
                      top: '-6px',
                      right: '-8px',
                      backgroundColor: '#ff6584',
                      color: '#FFFFFF',
                      fontSize: '0.68rem',
                      fontWeight: 700,
                      width: '18px',
                      height: '18px',
                      borderRadius: '50%',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: '0 0 10px #ff6584',
                    }}
                  >
                    {totalQuantity > 99 ? '99+' : totalQuantity}
                  </span>
                )}
              </div>
              <span style={{ fontSize: '0.82rem' }}>Giỏ hàng</span>
            </Link>

            {/* User Account / Auth Section */}
            {isAuthenticated && user ? (
              <div style={{ position: 'relative' }}>
                <button
                  onClick={() => setUserDropdownOpen(!userDropdownOpen)}
                  style={{
                    backgroundColor: 'rgba(255, 255, 255, 0.05)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    borderRadius: '9999px',
                    padding: '6px 14px',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    color: '#FFFFFF',
                    fontSize: '0.82rem',
                    fontWeight: 600,
                    cursor: 'pointer',
                  }}
                >
                  <div
                    style={{
                      width: '24px',
                      height: '24px',
                      borderRadius: '50%',
                      backgroundColor: 'rgba(255, 101, 132, 0.25)',
                      color: 'var(--color-sakura-300)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '0.75rem',
                      fontWeight: 700,
                    }}
                  >
                    {user.firstName ? user.firstName.charAt(0).toUpperCase() : user.username.charAt(0).toUpperCase()}
                  </div>
                  <span>{user.fullName || user.username}</span>
                  <ChevronDown size={14} color="#9ea3b0" />
                </button>

                {/* Dropdown Menu */}
                {userDropdownOpen && (
                  <div
                    style={{
                      position: 'absolute',
                      top: 'calc(100% + 8px)',
                      right: 0,
                      width: '210px',
                      backgroundColor: '#0e1017',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                      borderRadius: '12px',
                      padding: '8px',
                      boxShadow: '0 15px 35px rgba(0, 0, 0, 0.8), 0 0 20px rgba(255, 101, 132, 0.1)',
                      zIndex: 1010,
                    }}
                  >
                    <div style={{ padding: '8px 12px', borderBottom: '1px solid rgba(255, 255, 255, 0.06)' }}>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>Đăng nhập với tư cách</div>
                      <div style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--color-sakura-300)' }}>
                        {user.roles.includes('ROLE_ADMIN') ? '👑 Quản Trị Viên (Admin)' : '💎 Khách Hàng VIP'}
                      </div>
                    </div>

                    <Link
                      to="/account"
                      onClick={() => setUserDropdownOpen(false)}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '10px 12px',
                        borderRadius: '8px',
                        fontSize: '0.82rem',
                        color: 'var(--color-text-secondary)',
                        transition: 'all 0.2s ease',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
                        e.currentTarget.style.color = '#FFFFFF';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = 'transparent';
                        e.currentTarget.style.color = 'var(--color-text-secondary)';
                      }}
                    >
                      <User size={15} /> Hồ Sơ & Địa Chỉ
                    </Link>

                    <Link
                      to="/account?tab=orders"
                      onClick={() => setUserDropdownOpen(false)}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '10px 12px',
                        borderRadius: '8px',
                        fontSize: '0.82rem',
                        color: 'var(--color-text-secondary)',
                        transition: 'all 0.2s ease',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
                        e.currentTarget.style.color = '#FFFFFF';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = 'transparent';
                        e.currentTarget.style.color = 'var(--color-text-secondary)';
                      }}
                    >
                      <Package size={15} /> Lịch Sử Đơn Hàng
                    </Link>

                    <Link
                      to="/account?tab=wishlist"
                      onClick={() => setUserDropdownOpen(false)}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '10px 12px',
                        borderRadius: '8px',
                        fontSize: '0.82rem',
                        color: 'var(--color-text-secondary)',
                        transition: 'all 0.2s ease',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
                        e.currentTarget.style.color = '#FFFFFF';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = 'transparent';
                        e.currentTarget.style.color = 'var(--color-text-secondary)';
                      }}
                    >
                      <Heart size={15} /> Tuyệt Tác Yêu Thích
                    </Link>

                    {(isAdmin || isStaff) && (
                      <Link
                        to="/admin"
                        onClick={() => setUserDropdownOpen(false)}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: '10px',
                          padding: '10px 12px',
                          borderRadius: '8px',
                          fontSize: '0.82rem',
                          color: 'var(--color-gold-400)',
                          borderTop: '1px solid rgba(255, 255, 255, 0.06)',
                          marginTop: '4px',
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = 'rgba(212, 175, 55, 0.1)')}
                        onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'transparent')}
                      >
                        <ShieldCheck size={15} /> Trang Quản Trị CMS
                      </Link>
                    )}

                    <button
                      onClick={() => {
                        setUserDropdownOpen(false);
                        logout();
                      }}
                      style={{
                        width: '100%',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '10px 12px',
                        borderRadius: '8px',
                        fontSize: '0.82rem',
                        color: '#EF4444',
                        backgroundColor: 'transparent',
                        border: 'none',
                        cursor: 'pointer',
                        textAlign: 'left',
                        borderTop: '1px solid rgba(255, 255, 255, 0.06)',
                        marginTop: '4px',
                      }}
                      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = 'rgba(239, 68, 68, 0.1)')}
                      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'transparent')}
                    >
                      <LogOut size={15} /> Đăng Xuất
                    </button>
                  </div>
                )}
              </div>
            ) : (
              /* Guest Login CTA */
              <button
                onClick={() => openAuthModal('login')}
                className="btn-pill-white"
                style={{ padding: '8px 18px', fontSize: '0.82rem', cursor: 'pointer' }}
              >
                <User size={14} /> Đăng Nhập VIP
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Slide-Down Search Bar Drawer */}
      {searchOpen && (
        <div
          style={{
            position: 'fixed',
            top: isScrolled ? '65px' : '77px',
            left: 0,
            right: 0,
            zIndex: 999,
            backgroundColor: 'rgba(9, 10, 15, 0.95)',
            backdropFilter: 'blur(20px)',
            borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
            padding: '16px 0',
            animation: 'fadeInDown 0.3s ease',
          }}
        >
          <div className="container">
            <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '12px', maxWidth: '680px', margin: '0 auto' }}>
              <div style={{ position: 'relative', flex: 1 }}>
                <input
                  type="text"
                  autoFocus
                  placeholder="Tìm nhẫn kim cương, dây chuyền vàng Ý, vòng tay tennis..."
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px 16px 12px 42px',
                    borderRadius: '9999px',
                    backgroundColor: 'rgba(255, 255, 255, 0.06)',
                    border: '1px solid rgba(255, 255, 255, 0.15)',
                    color: '#FFFFFF',
                    fontSize: '0.9rem',
                    outline: 'none',
                  }}
                />
                <Search size={18} color="#9ea3b0" style={{ position: 'absolute', left: '16px', top: '13px' }} />
              </div>
              <button type="submit" className="btn-pill-sakura" style={{ padding: '0 24px' }}>
                Tìm Kiếm
              </button>
            </form>
          </div>
        </div>
      )}
    </>
  );
};
