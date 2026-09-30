import apiClient from '../api/axiosClient';
import { ApiResponse } from '../types/api.types';
import { PaymentInitiateResponse, VNPayReturnResponse } from '../types/payment.types';

export const paymentService = {
  /**
   * Initiate payment for an order (generates VNPay payment URL or VietQR transfer payload)
   */
  async initiatePayment(orderCode: string): Promise<PaymentInitiateResponse> {
    const response = await apiClient.post<ApiResponse<PaymentInitiateResponse>>(
      `/api/v1/payments/${orderCode}/initiate`
    );
    return response.data.data;
  },

  /**
   * Get payment status and details for an order
   */
  async getPaymentDetails(orderCode: string): Promise<PaymentInitiateResponse> {
    const response = await apiClient.get<ApiResponse<PaymentInitiateResponse>>(
      `/api/v1/payments/${orderCode}`
    );
    return response.data.data;
  },

  /**
   * Process VNPay return parameters after client is redirected back from VNPay
   */
  async processVNPayReturn(queryParams: Record<string, string>): Promise<VNPayReturnResponse> {
    const params = new URLSearchParams(queryParams);
    const response = await apiClient.get<ApiResponse<VNPayReturnResponse>>(
      `/api/v1/payments/vnpay-return?${params.toString()}`
    );
    return response.data.data;
  },
};
