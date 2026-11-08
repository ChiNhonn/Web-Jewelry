import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';

export interface AppNotification {
  id: number;
  title: string;
  message: string;
  orderCode: string;
  read: boolean;
  createdAt: string;
}

export const notificationService = {
  async list(): Promise<AppNotification[]> {
    const response = await apiClient.get<ApiResponse<AppNotification[]>>('/api/v1/notifications');
    return response.data.data || [];
  },

  async markRead(id: number): Promise<void> {
    await apiClient.put(`/api/v1/notifications/${id}/read`);
  },
};
