import React, { useState, useEffect } from 'react';
import { healthService } from '../services/healthService';
import { HealthStatus } from '../types/api.types';
import { Sparkles, Activity, CheckCircle2, XCircle, ArrowRight, RefreshCw, Server, Shield, Layers } from 'lucide-react';

export const HomePage: React.FC = () => {
  const [health, setHealth] = useState<HealthStatus | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [pingLatency, setPingLatency] = useState<number | null>(null);

  const fetchHealth = async () => {
    setLoading(true);
    setError(null);
    const startTime = performance.now();
    try {
      const response = await healthService.checkHealth();
      const endTime = performance.now();
      setHealth(response.data);
      setPingLatency(Math.round(endTime - startTime));
    } catch (err: unknown) {
      console.error('Lỗi kiểm tra Backend Health:', err);
      setError('Chưa kết nối được Backend Spring Boot (Cổng 8080 chưa khởi chạy hoặc đang build).');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHealth();
  }, []);

  return (
    <div className="animate-fade-in">
      {/* Hero Banner */}
      <section style={{
        position: 'relative',
        padding: '100px 0 80px',
        textAlign: 'center',
        background: 'radial-gradient(ellipse at center, rgba(212, 175, 55, 0.08) 0%, rgba(11, 12, 14, 0.95) 75%)',
        borderBottom: '1px solid var(--color-border)',
      }}>
        <div className="container" style={{ maxWidth: '900px' }}>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            padding: '6px 16px',
            borderRadius: '9999px',
            backgroundColor: 'rgba(212, 175, 55, 0.1)',
            border: '1px solid var(--color-border-gold)',
            color: 'var(--color-gold-400)',
            fontSize: '0.8rem',
            letterSpacing: '0.1em',
            textTransform: 'uppercase',
            marginBottom: '24px'
          }}>
            <Sparkles size={14} /> Bộ Sưu Tập Tuyệt Tác Trang Sức 2026
          </div>

          <h1 className="heading-serif gold-gradient-text" style={{
            fontSize: 'clamp(2.2rem, 5vw, 3.6rem)',
            fontWeight: 700,
            lineHeight: 1.2,
            marginBottom: '20px'
          }}>
            Tôn Vinh Vẻ Đẹp Vĩnh Cửu Qua Từng Giác Cắt
          </h1>

          <p style={{
            fontSize: '1.05rem',
            color: 'var(--color-text-secondary)',
            marginBottom: '36px',
            lineHeight: 1.7
          }}>
            Trải nghiệm nền tảng thương mại điện tử trang sức cao cấp AURA, được kiến trúc theo tiêu chuẩn enterprise với Java Spring Boot 3 & React TypeScript.
          </p>

          <div style={{ display: 'flex', gap: '16px', justifyContent: 'center', flexWrap: 'wrap' }}>
            <a href="#danh-muc" className="btn-gold">
              Khám Phá Danh Mục <ArrowRight size={16} />
            </a>
            <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noreferrer" className="btn-outline-gold">
              Tài Liệu Swagger API
            </a>
          </div>
        </div>
      </section>

      {/* Live System Connectivity Widget */}
      <section style={{ padding: '40px 0', borderBottom: '1px solid var(--color-border)' }}>
        <div className="container">
          <div className="luxury-card" style={{
            display: 'flex',
            flexWrap: 'wrap',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: '20px',
            background: 'linear-gradient(145deg, #15181F 0%, #101216 100%)',
            border: '1px solid rgba(212, 175, 55, 0.25)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                padding: '12px',
                borderRadius: '8px',
                backgroundColor: health?.status === 'UP' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
                color: health?.status === 'UP' ? 'var(--color-success)' : 'var(--color-error)'
              }}>
                <Server size={28} />
              </div>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontSize: '0.82rem', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-text-muted)' }}>
                    Trạng Thái Kết Nối Backend
                  </span>
                  {health?.status === 'UP' ? (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: 'var(--color-success)', fontSize: '0.78rem', fontWeight: 600 }}>
                      <CheckCircle2 size={14} /> LIVE (UP)
                    </span>
                  ) : (
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: 'var(--color-warning)', fontSize: '0.78rem', fontWeight: 600 }}>
                      <Activity size={14} /> {loading ? 'Đang kết nối...' : 'OFFLINE'}
                    </span>
                  )}
                </div>
                <div style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--color-text-primary)', marginTop: '4px' }}>
                  {health ? `${health.application} • Profile: [${health.environment}]` : 'Spring Boot 3.3.4 Service (Port 8080)'}
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '24px', flexWrap: 'wrap' }}>
              {pingLatency !== null && (
                <div style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                  Độ trễ API: <strong style={{ color: 'var(--color-gold-400)' }}>{pingLatency}ms</strong>
                </div>
              )}
              {health?.serverTime && (
                <div style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                  Server Time: <span style={{ color: 'var(--color-text-primary)' }}>{new Date(health.serverTime).toLocaleTimeString('vi-VN')}</span>
                </div>
              )}
              <button
                onClick={fetchHealth}
                disabled={loading}
                className="btn-outline-gold"
                style={{ padding: '8px 16px', fontSize: '0.78rem' }}
              >
                <RefreshCw size={14} className={loading ? 'animate-spin' : ''} /> {loading ? 'Đang kiểm tra...' : 'Ping Lại'}
              </button>
            </div>
          </div>
          {error && (
            <p style={{ marginTop: '10px', fontSize: '0.82rem', color: 'var(--color-error)', display: 'flex', alignItems: 'center', gap: '6px' }}>
              <XCircle size={14} /> {error}
            </p>
          )}
        </div>
      </section>

      {/* Featured Categories Showcase */}
      <section id="danh-muc" style={{ padding: '70px 0' }}>
        <div className="container">
          <div style={{ textAlign: 'center', marginBottom: '48px' }}>
            <span style={{ fontSize: '0.8rem', letterSpacing: '0.2em', color: 'var(--color-gold-400)', textTransform: 'uppercase' }}>
              Danh Mục Tinh Tuyển
            </span>
            <h2 className="heading-serif" style={{ fontSize: '2rem', marginTop: '8px' }}>
              Kiệt Tác Nghệ Thuật Kim Hoàn
            </h2>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
            gap: '24px'
          }}>
            {/* Category 1 */}
            <div className="luxury-card">
              <div style={{
                height: '180px',
                borderRadius: '6px',
                background: 'linear-gradient(135deg, #242933, #15181E)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '20px',
                border: '1px solid var(--color-border)',
                color: 'var(--color-gold-400)'
              }}>
                <Sparkles size={48} />
              </div>
              <h3 className="heading-serif" style={{ fontSize: '1.15rem', color: 'var(--color-text-primary)' }}>Nhẫn Kim Cương</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', marginTop: '8px' }}>
                Đạt chứng nhận GIA, giác cắt tròn Brilliant & giác cắt Princess trên nền vàng 18K.
              </p>
              <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-gold-400)', fontWeight: 600 }}>Từ 15.000.000₫</span>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>42 mẫu mã</span>
              </div>
            </div>

            {/* Category 2 */}
            <div className="luxury-card">
              <div style={{
                height: '180px',
                borderRadius: '6px',
                background: 'linear-gradient(135deg, #242933, #15181E)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '20px',
                border: '1px solid var(--color-border)',
                color: 'var(--color-gold-400)'
              }}>
                <Shield size={48} />
              </div>
              <h3 className="heading-serif" style={{ fontSize: '1.15rem', color: 'var(--color-text-primary)' }}>Dây Chuyền Vàng Ý</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', marginTop: '8px' }}>
                Dây chuyền vàng Ý 750 và vàng hồng 18K nhập khẩu trực tiếp với họa tiết thanh lịch.
              </p>
              <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-gold-400)', fontWeight: 600 }}>Từ 8.500.000₫</span>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>35 mẫu mã</span>
              </div>
            </div>

            {/* Category 3 */}
            <div className="luxury-card">
              <div style={{
                height: '180px',
                borderRadius: '6px',
                background: 'linear-gradient(135deg, #242933, #15181E)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '20px',
                border: '1px solid var(--color-border)',
                color: 'var(--color-gold-400)'
              }}>
                <Layers size={48} />
              </div>
              <h3 className="heading-serif" style={{ fontSize: '1.15rem', color: 'var(--color-text-primary)' }}>Vòng Tay Tennis</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)', marginTop: '8px' }}>
                Chuỗi đá quý và kim cương tấm đính viền chấu tinh xảo ôm trọn cổ tay quý phái.
              </p>
              <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--color-gold-400)', fontWeight: 600 }}>Từ 22.000.000₫</span>
                <span style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>18 mẫu mã</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
