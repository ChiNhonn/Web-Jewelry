import React, { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastProvider } from './context/ToastContext';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { Header } from './components/common/Header';
import { Footer } from './components/common/Footer';
import { AuthModal } from './components/auth/AuthModal';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { Sparkles, RefreshCw } from 'lucide-react';

// Core Home Page loaded eagerly for immediate First Meaningful Paint
import { HomePage } from './pages/HomePage';

// Route Code Splitting: Lazy-load sub-routes to optimize initial bundle size & TTI
const ProductCatalogPage = lazy(() =>
  import('./pages/ProductCatalogPage').then((m) => ({ default: m.ProductCatalogPage }))
);
const ProductDetailPage = lazy(() =>
  import('./pages/ProductDetailPage').then((m) => ({ default: m.ProductDetailPage }))
);
const CartPage = lazy(() =>
  import('./pages/CartPage').then((m) => ({ default: m.CartPage }))
);
const CheckoutPage = lazy(() =>
  import('./pages/CheckoutPage').then((m) => ({ default: m.CheckoutPage }))
);
const PaymentReturnPage = lazy(() =>
  import('./pages/PaymentReturnPage').then((m) => ({ default: m.PaymentReturnPage }))
);
const CustomerPortalPage = lazy(() =>
  import('./pages/CustomerPortalPage').then((m) => ({ default: m.CustomerPortalPage }))
);
const AdminPortalPage = lazy(() =>
  import('./pages/AdminPortalPage').then((m) => ({ default: m.AdminPortalPage }))
);

/**
 * Luxury Pulsing Sakura Loader for Suspense Fallback
 */
const LuxuryRouteLoader: React.FC = () => (
  <div
    style={{
      minHeight: '70vh',
      backgroundColor: '#050508',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '16px',
    }}
  >
    <div
      style={{
        width: '56px',
        height: '56px',
        borderRadius: '50%',
        backgroundColor: 'rgba(255, 101, 132, 0.1)',
        border: '1px solid rgba(255, 101, 132, 0.3)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <RefreshCw size={24} color="#ff6584" style={{ animation: 'spin 1.2s linear infinite' }} />
    </div>
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '6px',
        fontSize: '0.8rem',
        letterSpacing: '0.15em',
        color: 'var(--color-sakura-300)',
        textTransform: 'uppercase',
      }}
    >
      <Sparkles size={14} /> Khởi tạo không gian AURA...
    </div>
  </div>
);

export const App: React.FC = () => {
  return (
    <ErrorBoundary>
      <ToastProvider>
        <AuthProvider>
          <CartProvider>
            <BrowserRouter>
              <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh', backgroundColor: '#050508' }}>
                <Header />
                <main style={{ flex: 1 }}>
                  <Suspense fallback={<LuxuryRouteLoader />}>
                    <Routes>
                      {/* Public Pages */}
                      <Route path="/" element={<HomePage />} />
                      <Route path="/products" element={<ProductCatalogPage />} />
                      <Route path="/products/:slug" element={<ProductDetailPage />} />
                      <Route path="/cart" element={<CartPage />} />
                      <Route path="/checkout" element={<CheckoutPage />} />
                      <Route path="/payment/vnpay-return" element={<PaymentReturnPage />} />

                      {/* Customer Portal */}
                      <Route path="/account" element={<CustomerPortalPage />} />

                      {/* Admin CMS & Operations */}
                      <Route path="/admin" element={<AdminPortalPage />} />

                      {/* Fallback */}
                      <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                  </Suspense>
                </main>
                <Footer />
                {/* Global Auth Modal */}
                <AuthModal />
              </div>
            </BrowserRouter>
          </CartProvider>
        </AuthProvider>
      </ToastProvider>
    </ErrorBoundary>
  );
};

export default App;
