export type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'PROCESSING'
  | 'SHIPPING'
  | 'DELIVERED'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'RETURNED'
  | 'REFUNDED';

export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';

export type PaymentMethodType =
  | 'COD'
  | 'VNPAY'
  | 'MOMO'
  | 'BANK_TRANSFER'
  | 'CREDIT_CARD';

export interface OrderItem {
  id: number;
  variantId: number;
  productId: number;
  productName: string;
  productSlug: string;
  sku: string;
  variantAttributes?: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  thumbnailUrl?: string;
}

export interface Order {
  id: number;
  orderCode: string;
  userId: number;
  customerName?: string;
  customerEmail?: string;
  customerPhone?: string;
  orderStatus: OrderStatus;
  paymentStatus: PaymentStatus;
  paymentMethod: PaymentMethodType;
  transactionCode?: string;
  subtotal: number;
  discountAmount: number;
  shippingFee: number;
  totalAmount: number;
  couponCode?: string;
  shippingAddressSnapshot: string;
  customerNote?: string;
  items: OrderItem[];
  totalQuantity: number;
  createdAt: string;
  updatedAt?: string;
}

export interface OrderSummary {
  id: number;
  orderCode: string;
  userId: number;
  customerName?: string;
  orderStatus: OrderStatus;
  paymentStatus: PaymentStatus;
  paymentMethod: PaymentMethodType;
  totalAmount: number;
  itemCount: number;
  firstItemName?: string;
  firstItemThumbnail?: string;
  createdAt: string;
}

export interface CreateOrderRequest {
  shippingAddressId?: number;
  shippingAddress?: {
    receiverName: string;
    phone: string;
    streetAddress: string;
    ward: string;
    district: string;
    province: string;
    postalCode?: string;
  };
  paymentMethod: PaymentMethodType;
  couponCode?: string;
  customerNote?: string;
}

export interface CouponValidation {
  id?: number;
  code: string;
  discountType?: 'PERCENTAGE' | 'FIXED_AMOUNT';
  discountValue?: number;
  minOrderValue?: number;
  maxDiscount?: number;
  valid: boolean;
  calculatedDiscount: number;
  message: string;
}
