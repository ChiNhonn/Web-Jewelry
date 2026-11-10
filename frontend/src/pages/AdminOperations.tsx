import React, { useEffect, useState } from 'react';
import { adminProductService } from '../services/adminProductService';
import { productService } from '../services/productService';
import { orderService } from '../services/orderService';
import { reviewService } from '../services/reviewService';
import { uploadService } from '../services/uploadService';
import { Category, ProductSummary } from '../types/product.types';
import { OrderStatus, OrderSummary } from '../types/order.types';
import { Review } from '../types/review.types';
import { useToast } from '../context/ToastContext';
import apiClient from '../api/axiosClient';
import { Gem, ImagePlus, Lock, Pencil, Plus, RefreshCw, Search, Trash2, X } from 'lucide-react';

const sheet: React.CSSProperties = {
  background: '#fbf6ee',
  color: '#3d2b16',
  borderRadius: '22px',
  padding: '18px',
  display: 'grid',
  gap: '14px',
};

const card: React.CSSProperties = {
  background: '#fff',
  border: '1px solid #eadfca',
  borderRadius: '16px',
  padding: '14px',
};

const field: React.CSSProperties = {
  width: '100%',
  padding: '11px 12px',
  borderRadius: '12px',
  border: '1px solid #e4d3b4',
  background: '#fff',
  color: '#3d2b16',
};

const goldButton: React.CSSProperties = {
  display: 'inline-flex',
  alignItems: 'center',
  gap: '6px',
  border: 'none',
  borderRadius: '999px',
  background: '#c6a15a',
  color: '#fff',
  fontWeight: 700,
  padding: '10px 16px',
  cursor: 'pointer',
};

const iconAction: React.CSSProperties = {
  width: '34px',
  height: '34px',
  borderRadius: '10px',
  border: '1px solid #eadfca',
  background: '#fff',
  color: '#8a6840',
  display: 'grid',
  placeItems: 'center',
  cursor: 'pointer',
};

const statusOptions: { value: OrderStatus; label: string }[] = [
  { value: 'PENDING', label: 'Chờ xác nhận' },
  { value: 'CONFIRMED', label: 'Đã xác nhận' },
  { value: 'SHIPPING', label: 'Đang giao' },
  { value: 'DELIVERED', label: 'Đã giao' },
  { value: 'CANCELLED', label: 'Đã hủy' },
];

const editableStatuses = statusOptions.filter((option) => option.value !== 'CANCELLED');

const orderTone: Record<string, { color: string; bg: string }> = {
  PENDING: { color: '#c47b12', bg: '#fff1d6' },
  CONFIRMED: { color: '#1d6fb8', bg: '#e7f3ff' },
  PROCESSING: { color: '#1d6fb8', bg: '#e7f3ff' },
  SHIPPING: { color: '#5b4db8', bg: '#eee9ff' },
  DELIVERED: { color: '#1f8a4c', bg: '#e5f8ec' },
  COMPLETED: { color: '#1f8a4c', bg: '#e5f8ec' },
  CANCELLED: { color: '#d64545', bg: '#ffe4e4' },
};

const money = (value?: number) => `${Number(value || 0).toLocaleString('vi-VN')} đ`;

const formatWhen = (value?: string) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value.replace('T', ' ').slice(0, 19);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

const dayOffset = (days: number) => {
  const date = new Date();
  date.setDate(date.getDate() + days);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
};

type ReportPreview = {
  orderCount: number;
  totalAmount: number;
  orders: Array<{
    orderCode: string;
    customerName?: string;
    phone?: string;
    orderStatus: string;
    paymentMethod: string;
    paymentStatus: string;
    totalAmount: number;
    createdAt: string;
  }>;
  products: Array<{ productName: string; quantity: number; revenue: number }>;
};

export const AdminOperations: React.FC<{ tab: 'products' | 'categories' | 'orders' | 'reviews' | 'reports' | 'promotions' }> = ({ tab }) => {
  const { success, error } = useToast();
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [orderStatus, setOrderStatus] = useState('');
  const [promotions, setPromotions] = useState<Array<{
    id: number; code: string; name: string; description?: string; discountType: string;
    discountValue: number; startDate: string; endDate: string; targetType: string; active: boolean;
  }>>([]);
  const [form, setForm] = useState({
    name: '', baseSku: '', categoryId: '', materialOverview: '', weightGrams: '', stoneType: '',
    clarity: '', warranty: '', sizeOption: '', originalPrice: '', salePrice: '', stock: '1', imageUrl: '',
  });
  const [showProductForm, setShowProductForm] = useState(false);
  const [showCategoryForm, setShowCategoryForm] = useState(false);
  const [productQuery, setProductQuery] = useState('');
  const [productCategory, setProductCategory] = useState('');
  const [productStock, setProductStock] = useState('');
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '' });
  const [report, setReport] = useState({ type: 'revenue', format: 'pdf' as 'pdf' | 'csv', status: '', from: dayOffset(-30), to: dayOffset(0) });
  const [preview, setPreview] = useState<ReportPreview | null>(null);
  const [previewError, setPreviewError] = useState('');

  const load = () => {
    productService.getCategories().then(setCategories).catch(() => setCategories([]));
    if (tab === 'products') {
      adminProductService.getAllProducts(undefined, undefined, 0, 100).then((res) => setProducts(res.content || [])).catch(() => setProducts([]));
    }
    if (tab === 'orders') {
      orderService.getAllOrders(undefined, undefined, 0, 100).then((res) => setOrders(res.content || [])).catch(() => setOrders([]));
    }
    if (tab === 'reviews') {
      reviewService.getAllReviewsForAdmin(undefined, 0, 50).then((res) => setReviews(res.content || [])).catch(() => setReviews([]));
    }
    if (tab === 'promotions') {
      apiClient.get('/api/v1/admin/promotions').then((res) => setPromotions(res.data.data || [])).catch(() => setPromotions([]));
    }
  };

  useEffect(() => { load(); }, [tab]);

  useEffect(() => {
    if (tab !== 'reports') return;
    const params = new URLSearchParams({ type: report.type });
    if (report.status && report.type !== 'revenue') params.set('status', report.status);
    if (report.from) params.set('from', report.from);
    if (report.to) params.set('to', report.to);
    apiClient.get(`/api/v1/admin/tools/orders/preview?${params.toString()}`)
      .then((res) => {
        setPreview(res.data.data);
        setPreviewError('');
      })
      .catch(() => {
        setPreview(null);
        setPreviewError('Chưa tải được nội dung báo cáo.');
      });
  }, [tab, report.type, report.status, report.from, report.to]);

  const createProduct = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      await adminProductService.createProduct({
        categoryId: Number(form.categoryId),
        name: form.name,
        baseSku: form.baseSku,
        materialOverview: form.materialOverview || undefined,
        weightGrams: form.weightGrams ? Number(form.weightGrams) : undefined,
        stoneType: form.stoneType || undefined,
        clarity: form.clarity || undefined,
        warranty: form.warranty || undefined,
        variants: [{
          sku: `${form.baseSku}-1`,
          sizeOption: form.sizeOption || undefined,
          materialType: form.materialOverview || undefined,
          originalPrice: Number(form.originalPrice),
          salePrice: Number(form.salePrice || form.originalPrice),
          initialStock: Number(form.stock || 0),
        }],
        images: form.imageUrl ? [{ imageUrl: form.imageUrl, isThumbnail: true, sortOrder: 0 }] : [],
      });
      success('Đã thêm sản phẩm.', 'Sản phẩm');
      setShowProductForm(false);
      setForm({
        name: '', baseSku: '', categoryId: '', materialOverview: '', weightGrams: '', stoneType: '',
        clarity: '', warranty: '', sizeOption: '', originalPrice: '', salePrice: '', stock: '1', imageUrl: '',
      });
      load();
    } catch {
      error('Không thêm được sản phẩm. Kiểm tra danh mục, SKU và giá.');
    }
  };

  const exportReport = async () => {
    const params = new URLSearchParams({ format: report.format, type: report.type });
    if (report.status && report.type !== 'revenue') params.set('status', report.status);
    if (report.from) params.set('from', report.from);
    if (report.to) params.set('to', report.to);
    try {
      const response = await apiClient.get(`/api/v1/admin/tools/orders/export?${params.toString()}`, { responseType: 'blob' });
      const url = URL.createObjectURL(response.data);
      const link = document.createElement('a');
      link.href = url;
      link.download = report.format === 'pdf' ? 'bao-cao-aura.pdf' : 'bao-cao-aura.csv';
      link.click();
      URL.revokeObjectURL(url);
      success(report.format === 'pdf' ? 'Đã xuất PDF.' : 'Đã xuất Excel.');
    } catch {
      error('Không xuất được báo cáo.');
    }
  };

  const changeOrderStatus = async (order: OrderSummary, newStatus: OrderStatus) => {
    if (order.orderStatus === 'CANCELLED') {
      error('Đơn đã hủy không thể đổi trạng thái.');
      return;
    }
    try {
      await orderService.updateOrderStatus(order.orderCode, newStatus, 'Cập nhật từ trang quản trị');
      success('Đã chuyển trạng thái đơn.');
      load();
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      error(message || 'Không chuyển được trạng thái.');
    }
  };

  if (tab === 'products') {
    const needle = productQuery.trim().toLowerCase();
    const visibleProducts = products.filter((product) => {
      const matchesText = !needle || [product.name, product.baseSku, product.categoryName, product.materialOverview]
        .some((value) => value?.toLowerCase().includes(needle));
      const matchesCategory = !productCategory || product.categorySlug === productCategory;
      const inStock = (product.stockQuantity ?? 0) > 0 && product.status !== 'OUT_OF_STOCK';
      const matchesStock = !productStock || (productStock === 'in' ? inStock : !inStock);
      return matchesText && matchesCategory && matchesStock;
    });
    return (
      <div style={sheet}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px' }}>
          <strong style={{ fontSize: '1.15rem' }}>Quản lý sản phẩm</strong>
          <button type="button" style={goldButton} onClick={() => setShowProductForm((open) => !open)}>
            {showProductForm ? <><X size={15} /> Đóng</> : <><Plus size={15} /> Thêm mới</>}
          </button>
        </div>
        {showProductForm && (
          <form onSubmit={createProduct} style={{ ...card, display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '10px' }}>
            <input required placeholder="Tên sản phẩm" style={field} value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            <input required placeholder="SKU gốc" style={field} value={form.baseSku} onChange={(e) => setForm({ ...form, baseSku: e.target.value })} />
            <select required style={field} value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}>
              <option value="">Danh mục</option>
              {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
            </select>
            <input placeholder="Chất liệu" style={field} value={form.materialOverview} onChange={(e) => setForm({ ...form, materialOverview: e.target.value })} />
            <input placeholder="Trọng lượng (g)" style={field} value={form.weightGrams} onChange={(e) => setForm({ ...form, weightGrams: e.target.value })} />
            <input placeholder="Kích thước" style={field} value={form.sizeOption} onChange={(e) => setForm({ ...form, sizeOption: e.target.value })} />
            <input required placeholder="Giá gốc" style={field} value={form.originalPrice} onChange={(e) => setForm({ ...form, originalPrice: e.target.value })} />
            <input placeholder="Giá khuyến mãi" style={field} value={form.salePrice} onChange={(e) => setForm({ ...form, salePrice: e.target.value })} />
            <input placeholder="Tồn kho" style={field} value={form.stock} onChange={(e) => setForm({ ...form, stock: e.target.value })} />
            <input placeholder="Đường dẫn ảnh" style={field} value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />
            <label style={{ ...goldButton, justifyContent: 'center', background: '#fff', color: '#8a6840', border: '1px solid #e4d3b4' }}>
              <ImagePlus size={15} /> {form.imageUrl ? 'Đã chọn ảnh' : 'Chọn ảnh'}
              <input type="file" accept="image/*" hidden onChange={async (e) => {
                const file = e.target.files?.[0];
                if (!file) return;
                try {
                  const url = await uploadService.uploadSingleImage(file, 'products');
                  setForm((prev) => ({ ...prev, imageUrl: url }));
                  success('Đã tải ảnh.');
                } catch { error('Không tải được ảnh.'); }
              }} />
            </label>
            <button style={goldButton}>Lưu sản phẩm</button>
          </form>
        )}
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(180px, 1.4fr) repeat(2, minmax(140px, 1fr)) auto', gap: '8px' }}>
          <label style={{ ...field, display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Search size={16} color="#c6a15a" />
            <input value={productQuery} onChange={(e) => setProductQuery(e.target.value)} placeholder="Tìm kiếm trang sức theo tên..." style={{ flex: 1, border: 'none', outline: 'none', background: 'transparent', color: '#3d2b16' }} />
          </label>
          <select style={field} value={productCategory} onChange={(e) => setProductCategory(e.target.value)}>
            <option value="">Tất cả danh mục</option>
            {categories.map((category) => <option key={category.id} value={category.slug}>{category.name}</option>)}
          </select>
          <select style={field} value={productStock} onChange={(e) => setProductStock(e.target.value)}>
            <option value="">Tất cả tồn kho</option>
            <option value="in">Còn hàng</option>
            <option value="out">Hết hàng</option>
          </select>
          <button type="button" style={iconAction} onClick={load} title="Làm mới"><RefreshCw size={16} /></button>
        </div>
        <div style={{ color: '#8a7355', fontSize: '0.86rem' }}>Hiển thị {visibleProducts.length} sản phẩm</div>
        {visibleProducts.length === 0 && <div style={card}>Không có sản phẩm khớp bộ lọc.</div>}
        {visibleProducts.map((product) => {
          const qty = product.stockQuantity ?? 0;
          const out = qty <= 0 || product.status === 'OUT_OF_STOCK';
          return (
            <div key={product.id} style={{ ...card, display: 'grid', gridTemplateColumns: '72px 1fr auto', gap: '12px', alignItems: 'center' }}>
              <img src={product.thumbnailUrl || ''} alt="" style={{ width: '72px', height: '72px', objectFit: 'cover', borderRadius: '12px', background: '#f6efe4' }} onError={(event) => { (event.target as HTMLImageElement).style.visibility = 'hidden'; }} />
              <div>
                <div style={{ color: '#c6a15a', fontSize: '0.78rem', fontWeight: 700 }}>{product.categoryName}</div>
                <div style={{ fontWeight: 700, marginTop: '2px' }}>{product.name}</div>
                <div style={{ color: '#c6a15a', fontWeight: 800, marginTop: '4px' }}>{money(product.minPrice)}</div>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginTop: '6px', flexWrap: 'wrap' }}>
                  <span style={{ fontSize: '0.75rem', fontWeight: 700, color: out ? '#d64545' : '#1f8a4c', background: out ? '#ffe4e4' : '#e5f8ec', borderRadius: '999px', padding: '3px 8px' }}>
                    {out ? 'Hết hàng (0)' : `Tồn kho: ${qty}`}
                  </span>
                  <span style={{ color: '#8a7355', fontSize: '0.78rem' }}>{product.materialOverview}</span>
                </div>
              </div>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button type="button" style={iconAction} title="Sửa" onClick={async () => {
                  const name = window.prompt('Tên mới', product.name);
                  if (!name) return;
                  try {
                    await adminProductService.updateProduct(product.id, { name, categoryId: categories.find((c) => c.slug === product.categorySlug)?.id });
                    success('Đã sửa sản phẩm.');
                    load();
                  } catch { error('Không sửa được sản phẩm.'); }
                }}><Pencil size={15} /></button>
                {out ? (
                  <button type="button" style={{ ...iconAction, width: 'auto', padding: '0 10px', color: '#1f8a4c' }} onClick={async () => {
                    const qtyText = window.prompt('Số lượng đưa vào bán lại', '1');
                    if (qtyText === null) return;
                    const next = Number(qtyText);
                    if (!Number.isFinite(next) || next < 1) {
                      error('Số lượng bán lại phải từ 1.');
                      return;
                    }
                    const price = product.originalPrice || product.minPrice;
                    try {
                      await adminProductService.addVariant(product.id, {
                        sku: product.baseSku || `SP-${product.id}`,
                        originalPrice: price,
                        salePrice: product.minPrice || price,
                        initialStock: next,
                      });
                      success('Đã mở bán lại.');
                      load();
                    } catch { error('Không mở bán lại được.'); }
                  }}>Bán lại</button>
                ) : (
                  <button type="button" style={{ ...iconAction, width: 'auto', padding: '0 10px' }} onClick={async () => {
                    try { await adminProductService.stopSelling(product.id); success('Đã ngừng bán.'); load(); } catch { error('Không ngừng bán được.'); }
                  }}>Ngừng bán</button>
                )}
                <button type="button" style={{ ...iconAction, color: '#d64545' }} title="Xóa" onClick={async () => {
                  if (!window.confirm('Xóa hẳn sản phẩm này?')) return;
                  try { await adminProductService.purgeProduct(product.id); success('Đã xóa.'); load(); } catch { error('Không xóa được. Nếu đã có đơn, hãy ngừng bán.'); }
                }}><Trash2 size={15} /></button>
              </div>
            </div>
          );
        })}
      </div>
    );
  }

  if (tab === 'categories') {
    return (
      <div style={sheet}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <strong style={{ fontSize: '1.15rem' }}>Quản lý danh mục</strong>
          <button type="button" style={goldButton} onClick={() => setShowCategoryForm((open) => !open)}>
            {showCategoryForm ? <><X size={15} /> Đóng</> : <><Plus size={15} /> Thêm mới</>}
          </button>
        </div>
        {showCategoryForm && (
          <form style={{ ...card, display: 'grid', gridTemplateColumns: '1fr 1.4fr auto', gap: '8px' }} onSubmit={async (event) => {
            event.preventDefault();
            try {
              await productService.createCategory({ name: categoryForm.name, description: categoryForm.description, promotionPercent: 0 });
              success('Đã thêm danh mục.');
              setCategoryForm({ name: '', description: '' });
              setShowCategoryForm(false);
              load();
            } catch { error('Không thêm được danh mục.'); }
          }}>
            <input required placeholder="Tên danh mục" style={field} value={categoryForm.name} onChange={(e) => setCategoryForm({ ...categoryForm, name: e.target.value })} />
            <input placeholder="Mô tả" style={field} value={categoryForm.description} onChange={(e) => setCategoryForm({ ...categoryForm, description: e.target.value })} />
            <button style={goldButton}>Lưu</button>
          </form>
        )}
        <div style={{ display: 'flex', justifyContent: 'space-between', color: '#8a7355', fontSize: '0.86rem' }}>
          <span>Hiện có {categories.length} danh mục trang sức</span>
          <button type="button" style={iconAction} onClick={() => { load(); success('Đã làm mới danh mục trang sức.'); }}><RefreshCw size={15} /></button>
        </div>
        {categories.map((category) => (
          <div key={category.id} style={{ ...card, display: 'flex', justifyContent: 'space-between', gap: '12px', alignItems: 'center' }}>
            <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
              <span style={{ width: '42px', height: '42px', borderRadius: '14px', background: '#f8efdc', color: '#c6a15a', display: 'grid', placeItems: 'center' }}><Gem size={18} /></span>
              <div>
                <div style={{ fontWeight: 800 }}>{category.name}</div>
                <div style={{ color: '#8a7355', fontSize: '0.84rem', marginTop: '3px' }}>{category.description || 'Chưa có mô tả'}</div>
              </div>
            </div>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button type="button" style={iconAction} onClick={async () => {
                const name = window.prompt('Tên danh mục', category.name);
                if (!name) return;
                const description = window.prompt('Mô tả', category.description || '');
                if (description === null) return;
                try {
                  await productService.updateCategory(category.id, { name, description, promotionPercent: category.promotionPercent, status: category.status });
                  success('Đã sửa danh mục.');
                  load();
                } catch { error('Không sửa được danh mục.'); }
              }}><Pencil size={15} /></button>
              <button type="button" style={{ ...iconAction, color: '#d64545' }} onClick={async () => {
                if (!window.confirm(`Xóa danh mục ${category.name}?`)) return;
                try {
                  await productService.deleteCategory(category.id);
                  success('Đã xóa danh mục.');
                  load();
                } catch { error('Không xóa được danh mục đang có sản phẩm.'); }
              }}><Trash2 size={15} /></button>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (tab === 'promotions') {
    const discountLabel = (type: string, value: number) => type === 'PERCENTAGE' ? `${value}%` : money(value);
    return (
      <div style={sheet}>
        <strong style={{ fontSize: '1.15rem' }}>Sự kiện khuyến mãi</strong>
        <p style={{ color: '#8a7355', fontSize: '0.86rem' }}>Sự kiện áp vào giá bán. Mã giảm giá không cộng thêm khi sản phẩm đã có khuyến mãi.</p>
        {promotions.length === 0 && <div style={card}>Chưa có sự kiện.</div>}
        {promotions.map((promotion) => (
          <div key={promotion.id} style={{ ...card, display: 'flex', justifyContent: 'space-between', gap: '12px', alignItems: 'center' }}>
            <div>
              <div style={{ fontWeight: 800 }}>{promotion.name}</div>
              <div style={{ color: '#8a7355', fontSize: '0.82rem', marginTop: '4px' }}>{promotion.code} · giảm {discountLabel(promotion.discountType, promotion.discountValue)} · {promotion.targetType === 'ALL' ? 'Toàn bộ sản phẩm' : 'Theo danh mục'}</div>
              <div style={{ color: '#8a7355', fontSize: '0.78rem' }}>{formatWhen(promotion.startDate)} – {formatWhen(promotion.endDate)}</div>
            </div>
            <button type="button" style={{ ...goldButton, background: promotion.active ? '#1f8a4c' : '#d64545' }} onClick={async () => {
              try {
                await apiClient.put(`/api/v1/admin/promotions/${promotion.id}/active`, { active: !promotion.active });
                success(promotion.active ? 'Đã tắt sự kiện.' : 'Đã bật sự kiện.');
                load();
              } catch { error('Không đổi được trạng thái sự kiện.'); }
            }}>{promotion.active ? 'Đang chạy' : 'Đang tắt'}</button>
          </div>
        ))}
        <strong>Khuyến mãi theo danh mục</strong>
        {categories.map((category) => (
          <div key={category.id} style={{ ...card, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>{category.name} {category.promotionPercent ? `· −${category.promotionPercent}%` : '· chưa giảm'}</span>
            <button type="button" style={iconAction} onClick={async () => {
              const percent = window.prompt('Phần trăm khuyến mãi danh mục', String(category.promotionPercent || 0));
              if (percent === null) return;
              try {
                await productService.updateCategory(category.id, { name: category.name, description: category.description, promotionPercent: Number(percent), status: category.status });
                success('Đã cập nhật khuyến mãi danh mục.');
                load();
              } catch { error('Không cập nhật được danh mục.'); }
            }}><Pencil size={15} /></button>
          </div>
        ))}
      </div>
    );
  }

  if (tab === 'orders') {
    const visibleOrders = orders.filter((order) => !orderStatus || order.orderStatus === orderStatus);
    return (
      <div style={sheet}>
        <strong style={{ fontSize: '1.15rem' }}>Quản lý đơn hàng</strong>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <span style={{ color: '#8a7355' }}>Trạng thái</span>
          <select style={{ ...field, maxWidth: '240px' }} value={orderStatus} onChange={(e) => setOrderStatus(e.target.value)}>
            <option value="">Tất cả trạng thái</option>
            {statusOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
          </select>
          <button type="button" style={iconAction} onClick={load}><RefreshCw size={16} /></button>
        </div>
        <div style={{ color: '#8a7355', fontSize: '0.86rem' }}>Hiển thị {visibleOrders.length} đơn hàng. Đơn đã hủy chỉ để xem, không đổi trạng thái.</div>
        {visibleOrders.length === 0 && <div style={card}>Không có đơn ở trạng thái này.</div>}
        {visibleOrders.map((order) => {
          const tone = orderTone[order.orderStatus] || { color: '#3d2b16', bg: '#f6efe4' };
          const locked = order.orderStatus === 'CANCELLED';
          const paid = order.paymentStatus === 'COMPLETED' ? 'Đã thanh toán' : order.paymentStatus === 'REFUNDED' ? 'Đã hoàn tiền' : 'Chưa thanh toán';
          return (
            <div key={order.id} style={card}>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '12px', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ fontWeight: 800 }}>Đơn hàng #{order.orderCode}</div>
                  <div style={{ color: '#8a7355', fontSize: '0.8rem', marginTop: '3px' }}>Ngày đặt: {formatWhen(order.createdAt)}</div>
                </div>
                {locked ? (
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px', color: tone.color, background: tone.bg, borderRadius: '999px', padding: '5px 10px', fontWeight: 700, fontSize: '0.78rem' }}>
                    <Lock size={12} /> Đã hủy
                  </span>
                ) : (
                  <select
                    value={order.orderStatus}
                    style={{ ...field, width: 'auto', color: tone.color, background: tone.bg, borderColor: tone.color, fontWeight: 700 }}
                    onChange={(event) => changeOrderStatus(order, event.target.value as OrderStatus)}
                  >
                    {editableStatuses.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
                    {!editableStatuses.some((option) => option.value === order.orderStatus) && <option value={order.orderStatus}>{order.orderStatus}</option>}
                  </select>
                )}
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '12px', marginTop: '12px', alignItems: 'flex-end' }}>
                <div style={{ color: '#6d573c', fontSize: '0.88rem' }}>
                  <div>Người nhận: {order.customerName || 'Khách'}{order.customerPhone ? ` (${order.customerPhone})` : ''}</div>
                  <div style={{ marginTop: '4px' }}>Thanh toán: {order.paymentMethod === 'VNPAY' ? 'VNPay' : 'COD'} · {paid}</div>
                  {locked && <div style={{ marginTop: '6px', color: '#d64545', fontSize: '0.78rem' }}>Trạng thái đã hủy được khóa.</div>}
                </div>
                <strong style={{ color: '#c6a15a', fontSize: '1.05rem' }}>{money(order.totalAmount)}</strong>
              </div>
            </div>
          );
        })}
      </div>
    );
  }

  if (tab === 'reviews') {
    return (
      <div style={sheet}>
        <strong style={{ fontSize: '1.15rem' }}>Quản lý đánh giá sản phẩm</strong>
        {reviews.length === 0 && <div style={card}>Chưa có đánh giá.</div>}
        {reviews.map((review) => {
          const visible = review.status === 'APPROVED';
          return (
            <div key={review.id} style={card}>
              <div style={{ display: 'flex', justifyContent: 'space-between', gap: '8px' }}>
                <strong>{review.customerName}</strong>
                <span style={{ color: '#8a7355', fontSize: '0.8rem' }}>{formatWhen(review.createdAt)}</span>
              </div>
              <div style={{ color: '#c6a15a', fontWeight: 700, marginTop: '6px' }}>{review.productName}{review.orderId ? ` · Đơn hàng #${review.orderId}` : ''}</div>
              <div style={{ color: '#e0b15a', letterSpacing: '2px', marginTop: '4px' }}>{'★'.repeat(review.rating)}{'☆'.repeat(Math.max(0, 5 - review.rating))}</div>
              <div style={{ marginTop: '6px' }}>{review.comment || '(Không có nhận xét viết kèm)'}</div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px', paddingTop: '10px', borderTop: '1px solid #f0e6d4' }}>
                <span style={{ color: visible ? '#1f8a4c' : '#d64545', fontWeight: 700, fontSize: '0.86rem' }}>Trạng thái: {visible ? 'Đang hiển thị' : 'Đang ẩn'}</span>
                <label style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                  Hiển thị
                  <input type="checkbox" checked={visible} onChange={async () => {
                    try {
                      await reviewService.updateReviewStatus(review.id, { status: visible ? 'REJECTED' : 'APPROVED' });
                      success(visible ? 'Đã ẩn nhận xét.' : 'Đã hiện nhận xét.');
                      load();
                    } catch { error('Không đổi được trạng thái nhận xét.'); }
                  }} />
                </label>
              </div>
            </div>
          );
        })}
      </div>
    );
  }

  const rangeText = `${new Date(report.from).toLocaleDateString('vi-VN')} – ${new Date(report.to).toLocaleDateString('vi-VN')}`;
  return (
    <div style={sheet}>
      <strong style={{ fontSize: '1.15rem' }}>Xuất báo cáo</strong>
      <div style={card}>
        <div style={{ fontWeight: 700, marginBottom: '8px' }}>Loại báo cáo</div>
        <select style={field} value={report.type} onChange={(e) => setReport({ ...report, type: e.target.value })}>
          <option value="revenue">Doanh thu</option>
          <option value="orders">Đơn hàng</option>
          <option value="products">Sản phẩm</option>
        </select>
      </div>
      <div style={card}>
        <div style={{ fontWeight: 700, marginBottom: '8px' }}>Trạng thái đơn hàng</div>
        <select style={field} value={report.type === 'revenue' ? '' : report.status} disabled={report.type === 'revenue'} onChange={(e) => setReport({ ...report, status: e.target.value })}>
          <option value="">Tất cả trạng thái</option>
          {statusOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
        </select>
        <p style={{ color: '#8a7355', fontSize: '0.8rem', marginTop: '8px' }}>Áp dụng cho báo cáo đơn hàng và sản phẩm. Doanh thu luôn chỉ tính đơn đã giao.</p>
      </div>
      <div style={card}>
        <div style={{ fontWeight: 700, marginBottom: '8px' }}>Khoảng thời gian</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
          <label>Từ ngày<input type="date" style={field} value={report.from} onChange={(e) => setReport({ ...report, from: e.target.value })} /></label>
          <label>Đến ngày<input type="date" style={field} value={report.to} onChange={(e) => setReport({ ...report, to: e.target.value })} /></label>
        </div>
      </div>
      <div style={card}>
        <div style={{ fontWeight: 700, marginBottom: '8px' }}>Định dạng xuất</div>
        <div style={{ display: 'flex', gap: '8px' }}>
          {([['pdf', 'PDF'], ['csv', 'EXCEL']] as const).map(([value, label]) => (
            <button key={value} type="button" onClick={() => setReport({ ...report, format: value })} style={{
              ...field, width: 'auto', fontWeight: 800, cursor: 'pointer',
              borderColor: report.format === value ? '#c6a15a' : '#e4d3b4',
              background: report.format === value ? '#fff8ea' : '#fff',
            }}>{label}</button>
          ))}
        </div>
      </div>
      <button type="button" style={{ ...goldButton, justifyContent: 'center', padding: '14px' }} onClick={exportReport}>Xuất báo cáo</button>

      <div style={card}>
        <div style={{ fontWeight: 800, marginBottom: '6px' }}>Nội dung từ {rangeText}</div>
        {previewError && <p style={{ color: '#d64545' }}>{previewError}</p>}
        {!preview && !previewError && <p style={{ color: '#8a7355' }}>Đang lấy dữ liệu...</p>}
        {preview && (
          <>
            <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', marginBottom: '12px' }}>
              <span style={{ background: '#fff8ea', borderRadius: '999px', padding: '6px 10px', fontWeight: 700 }}>{preview.orderCount} đơn</span>
              <span style={{ background: '#e5f8ec', color: '#1f8a4c', borderRadius: '999px', padding: '6px 10px', fontWeight: 700 }}>{money(preview.totalAmount)}</span>
            </div>
            {report.type === 'products' ? (
              preview.products.length === 0 ? <p>Không có sản phẩm nào trong khoảng ngày này.</p> : (
                <div style={{ display: 'grid', gap: '8px' }}>
                  {preview.products.map((item) => (
                    <div key={item.productName} style={{ display: 'flex', justifyContent: 'space-between', gap: '8px', borderBottom: '1px solid #f0e6d4', paddingBottom: '8px' }}>
                      <span>{item.productName}</span>
                      <span style={{ color: '#8a7355' }}>{item.quantity} món</span>
                      <strong style={{ color: '#c6a15a' }}>{money(item.revenue)}</strong>
                    </div>
                  ))}
                </div>
              )
            ) : preview.orders.length === 0 ? <p>Không có đơn nào trong khoảng ngày này.</p> : (
              <div style={{ display: 'grid', gap: '8px' }}>
                {preview.orders.map((order) => (
                  <div key={order.orderCode} style={{ display: 'flex', justifyContent: 'space-between', gap: '10px', borderBottom: '1px solid #f0e6d4', paddingBottom: '8px' }}>
                    <div>
                      <strong>#{order.orderCode}</strong> · {order.customerName}
                      <div style={{ color: '#8a7355', fontSize: '0.8rem' }}>{formatWhen(order.createdAt)} · {order.orderStatus} · {order.paymentMethod} · {order.paymentStatus}</div>
                    </div>
                    <strong style={{ color: '#c6a15a' }}>{money(order.totalAmount)}</strong>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};
