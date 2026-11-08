import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { productService } from '../services/productService';
import { ProductSummary, Category } from '../types/product.types';
import { ProductCard } from '../components/product/ProductCard';
import { Filter, SlidersHorizontal, ArrowUpDown, RefreshCw, Gem, Sparkles } from 'lucide-react';
import { useScrollReveal } from '../hooks/useScrollReveal';

export const ProductCatalogPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);

  // Filter States from URL or Defaults
  const currentCategory = searchParams.get('category') || '';
  const currentSearch = searchParams.get('search') || '';
  const currentSort = searchParams.get('sort') || 'createdAt,desc';
  const currentPage = parseInt(searchParams.get('page') || '0', 10);
  const currentPriceRange = searchParams.get('priceRange') || 'all';
  const currentMaterial = searchParams.get('material') || 'all';

  useScrollReveal(products);

  // Load Categories once
  useEffect(() => {
    productService
      .getCategories()
      .then((data: Category[]) => setCategories(data))
      .catch((err: unknown) => console.warn('Could not load categories:', err));
  }, []);

  // Fetch Products based on current active query params
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      let minPrice: number | undefined;
      let maxPrice: number | undefined;

      if (currentPriceRange === 'under-20') {
        maxPrice = 20000000;
      } else if (currentPriceRange === '20-50') {
        minPrice = 20000000;
        maxPrice = 50000000;
      } else if (currentPriceRange === '50-100') {
        minPrice = 50000000;
        maxPrice = 100000000;
      } else if (currentPriceRange === 'over-100') {
        minPrice = 100000000;
      }

      const [sortBy, sortDir] = currentSort.split(',');

      const res = await productService.filterProducts({
        categorySlug: currentCategory || undefined,
        keyword: currentSearch || undefined,
        minPrice,
        maxPrice,
        material: currentMaterial !== 'all' ? currentMaterial : undefined,
        inStockOnly: searchParams.get('stock') !== 'all',
        page: currentPage,
        size: 9,
        sortBy: sortBy || 'createdAt',
        sortDirection: (sortDir as 'asc' | 'desc') || 'desc',
      });

      if (res && res.content) {
        setProducts(res.content);
        setTotalPages(res.totalPages || 1);
        setTotalElements(res.totalElements || res.content.length);
      }
    } catch (err) {
      console.warn('Failed to fetch filtered products:', err);
      setProducts([]);
      setTotalPages(1);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  }, [currentCategory, currentSearch, currentSort, currentPage, currentPriceRange, currentMaterial, searchParams]);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const updateFilterParam = (key: string, value: string) => {
    const nextParams = new URLSearchParams(searchParams);
    if (value && value !== 'all') {
      nextParams.set(key, value);
    } else {
      nextParams.delete(key);
    }
    nextParams.set('page', '0'); // Reset to first page on filter change
    setSearchParams(nextParams);
  };

  const handlePageChange = (newPage: number) => {
    const nextParams = new URLSearchParams(searchParams);
    nextParams.set('page', newPage.toString());
    setSearchParams(nextParams);
    window.scrollTo({ top: 300, behavior: 'smooth' });
  };

  const clearAllFilters = () => {
    setSearchParams(new URLSearchParams());
  };

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '100px', paddingBottom: '80px' }}>
      {/* Catalog Hero Banner */}
      <section
        style={{
          padding: '40px 0 30px',
          borderBottom: '1px solid rgba(255, 255, 255, 0.06)',
          background: 'linear-gradient(180deg, rgba(255, 101, 132, 0.05) 0%, transparent 100%)',
          marginBottom: '40px',
        }}
      >
        <div className="container">
          <div style={{ maxWidth: '720px' }}>
            <div
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '8px',
                fontSize: '0.78rem',
                fontWeight: 600,
                letterSpacing: '0.15em',
                color: 'var(--color-sakura-300)',
                textTransform: 'uppercase',
                marginBottom: '12px',
              }}
            >
              <Sparkles size={14} /> BỘ SƯU TẬP TUYỆT TÁC KIM HOÀN 2026
            </div>
            <h1
              style={{
                fontSize: 'clamp(2.2rem, 4vw, 3.2rem)',
                fontWeight: 700,
                color: '#FFFFFF',
                letterSpacing: '-0.02em',
                lineHeight: 1.2,
                marginBottom: '14px',
              }}
            >
              {currentCategory
                ? categories.find((c) => c.slug === currentCategory)?.name || 'Bộ Sưu Tập Trang Sức'
                : 'Đẳng Cấp Tuyệt Tác & Độc Bản'}
            </h1>
            <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.98rem', lineHeight: 1.6 }}>
              Tuyển tập những món trang sức cao cấp chế tác thủ công từ vàng Ý 18K và kim cương tự nhiên tiêu chuẩn GIA quốc tế.
            </p>
          </div>
        </div>
      </section>

      {/* Main Content Area */}
      <div className="container">
        <div style={{ display: 'grid', gridTemplateColumns: '260px 1fr', gap: '36px', alignItems: 'start' }}>
          {/* =========================================================================
              SIDEBAR FILTERS
              ========================================================================= */}
          <aside
            style={{
              backgroundColor: 'rgba(14, 16, 23, 0.7)',
              backdropFilter: 'blur(16px)',
              border: '1px solid rgba(255, 255, 255, 0.08)',
              borderRadius: '16px',
              padding: '24px 20px',
              position: 'sticky',
              top: '100px',
            }}
          >
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                paddingBottom: '16px',
                borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
                marginBottom: '20px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.9rem', fontWeight: 700, color: '#FFFFFF' }}>
                <SlidersHorizontal size={16} color="#ff6584" /> BỘ LỌC
              </div>
              {(currentCategory || currentPriceRange !== 'all' || currentMaterial !== 'all' || currentSearch) && (
                <button
                  onClick={clearAllFilters}
                  style={{
                    backgroundColor: 'transparent',
                    border: 'none',
                    color: 'var(--color-sakura-300)',
                    fontSize: '0.75rem',
                    cursor: 'pointer',
                    textDecoration: 'underline',
                  }}
                >
                  Xóa lọc
                </button>
              )}
            </div>

            {/* Filter 1: Danh Mục */}
            <div style={{ marginBottom: '24px' }}>
              <h4 style={{ fontSize: '0.8rem', fontWeight: 600, color: '#FFFFFF', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '12px' }}>
                Danh Mục Trang Sức
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <button
                  onClick={() => updateFilterParam('category', '')}
                  style={{
                    padding: '8px 12px',
                    borderRadius: '8px',
                    fontSize: '0.82rem',
                    textAlign: 'left',
                    cursor: 'pointer',
                    border: 'none',
                    transition: 'all 0.2s ease',
                    backgroundColor: !currentCategory ? 'rgba(255, 101, 132, 0.15)' : 'transparent',
                    color: !currentCategory ? 'var(--color-sakura-300)' : 'var(--color-text-secondary)',
                    fontWeight: !currentCategory ? 600 : 400,
                  }}
                >
                  Tất Cả Danh Mục
                </button>

                {(categories.length > 0
                  ? categories.map((category) => ({ slug: category.slug, name: category.name }))
                  : [
                      { slug: 'nhan-kim-cuong', name: 'Nhẫn Kim Cương' },
                      { slug: 'day-chuyen-mat-day', name: 'Dây Chuyền & Mặt Dây' },
                      { slug: 'vong-tay-lac', name: 'Vòng Tay & Lắc' },
                      { slug: 'bong-tai', name: 'Bông Tai' },
                      { slug: 'bo-trang-suc', name: 'Bộ Trang Sức' },
                      { slug: 'nhan-cuoi', name: 'Nhẫn Cưới' },
                      { slug: 'kieng-vong-co', name: 'Kiềng và Vòng Cổ' },
                      { slug: 'charm-phong-thuy', name: 'Charm Phong Thủy' },
                    ]
                ).map((cat) => (
                  <button
                    key={cat.slug}
                    onClick={() => updateFilterParam('category', cat.slug)}
                    style={{
                      padding: '8px 12px',
                      borderRadius: '8px',
                      fontSize: '0.82rem',
                      textAlign: 'left',
                      cursor: 'pointer',
                      border: 'none',
                      transition: 'all 0.2s ease',
                      backgroundColor: currentCategory === cat.slug ? 'rgba(255, 101, 132, 0.15)' : 'transparent',
                      color: currentCategory === cat.slug ? 'var(--color-sakura-300)' : 'var(--color-text-secondary)',
                      fontWeight: currentCategory === cat.slug ? 600 : 400,
                    }}
                  >
                    {cat.name}
                  </button>
                ))}
              </div>
            </div>

            {/* Filter 2: Khoảng Giá */}
            <div style={{ marginBottom: '24px' }}>
              <h4 style={{ fontSize: '0.8rem', fontWeight: 600, color: '#FFFFFF', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '12px' }}>
                Khoảng Giá (VND)
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {[
                  { id: 'all', label: 'Tất cả mức giá' },
                  { id: 'under-20', label: 'Dưới 20 triệu' },
                  { id: '20-50', label: '20 - 50 triệu' },
                  { id: '50-100', label: '50 - 100 triệu' },
                  { id: 'over-100', label: 'Trên 100 triệu' },
                ].map((p) => (
                  <label
                    key={p.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px',
                      fontSize: '0.82rem',
                      color: currentPriceRange === p.id ? '#FFFFFF' : 'var(--color-text-secondary)',
                      cursor: 'pointer',
                    }}
                  >
                    <input
                      type="radio"
                      name="priceRange"
                      checked={currentPriceRange === p.id}
                      onChange={() => updateFilterParam('priceRange', p.id)}
                      style={{ accentColor: '#ff6584', cursor: 'pointer' }}
                    />
                    {p.label}
                  </label>
                ))}
              </div>
            </div>

            {/* Filter 3: Chất Liệu Chế Tác */}
            <div>
              <h4 style={{ fontSize: '0.8rem', fontWeight: 600, color: '#FFFFFF', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '12px' }}>
                Chất Liệu Kim Hoàn
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {[
                  { id: 'all', label: 'Tất cả chất liệu' },
                  { id: 'Vàng Ý', label: 'Vàng Ý 750 / 18K' },
                  { id: 'Kim Cương', label: 'Kim Cương GIA' },
                  { id: 'Bạch Kim', label: 'Bạch Kim (Platinum 950)' },
                  { id: 'Ngọc Trai', label: 'Ngọc Trai Tự Nhiên' },
                ].map((m) => (
                  <label
                    key={m.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px',
                      fontSize: '0.82rem',
                      color: currentMaterial === m.id ? '#FFFFFF' : 'var(--color-text-secondary)',
                      cursor: 'pointer',
                    }}
                  >
                    <input
                      type="radio"
                      name="material"
                      checked={currentMaterial === m.id}
                      onChange={() => updateFilterParam('material', m.id)}
                      style={{ accentColor: '#ff6584', cursor: 'pointer' }}
                    />
                    {m.label}
                  </label>
                ))}
              </div>
            </div>
          </aside>

          {/* =========================================================================
              PRODUCT GRID & TOOLBAR
              ========================================================================= */}
          <div>
            {/* Toolbar */}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '14px 20px',
                backgroundColor: 'rgba(14, 16, 23, 0.7)',
                backdropFilter: 'blur(16px)',
                border: '1px solid rgba(255, 255, 255, 0.08)',
                borderRadius: '12px',
                marginBottom: '28px',
                flexWrap: 'wrap',
                gap: '12px',
              }}
            >
              <div style={{ fontSize: '0.86rem', color: 'var(--color-text-secondary)' }}>
                Hiển thị <span style={{ color: '#FFFFFF', fontWeight: 600 }}>{products.length}</span> trên{' '}
                <span style={{ color: '#FFFFFF', fontWeight: 600 }}>{totalElements}</span> tuyệt tác
                {currentSearch && (
                  <span>
                    {' '}
                    cho từ khóa "<span style={{ color: 'var(--color-sakura-300)' }}>{currentSearch}</span>"
                  </span>
                )}
              </div>

              <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.82rem', color: '#FFFFFF', cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={searchParams.get('stock') !== 'all'}
                  onChange={(e) => updateFilterParam('stock', e.target.checked ? '' : 'all')}
                />
                Chỉ hiện món còn hàng
              </label>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <ArrowUpDown size={15} color="#9ea3b0" />
                <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Sắp xếp:</span>
                <select
                  value={currentSort}
                  onChange={(e) => updateFilterParam('sort', e.target.value)}
                  style={{
                    backgroundColor: 'rgba(255, 255, 255, 0.06)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    borderRadius: '8px',
                    padding: '6px 12px',
                    fontSize: '0.82rem',
                    outline: 'none',
                    cursor: 'pointer',
                  }}
                >
                  <option value="createdAt,desc" style={{ backgroundColor: '#0e1017' }}>
                    Mới ra mắt nhất
                  </option>
                  <option value="minPrice,asc" style={{ backgroundColor: '#0e1017' }}>
                    Giá: Thấp đến Cao
                  </option>
                  <option value="minPrice,desc" style={{ backgroundColor: '#0e1017' }}>
                    Giá: Cao đến Thấp
                  </option>
                  <option value="name,asc" style={{ backgroundColor: '#0e1017' }}>
                    Tên tác phẩm: A - Z
                  </option>
                </select>
              </div>
            </div>

            {/* Loading State */}
            {loading ? (
              <div style={{ textAlign: 'center', padding: '100px 0' }}>
                <RefreshCw size={36} color="#ff6584" style={{ animation: 'spin 1.5s linear infinite' }} />
                <p style={{ marginTop: '16px', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                  Đang tải bộ sưu tập trang sức...
                </p>
              </div>
            ) : products.length > 0 ? (
              <>
                {/* Product Grid */}
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(290px, 1fr))',
                    gap: '24px',
                    marginBottom: '40px',
                  }}
                >
                  {products.map((p, idx) => (
                    <div key={p.id} className={`reveal delay-${((idx % 3) + 1) * 100}`}>
                      <ProductCard product={p} />
                    </div>
                  ))}
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div style={{ display: 'flex', justifyContent: 'center', gap: '8px', marginTop: '20px' }}>
                    {Array.from({ length: totalPages }).map((_, pageIdx) => (
                      <button
                        key={pageIdx}
                        onClick={() => handlePageChange(pageIdx)}
                        style={{
                          width: '38px',
                          height: '38px',
                          borderRadius: '8px',
                          border: pageIdx === currentPage ? '1px solid #ff6584' : '1px solid rgba(255, 255, 255, 0.08)',
                          backgroundColor: pageIdx === currentPage ? '#ff6584' : 'rgba(255, 255, 255, 0.04)',
                          color: '#FFFFFF',
                          fontSize: '0.85rem',
                          fontWeight: 600,
                          cursor: 'pointer',
                          transition: 'all 0.2s ease',
                        }}
                      >
                        {pageIdx + 1}
                      </button>
                    ))}
                  </div>
                )}
              </>
            ) : (
              /* Empty State */
              <div
                style={{
                  textAlign: 'center',
                  padding: '80px 20px',
                  backgroundColor: 'rgba(14, 16, 23, 0.5)',
                  borderRadius: '16px',
                  border: '1px solid rgba(255, 255, 255, 0.06)',
                }}
              >
                <Gem size={64} color="#ff6584" style={{ marginBottom: '16px', opacity: 0.8 }} />
                <h3 style={{ fontSize: '1.3rem', fontWeight: 600, color: '#FFFFFF', marginBottom: '8px' }}>
                  Không tìm thấy tuyệt tác phù hợp
                </h3>
                <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.88rem', maxWidth: '400px', margin: '0 auto 24px' }}>
                  Vui lòng thử điều chỉnh lại mức giá, chất liệu hoặc tìm kiếm từ khóa khác.
                </p>
                <button onClick={clearAllFilters} className="btn-pill-sakura" style={{ padding: '10px 24px' }}>
                  <Filter size={15} /> Xóa Bộ Lọc Tìm Kiếm
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
