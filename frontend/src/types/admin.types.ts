import { Address } from './address.types';

export type InventoryStatus = 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
export type AdjustmentType = 'ADD' | 'DEDUCT' | 'SET';
export type UserStatusType = 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'DELETED';
export type RoleTypeName = 'ROLE_CUSTOMER' | 'ROLE_STAFF' | 'ROLE_ADMIN';
export type DiscountTypeValue = 'PERCENTAGE' | 'FIXED_AMOUNT';

/**
 * Inventory Detail Item for Warehouse Management
 */
export interface InventoryItem {
  inventoryId: number;
  variantId: number;
  productId: number;
  productName: string;
  productSlug: string;
  sku: string;
  sizeOption?: string;
  materialType?: string;
  quantity: number;
  reservedQuantity: number;
  availableQuantity: number;
  lowStockThreshold: number;
  version: number;
  stockStatus: InventoryStatus;
  updatedAt: string;
}

export interface StockAdjustmentPayload {
  variantId: number;
  adjustmentType: AdjustmentType;
  quantity: number;
  reason?: string;
}

export interface UpdateThresholdPayload {
  lowStockThreshold: number;
}

/**
 * Admin Coupon Management Types
 */
export interface AdminCoupon {
  id: number;
  code: string;
  discountType: DiscountTypeValue;
  discountValue: number;
  minOrderValue?: number;
  maxDiscount?: number;
  usageLimit: number;
  usedCount: number;
  startDate: string;
  endDate: string;
  isActive: boolean;
  isExpired: boolean;
  createdAt: string;
}

export interface CreateCouponPayload {
  code: string;
  discountType: DiscountTypeValue;
  discountValue: number;
  minOrderValue?: number;
  maxDiscount?: number;
  usageLimit: number;
  startDate: string;
  endDate: string;
  isActive?: boolean;
}

export interface UpdateCouponPayload {
  discountType?: DiscountTypeValue;
  discountValue?: number;
  minOrderValue?: number;
  maxDiscount?: number;
  usageLimit?: number;
  startDate?: string;
  endDate?: string;
  isActive?: boolean;
}

/**
 * Admin Customer & User Types
 */
export interface AdminUserSummary {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  phone?: string;
  status: UserStatusType;
  roles: RoleTypeName[];
  totalOrders: number;
  totalSpent: number;
  createdAt: string;
}

export interface AdminUserDetail {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  phone?: string;
  avatarUrl?: string;
  status: UserStatusType;
  roles: RoleTypeName[];
  addresses: Address[];
  totalOrders: number;
  totalSpent: number;
  createdAt: string;
  updatedAt?: string;
}

export interface UpdateUserStatusPayload {
  status: UserStatusType;
  reason?: string;
}

export interface UpdateUserRolePayload {
  roles: RoleTypeName[];
}

/**
 * Admin Product Variants & Images
 */
export interface CreateVariantPayload {
  sku: string;
  sizeOption?: string;
  materialType?: string;
  originalPrice: number;
  salePrice: number;
  initialStock?: number;
  lowStockThreshold?: number;
  status?: 'ACTIVE' | 'INACTIVE';
}

export interface UpdateVariantPayload {
  sizeOption?: string;
  materialType?: string;
  originalPrice?: number;
  salePrice?: number;
  status?: 'ACTIVE' | 'INACTIVE';
}

export interface AddProductImagePayload {
  imageUrl: string;
  publicId?: string;
  isThumbnail?: boolean;
  sortOrder?: number;
}
