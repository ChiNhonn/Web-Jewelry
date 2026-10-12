import { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RefreshCw, Home } from 'lucide-react';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Uncaught error caught by ErrorBoundary:', error, errorInfo);
  }

  private handleReload = () => {
    window.location.reload();
  };

  private handleGoHome = () => {
    window.location.href = '/';
  };

  public render() {
    if (this.state.hasError) {
      return (
        <div
          style={{
            minHeight: '100vh',
            backgroundColor: '#050508',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '24px',
          }}
        >
          <div
            style={{
              maxWidth: '540px',
              width: '100%',
              backgroundColor: 'rgba(14, 16, 23, 0.85)',
              border: '1px solid rgba(255, 101, 132, 0.3)',
              borderRadius: '20px',
              padding: '48px 36px',
              textAlign: 'center',
              boxShadow: '0 25px 60px rgba(0, 0, 0, 0.9), 0 0 40px rgba(255, 101, 132, 0.1)',
            }}
          >
            <div
              style={{
                width: '72px',
                height: '72px',
                borderRadius: '50%',
                backgroundColor: 'rgba(255, 101, 132, 0.15)',
                border: '1px solid rgba(255, 101, 132, 0.4)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 20px',
              }}
            >
              <AlertTriangle size={36} color="#ff6584" />
            </div>

            <h2 style={{ fontSize: '1.8rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '12px' }}>
              Trải Nghiệm Bị Gián Đoạn
            </h2>

            <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.92rem', lineHeight: 1.6, marginBottom: '32px' }}>
              Hệ thống AURA gặp một sự cố kết xuất ngoài ý muốn. Mọi dữ liệu giỏ hàng và bảo mật của quý khách vẫn được lưu giữ an toàn.
            </p>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '14px', flexWrap: 'wrap' }}>
              <button
                onClick={this.handleReload}
                className="btn-pill-sakura"
                style={{ padding: '12px 28px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}
              >
                <RefreshCw size={16} /> Tải Lại Trang
              </button>

              <button
                onClick={this.handleGoHome}
                className="btn-outline-pill"
                style={{ padding: '12px 28px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}
              >
                <Home size={16} /> Về Trang Chủ
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
