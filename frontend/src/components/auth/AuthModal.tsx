import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { X, Lock, Mail, User, Phone, Sparkles, ShieldCheck, ArrowRight } from 'lucide-react';

export const AuthModal: React.FC = () => {
  const { isAuthModalOpen, closeAuthModal, authModalMode, openAuthModal, login, register } = useAuth();

  const [loginForm, setLoginForm] = useState({
    usernameOrEmail: '',
    password: '',
  });

  const [registerForm, setRegisterForm] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
  });

  const [submitting, setSubmitting] = useState(false);

  if (!isAuthModalOpen) return null;

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await login(loginForm);
    } catch {
      // Handled by toast
    } finally {
      setSubmitting(false);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await register(registerForm);
    } catch {
      // Handled by toast
    } finally {
      setSubmitting(false);
    }
  };

  const fillQuickAccount = (username: string, pass: string) => {
    setLoginForm({
      usernameOrEmail: username,
      password: pass,
    });
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 9990,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'rgba(5, 5, 8, 0.85)',
        backdropFilter: 'blur(16px)',
        WebkitBackdropFilter: 'blur(16px)',
        padding: '20px',
      }}
      onClick={closeAuthModal}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '460px',
          backgroundColor: '#0e1017',
          border: '1px solid rgba(255, 255, 255, 0.1)',
          borderRadius: '16px',
          padding: '36px 32px',
          position: 'relative',
          boxShadow: '0 25px 60px rgba(0, 0, 0, 0.9), 0 0 40px rgba(255, 101, 132, 0.15)',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Close Button */}
        <button
          onClick={closeAuthModal}
          style={{
            position: 'absolute',
            top: '20px',
            right: '20px',
            color: 'var(--color-text-muted)',
            backgroundColor: 'transparent',
            border: 'none',
            cursor: 'pointer',
            padding: '4px',
            display: 'flex',
          }}
          onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
          onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-muted)')}
        >
          <X size={20} />
        </button>

        {/* Modal Header */}
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '6px',
              fontSize: '0.78rem',
              letterSpacing: '0.15em',
              color: 'var(--color-sakura-400)',
              textTransform: 'uppercase',
              fontWeight: 600,
              marginBottom: '8px',
            }}
          >
            <Sparkles size={14} /> AURA PRIVILEGE PORTAL
          </div>
          <h2 style={{ fontSize: '1.6rem', fontWeight: 700, color: '#FFFFFF' }}>
            {authModalMode === 'login' ? 'Đăng Nhập Khách Quý' : 'Đăng Ký Thành Viên VIP'}
          </h2>
        </div>

        {/* Tabs Switcher */}
        <div
          style={{
            display: 'flex',
            backgroundColor: 'rgba(255, 255, 255, 0.04)',
            borderRadius: '9999px',
            padding: '4px',
            marginBottom: '26px',
            border: '1px solid rgba(255, 255, 255, 0.06)',
          }}
        >
          <button
            type="button"
            onClick={() => openAuthModal('login')}
            style={{
              flex: 1,
              padding: '8px',
              borderRadius: '9999px',
              fontSize: '0.84rem',
              fontWeight: 600,
              border: 'none',
              cursor: 'pointer',
              transition: 'all 0.25s ease',
              backgroundColor: authModalMode === 'login' ? '#FFFFFF' : 'transparent',
              color: authModalMode === 'login' ? '#050508' : 'var(--color-text-secondary)',
            }}
          >
            Đăng Nhập
          </button>
          <button
            type="button"
            onClick={() => openAuthModal('register')}
            style={{
              flex: 1,
              padding: '8px',
              borderRadius: '9999px',
              fontSize: '0.84rem',
              fontWeight: 600,
              border: 'none',
              cursor: 'pointer',
              transition: 'all 0.25s ease',
              backgroundColor: authModalMode === 'register' ? '#FFFFFF' : 'transparent',
              color: authModalMode === 'register' ? '#050508' : 'var(--color-text-secondary)',
            }}
          >
            Đăng Ký
          </button>
        </div>

        {/* LOGIN FORM */}
        {authModalMode === 'login' ? (
          <form onSubmit={handleLoginSubmit}>
            <div style={{ marginBottom: '18px' }}>
              <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                Tên đăng nhập hoặc Email
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type="text"
                  required
                  placeholder="admin hoặc customer@example.com"
                  value={loginForm.usernameOrEmail}
                  onChange={(e) => setLoginForm({ ...loginForm, usernameOrEmail: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '12px 14px 12px 42px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '0.9rem',
                    outline: 'none',
                  }}
                />
                <User size={18} color="#9ea3b0" style={{ position: 'absolute', left: '14px', top: '13px' }} />
              </div>
            </div>

            <div style={{ marginBottom: '22px' }}>
              <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                Mật khẩu
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type="password"
                  required
                  placeholder="••••••••"
                  value={loginForm.password}
                  onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '12px 14px 12px 42px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '0.9rem',
                    outline: 'none',
                  }}
                />
                <Lock size={18} color="#9ea3b0" style={{ position: 'absolute', left: '14px', top: '13px' }} />
              </div>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="btn-pill-sakura"
              style={{ width: '100%', padding: '13px', fontSize: '0.92rem', cursor: submitting ? 'wait' : 'pointer' }}
            >
              {submitting ? 'Đang xác thực...' : 'Đăng Nhập Ngay'} <ArrowRight size={16} />
            </button>

            {/* Quick Demo Credentials */}
            <div style={{ marginTop: '24px', paddingTop: '20px', borderTop: '1px solid rgba(255, 255, 255, 0.08)' }}>
              <p style={{ fontSize: '0.74rem', color: 'var(--color-text-muted)', marginBottom: '10px', textAlign: 'center' }}>
                TÀI KHOẢN MẪU ĐỂ TEST HỆ THỐNG NHANH
              </p>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                <button
                  type="button"
                  onClick={() => fillQuickAccount('customer', 'Password123!')}
                  style={{
                    padding: '8px 10px',
                    borderRadius: '6px',
                    backgroundColor: 'rgba(255, 255, 255, 0.03)',
                    border: '1px solid rgba(255, 255, 255, 0.08)',
                    color: 'var(--color-sakura-300)',
                    fontSize: '0.76rem',
                    cursor: 'pointer',
                    textAlign: 'center',
                    transition: 'all 0.2s ease',
                  }}
                >
                  👤 Khách Hàng Demo
                </button>
                <button
                  type="button"
                  onClick={() => fillQuickAccount('admin', 'Password123!')}
                  style={{
                    padding: '8px 10px',
                    borderRadius: '6px',
                    backgroundColor: 'rgba(255, 255, 255, 0.03)',
                    border: '1px solid rgba(212, 175, 55, 0.3)',
                    color: 'var(--color-gold-400)',
                    fontSize: '0.76rem',
                    cursor: 'pointer',
                    textAlign: 'center',
                    transition: 'all 0.2s ease',
                  }}
                >
                  <ShieldCheck size={12} style={{ display: 'inline', marginRight: '4px' }} /> Quản Trị Admin
                </button>
              </div>
            </div>
          </form>
        ) : (
          /* REGISTER FORM */
          <form onSubmit={handleRegisterSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '14px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                  Họ & Tên đệm
                </label>
                <input
                  type="text"
                  required
                  placeholder="Nguyễn Văn"
                  value={registerForm.firstName}
                  onChange={(e) => setRegisterForm({ ...registerForm, firstName: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '10px 12px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '0.85rem',
                    outline: 'none',
                  }}
                />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                  Tên
                </label>
                <input
                  type="text"
                  required
                  placeholder="An"
                  value={registerForm.lastName}
                  onChange={(e) => setRegisterForm({ ...registerForm, lastName: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '10px 12px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '0.85rem',
                    outline: 'none',
                  }}
                />
              </div>
            </div>

            <div style={{ marginBottom: '14px' }}>
              <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                Tên tài khoản (Username)
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type="text"
                  required
                  placeholder="an_nguyen2026"
                  value={registerForm.username}
                  onChange={(e) => setRegisterForm({ ...registerForm, username: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '10px 12px 10px 38px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '0.85rem',
                    outline: 'none',
                  }}
                />
                <User size={16} color="#9ea3b0" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              </div>
            </div>

            <div style={{ marginBottom: '14px' }}>
              <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                Địa chỉ Email
              </label>
              <div style={{ position: 'relative' }}>
                <input
                  type="email"
                  required
                  placeholder="an.nguyen@gmail.com"
                  value={registerForm.email}
                  onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
                  style={{
                    width: '100%',
                    padding: '10px 12px 10px 38px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '0.85rem',
                    outline: 'none',
                  }}
                />
                <Mail size={16} color="#9ea3b0" style={{ position: 'absolute', left: '12px', top: '12px' }} />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '20px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                  Số điện thoại
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    type="tel"
                    placeholder="0912345678"
                    value={registerForm.phone}
                    onChange={(e) => setRegisterForm({ ...registerForm, phone: e.target.value })}
                    style={{
                      width: '100%',
                      padding: '10px 10px 10px 34px',
                      borderRadius: '8px',
                      backgroundColor: 'rgba(255, 255, 255, 0.04)',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                      color: '#FFFFFF',
                      fontSize: '0.85rem',
                      outline: 'none',
                    }}
                  />
                  <Phone size={14} color="#9ea3b0" style={{ position: 'absolute', left: '10px', top: '13px' }} />
                </div>
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '0.76rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>
                  Mật khẩu
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    type="password"
                    required
                    placeholder="Tối thiểu 6 ký tự"
                    value={registerForm.password}
                    onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
                    style={{
                      width: '100%',
                      padding: '10px 10px 10px 34px',
                      borderRadius: '8px',
                      backgroundColor: 'rgba(255, 255, 255, 0.04)',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                      color: '#FFFFFF',
                      fontSize: '0.85rem',
                      outline: 'none',
                    }}
                  />
                  <Lock size={14} color="#9ea3b0" style={{ position: 'absolute', left: '10px', top: '13px' }} />
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="btn-pill-sakura"
              style={{ width: '100%', padding: '13px', fontSize: '0.92rem', cursor: submitting ? 'wait' : 'pointer' }}
            >
              {submitting ? 'Đang tạo tài khoản...' : 'Hoàn Tất Đăng Ký'} <ArrowRight size={16} />
            </button>
          </form>
        )}
      </div>
    </div>
  );
};
