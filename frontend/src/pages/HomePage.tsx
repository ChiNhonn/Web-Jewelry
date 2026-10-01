import React, { useState, useEffect } from 'react';
import { healthService } from '../services/healthService';
import { HealthStatus } from '../types/api.types';
import { useScrollReveal } from '../hooks/useScrollReveal';
import {
  Sparkles,
  ArrowRight,
  Shield,
  Layers,
  Award,
  CheckCircle2,
  RefreshCw,
  Server,
  Star,
  Clock,
  Gem,
  Compass,
  Hammer,
  Eye,
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

  // Trigger Scroll Reveal Hook
  useScrollReveal();

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
              THƯƠNG HIỆU KIM HOÀN HOÀNG GIA ĐỘC BẢN
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
              Tuyệt tác trang sức, <br />
              <span className="sakura-gradient-text" style={{ fontStyle: 'normal' }}>
                khẳng định đẳng cấp.
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
              Khám phá bộ sưu tập kim cương tự nhiên chuẩn GIA và vàng Ý 18K chế tác thủ công tinh xảo, lưu giữ giá trị vĩnh cửu qua từng thế hệ.
            </p>

            {/* Action Buttons */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px', flexWrap: 'wrap', marginBottom: '70px' }}>
              <a href="#danh-muc" className="btn-pill-sakura">
                Khám phá bộ sưu tập <ArrowRight size={16} />
              </a>
              <a href="#quy-trinh" className="btn-outline-pill">
                Quy trình chế tác
              </a>
            </div>

            {/* Metrics Bar at Bottom Left (matching 8+ / 03 năm / 24/7 in reference) */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '40px',
              paddingTop: '28px',
              borderTop: '1px solid rgba(255, 255, 255, 0.08)',
            }}>
              <div>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#FFFFFF', lineHeight: 1.1 }}>100%</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                  Kim cương GIA
                </div>
              </div>

              <div style={{ width: '1px', height: '36px', backgroundColor: 'rgba(255, 255, 255, 0.1)' }} />

              <div>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#FFFFFF', lineHeight: 1.1 }}>18K & PT</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                  Vàng Ý & Bạch kim
                </div>
              </div>

              <div style={{ width: '1px', height: '36px', backgroundColor: 'rgba(255, 255, 255, 0.1)' }} />

              <div>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#FFFFFF', lineHeight: 1.1 }}>24/7</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', marginTop: '4px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                  Cố vấn VIP
                </div>
              </div>
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
              Đỉnh Cao Chế Tác Kim Hoàn
            </h2>
            <p style={{
              color: 'var(--color-text-secondary)',
              fontSize: '1rem',
              maxWidth: '600px',
              margin: '14px auto 0',
              lineHeight: 1.6,
            }}>
              Mỗi tác phẩm là một câu chuyện tình yêu và khí chất riêng biệt, được tuyển chọn từ những viên đá quý hiếm nhất.
            </p>

            {/* Category Filter Pills */}
            <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '32px', flexWrap: 'wrap' }}>
              {[
                { id: 'all', name: 'Tất Cả' },
                { id: 'nhan', name: 'Nhẫn Kim Cương' },
                { id: 'day-chuyen', name: 'Dây Chuyền Vàng Ý' },
                { id: 'vong-tay', name: 'Vòng Tay Tennis' },
                { id: 'bong-tai', name: 'Bông Tai Quý Phái' },
                { id: 'bo-suu-tap', name: 'Bộ Trang Sức' },
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
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))',
            gap: '28px',
          }}>
            {/* Card 1 */}
            <div className="luxury-glass-card reveal delay-100" style={{ position: 'relative' }}>
              <div style={{
                position: 'relative',
                height: '240px',
                borderRadius: '8px',
                background: 'radial-gradient(circle at center, #1e2230 0%, #0d0f15 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '22px',
                overflow: 'hidden',
                border: '1px solid rgba(255, 255, 255, 0.06)',
              }}>
                <Gem size={72} color="#ff9bb2" style={{ filter: 'drop-shadow(0 0 20px rgba(255,101,132,0.4))' }} />
                <span style={{
                  position: 'absolute',
                  top: '14px',
                  left: '14px',
                  backgroundColor: 'rgba(255, 101, 132, 0.15)',
                  border: '1px solid rgba(255, 101, 132, 0.35)',
                  color: 'var(--color-sakura-300)',
                  fontSize: '0.72rem',
                  fontWeight: 600,
                  padding: '4px 10px',
                  borderRadius: '9999px',
                }}>
                  GIA CERTIFIED
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

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#FFFFFF' }}>Nhẫn Solitaire Diamond 2ct</h3>
                <span style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--color-gold-400)' }}>48.500.000₫</span>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', lineHeight: 1.6, marginBottom: '20px' }}>
                Kim cương tự nhiên 2.0 carat chuẩn GIA F/VVS1, giác cắt Brilliant đính trên nền vàng trắng Ý 18K nguyên khối.
              </p>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>42 mẫu ni nhẫn sẵn có</span>
                <a href="#chi-tiet" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', fontSize: '0.82rem', fontWeight: 600, color: 'var(--color-sakura-300)' }}>
                  Xem tác phẩm <ChevronRight size={14} />
                </a>
              </div>
            </div>

            {/* Card 2 */}
            <div className="luxury-glass-card reveal delay-200" style={{ position: 'relative' }}>
              <div style={{
                position: 'relative',
                height: '240px',
                borderRadius: '8px',
                background: 'radial-gradient(circle at center, #1e2230 0%, #0d0f15 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '22px',
                overflow: 'hidden',
                border: '1px solid rgba(255, 255, 255, 0.06)',
              }}>
                <Compass size={72} color="#f3d08c" style={{ filter: 'drop-shadow(0 0 20px rgba(212,175,55,0.4))' }} />
                <span style={{
                  position: 'absolute',
                  top: '14px',
                  left: '14px',
                  backgroundColor: 'rgba(212, 175, 55, 0.15)',
                  border: '1px solid rgba(212, 175, 55, 0.35)',
                  color: 'var(--color-gold-300)',
                  fontSize: '0.72rem',
                  fontWeight: 600,
                  padding: '4px 10px',
                  borderRadius: '9999px',
                }}>
                  ITALIAN 18K GOLD
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

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#FFFFFF' }}>Dây Chuyền Aurora Italian 750</h3>
                <span style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--color-gold-400)' }}>18.200.000₫</span>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', lineHeight: 1.6, marginBottom: '20px' }}>
                Sợi dây vàng Ý 750 liên kết mắt xích mềm mại kết hợp mặt dây đính kim cương tấm tự nhiên tỏa sáng huyền ảo.
              </p>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>Chiều dài 42cm - 45cm</span>
                <a href="#chi-tiet" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', fontSize: '0.82rem', fontWeight: 600, color: 'var(--color-sakura-300)' }}>
                  Xem tác phẩm <ChevronRight size={14} />
                </a>
              </div>
            </div>

            {/* Card 3 */}
            <div className="luxury-glass-card reveal delay-300" style={{ position: 'relative' }}>
              <div style={{
                position: 'relative',
                height: '240px',
                borderRadius: '8px',
                background: 'radial-gradient(circle at center, #1e2230 0%, #0d0f15 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '22px',
                overflow: 'hidden',
                border: '1px solid rgba(255, 255, 255, 0.06)',
              }}>
                <Sparkles size={72} color="#ff9bb2" style={{ filter: 'drop-shadow(0 0 20px rgba(255,101,132,0.4))' }} />
                <span style={{
                  position: 'absolute',
                  top: '14px',
                  left: '14px',
                  backgroundColor: 'rgba(255, 101, 132, 0.15)',
                  border: '1px solid rgba(255, 101, 132, 0.35)',
                  color: 'var(--color-sakura-300)',
                  fontSize: '0.72rem',
                  fontWeight: 600,
                  padding: '4px 10px',
                  borderRadius: '9999px',
                }}>
                  ROYAL TENNIS
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

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#FFFFFF' }}>Lắc Tay Tennis Diamond Pave</h3>
                <span style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--color-gold-400)' }}>65.000.000₫</span>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', lineHeight: 1.6, marginBottom: '20px' }}>
                Chuỗi 54 viên kim cương đồng nhất giác cắt Round Brilliant đính chấu 4 chấu chắc chắn trên vàng hồng 18K.
              </p>

              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>Bản giới hạn 18 chiếc</span>
                <a href="#chi-tiet" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', fontSize: '0.82rem', fontWeight: 600, color: 'var(--color-sakura-300)' }}>
                  Xem tác phẩm <ChevronRight size={14} />
                </a>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* =========================================================================
          SECTION 3: QUY TRÌNH CHẾ TÁC HOÀNG GIA (Reveals on Scroll)
          ========================================================================= */}
      <section id="quy-trinh" style={{
        padding: '120px 0',
        backgroundColor: '#07080c',
        borderTop: '1px solid rgba(255, 255, 255, 0.05)',
        borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
      }}>
        <div className="container">
          <div className="reveal" style={{ textAlign: 'center', marginBottom: '64px' }}>
            <span style={{
              fontSize: '0.8rem',
              letterSpacing: '0.2em',
              color: 'var(--color-sakura-400)',
              textTransform: 'uppercase',
              fontWeight: 600,
            }}>
              — QUY TRÌNH CHẾ TÁC —
            </span>
            <h2 style={{
              fontSize: 'clamp(2rem, 4vw, 2.8rem)',
              fontWeight: 700,
              color: '#FFFFFF',
              marginTop: '12px',
            }}>
              5 Bước Khắc Họa Tinh Hoa Kim Hoàn
            </h2>
            <p style={{ color: 'var(--color-text-secondary)', fontSize: '1rem', maxWidth: '600px', margin: '14px auto 0' }}>
              Từ viên kim cương thô đến kiệt tác lộng lẫy trên ngón tay bạn là hành trình của lòng kiên định và đam mê cái đẹp.
            </p>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
            gap: '24px',
          }}>
            {[
              {
                step: '01',
                title: 'Tuyển Chọn Kim Cương',
                desc: 'Chỉ chọn top 1% kim cương tự nhiên đạt chứng chỉ GIA với độ tinh khiết từ VVS1 trở lên.',
                icon: <Gem size={28} color="#ff9bb2" />,
              },
              {
                step: '02',
                title: 'Bản Vẽ CAD 3D',
                desc: 'Phác thảo mô phỏng từng đường cong và chấu giữ theo thông số ni đo chính xác của khách hàng.',
                icon: <Eye size={28} color="#f3d08c" />,
              },
              {
                step: '03',
                title: 'Đúc Vàng Ý 18K',
                desc: 'Nung chảy hợp kim vàng Ý 750 nguyên khối dưới lò áp suất cao chống bọt khí tuyệt đối.',
                icon: <Hammer size={28} color="#ff9bb2" />,
              },
              {
                step: '04',
                title: 'Đính Chấu Micro-Pave',
                desc: 'Nghệ nhân trên 20 năm kinh nghiệm thao tác tỉ mỉ dưới kính hiển vi chuyên dụng độ phóng đại 40x.',
                icon: <Layers size={28} color="#f3d08c" />,
              },
              {
                step: '05',
                title: 'Kiểm Định & Đóng Gói',
                desc: 'Kiểm tra quang học đa tầng, khắc laser mã số cạnh và đóng gói trong hộp nhung hoàng gia.',
                icon: <Award size={28} color="#ff9bb2" />,
              },
            ].map((item, index) => (
              <div
                key={item.step}
                className={`luxury-glass-card reveal delay-${(index + 1) * 100}`}
                style={{
                  position: 'relative',
                  padding: '32px 24px',
                  border: '1px solid rgba(255, 255, 255, 0.08)',
                }}
              >
                <div style={{
                  fontSize: '2.5rem',
                  fontWeight: 900,
                  color: 'rgba(255, 101, 132, 0.18)',
                  position: 'absolute',
                  top: '16px',
                  right: '20px',
                  lineHeight: 1,
                  fontFamily: 'var(--font-sans)',
                }}>
                  {item.step}
                </div>
                <div style={{ marginBottom: '20px' }}>{item.icon}</div>
                <h4 style={{ fontSize: '1.05rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '10px' }}>
                  {item.title}
                </h4>
                <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', lineHeight: 1.6 }}>
                  {item.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* =========================================================================
          SECTION 4: CAM KẾT VÀNG & ĐẶC QUYỀN THƯỢNG LƯU (Reveals on Scroll)
          ========================================================================= */}
      <section id="cam-ket" style={{ padding: '120px 0' }}>
        <div className="container">
          <div className="reveal" style={{ textAlign: 'center', marginBottom: '60px' }}>
            <span style={{
              fontSize: '0.8rem',
              letterSpacing: '0.2em',
              color: 'var(--color-sakura-400)',
              textTransform: 'uppercase',
              fontWeight: 600,
            }}>
              — ĐẶC QUYỀN THƯỢNG LƯU —
            </span>
            <h2 style={{
              fontSize: 'clamp(2rem, 4vw, 2.8rem)',
              fontWeight: 700,
              color: '#FFFFFF',
              marginTop: '12px',
            }}>
              Cam Kết Vĩnh Cửu Từ AURA
            </h2>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '24px',
          }}>
            {[
              {
                title: 'Thu Đổi 100% Giá Trị',
                desc: 'Chính sách bảo toàn giá trị và nâng cấp kim cương lớn trọn đời với bảng giá quy đổi công khai.',
                icon: <RefreshCw size={32} color="#ff6584" />,
              },
              {
                title: 'Bảo Hành Làm Mới Trọn Đời',
                desc: 'Dịch vụ vệ sinh sóng siêu âm, đánh bóng và gắn lại đá tấm rơi rụng hoàn toàn miễn phí.',
                icon: <Sparkles size={32} color="#f3d08c" />,
              },
              {
                title: 'Khắc Tên Laser Cá Nhân Hóa',
                desc: 'Khắc thông điệp bí mật, ngày cưới hoặc vân tay bên trong lòng nhẫn với độ sắc nét tuyệt đối.',
                icon: <Shield size={32} color="#ff6584" />,
              },
              {
                title: 'Vận Chuyển Hỏa Tốc Bảo Hiểm',
                desc: 'Bàn giao kín đáo trong vòng 2 giờ tại trung tâm bằng xe chuyên dụng có bảo hiểm 100%.',
                icon: <Clock size={32} color="#f3d08c" />,
              },
            ].map((p, idx) => (
              <div
                key={p.title}
                className={`luxury-glass-card reveal delay-${(idx + 1) * 100}`}
                style={{ padding: '32px 28px' }}
              >
                <div style={{
                  width: '56px',
                  height: '56px',
                  borderRadius: '12px',
                  backgroundColor: 'rgba(255, 255, 255, 0.04)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '20px',
                }}>
                  {p.icon}
                </div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '10px' }}>
                  {p.title}
                </h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', lineHeight: 1.6 }}>
                  {p.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* =========================================================================
          SECTION 5: ĐÁNH GIÁ TỪ KHÁCH HÀNG VIP (Reveals on Scroll)
          ========================================================================= */}
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
              — ĐÁNH GIÁ TỪ CHỦ NHÂN —
            </span>
            <h2 style={{ fontSize: '2.4rem', fontWeight: 700, color: '#FFFFFF', marginTop: '12px' }}>
              Trải Nghiệm Thượng Lưu Đích Thực
            </h2>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
            gap: '24px',
          }}>
            {[
              {
                name: 'Minh Trang',
                title: 'Doanh nhân • Hà Nội',
                review: 'Chiếc nhẫn Solitaire 2.5 carat cầu hôn đẹp ngoài sức tưởng tượng. Ánh lửa kim cương rực rỡ và giác cắt Triple Excellent không tì vết. Bạn đời của mình thực sự xúc động!',
                item: 'Nhẫn Solitaire Diamond 2.5ct',
              },
              {
                name: 'Bác sĩ Hoàng Nam',
                title: 'Bệnh viện Vinmec • TP.HCM',
                review: 'Sợi dây chuyền vàng Ý 750 gia công cực kỳ đầm và êm cổ. Hộp đựng bọc nhung dạ kèm đèn chiếu điểm sang trọng vô cùng. Rất hài lòng về độ chuyên nghiệp của AURA.',
                item: 'Dây Chuyền Ý 750 Aurora',
              },
              {
                name: 'Phương Thảo',
                title: 'CEO Real Estate • Đà Nẵng',
                review: 'Đã mua chiếc vòng tay Tennis thứ hai tại đây. Dịch vụ cố vấn VIP riêng biệt rất tinh tế, bảo hành kiểm định minh bạch khiến mình hoàn toàn yên tâm.',
                item: 'Vòng Tay Tennis Diamond Pave',
              },
            ].map((r, i) => (
              <div
                key={r.name}
                className={`luxury-glass-card reveal delay-${(i + 1) * 150}`}
                style={{ padding: '32px' }}
              >
                <div style={{ display: 'flex', gap: '4px', marginBottom: '16px' }}>
                  {[1, 2, 3, 4, 5].map((s) => (
                    <Star key={s} size={16} fill="#ff6584" color="#ff6584" />
                  ))}
                </div>
                <p style={{ fontSize: '0.92rem', color: 'var(--color-text-secondary)', lineHeight: 1.7, fontStyle: 'italic', marginBottom: '24px' }}>
                  "{r.review}"
                </p>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '16px', borderTop: '1px solid rgba(255, 255, 255, 0.06)' }}>
                  <div>
                    <div style={{ fontSize: '0.95rem', fontWeight: 600, color: '#FFFFFF' }}>{r.name}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>{r.title}</div>
                  </div>
                  <span style={{
                    fontSize: '0.72rem',
                    color: 'var(--color-sakura-300)',
                    backgroundColor: 'rgba(255, 101, 132, 0.1)',
                    padding: '4px 10px',
                    borderRadius: '9999px',
                    border: '1px solid rgba(255, 101, 132, 0.25)',
                  }}>
                    Verified Purchase
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

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
                  {health ? `${health.application} • Profile [${health.environment}]` : 'Spring Boot 3.3.4 Enterprise Service'}
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
