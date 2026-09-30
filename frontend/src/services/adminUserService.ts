import apiClient from '../api/axiosClient';
import { ApiResponse, PageResponse } from '../types/api.types';
import {
  AdminUserDetail,
  AdminUserSummary,
  RoleTypeName,
  UpdateUserRolePayload,
  UpdateUserStatusPayload,
  UserStatusType,
} from '../types/admin.types';

export const adminUserService = {
  /**
   * Get paginated users with filters
   */
  async getAllUsers(
    keyword?: string,
    status?: UserStatusType,
    role?: RoleTypeName,
    page: number = 0,
    size: number = 10
  ): Promise<PageResponse<AdminUserSummary>> {
    const params = new URLSearchParams();
    if (keyword) params.append('keyword', keyword);
    if (status) params.append('status', status);
    if (role) params.append('role', role);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await apiClient.get<ApiResponse<PageResponse<AdminUserSummary>>>(
      `/api/v1/admin/users?${params.toString()}`
    );
    return response.data.data;
  },

  /**
   * Get user detail by ID
   */
  async getUserDetail(id: number): Promise<AdminUserDetail> {
    const response = await apiClient.get<ApiResponse<AdminUserDetail>>(
      `/api/v1/admin/users/${id}`
    );
    return response.data.data;
  },

  /**
   * Moderate user status (Lock/Unlock)
   */
  async updateUserStatus(
    id: number,
    payload: UpdateUserStatusPayload
  ): Promise<AdminUserDetail> {
    const response = await apiClient.put<ApiResponse<AdminUserDetail>>(
      `/api/v1/admin/users/${id}/status`,
      payload
    );
    return response.data.data;
  },

  /**
   * Assign/Update user roles
   */
  async updateUserRole(
    id: number,
    payload: UpdateUserRolePayload
  ): Promise<AdminUserDetail> {
    const response = await apiClient.put<ApiResponse<AdminUserDetail>>(
      `/api/v1/admin/users/${id}/role`,
      payload
    );
    return response.data.data;
  },
};
