import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { AuthResponse, LoginRequest, RegisterRequest, UserSummary } from '../types/auth.types';

export const authService = {
  /**
   * Register new customer account
   */
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post<ApiResponse<AuthResponse>>('/api/v1/auth/register', data);
    const authData = response.data.data;
    if (authData.accessToken) {
      localStorage.setItem('access_token', authData.accessToken);
      localStorage.setItem('refresh_token', authData.refreshToken);
    }
    return authData;
  },

  /**
   * Login user with username/email & password
   */
  async login(data: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post<ApiResponse<AuthResponse>>('/api/v1/auth/login', data);
    const authData = response.data.data;
    if (authData.accessToken) {
      localStorage.setItem('access_token', authData.accessToken);
      localStorage.setItem('refresh_token', authData.refreshToken);
    }
    return authData;
  },

  /**
   * Refresh expired access token
   */
  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = localStorage.getItem('refresh_token');
    const response = await apiClient.post<ApiResponse<AuthResponse>>('/api/v1/auth/refresh', { refreshToken });
    const authData = response.data.data;
    if (authData.accessToken) {
      localStorage.setItem('access_token', authData.accessToken);
      localStorage.setItem('refresh_token', authData.refreshToken);
    }
    return authData;
  },

  /**
   * Logout user session
   */
  async logout(): Promise<void> {
    const refreshToken = localStorage.getItem('refresh_token');
    try {
      await apiClient.post('/api/v1/auth/logout', { refreshToken });
    } finally {
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
    }
  },

  /**
   * Get current authenticated user profile
   */
  async getCurrentUser(): Promise<UserSummary> {
    const response = await apiClient.get<ApiResponse<UserSummary>>('/api/v1/auth/me');
    return response.data.data;
  },
};
