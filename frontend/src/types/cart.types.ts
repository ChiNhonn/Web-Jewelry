export interface CartItem {
  id: number;
  variantId: number;
  productId: number;
  productName: string;
  productSlug: string;
  sku: string;
  sizeOption?: string;
  materialType?: string;
  unitPrice: number;
  originalPrice: number;
  quantity: number;
  subtotal: number;
  thumbnailUrl?: string;
  availableStock: number;
  inStock: boolean;
}

export interface Cart {
  id: number;
  items: CartItem[];
  totalItems: number;
  totalQuantity: number;
  subtotal: number;
  shippingFee: number;
  estimatedTotal: number;
}

export interface AddToCartRequest {
  variantId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

export interface WishlistItem {
  id: number;
  productId: number;
  productName: string;
  productSlug: string;
  thumbnailUrl?: string;
  minPrice: number;
  maxPrice: number;
  createdAt: string;
}
