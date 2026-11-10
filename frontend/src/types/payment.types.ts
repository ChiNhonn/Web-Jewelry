import { PaymentMethodType, PaymentStatus } from './order.types';

export interface PaymentInitiateResponse {
  orderCode: string;
  paymentMethod: PaymentMethodType;
  paymentStatus: PaymentStatus;
  amount: number;
  paymentUrl?: string;
  message: string;
}

export interface VNPayReturnResponse {
  orderCode: string;
  transactionCode?: string;
  responseCode: string;
  isSuccess: boolean;
  amount: number;
  bankCode?: string;
  payDate?: string;
  message: string;
}
