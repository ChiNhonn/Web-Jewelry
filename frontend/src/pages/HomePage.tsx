import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { healthService } from '../services/healthService';
import { reviewService } from '../services/reviewService';
import { Review } from '../types/review.types';
import { HealthStatus } from '../types/api.types';
import { useScrollReveal } from '../hooks/useScrollReveal';
import {
  ArrowRight,
  CheckCircle2,
  RefreshCw,
  Server,
  Star,
  Clock,
  ChevronRight,
  Heart,
} from 'lucide-react';

export const HomePage: React.FC = () => {
  // Live Backend Health State
  const [health, setHealth] = useState<HealthStatus | null>(null);
  const [loadingHealth, setLoadingHealth] = useState<boolean>(true);
  const [pingLatency, setPingLatency] = useState<number | null>(null);

  // Active Category Tab
  const [activeTab, setActiveTab] = useState<string>('all');
  const [customerReviews, setCustomerReviews] = useState<Review[]>([]);

  const spotlight = [
    {
      tab: 'nhan',
      name: 'Nhẫn Kim Cương Solitaire Hoàng Gia 18K',
      price: '32.725.000₫',
      description: 'Nhẫn đính kim cương tự nhiên 5.4 ly nước D, độ trong VVS1, ổ nhẫn chế tác từ vàng trắng 18K nguyên khối.',
      meta: 'Vàng trắng 18K · 1.2 chỉ',
      href: '/products/sp-1',
      badge: 'Vàng trắng 18K',
      tone: 'sakura' as const,
      image: 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=1000&q=80',
    },
    {
      tab: 'day-chuyen',
      name: 'Dây Chuyền Vàng Ý Khắc Hoa Văn Cung Đình',
      price: '16.380.000₫',
      description: 'Dây chuyền mắt xích kép thanh lịch, điểm xuyết họa tiết hoàng gia Ý sắc nét và quý phái.',
      meta: 'Vàng Ý 750 · 2.8 chỉ',
      href: '/products/sp-3',
      badge: 'Vàng Ý 18K',
      tone: 'gold' as const,
      image: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=1000&q=80',
    },
    {
      tab: 'vong-tay',
      name: 'Vòng Tay Bản Liền Vàng 18K Chạm Rồng Phượng',
      price: '42.120.000₫',
      description: 'Vòng tay truyền thống chạm nổi rồng phượng tinh xảo mang ý nghĩa may mắn, tài lộc cho gia chủ.',
      meta: 'Vàng 18K · 5.0 chỉ',
      href: '/products/sp-6',
      badge: 'Vàng 18K',
      tone: 'sakura' as const,
      image: 'https://images.unsplash.com/photo-1596944924616-7b38e7cfac36?auto=format&fit=crop&w=1000&q=80',
    },
    {
      tab: 'bong-tai',
      name: 'Bông Tai Ngọc Trai Akoya Ánh Hồng',
      price: '8.820.000₫',
      description: 'Cặp ngọc trai biển Akoya 8.5mm tròn hoàn hảo ánh xà cừ hồng dịu, chấu vàng trắng 18K cao cấp.',
      meta: 'Ngọc trai Akoya · 0.6 chỉ',
      href: '/products/sp-7',
      badge: 'Ngọc trai',
      tone: 'sakura' as const,
      image: 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=1000&q=80',
    },
  ];
  const visibleSpotlight = spotlight.filter((item) => activeTab === 'all' || item.tab === activeTab);

  // Trigger Scroll Reveal Hook
  useScrollReveal(activeTab);

  const fetchHealth = async () => {
    setLoadingHealth(true);
    const startTime = performance.now();
    try {
      const response = await healthService.checkHealth();
      const endTime = performance.now();
      setHealth(response.data);
      setPingLatency(Math.round(endTime - startTime));
    } catch (err: unknown) {
      console.error('Lỗi kiểm tra Backend Health:', err);
    } finally {
      setLoadingHealth(false);
    }
  };

  useEffect(() => {
    fetchHealth();
    reviewService.getLatestReviews(6).then(setCustomerReviews).catch(() => setCustomerReviews([]));
  }, []);

  return (
    <div style={{ backgroundColor: 'var(--color-bg-pure)', minHeight: '100vh', overflowX: 'hidden' }}>
      {/* =========================================================================
          HERO BANNER - Matching the exact visual aesthetic of reference photo
          ========================================================================= */}
      <section style={{
        position: 'relative',
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        background: `
          linear-gradient(to right, #050508 0%, #050508 35%, rgba(5,5,8,0.7) 55%, rgba(5,5,8,0.15) 75%, transparent 100%),
          linear-gradient(to top, #050508 0%, rgba(5,5,8,0.7) 15%, transparent 50%),
          linear-gradient(to bottom, #050508 0%, rgba(5,5,8,0.5) 12%, transparent 40%),
          url('/assets/images/hero-tree.jpg') no-repeat right center / cover
        `,
        paddingTop: '100px',
        paddingBottom: '80px',
      }}>
        {/* Floating sakura petals effect */}
        <div className="floating-petal" style={{ width: '8px', height: '12px', top: '25%', right: '22%', animationDuration: '7s' }} />
        <div className="floating-petal" style={{ width: '6px', height: '10px', top: '35%', right: '35%', animationDuration: '9s', animationDelay: '2s' }} />
        <div className="floating-petal" style={{ width: '9px', height: '13px', top: '15%', right: '15%', animationDuration: '11s', animationDelay: '4s' }} />
        <div className="floating-petal" style={{ width: '7px', height: '11px', top: '50%', right: '28%', animationDuration: '8s', animationDelay: '1s' }} />

        <div className="container" style={{ position: 'relative', zIndex: 10 }}>
          <div style={{ maxWidth: '640px' }}>
            {/* Top Subtitle Badge with dash (matching reference image) */}
            <div style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px',
              fontSize: '0.82rem',
              fontWeight: 600,
              letterSpacing: '0.12em',
              color: 'var(--color-sakura-300)',
              textTransform: 'uppercase',
              marginBottom: '20px',
            }}>
              <span style={{ width: '24px', height: '2px', backgroundColor: '#ff6584', display: 'inline-block' }} />
              CỬA HÀNG TRANG SỨC AURA
            </div>

            {/* Main Headline (matching typography layout in reference photo) */}
            <h1 style={{
              fontSize: 'clamp(2.6rem, 5.5vw, 4.4rem)',
              fontWeight: 700,
              lineHeight: 1.15,
              color: '#FFFFFF',
              letterSpacing: '-0.02em',
              marginBottom: '24px',
            }}>
              Trang sức đang có <br />
              <span className="sakura-gradient-text" style={{ fontStyle: 'normal' }}>
                tại cửa hàng.
              </span>
            </h1>

            {/* Description Subtitle */}
            <p style={{
              fontSize: '1.05rem',
              color: 'var(--color-text-secondary)',
              lineHeight: 1.7,
              marginBottom: '38px',
              maxWidth: '520px',
            }}>
              Nhẫn, dây chuyền, vòng tay, bông tai, vàng 18K và bạc đang bán tại cửa hàng. Giá và tồn kho lấy theo sản phẩm thật.
            </p>

            {/* Action Buttons */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap' }}>
              <Link to="/products" className="btn-pill-sakura">
                Khám phá bộ sưu tập <ArrowRight size={16} />
              </Link>
              <Link to="/products" className="btn-outline-pill">
                Xem danh mục
              </Link>
            </div>

          </div>
        </div>

        {/* Floating built-with badge at bottom right (matching reference image) */}
        <div style={{
          position: 'absolute',
          bottom: '24px',
          right: '32px',
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          padding: '6px 14px',
          borderRadius: '9999px',
          backgroundColor: 'rgba(15, 18, 25, 0.7)',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(255, 255, 255, 0.1)',
          fontSize: '0.72rem',
          color: 'var(--color-text-secondary)',
          zIndex: 20,
        }}>
          <span>AURA 2026</span>
          <span style={{ width: '4px', height: '4px', borderRadius: '50%', backgroundColor: '#ff6584' }} />
          <span>Java 21 & React 18</span>
        </div>
      </section>

      {/* =========================================================================
          SECTION 2: DANH MỤC TINH TUYỂN (Reveals on Scroll)
          ========================================================================= */}
      <section id="danh-muc" style={{ padding: '120px 0 80px', position: 'relative' }}>
        <div className="container">
          {/* Section Header */}
          <div className="reveal" style={{ textAlign: 'center', marginBottom: '56px' }}>
            <span style={{
              fontSize: '0.8rem',
              letterSpacing: '0.2em',
              color: 'var(--color-sakura-400)',
              textTransform: 'uppercase',
              fontWeight: 600,
            }}>
              — DANH MỤC TINH TUYỂN —
            </span>
            <h2 style={{
              fontSize: 'clamp(2rem, 4vw, 2.8rem)',
              fontWeight: 700,
              color: '#FFFFFF',
              marginTop: '12px',
              letterSpacing: '-0.01em',
            }}>
              Sản phẩm nổi bật
            </h2>
            <p style={{
              color: 'var(--color-text-secondary)',
              fontSize: '1rem',
              maxWidth: '600px',
              margin: '14px auto 0',
              lineHeight: 1.6,
            }}>
              Một số sản phẩm đang bán. Giá trên thẻ là giá niêm yết của món đó.
            </p>

            {/* Category Filter Pills */}
            <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '32px', flexWrap: 'wrap' }}>
              {[
                { id: 'all', name: 'Tất Cả' },
                { id: 'nhan', name: 'Nhẫn Kim Cương' },
                { id: 'day-chuyen', name: 'Dây Chuyền Vàng Ý' },
                { id: 'vong-tay', name: 'Vòng Tay & Lắc' },
                { id: 'bong-tai', name: 'Bông Tai Quý Phái' },
              ].map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  style={{
                    padding: '8px 20px',
                    borderRadius: '9999px',
                    fontSize: '0.82rem',
                    fontWeight: 500,
                    transition: 'all 0.3s ease',
                    backgroundColor: activeTab === tab.id ? '#FFFFFF' : 'rgba(255, 255, 255, 0.04)',
                    color: activeTab === tab.id ? '#050508' : 'var(--color-text-secondary)',
                    border: activeTab === tab.id ? '1px solid #FFFFFF' : '1px solid rgba(255, 255, 255, 0.08)',
                  }}
                >
                  {tab.name}
                </button>
              ))}
            </div>
          </div>

          {/* Cards Grid with Staggered Scroll Reveal */}
          {visibleSpotlight.length === 0 ? (
            <p style={{ textAlign: 'center', color: 'var(--color-text-secondary)', padding: '48px 0' }}>
              Nhóm này chưa có tác phẩm trưng bày. Xem toàn bộ trong Bộ sưu tập.
            </p>
          ) : (
          <div className={visibleSpotlight.length > 1 ? 'spotlight-marquee' : undefined}>
            <div className={visibleSpotlight.length > 1 ? 'spotlight-track' : undefined} style={visibleSpotlight.length > 1 ? undefined : { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '28px' }}>
            {(visibleSpotlight.length > 1 ? [0, 1] : [0]).map((copy) => (
            <div key={copy} className={visibleSpotlight.length > 1 ? 'spotlight-group' : undefined} style={visibleSpotlight.length > 1 ? undefined : { display: 'contents' }}>
            {visibleSpotlight.map((item) => {
              const sakura = item.tone === 'sakura';
              return (
            <div key={`${item.href}-${copy}`} className="luxury-glass-card spotlight-card" style={{ position: 'relative' }}>
              <div style={{
                position: 'relative',
                height: '240px',
                borderRadius: '8px',
                background: '#0d0f15',
                marginBottom: '22px',
                overflow: 'hidden',
                border: '1px solid rgba(255, 255, 255, 0.06)',
              }}>
                <img
                  src={item.image}
                  alt={item.name}
                  style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
                />
                <span style={{
                  position: 'absolute',
                  top: '14px',
                  left: '14px',
                  maxWidth: 'calc(100% - 72px)',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  backgroundColor: sakura ? 'rgba(255, 101, 132, 0.15)' : 'rgba(212, 175, 55, 0.15)',
                  border: sakura ? '1px solid rgba(255, 101, 132, 0.35)' : '1px solid rgba(212, 175, 55, 0.35)',
                  color: sakura ? 'var(--color-sakura-300)' : 'var(--color-gold-300)',
                  fontSize: '0.72rem',
                  fontWeight: 600,
                  padding: '4px 10px',
                  borderRadius: '9999px',
                }}>
                  {item.badge}
                </span>
                <button
                  title="Yêu thích"
                  style={{
                    position: 'absolute',
                    top: '14px',
                    right: '14px',
                    width: '34px',
                    height: '34px',
                    borderRadius: '50%',
                    backgroundColor: 'rgba(0, 0, 0, 0.5)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'var(--color-text-secondary)',
                  }}
                >
                  <Heart size={16} />
                </button>
              </div>

              <div style={{ marginBottom: '8px' }}>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 600, color: '#FFFFFF', lineHeight: 1.35 }}>{item.name}</h3>
                <div style={{ marginTop: '6px', fontSize: '1.1rem', fontWeight: 700, color: 'var(--color-gold-400)' }}>{item.price}</div>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', lineHeight: 1.6, marginBottom: '20px' }}>
                {item.description}
              </p>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '12px', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>{item.meta}</span>
                <Link to={item.href} style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', fontSize: '0.82rem', fontWeight: 600, color: 'var(--color-sakura-300)', whiteSpace: 'nowrap' }}>
                  Xem tác phẩm <ChevronRight size={14} />
                </Link>
              </div>
            </div>
              );
            })}
            </div>
            ))}
            </div>
          </div>
          )}
        </div>
      </section>


      {customerReviews.some((review) => review.comment?.trim()) && (
      <section style={{
        padding: '100px 0',
        backgroundColor: '#07080c',
        borderTop: '1px solid rgba(255, 255, 255, 0.05)',
      }}>
        <div className="container">
          <div className="reveal" style={{ textAlign: 'center', marginBottom: '56px' }}>
            <span style={{
              fontSize: '0.8rem',
              letterSpacing: '0.2em',
              color: 'var(--color-sakura-400)',
              textTransform: 'uppercase',
              fontWeight: 600,
            }}>
              — NHẬN XÉT SAU KHI NHẬN HÀNG —
            </span>
            <h2 style={{ fontSize: '2.4rem', fontWeight: 700, color: '#FFFFFF', marginTop: '12px' }}>
              Khách đã mua nói gì
            </h2>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
            gap: '24px',
          }}>
            {customerReviews.filter((review) => review.comment?.trim()).map((review) => {
              return (
              <div key={review.id} className="luxury-glass-card" style={{ padding: '32px' }}>
                <div style={{ display: 'flex', gap: '4px', marginBottom: '16px' }}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <Star key={star} size={16} fill={star <= review.rating ? '#ff6584' : 'transparent'} color="#ff6584" />
                  ))}
                </div>
                <p style={{ fontSize: '0.92rem', color: 'var(--color-text-secondary)', lineHeight: 1.7, fontStyle: 'italic', marginBottom: '24px' }}>
                  “{review.comment}”
                </p>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                  <div>
                    <div style={{ fontSize: '0.95rem', fontWeight: 600, color: '#FFFFFF' }}>{review.customerName}</div>
                    <Link to={`/products/sp-${review.productId}`} style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>{review.productName}</Link>
                  </div>
                  <span style={{
                    fontSize: '0.72rem',
                    color: 'var(--color-sakura-300)',
                    backgroundColor: 'rgba(255, 101, 132, 0.1)',
                    padding: '4px 10px',
                    borderRadius: '9999px',
                    border: '1px solid rgba(255, 101, 132, 0.25)',
                    whiteSpace: 'nowrap',
                  }}>
                    Đơn #{review.orderId}
                  </span>
                </div>
              </div>
              );
            })}
          </div>
        </div>
      </section>
      )}

      {/* =========================================================================
          SECTION 6: REAL-TIME SYSTEM CONNECTIVITY (Reveals on Scroll)
          ========================================================= */}
      <section style={{ padding: '80px 0', borderTop: '1px solid rgba(255, 255, 255, 0.05)' }}>
        <div className="container">
          <div className="luxury-glass-card reveal" style={{
            display: 'flex',
            flexWrap: 'wrap',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: '20px',
            background: 'linear-gradient(145deg, rgba(18, 20, 28, 0.9) 0%, rgba(9, 10, 15, 0.95) 100%)',
            border: '1px solid rgba(255, 101, 132, 0.25)',
            boxShadow: '0 0 35px rgba(255, 101, 132, 0.1)',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '18px' }}>
              <div style={{
                width: '52px',
                height: '52px',
                borderRadius: '12px',
                backgroundColor: health?.status === 'UP' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
                color: health?.status === 'UP' ? 'var(--color-success)' : 'var(--color-error)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}>
                <Server size={28} />
              </div>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '0.78rem', textTransform: 'uppercase', letterSpacing: '0.1em', color: 'var(--color-text-muted)' }}>
                    Hệ Thống API Thời Gian Thực
                  </span>
                  {health?.status === 'UP' ? (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: 'var(--color-success)', fontSize: '0.78rem', fontWeight: 600 }}>
                      <CheckCircle2 size={14} /> LIVE (UP)
                    </span>
                  ) : (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: 'var(--color-warning)', fontSize: '0.78rem', fontWeight: 600 }}>
                      <Clock size={14} /> {loadingHealth ? 'Đang kết nối...' : 'OFFLINE'}
                    </span>
                  )}
                </div>
                <div style={{ fontSize: '1rem', fontWeight: 600, color: '#FFFFFF', marginTop: '2px' }}>
                  {health ? 'Cửa hàng đang hoạt động' : 'Đang kết nối cửa hàng'}
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '24px', flexWrap: 'wrap' }}>
              {pingLatency !== null && (
                <div style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                  Độ trễ phản hồi: <strong style={{ color: 'var(--color-sakura-300)' }}>{pingLatency}ms</strong>
                </div>
              )}
              {health?.serverTime && (
                <div style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                  Giờ hệ thống: <span style={{ color: '#FFFFFF' }}>{new Date(health.serverTime).toLocaleTimeString('vi-VN')}</span>
                </div>
              )}
              <button
                onClick={fetchHealth}
                disabled={loadingHealth}
                className="btn-outline-pill"
                style={{ padding: '8px 18px', fontSize: '0.8rem' }}
              >
                <RefreshCw size={14} className={loadingHealth ? 'animate-spin' : ''} /> {loadingHealth ? 'Đang đo...' : 'Kiểm Tra Lại'}
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
