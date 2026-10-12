import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { CartItem } from '../types/cart.types';
import { cartService } from '../services/cartService';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';
import apiClient from '../api/axiosClient';

export interface AppliedCoupon {
  code: string;
  discountType: 'PERCENTAGE' | 'FIXED_AMOUNT';
  discountValue: number;
  maxDiscount?: number;
  minOrderValue?: number;
}

interface CartContextValue {
  items: CartItem[];
  totalQuantity: number;
  subtotal: number;
  shippingFee: number;
  appliedCoupon: AppliedCoupon | null;
  discountAmount: number;
  finalTotal: number;
  isLoading: boolean;
  addToCart: (item: {
    variantId: number;
    productId: number;
    productName: string;
    productSlug: string;
    sku: string;
    sizeOption?: string;
    materialType?: string;
    unitPrice: number;
    originalPrice: number;
    thumbnailUrl?: string;
    availableStock?: number;
  }, quantity?: number) => Promise<void>;
  updateQuantity: (variantId: number, quantity: number) => Promise<void>;
  removeItem: (variantId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  applyCoupon: (code: string) => Promise<boolean>;
  removeCoupon: () => void;
  refreshCart: () => Promise<void>;
}

const CartContext = createContext<CartContextValue | undefined>(undefined);

const LOCAL_STORAGE_CART_KEY = 'aura_guest_cart';

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>(() => {
    try {
      const saved = localStorage.getItem(LOCAL_STORAGE_CART_KEY);
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  const [appliedCoupon, setAppliedCoupon] = useState<AppliedCoupon | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { isAuthenticated } = useAuth();
  const { success, error, info } = useToast();

  // Save to local storage whenever items change
  useEffect(() => {
    try {
      localStorage.setItem(LOCAL_STORAGE_CART_KEY, JSON.stringify(items));
    } catch (e) {
      console.warn('Failed to save cart to localStorage', e);
    }
  }, [items]);

  // Load server cart when authenticated
  const refreshCart = useCallback(async () => {
    if (!isAuthenticated) return;
    setIsLoading(true);
    try {
      const serverCart = await cartService.getCart();
      if (serverCart && serverCart.items) {
        setItems(serverCart.items);
      }
    } catch (err) {
      console.warn('Could not fetch server cart:', err);
    } finally {
      setIsLoading(false);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (isAuthenticated) {
      refreshCart();
    }
  }, [isAuthenticated, refreshCart]);

  // Calculations
  const totalQuantity = items.reduce((sum, item) => sum + item.quantity, 0);
  const subtotal = items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
  const shippingFee = 0; // Free luxury white-glove shipping on all jewelry orders

  let discountAmount = 0;
  if (appliedCoupon) {
    if (appliedCoupon.discountType === 'PERCENTAGE') {
      const calculated = (subtotal * appliedCoupon.discountValue) / 100;
      discountAmount = appliedCoupon.maxDiscount ? Math.min(calculated, appliedCoupon.maxDiscount) : calculated;
    } else {
      discountAmount = Math.min(appliedCoupon.discountValue, subtotal);
    }
  }

  const finalTotal = Math.max(0, subtotal - discountAmount + shippingFee);

  const addToCart = useCallback(
    async (
      productInfo: {
        variantId: number;
        productId: number;
        productName: string;
        productSlug: string;
        sku: string;
        sizeOption?: string;
        materialType?: string;
        unitPrice: number;
        originalPrice: number;
        thumbnailUrl?: string;
        availableStock?: number;
      },
      quantity = 1
    ) => {
      if (isAuthenticated) {
        try {
          const updated = await cartService.addToCart({
            variantId: productInfo.variantId,
            quantity,
          });
          setItems(updated.items);
          success(`Đã thêm "${productInfo.productName}" vào túi trang sức.`, 'Thành Công');
          return;
        } catch (err) {
          console.warn('Failed server add to cart, falling back to local state:', err);
        }
      }

      // Guest / Local fallback
      setItems((prev) => {
        const existingIdx = prev.findIndex((i) => i.variantId === productInfo.variantId);
        if (existingIdx > -1) {
          const updated = [...prev];
          const currentItem = updated[existingIdx];
          const newQty = currentItem.quantity + quantity;
          updated[existingIdx] = {
            ...currentItem,
            quantity: newQty,
            subtotal: newQty * currentItem.unitPrice,
          };
          return updated;
        } else {
          const newItem: CartItem = {
            id: Date.now(),
            variantId: productInfo.variantId,
            productId: productInfo.productId,
            productName: productInfo.productName,
            productSlug: productInfo.productSlug,
            sku: productInfo.sku,
            sizeOption: productInfo.sizeOption,
            materialType: productInfo.materialType,
            unitPrice: productInfo.unitPrice,
            originalPrice: productInfo.originalPrice,
            quantity,
            subtotal: quantity * productInfo.unitPrice,
            thumbnailUrl: productInfo.thumbnailUrl,
            availableStock: productInfo.availableStock ?? 10,
            inStock: true,
          };
          return [...prev, newItem];
        }
      });

      success(`Đã thêm "${productInfo.productName}" vào túi trang sức.`, 'Thành Công');
    },
    [isAuthenticated, success]
  );

  const updateQuantity = useCallback(
    async (variantId: number, quantity: number) => {
      if (quantity <= 0) {
        removeItem(variantId);
        return;
      }

      if (isAuthenticated) {
        const item = items.find((i) => i.variantId === variantId);
        if (item) {
          try {
            const updated = await cartService.updateCartItem(item.id, { quantity });
            setItems(updated.items);
            return;
          } catch (err) {
            console.warn('Failed server updateCartItem:', err);
          }
        }
      }

      setItems((prev) =>
        prev.map((i) =>
          i.variantId === variantId
            ? { ...i, quantity, subtotal: quantity * i.unitPrice }
            : i
        )
      );
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [isAuthenticated, items]
  );

  const removeItem = useCallback(
    async (variantId: number) => {
      const item = items.find((i) => i.variantId === variantId);
      if (isAuthenticated && item) {
        try {
          const updated = await cartService.removeCartItem(item.id);
          setItems(updated.items);
          info('Đã xóa món trang sức khỏi giỏ hàng.');
          return;
        } catch (err) {
          console.warn('Failed server removeCartItem:', err);
        }
      }

      setItems((prev) => prev.filter((i) => i.variantId !== variantId));
      info('Đã xóa món trang sức khỏi giỏ hàng.');
    },
    [isAuthenticated, items, info]
  );

  const clearCart = useCallback(async () => {
    if (isAuthenticated) {
      try {
        await cartService.clearCart();
      } catch (err) {
        console.warn('Failed server clearCart:', err);
      }
    }
    setItems([]);
    setAppliedCoupon(null);
    localStorage.removeItem(LOCAL_STORAGE_CART_KEY);
  }, [isAuthenticated]);

  const applyCoupon = useCallback(
    async (code: string): Promise<boolean> => {
      const trimmed = code.trim().toUpperCase();
      if (!trimmed) return false;

      try {
        const res = await apiClient.get(`/api/v1/coupons/${trimmed}`);
        const coupon = res.data.data;
        if (coupon && coupon.active) {
          if (coupon.minOrderValue && subtotal < coupon.minOrderValue) {
            error(
              `Đơn hàng cần tối thiểu ${coupon.minOrderValue.toLocaleString('vi-VN')}₫ để áp dụng mã ${trimmed}`,
              'Chưa Đủ Điều Kiện'
            );
            return false;
          }
          setAppliedCoupon({
            code: coupon.code,
            discountType: coupon.discountType,
            discountValue: coupon.discountValue,
            maxDiscount: coupon.maxDiscount,
            minOrderValue: coupon.minOrderValue,
          });
          success(`Áp dụng mã ưu đãi ${trimmed} thành công!`, 'Ưu Đãi Đặc Quyền');
          return true;
        }
      } catch {
        // Fallback for demo coupon codes if offline
        if (trimmed === 'AURA10') {
          setAppliedCoupon({
            code: 'AURA10',
            discountType: 'PERCENTAGE',
            discountValue: 10,
            maxDiscount: 2000000,
            minOrderValue: 5000000,
          });
          success('Áp dụng mã ưu đãi AURA10 giảm 10% thành công!', 'Ưu Đãi Đặc Quyền');
          return true;
        } else if (trimmed === 'DIAMOND500K') {
          setAppliedCoupon({
            code: 'DIAMOND500K',
            discountType: 'FIXED_AMOUNT',
            discountValue: 500000,
            minOrderValue: 10000000,
          });
          success('Áp dụng mã ưu đãi DIAMOND500K giảm 500.000₫ thành công!', 'Ưu Đãi Đặc Quyền');
          return true;
        }
      }

      error('Mã giảm giá không hợp lệ hoặc đã hết lượt sử dụng.', 'Không Thể Áp Dụng');
      return false;
    },
    [subtotal, success, error]
  );

  const removeCoupon = useCallback(() => {
    setAppliedCoupon(null);
    info('Đã gỡ bỏ mã giảm giá.');
  }, [info]);

  return (
    <CartContext.Provider
      value={{
        items,
        totalQuantity,
        subtotal,
        shippingFee,
        appliedCoupon,
        discountAmount,
        finalTotal,
        isLoading,
        addToCart,
        updateQuantity,
        removeItem,
        clearCart,
        applyCoupon,
        removeCoupon,
        refreshCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = (): CartContextValue => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
