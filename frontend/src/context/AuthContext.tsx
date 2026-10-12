import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authService } from '../services/authService';
import { UserSummary, LoginRequest, RegisterRequest, AuthResponse } from '../types/auth.types';
import { useToast } from './ToastContext';

interface AuthContextValue {
  user: UserSummary | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isStaff: boolean;
  isLoading: boolean;
  isAuthModalOpen: boolean;
  authModalMode: 'login' | 'register';
  openAuthModal: (mode?: 'login' | 'register') => void;
  closeAuthModal: () => void;
  login: (data: LoginRequest) => Promise<AuthResponse>;
  register: (data: RegisterRequest) => Promise<AuthResponse>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserSummary | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(false);
  const [authModalMode, setAuthModalMode] = useState<'login' | 'register'>('login');
  const { success, error } = useToast();

  const refreshUser = useCallback(async () => {
    const token = localStorage.getItem('access_token');
    if (!token) {
      setUser(null);
      setIsLoading(false);
      return;
    }

    try {
      const userData = await authService.getCurrentUser();
      setUser(userData);
    } catch (err: unknown) {
      console.warn('Session expired or invalid token:', err);
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshUser();
  }, [refreshUser]);

  const openAuthModal = useCallback((mode: 'login' | 'register' = 'login') => {
    setAuthModalMode(mode);
    setIsAuthModalOpen(true);
  }, []);

  const closeAuthModal = useCallback(() => {
    setIsAuthModalOpen(false);
  }, []);

  const login = useCallback(
    async (data: LoginRequest): Promise<AuthResponse> => {
      try {
        const response = await authService.login(data);
        setUser(response.user);
        setIsAuthModalOpen(false);
        success(`Chào mừng ${response.user.fullName || response.user.username} quay trở lại!`, 'Đăng Nhập Thành Công');
        return response;
      } catch (err: unknown) {
        let msg = 'Tài khoản hoặc mật khẩu không chính xác.';
        if (err && typeof err === 'object' && 'response' in err) {
          const axiosErr = err as { response?: { data?: { message?: string } } };
          if (axiosErr.response?.data?.message) {
            msg = axiosErr.response.data.message;
          }
        }
        error(msg, 'Đăng Nhập Thất Bại');
        throw err;
      }
    },
    [success, error]
  );

  const register = useCallback(
    async (data: RegisterRequest): Promise<AuthResponse> => {
      try {
        const response = await authService.register(data);
        setUser(response.user);
        setIsAuthModalOpen(false);
        success(`Chào mừng ${response.user.fullName || response.user.username} gia nhập AURA Fine Jewelry!`, 'Đăng Ký Thành Công');
        return response;
      } catch (err: unknown) {
        let msg = 'Đăng ký không thành công. Vui lòng kiểm tra lại thông tin.';
        if (err && typeof err === 'object' && 'response' in err) {
          const axiosErr = err as { response?: { data?: { message?: string } } };
          if (axiosErr.response?.data?.message) {
            msg = axiosErr.response.data.message;
          }
        }
        error(msg, 'Đăng Ký Thất Bại');
        throw err;
      }
    },
    [success, error]
  );

  const logout = useCallback(async () => {
    try {
      await authService.logout();
    } catch (err) {
      console.error('Logout error:', err);
    } finally {
      setUser(null);
      success('Đã đăng xuất phiên làm việc an toàn.', 'Đăng Xuất');
    }
  }, [success]);

  const isAuthenticated = !!user;
  const isAdmin = !!user?.roles?.includes('ROLE_ADMIN');
  const isStaff = !!user?.roles?.some((r) => r === 'ROLE_ADMIN' || r === 'ROLE_STAFF');

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        isAdmin,
        isStaff,
        isLoading,
        isAuthModalOpen,
        authModalMode,
        openAuthModal,
        closeAuthModal,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextValue => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
