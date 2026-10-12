import React, { createContext, useContext, useState, useCallback } from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface ToastMessage {
  id: string;
  type: ToastType;
  title?: string;
  message: string;
  duration?: number;
}

interface ToastContextValue {
  showToast: (type: ToastType, message: string, title?: string, duration?: number) => void;
  success: (message: string, title?: string) => void;
  error: (message: string, title?: string) => void;
  info: (message: string, title?: string) => void;
  warning: (message: string, title?: string) => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const showToast = useCallback(
    (type: ToastType, message: string, title?: string, duration = 4000) => {
      const id = `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
      const newToast: ToastMessage = { id, type, message, title, duration };

      setToasts((prev) => [...prev, newToast]);

      if (duration > 0) {
        setTimeout(() => {
          removeToast(id);
        }, duration);
      }
    },
    [removeToast]
  );

  const success = useCallback((msg: string, title?: string) => showToast('success', msg, title), [showToast]);
  const error = useCallback((msg: string, title?: string) => showToast('error', msg, title), [showToast]);
  const info = useCallback((msg: string, title?: string) => showToast('info', msg, title), [showToast]);
  const warning = useCallback((msg: string, title?: string) => showToast('warning', msg, title), [showToast]);

  return (
    <ToastContext.Provider value={{ showToast, success, error, info, warning }}>
      {children}
      {/* Toast Render Container */}
      <div
        style={{
          position: 'fixed',
          top: '24px',
          right: '24px',
          zIndex: 9999,
          display: 'flex',
          flexDirection: 'column',
          gap: '12px',
          maxWidth: '420px',
          width: 'calc(100vw - 48px)',
          pointerEvents: 'none',
        }}
      >
        {toasts.map((toast) => {
          const isSuccess = toast.type === 'success';
          const isError = toast.type === 'error';
          const isWarning = toast.type === 'warning';

          const accentColor = isSuccess
            ? '#10B981'
            : isError
            ? '#EF4444'
            : isWarning
            ? '#F59E0B'
            : '#ff6584';

          const bgGlow = isSuccess
            ? 'rgba(16, 185, 129, 0.15)'
            : isError
            ? 'rgba(239, 68, 68, 0.15)'
            : 'rgba(255, 101, 132, 0.15)';

          return (
            <div
              key={toast.id}
              style={{
                pointerEvents: 'auto',
                backgroundColor: 'rgba(14, 16, 23, 0.95)',
                backdropFilter: 'blur(20px)',
                WebkitBackdropFilter: 'blur(20px)',
                border: `1px solid ${accentColor}40`,
                boxShadow: `0 12px 30px rgba(0, 0, 0, 0.7), 0 0 25px ${bgGlow}`,
                borderRadius: '12px',
                padding: '14px 18px',
                display: 'flex',
                alignItems: 'flex-start',
                gap: '12px',
                animation: 'slideInRight 0.35s cubic-bezier(0.16, 1, 0.3, 1)',
                transition: 'all 0.3s ease',
              }}
            >
              <div style={{ color: accentColor, marginTop: '2px', flexShrink: 0 }}>
                {isSuccess && <CheckCircle2 size={20} />}
                {isError && <AlertCircle size={20} />}
                {isWarning && <AlertCircle size={20} />}
                {!isSuccess && !isError && !isWarning && <Info size={20} />}
              </div>

              <div style={{ flex: 1 }}>
                {toast.title && (
                  <h4 style={{ fontSize: '0.88rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '2px' }}>
                    {toast.title}
                  </h4>
                )}
                <p style={{ fontSize: '0.82rem', color: 'var(--color-text-secondary)', lineHeight: 1.4 }}>
                  {toast.message}
                </p>
              </div>

              <button
                onClick={() => removeToast(toast.id)}
                style={{
                  color: 'var(--color-text-muted)',
                  cursor: 'pointer',
                  padding: '2px',
                  borderRadius: '4px',
                  display: 'flex',
                  transition: 'color 0.2s ease',
                }}
                onMouseEnter={(e) => (e.currentTarget.style.color = '#FFFFFF')}
                onMouseLeave={(e) => (e.currentTarget.style.color = 'var(--color-text-muted)')}
              >
                <X size={16} />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = (): ToastContextValue => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};
