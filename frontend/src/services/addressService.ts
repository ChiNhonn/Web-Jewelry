import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { Address, AddressRequest } from '../types/address.types';

export const addressService = {
  /**
   * Get user's saved shipping addresses
   */
  async getAddresses(): Promise<Address[]> {
    const response = await apiClient.get<ApiResponse<Address[]>>('/api/v1/addresses');
    return response.data.data;
  },

  /**
   * Get shipping address details
   */
  async getAddressById(id: number): Promise<Address> {
    const response = await apiClient.get<ApiResponse<Address>>(`/api/v1/addresses/${id}`);
    return response.data.data;
  },

  /**
   * Create new shipping address
   */
  async createAddress(data: AddressRequest): Promise<Address> {
    const response = await apiClient.post<ApiResponse<Address>>('/api/v1/addresses', data);
    return response.data.data;
  },

  /**
   * Update existing shipping address
   */
  async updateAddress(id: number, data: AddressRequest): Promise<Address> {
    const response = await apiClient.put<ApiResponse<Address>>(`/api/v1/addresses/${id}`, data);
    return response.data.data;
  },

  /**
   * Delete shipping address
   */
  async deleteAddress(id: number): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/api/v1/addresses/${id}`);
  },

  /**
   * Set address as default
   */
  async setDefaultAddress(id: number): Promise<Address> {
    const response = await apiClient.put<ApiResponse<Address>>(`/api/v1/addresses/${id}/default`);
    return response.data.data;
  },
};
