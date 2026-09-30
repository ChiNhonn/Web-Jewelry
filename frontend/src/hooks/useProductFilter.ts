import { useState, useCallback } from 'react';
import { ProductFilterParams, ProductSummary } from '../types/product.types';
import { PageResponse } from '../types/api.types';
import { productService } from '../services/productService';

export const useProductFilter = (initialParams: ProductFilterParams = { page: 0, size: 20 }) => {
  const [params, setParams] = useState<ProductFilterParams>(initialParams);
  const [data, setData] = useState<PageResponse<ProductSummary> | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchProducts = useCallback(async (overrideParams?: ProductFilterParams) => {
    setLoading(true);
    setError(null);
    try {
      const activeParams = overrideParams || params;
      const result = await productService.filterProducts(activeParams);
      setData(result);
    } catch (err: unknown) {
      console.error('Lỗi khi tải danh sách sản phẩm:', err);
      setError('Không thể tải danh sách sản phẩm. Vui lòng thử lại sau.');
    } finally {
      setLoading(false);
    }
  }, [params]);

  const updateFilters = (newFilters: Partial<ProductFilterParams>) => {
    setParams((prev) => {
      const updated = { ...prev, ...newFilters, page: 0 }; // Reset về page 0 khi đổi filter
      fetchProducts(updated);
      return updated;
    });
  };

  const changePage = (newPage: number) => {
    setParams((prev) => {
      const updated = { ...prev, page: newPage };
      fetchProducts(updated);
      return updated;
    });
  };

  return {
    params,
    data,
    loading,
    error,
    fetchProducts,
    updateFilters,
    changePage,
  };
};
