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
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>100% Vàng & Đá Quý Thật</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Đầy đủ chứng nhận kiểm định GIA / PNJ</p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <Truck size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Giao Hàng Hỏa Tốc & Bảo Hiểm</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Bảo hiểm 100% giá trị đơn hàng</p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <RefreshCw size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Đổi Mới Trong 48 Giờ</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Làm mới và chỉnh ni nhẫn trọn đời</p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <ShieldCheck size={32} color="var(--color-gold-400)" />
          <div>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--color-text-primary)' }}>Bảo Mật & Thanh Toán An Toàn</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--color-text-secondary)' }}>Mã hóa SSL 256-bit chuẩn quốc tế</p>
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
            Kiến tạo những tác phẩm trang sức đỉnh cao, tôn vinh vẻ đẹp vĩnh cửu và sự quý phái của bạn qua từng giác cắt tinh xảo.
          </p>
        </div>

        <div>
          <h4 className="heading-serif" style={{ fontSize: '0.9rem', color: 'var(--color-gold-400)', marginBottom: '16px' }}>Dòng Sản Phẩm</h4>
          <ul style={{ listStyle: 'none', fontSize: '0.85rem', color: 'var(--color-text-secondary)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <li><a href="#nhan-cuoi">Nhẫn Cưới & Cầu Hôn</a></li>
            <li><a href="#kim-cuong">Kim Cương Tự Nhiên GIA</a></li>
            <li><a href="#vang-18k">Trang Sức Vàng 18K & Ý 750</a></li>
            <li><a href="#vong-tay">Vòng Tay & Lắc Vàng Phong Thủy</a></li>
          </ul>
        </div>

        <div>
          <h4 className="heading-serif" style={{ fontSize: '0.9rem', color: 'var(--color-gold-400)', marginBottom: '16px' }}>Dịch Vụ Khách Hàng</h4>
          <ul style={{ listStyle: 'none', fontSize: '0.85rem', color: 'var(--color-text-secondary)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <li><a href="#huong-dan-do-ni">Hướng Dẫn Đo Ni Nhẫn</a></li>
            <li><a href="#chinh-sach-bao-hanh">Chính Sách Bảo Hành & Thu Đổi</a></li>
            <li><a href="#giao-hang">Vận Chuyển & Thanh Toán</a></li>
            <li><a href="#cau-hoi">Câu Hỏi Thường Gặp (FAQ)</a></li>
          </ul>
        </div>

        <div>
          <h4 className="heading-serif" style={{ fontSize: '0.9rem', color: 'var(--color-gold-400)', marginBottom: '16px' }}>Hệ Thống Công Nghệ</h4>
          <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', lineHeight: 1.6 }}>
            <strong>Architecture:</strong> Spring Boot 3.3.4 (Java 21) + React 18 (TypeScript)<br />
            <strong>Database:</strong> MySQL 8.0 with Optimistic Locking<br />
            <strong>API Spec:</strong> OpenAPI 3.0 (Swagger)
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
        © 2026 AURA Luxury Jewelry System. Thiết kế kiến trúc chuẩn Enterprise E-Commerce.
      </div>
    </footer>
  );
};
