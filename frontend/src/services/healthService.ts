import apiClient from '../api/axiosClient';
import { ApiResponse, HealthStatus } from '../types/api.types';

export const healthService = {
  /**
   * Check backend server connectivity and system status
   */
  async checkHealth(): Promise<ApiResponse<HealthStatus>> {
    const response = await apiClient.get<ApiResponse<HealthStatus>>('/api/v1/health');
    return response.data;
  },

  /**
   * Simple ping check
   */
  async ping(): Promise<string> {
    const response = await apiClient.get<ApiResponse<string>>('/api/v1/health/ping');
    return response.data.data;
  },
};
