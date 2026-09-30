import { PaymentMethodType, PaymentStatus } from './order.types';

export interface BankTransferDetails {
  bankId: string;
  accountNo: string;
  accountName: string;
  amount: number;
  orderCode: string;
  transferContent: string;
  qrCodeUrl: string;
}

export interface PaymentInitiateResponse {
  orderCode: string;
  paymentMethod: PaymentMethodType;
  paymentStatus: PaymentStatus;
  amount: number;
  paymentUrl?: string; // Dành cho VNPay redirect
  bankTransfer?: BankTransferDetails; // Dành cho VietQR chuyển khoản
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
