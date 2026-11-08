import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { productService } from '../services/productService';
import { ProductDetail } from '../types/product.types';
import { useCompare } from '../context/CompareContext';
import { useCart } from '../context/CartContext';

export const ComparePage: React.FC = () => {
  const { slugs, remove, clear } = useCompare();
  const { addToCart } = useCart();
  const [products, setProducts] = useState<ProductDetail[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    if (slugs.length === 0) {
      setProducts([]);
      return;
    }
    setLoading(true);
    Promise.all(slugs.map((slug) => productService.getProductBySlug(slug).catch(() => null)))
      .then((rows) => {
        if (!cancelled) setProducts(rows.filter((row): row is ProductDetail => !!row));
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [slugs]);

  const cell = (value?: string | number | null) => (value === undefined || value === null || value === '' ? '—' : String(value));

  const rows: { label: string; value: (product: ProductDetail) => string }[] = [
    { label: 'Giá', value: (p) => `${(p.variants?.[0]?.salePrice || 0).toLocaleString('vi-VN')}₫` },
    { label: 'Giá gốc', value: (p) => `${(p.variants?.[0]?.originalPrice || 0).toLocaleString('vi-VN')}₫` },
    { label: 'Khuyến mãi', value: (p) => (p.variants?.[0] && p.variants[0].salePrice < p.variants[0].originalPrice ? 'Đang giảm giá' : 'Không') },
    { label: 'Tồn kho', value: (p) => cell(p.variants?.reduce((sum, v) => sum + (v.availableQuantity || 0), 0)) },
    { label: 'Chất liệu', value: (p) => cell(p.materialOverview || p.variants?.[0]?.materialType) },
    { label: 'Trọng lượng', value: (p) => (p.weightGrams ? `${p.weightGrams} g` : '—') },
    { label: 'Loại đá', value: (p) => cell(p.stoneType) },
    { label: 'Độ tinh khiết', value: (p) => cell(p.clarity) },
    { label: 'Kích thước', value: (p) => cell(p.variants?.map((v) => v.sizeOption).filter(Boolean).join(', ')) },
    { label: 'Bảo hành', value: (p) => cell(p.warranty) },
  ];

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '110px', paddingBottom: '80px' }}>
      <div className="container">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', gap: '12px', flexWrap: 'wrap' }}>
          <h1 style={{ color: '#fff', fontSize: '2rem' }}>So sánh sản phẩm ({slugs.length}/3)</h1>
          {slugs.length > 0 && (
            <button className="btn-outline-pill" onClick={clear} style={{ padding: '8px 16px' }}>
              Xóa danh sách
            </button>
          )}
        </div>
        {loading && <p style={{ color: 'var(--color-text-secondary)' }}>Đang tải sản phẩm...</p>}
        {!loading && products.length === 0 && (
          <div style={{ color: 'var(--color-text-secondary)' }}>
            Chưa chọn sản phẩm. Hãy bấm So sánh trên trang sản phẩm.
            <div style={{ marginTop: '16px' }}>
              <Link to="/products" className="btn-pill-sakura" style={{ padding: '10px 18px' }}>Xem cửa hàng</Link>
            </div>
          </div>
        )}
        {products.length > 0 && (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', minWidth: '720px', color: '#fff' }}>
              <thead>
                <tr>
                  <th style={{ textAlign: 'left', padding: '12px', color: 'var(--color-text-muted)' }}>Thông số</th>
                  {products.map((product) => (
                    <th key={product.id} style={{ padding: '12px', textAlign: 'left', minWidth: '220px' }}>
                      <img src={product.images?.[0]?.imageUrl} alt="" style={{ width: '100%', height: '160px', objectFit: 'cover', borderRadius: '12px', background: '#111' }} />
                      <div style={{ marginTop: '10px' }}>{product.name}</div>
                      <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                        <Link to={`/products/${product.slug}`} className="btn-outline-pill" style={{ padding: '6px 10px', fontSize: '0.78rem' }}>Chi tiết</Link>
                        <button
                          className="btn-pill-sakura"
                          style={{ padding: '6px 10px', fontSize: '0.78rem' }}
                          onClick={() => {
                            const variant = product.variants?.[0];
                            if (!variant) return;
                            addToCart({
                              variantId: variant.id,
                              productId: product.id,
                              productName: product.name,
                              productSlug: product.slug,
                              sku: variant.sku,
                              sizeOption: variant.sizeOption,
                              materialType: variant.materialType,
                              unitPrice: variant.salePrice,
                              originalPrice: variant.originalPrice,
                              thumbnailUrl: product.images?.[0]?.imageUrl,
                              availableStock: variant.availableQuantity,
                            });
                          }}
                        >
                          Thêm giỏ
                        </button>
                        <button onClick={() => remove(product.slug)} style={{ background: 'transparent', color: '#EF4444', border: 'none', cursor: 'pointer' }}>Bỏ</button>
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.label} style={{ borderTop: '1px solid rgba(255,255,255,0.08)' }}>
                    <td style={{ padding: '12px', color: 'var(--color-text-muted)' }}>{row.label}</td>
                    {products.map((product) => (
                      <td key={product.id} style={{ padding: '12px' }}>{row.value(product)}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
