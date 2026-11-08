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

const inputStyle: React.CSSProperties = {
  width: '100%',
  padding: '10px',
  borderRadius: '8px',
  border: '1px solid rgba(255,255,255,0.12)',
  background: 'transparent',
  color: '#fff',
};

const statusOptions: { value: OrderStatus; label: string }[] = [
  { value: 'PENDING', label: 'Chờ xác nhận' },
  { value: 'CONFIRMED', label: 'Đã xác nhận' },
  { value: 'SHIPPING', label: 'Đang giao' },
  { value: 'DELIVERED', label: 'Đã giao' },
  { value: 'CANCELLED', label: 'Đã hủy' },
];

export const AdminOperations: React.FC<{ tab: 'products' | 'categories' | 'orders' | 'reviews' | 'reports' }> = ({ tab }) => {
  const { success, error } = useToast();
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [orders, setOrders] = useState<OrderSummary[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [form, setForm] = useState({
    name: '',
    baseSku: '',
    categoryId: '',
    materialOverview: '',
    weightGrams: '',
    stoneType: '',
    clarity: '',
    warranty: '',
    sizeOption: '',
    originalPrice: '',
    salePrice: '',
    stock: '1',
    imageUrl: '',
  });
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '', promotionPercent: '0' });
  const [report, setReport] = useState({ format: 'csv', status: '', from: '', to: '' });

  const load = () => {
    productService.getCategories().then(setCategories).catch(() => setCategories([]));
    if (tab === 'products') {
      adminProductService.getAllProducts(undefined, undefined, 0, 50).then((res) => setProducts(res.content || [])).catch(() => setProducts([]));
    }
    if (tab === 'orders') {
      orderService.getAllOrders(undefined, undefined, 0, 50).then((res) => setOrders(res.content || [])).catch(() => setOrders([]));
    }
    if (tab === 'reviews') {
      reviewService.getAllReviewsForAdmin(undefined, 0, 50).then((res) => setReviews(res.content || [])).catch(() => setReviews([]));
    }
  };

  useEffect(() => { load(); }, [tab]);

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
      load();
    } catch {
      error('Không thêm được sản phẩm. Kiểm tra danh mục, SKU và giá.');
    }
  };

  if (tab === 'products') {
    return (
      <div style={{ display: 'grid', gap: '20px' }}>
        <form onSubmit={createProduct} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '10px' }}>
          <input required placeholder="Tên sản phẩm" style={inputStyle} value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <input required placeholder="SKU gốc" style={inputStyle} value={form.baseSku} onChange={(e) => setForm({ ...form, baseSku: e.target.value })} />
          <select required style={inputStyle} value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}>
            <option value="">Danh mục</option>
            {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
          </select>
          <input placeholder="Chất liệu" style={inputStyle} value={form.materialOverview} onChange={(e) => setForm({ ...form, materialOverview: e.target.value })} />
          <input placeholder="Trọng lượng (g)" style={inputStyle} value={form.weightGrams} onChange={(e) => setForm({ ...form, weightGrams: e.target.value })} />
          <input placeholder="Loại đá" style={inputStyle} value={form.stoneType} onChange={(e) => setForm({ ...form, stoneType: e.target.value })} />
          <input placeholder="Độ tinh khiết" style={inputStyle} value={form.clarity} onChange={(e) => setForm({ ...form, clarity: e.target.value })} />
          <input placeholder="Bảo hành" style={inputStyle} value={form.warranty} onChange={(e) => setForm({ ...form, warranty: e.target.value })} />
          <input placeholder="Kích thước" style={inputStyle} value={form.sizeOption} onChange={(e) => setForm({ ...form, sizeOption: e.target.value })} />
          <input required placeholder="Giá gốc" style={inputStyle} value={form.originalPrice} onChange={(e) => setForm({ ...form, originalPrice: e.target.value })} />
          <input placeholder="Giá khuyến mãi" style={inputStyle} value={form.salePrice} onChange={(e) => setForm({ ...form, salePrice: e.target.value })} />
          <input placeholder="Tồn kho" style={inputStyle} value={form.stock} onChange={(e) => setForm({ ...form, stock: e.target.value })} />
          <input placeholder="Đường dẫn ảnh có sẵn" style={inputStyle} value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />
          <input type="file" accept="image/*" onChange={async (e) => {
            const file = e.target.files?.[0];
            if (!file) return;
            try {
              const url = await uploadService.uploadSingleImage(file, 'products');
              setForm((prev) => ({ ...prev, imageUrl: url }));
              success('Đã tải ảnh từ điện thoại.');
            } catch {
              error('Không tải được ảnh.');
            }
          }} />
          <button className="btn-pill-sakura" style={{ padding: '10px' }}>Thêm sản phẩm</button>
        </form>
        {products.map((product) => (
          <div key={product.id} style={{ display: 'flex', justifyContent: 'space-between', gap: '12px', color: '#fff', borderBottom: '1px solid rgba(255,255,255,0.08)', padding: '10px 0' }}>
            <div>{product.name}<div style={{ fontSize: '0.78rem', color: 'var(--color-text-muted)' }}>{product.status}</div></div>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button className="btn-outline-pill" onClick={async () => {
                const name = window.prompt('Tên mới', product.name);
                if (!name) return;
                try {
                  await adminProductService.updateProduct(product.id, { name, categoryId: categories.find((c) => c.slug === product.categorySlug)?.id });
                  success('Đã sửa sản phẩm.');
                  load();
                } catch { error('Không sửa được sản phẩm.'); }
              }}>Sửa</button>
              <button className="btn-outline-pill" onClick={async () => {
                try { await adminProductService.stopSelling(product.id); success('Đã ngừng bán.'); load(); } catch { error('Không ngừng bán được.'); }
              }}>Ngừng bán</button>
              <button className="btn-outline-pill" onClick={async () => {
                if (!window.confirm('Xóa hẳn sản phẩm này?')) return;
                try { await adminProductService.purgeProduct(product.id); success('Đã xóa.'); load(); } catch { error('Không xóa được. Nếu đã có đơn, hãy ngừng bán.'); }
              }}>Xóa</button>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (tab === 'categories') {
    return (
      <div style={{ display: 'grid', gap: '16px', color: '#fff' }}>
        <form onSubmit={async (event) => {
          event.preventDefault();
          try {
            await productService.createCategory({ name: categoryForm.name, description: categoryForm.description, promotionPercent: Number(categoryForm.promotionPercent || 0) });
            success('Đã thêm danh mục.');
            load();
          } catch { error('Không thêm được danh mục.'); }
        }} style={{ display: 'grid', gridTemplateColumns: '2fr 2fr 1fr auto', gap: '8px' }}>
          <input required placeholder="Tên danh mục" style={inputStyle} value={categoryForm.name} onChange={(e) => setCategoryForm({ ...categoryForm, name: e.target.value })} />
          <input placeholder="Mô tả" style={inputStyle} value={categoryForm.description} onChange={(e) => setCategoryForm({ ...categoryForm, description: e.target.value })} />
          <input placeholder="% khuyến mãi" style={inputStyle} value={categoryForm.promotionPercent} onChange={(e) => setCategoryForm({ ...categoryForm, promotionPercent: e.target.value })} />
          <button className="btn-pill-sakura">Thêm</button>
        </form>
        <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.85rem' }}>Khuyến mãi danh mục không cộng với mã giảm giá. Giá bán thấp hơn giá gốc cũng là khuyến mãi theo sản phẩm.</p>
        {categories.map((category) => (
          <div key={category.id} style={{ display: 'flex', justifyContent: 'space-between', gap: '8px' }}>
            <span>{category.name} {category.promotionPercent ? `• giảm ${category.promotionPercent}%` : ''}</span>
            <button className="btn-outline-pill" onClick={async () => {
              const percent = window.prompt('Phần trăm khuyến mãi danh mục', String(category.promotionPercent || 0));
              if (percent === null) return;
              try {
                await productService.updateCategory(category.id, { name: category.name, description: category.description, promotionPercent: Number(percent), status: category.status });
                success('Đã cập nhật khuyến mãi danh mục.');
                load();
              } catch { error('Không cập nhật được danh mục.'); }
            }}>Sửa khuyến mãi</button>
          </div>
        ))}
      </div>
    );
  }

  if (tab === 'orders') {
    return (
      <div style={{ display: 'grid', gap: '12px', color: '#fff' }}>
        {orders.map((order) => (
          <div key={order.id} style={{ display: 'flex', justifyContent: 'space-between', gap: '12px', flexWrap: 'wrap', borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '10px' }}>
            <div>
              <strong>{order.orderCode}</strong>
              <div style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)' }}>{order.customerName} • {order.totalAmount.toLocaleString('vi-VN')}₫</div>
            </div>
            <select
              value={order.orderStatus}
              style={inputStyle}
              onChange={async (event) => {
                const newStatus = event.target.value as OrderStatus;
                try {
                  await orderService.updateOrderStatus(order.orderCode, newStatus, 'Cập nhật từ trang quản trị');
                  success('Đã chuyển trạng thái đơn.');
                  load();
                } catch { error('Không chuyển được trạng thái.'); }
              }}
            >
              {statusOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
              {!statusOptions.some((option) => option.value === order.orderStatus) && <option value={order.orderStatus}>{order.orderStatus}</option>}
            </select>
          </div>
        ))}
        {orders.length === 0 && <p>Chưa có đơn hàng.</p>}
      </div>
    );
  }

  if (tab === 'reviews') {
    return (
      <div style={{ display: 'grid', gap: '12px', color: '#fff' }}>
        {reviews.map((review) => (
          <div key={review.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '10px' }}>
            <div>{review.productName} • {review.rating} sao • {review.customerName}</div>
            <div style={{ color: 'var(--color-text-secondary)', margin: '6px 0' }}>{review.comment || 'Không có nhận xét'}</div>
            <div style={{ display: 'flex', gap: '8px' }}>
              <button className="btn-outline-pill" onClick={async () => { await reviewService.updateReviewStatus(review.id, { status: 'APPROVED' }); success('Đã hiện nhận xét.'); load(); }}>Hiện</button>
              <button className="btn-outline-pill" onClick={async () => { await reviewService.updateReviewStatus(review.id, { status: 'REJECTED' }); success('Đã ẩn nhận xét.'); load(); }}>Ẩn</button>
            </div>
          </div>
        ))}
        {reviews.length === 0 && <p>Chưa có đánh giá.</p>}
      </div>
    );
  }

  return (
    <div style={{ display: 'grid', gap: '12px', maxWidth: '640px', color: '#fff' }}>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
        <label>Từ ngày<input type="date" style={inputStyle} value={report.from} onChange={(e) => setReport({ ...report, from: e.target.value })} /></label>
        <label>Đến ngày<input type="date" style={inputStyle} value={report.to} onChange={(e) => setReport({ ...report, to: e.target.value })} /></label>
        <label>Trạng thái
          <select style={inputStyle} value={report.status} onChange={(e) => setReport({ ...report, status: e.target.value })}>
            <option value="">Tất cả</option>
            {statusOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}
          </select>
        </label>
        <label>Định dạng
          <select style={inputStyle} value={report.format} onChange={(e) => setReport({ ...report, format: e.target.value })}>
            <option value="csv">Excel (CSV)</option>
            <option value="pdf">PDF</option>
          </select>
        </label>
      </div>
      <button className="btn-pill-sakura" onClick={async () => {
        const params = new URLSearchParams({ format: report.format });
        if (report.status) params.set('status', report.status);
        if (report.from) params.set('from', report.from);
        if (report.to) params.set('to', report.to);
        try {
          const response = await apiClient.get(`/api/v1/admin/tools/orders/export?${params.toString()}`, { responseType: 'blob' });
          const url = URL.createObjectURL(response.data);
          const link = document.createElement('a');
          link.href = url;
          link.download = report.format === 'pdf' ? 'bao-cao-don-hang.pdf' : 'bao-cao-don-hang.csv';
          link.click();
          URL.revokeObjectURL(url);
        } catch { error('Không xuất được báo cáo.'); }
      }}>Xuất báo cáo</button>
      <button className="btn-outline-pill" onClick={async () => {
        if (!window.confirm('Khôi phục dữ liệu mẫu sẽ xóa sản phẩm, đơn hàng và đánh giá hiện tại. Tiếp tục?')) return;
        try {
          await apiClient.post('/api/v1/admin/tools/restore-sample');
          success('Đã khôi phục dữ liệu mẫu.');
        } catch { error('Không khôi phục được dữ liệu mẫu.'); }
      }}>Khôi phục dữ liệu mẫu</button>
    </div>
  );
};
