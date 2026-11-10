import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useToast } from '../context/ToastContext';
import { dashboardService } from '../services/dashboardService';
import { adminInventoryService } from '../services/adminInventoryService';
import { adminCouponService } from '../services/adminCouponService';
import { adminUserService } from '../services/adminUserService';
import { DashboardOverview, LowStockAlert } from '../types/dashboard.types';
import { InventoryItem, AdminCoupon, AdminUserSummary } from '../types/admin.types';
import { AdminOperations } from './AdminOperations';
import {
  ShieldCheck,
  TrendingUp,
  Users,
  AlertTriangle,
  RefreshCw,
  Plus,
  Lock,
  Unlock,
  CheckCircle2,
  DollarSign,
  ShoppingBag,
  ArrowUpRight,
  Gem,
  Ticket,
  Star,
  Sparkles,
  Truck,
  FileText,
} from 'lucide-react';

export const AdminPortalPage: React.FC = () => {
  const { success, error } = useToast();

  const [activeTab, setActiveTab] = useState<'overview' | 'inventory' | 'coupons' | 'users' | 'products' | 'categories' | 'orders' | 'reviews' | 'reports' | 'promotions'>('products');

  // Overview metrics state
  const [overview, setOverview] = useState<DashboardOverview | null>(null);
  const [lowStockAlerts, setLowStockAlerts] = useState<LowStockAlert[]>([]);
  const [loadingOverview, setLoadingOverview] = useState(false);

  // Inventory state
  const [inventories, setInventories] = useState<InventoryItem[]>([]);
  const [loadingInventory, setLoadingInventory] = useState(false);
  const [adjustModalItem, setAdjustModalItem] = useState<InventoryItem | null>(null);
  const [adjustQty, setAdjustQty] = useState<number>(10);
  const [adjustType, setAdjustType] = useState<'ADD' | 'DEDUCT' | 'SET'>('ADD');

  // Coupon state
  const [coupons, setCoupons] = useState<AdminCoupon[]>([]);
  const [loadingCoupons, setLoadingCoupons] = useState(false);
  const [showCreateCouponModal, setShowCreateCouponModal] = useState(false);
  const [newCouponForm, setNewCouponForm] = useState({
    code: '',
    discountType: 'PERCENTAGE' as 'PERCENTAGE' | 'FIXED_AMOUNT',
    discountValue: 10,
    minOrderValue: 5000000,
    maxDiscount: 2000000,
    usageLimit: 100,
    startDate: new Date().toISOString().slice(0, 16),
    endDate: new Date(Date.now() + 30 * 86400000).toISOString().slice(0, 16),
    isActive: true,
  });

  // Users state
  const [users, setUsers] = useState<AdminUserSummary[]>([]);
  const [loadingUsers, setLoadingUsers] = useState(false);

  // Load Overview Data
  useEffect(() => {
    if (activeTab === 'overview') {
      setLoadingOverview(true);
      Promise.all([
        dashboardService.getOverview().catch(() => null),
        dashboardService.getLowStockAlerts().catch(() => []),
      ])
        .then(([ov, alerts]) => {
          if (ov) setOverview(ov);
          else setOverview(null);
                    if (alerts) setLowStockAlerts(alerts);
        })
        .finally(() => setLoadingOverview(false));
    } else if (activeTab === 'products' || activeTab === 'inventory') {
      setLoadingInventory(true);
      adminInventoryService
        .getInventories()
        .then((res) => setInventories(res.content))
        .catch(() => setInventories([]))
                .finally(() => setLoadingInventory(false));
    } else if (activeTab === 'coupons') {
      setLoadingCoupons(true);
      adminCouponService
        .getAllCoupons(undefined, 0, 50)
        .then((res) => setCoupons(res.content))
        .catch(() => setCoupons([]))
                .finally(() => setLoadingCoupons(false));
    } else if (activeTab === 'users') {
      setLoadingUsers(true);
      adminUserService
        .getAllUsers()
        .then((res: any) => setUsers(res.content))
        .catch(() => setUsers([]))
                .finally(() => setLoadingUsers(false));
    }
  }, [activeTab]);

  // Handle Stock Adjustment
  const handleStockAdjustmentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!adjustModalItem) return;

    try {
      await adminInventoryService.adjustStock({
        variantId: adjustModalItem.variantId,
        adjustmentType: adjustType,
        quantity: adjustQty,
        reason: 'Điều chỉnh tồn kho',
      });
      success(`Đã cập nhật tồn kho cho mã ${adjustModalItem.sku}.`, 'Kho Vận Thành Công');
      setAdjustModalItem(null);
      // Reload inventory
      adminInventoryService.getInventories().then((res) => setInventories(res.content));
    } catch {
      error('Không cập nhật được tồn kho.', 'Kho');
    }
  };

  // Handle Toggle User Status
  const handleToggleUserStatus = async (userSummary: AdminUserSummary) => {
    const nextStatus = userSummary.status === 'ACTIVE' ? 'LOCKED' : 'ACTIVE';
    try {
      await adminUserService.updateUserStatus(userSummary.id, { status: nextStatus, reason: 'Điều chỉnh bởi Quản trị viên' });
      setUsers((prev) =>
        prev.map((u) => (u.id === userSummary.id ? { ...u, status: nextStatus } : u))
      );
      success(`Đã cập nhật trạng thái người dùng thành ${nextStatus}.`, 'Thành Công');
    } catch {
      error('Không đổi được trạng thái tài khoản.', 'Khách hàng');
    }
  };

  // Handle Create Coupon
  const handleCreateCouponSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const created = await adminCouponService.createCoupon({
        ...newCouponForm,
        startDate: new Date(newCouponForm.startDate).toISOString(),
        endDate: new Date(newCouponForm.endDate).toISOString(),
      });
      setCoupons((prev) => [created, ...prev]);
      success(`Đã tạo mã ưu đãi ${created.code} thành công!`, 'Khuyến Mãi');
      setShowCreateCouponModal(false);
    } catch {
      error('Không tạo được mã giảm giá.', 'Mã giảm giá');
    }
  };

  return (
    <div style={{ backgroundColor: '#050508', minHeight: '100vh', paddingTop: '100px', paddingBottom: '90px' }}>
      <div className="container">
        {/* Admin Header Bar */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '32px',
            flexWrap: 'wrap',
            gap: '16px',
          }}
        >
          <div>
            <div
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '6px',
                fontSize: '0.78rem',
                letterSpacing: '0.15em',
                color: 'var(--color-gold-400)',
                textTransform: 'uppercase',
                fontWeight: 600,
                marginBottom: '6px',
              }}
            >
              <ShieldCheck size={16} /> AURA EXECUTIVE MANAGEMENT SYSTEM
            </div>
            <h1 style={{ fontSize: '2.3rem', fontWeight: 700, color: '#FFFFFF' }}>
              Bảng điều khiển AURA
            </h1>
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <Link to="/products" className="btn-outline-pill" style={{ padding: '8px 18px', fontSize: '0.82rem' }}>
              Xem Web Khách Hàng
            </Link>
          </div>
        </div>

        {/* Tab Buttons */}
        <div
          style={{
            display: 'flex',
            gap: '10px',
            borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
            paddingBottom: '16px',
            marginBottom: '32px',
            flexWrap: 'wrap',
          }}
        >
          {[
            { id: 'products', label: 'Quản lý sản phẩm', icon: ShoppingBag, color: '#ff8fa3' },
            { id: 'categories', label: 'Quản lý danh mục trang sức', icon: Gem, color: '#e8c56b' },
            { id: 'orders', label: 'Quản lý đơn hàng vận chuyển', icon: Truck, color: '#8ec5ff' },
            { id: 'users', label: 'Quản lý khách hàng đăng ký', icon: Users, color: '#f5c16c' },
            { id: 'overview', label: 'Thống kê doanh thu', icon: TrendingUp, color: '#7dffa1' },
            { id: 'promotions', label: 'Quản lý khuyến mãi và sự kiện', icon: Sparkles, color: '#ffb703' },
            { id: 'coupons', label: 'Quản lý mã giảm giá', icon: Ticket, color: '#c4b5fd' },
            { id: 'reviews', label: 'Quản lý kiểm duyệt sản phẩm', icon: Star, color: '#ff8fa3' },
            { id: 'reports', label: 'Xuất báo cáo PDF, Excel', icon: FileText, color: '#8ec5ff' },
          ].map((tab) => {
            const Icon = tab.icon;
            const isCur = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as typeof activeTab)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '10px 20px',
                  borderRadius: '9999px',
                  border: isCur ? `1px solid ${tab.color}` : '1px solid rgba(255, 255, 255, 0.08)',
                  backgroundColor: isCur ? `${tab.color}22` : 'rgba(255, 255, 255, 0.02)',
                  color: isCur ? tab.color : 'var(--color-text-secondary)',
                  fontSize: '0.86rem',
                  fontWeight: 600,
                  cursor: 'pointer',
                  transition: 'all 0.25s ease',
                }}
              >
                <Icon size={16} /> {tab.label}
              </button>
            );
          })}
        </div>

        {/* TAB 1: EXECUTIVE OVERVIEW */}
        {(activeTab === 'products' || activeTab === 'categories' || activeTab === 'orders' || activeTab === 'reviews' || activeTab === 'reports' || activeTab === 'promotions') && (
          <AdminOperations tab={activeTab} />
        )}

        {activeTab === 'overview' && (
          <div>
            {loadingOverview ? (
              <div style={{ textAlign: 'center', padding: '60px 0' }}>
                <RefreshCw size={32} color="#ff6584" style={{ animation: 'spin 1.5s linear infinite' }} />
              </div>
            ) : (
              <>
                {/* 4 Metric Cards */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '20px', marginBottom: '32px' }}>
                  <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px', padding: '24px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Tổng Doanh Thu</span>
                      <DollarSign size={20} color="#D4AF37" />
                    </div>
                    <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-gold-400)' }}>
                      {(overview?.totalRevenue || 0).toLocaleString('vi-VN')}₫
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.76rem', color: '#10B981', marginTop: '6px' }}>
                      <ArrowUpRight size={14} /> +{overview?.revenueGrowthPercent || 0}% so với tháng trước
                    </div>
                  </div>

                  <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px', padding: '24px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Đơn Hàng Mới</span>
                      <ShoppingBag size={20} color="#ff6584" />
                    </div>
                    <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#FFFFFF' }}>
                      {overview?.totalOrders || 0}
                    </div>
                  </div>

                  <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px', padding: '24px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <span style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)' }}>Khách Hàng Đăng Ký</span>
                      <Users size={20} color="#3B82F6" />
                    </div>
                    <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#FFFFFF' }}>
                      {overview?.totalCustomers || 0}
                    </div>
                    <div style={{ fontSize: '0.76rem', color: 'var(--color-text-muted)', marginTop: '6px' }}>
                      Tài khoản khách hàng
                    </div>
                  </div>

                  <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(239, 68, 68, 0.25)', borderRadius: '16px', padding: '24px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <span style={{ fontSize: '0.82rem', color: '#EF4444' }}>Cảnh Báo Hết Hàng</span>
                      <AlertTriangle size={20} color="#EF4444" />
                    </div>
                    <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#EF4444' }}>
                      {overview?.lowStockCount ?? lowStockAlerts.length}
                    </div>
                    <div style={{ fontSize: '0.76rem', color: 'var(--color-text-muted)', marginTop: '6px' }}>
                      Mã SKU dưới ngưỡng an toàn
                    </div>
                  </div>
                </div>

                {/* Low Stock Alerts Section */}
                <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px', padding: '28px' }}>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '18px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <AlertTriangle size={20} color="#EF4444" /> Danh Sách Biến Thể Cần Nhập Thêm Từ Xưởng
                  </h3>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {lowStockAlerts.length > 0 ? (
                      lowStockAlerts.map((alert) => (
                        <div
                          key={alert.variantId}
                          style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            padding: '14px 18px',
                            backgroundColor: 'rgba(239, 68, 68, 0.08)',
                            border: '1px solid rgba(239, 68, 68, 0.2)',
                            borderRadius: '10px',
                          }}
                        >
                          <div>
                            <div style={{ fontSize: '0.9rem', fontWeight: 600, color: '#FFFFFF' }}>
                              {alert.productName} ({[alert.sizeOption, alert.materialType].filter(Boolean).join(' - ') || alert.sku})
                            </div>
                            <div style={{ fontSize: '0.76rem', color: 'var(--color-text-muted)' }}>SKU: {alert.sku}</div>
                          </div>

                          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                            <span style={{ fontSize: '0.85rem', color: '#EF4444', fontWeight: 700 }}>
                              Chỉ còn {alert.availableQuantity ?? alert.quantity} món (Định mức: {alert.lowStockThreshold})
                            </span>
                            <button
                              onClick={() => {
                                setActiveTab('products');
                              }}
                              className="btn-pill-sakura"
                              style={{ padding: '6px 14px', fontSize: '0.76rem' }}
                            >
                              Nhập Kho Ngay
                            </button>
                          </div>
                        </div>
                      ))
                    ) : (
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#10B981', fontSize: '0.88rem' }}>
                        <CheckCircle2 size={18} /> Tất cả các biến thể trang sức đều đang duy trì mức tồn kho an toàn.
                      </div>
                    )}
                  </div>
                </div>
              </>
            )}
          </div>
        )}

        {/* TAB 2: INVENTORY MANAGEMENT */}
        {activeTab === 'products' && (
          <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px', padding: '28px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF' }}>
                  Tồn kho trong quản lý sản phẩm
                </h3>
                <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.82rem', marginTop: '4px' }}>
                  Bảo vệ toàn vẹn dữ liệu đa luồng qua cơ chế Optimistic Locking (@Version)
                </p>
              </div>
            </div>

            {loadingInventory ? (
              <div style={{ textAlign: 'center', padding: '60px 0' }}>
                <RefreshCw size={28} color="#ff6584" style={{ animation: 'spin 1.5s linear infinite' }} />
              </div>
            ) : (
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.86rem' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.1)', color: 'var(--color-sakura-300)' }}>
                    <th style={{ padding: '12px' }}>Tên sản phẩm</th>
                    <th style={{ padding: '12px' }}>Mã SKU</th>
                    <th style={{ padding: '12px' }}>Tồn Thực Tế</th>
                    <th style={{ padding: '12px' }}>Khả Dụng</th>
                    <th style={{ padding: '12px' }}>Ngưỡng Báo Động</th>
                    <th style={{ padding: '12px' }}>Trạng Thái</th>
                    <th style={{ padding: '12px', textAlign: 'right' }}>Thao Tác</th>
                  </tr>
                </thead>
                <tbody style={{ color: 'var(--color-text-secondary)' }}>
                  {inventories.map((item) => (
                    <tr key={item.inventoryId} style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.05)' }}>
                      <td style={{ padding: '14px 12px', color: '#FFFFFF', fontWeight: 600 }}>
                        {item.productName}
                        <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>
                          {item.sizeOption} • {item.materialType}
                        </div>
                      </td>
                      <td style={{ padding: '14px 12px' }}>{item.sku}</td>
                      <td style={{ padding: '14px 12px', color: '#FFFFFF', fontWeight: 600 }}>{item.quantity}</td>
                      <td style={{ padding: '14px 12px', color: '#10B981', fontWeight: 600 }}>{item.availableQuantity}</td>
                      <td style={{ padding: '14px 12px' }}>{item.lowStockThreshold}</td>
                      <td style={{ padding: '14px 12px' }}>
                        <span
                          style={{
                            fontSize: '0.72rem',
                            fontWeight: 700,
                            padding: '3px 8px',
                            borderRadius: '4px',
                            backgroundColor: item.stockStatus === 'IN_STOCK' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
                            color: item.stockStatus === 'IN_STOCK' ? '#10B981' : '#EF4444',
                          }}
                        >
                          {item.stockStatus}
                        </span>
                      </td>
                      <td style={{ padding: '14px 12px', textAlign: 'right' }}>
                        <button
                          onClick={() => setAdjustModalItem(item)}
                          className="btn-pill-sakura"
                          style={{ padding: '6px 14px', fontSize: '0.76rem' }}
                        >
                          Điều Chỉnh Kho
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* TAB 3: COUPONS CMS */}
        {activeTab === 'coupons' && (
          <div style={{ backgroundColor: '#fbf6ee', border: '1px solid #eadfca', borderRadius: '22px', padding: '22px', color: '#3d2b16' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#3d2b16' }}>
                  Quản lý mã giảm giá
                </h3>
                <p style={{ color: '#8a7355', fontSize: '0.82rem', marginTop: '4px' }}>
                  Bật, tắt hoặc xóa mã. Mã không cộng thêm khi sản phẩm đang có khuyến mãi.
                </p>
              </div>
              <button
                onClick={() => setShowCreateCouponModal(true)}
                className="btn-pill-sakura"
                style={{ padding: '8px 18px', fontSize: '0.82rem' }}
              >
                <Plus size={15} /> Tạo Mã Mới
              </button>
            </div>

            {loadingCoupons ? (
              <div style={{ textAlign: 'center', padding: '60px 0' }}>
                <RefreshCw size={28} color="#c6a15a" style={{ animation: 'spin 1.5s linear infinite' }} />
              </div>
            ) : (
              <div style={{ display: 'grid', gap: '12px' }}>
                {coupons.map((c) => {
                  const discount = c.discountType === 'PERCENTAGE'
                    ? `Giảm ${c.discountValue}%${c.maxDiscount ? ` (tối đa ${c.maxDiscount.toLocaleString('vi-VN')} đ)` : ''}`
                    : `Giảm ${c.discountValue.toLocaleString('vi-VN')} đ`;
                  return (
                    <div key={c.id} style={{ background: '#fff', color: '#3d2b16', border: '1px solid #eadfca', borderRadius: '16px', padding: '16px' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', gap: '8px', alignItems: 'center' }}>
                        <strong style={{ letterSpacing: '0.04em' }}>{c.code}</strong>
                        <span style={{ color: c.isActive ? '#1f8a4c' : '#d64545', background: c.isActive ? '#e5f8ec' : '#ffe4e4', borderRadius: '999px', padding: '4px 10px', fontSize: '0.75rem', fontWeight: 700 }}>
                          {c.isActive ? 'Đang hoạt động' : 'Đang tắt'}
                        </span>
                      </div>
                      <div style={{ marginTop: '6px' }}>{c.title || 'Mã giảm giá'}</div>
                      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginTop: '10px', fontSize: '0.82rem' }}>
                        <span style={{ background: '#fff6e4', color: '#8a6840', borderRadius: '8px', padding: '4px 8px' }}>{discount}</span>
                        <span style={{ background: '#fff6e4', color: '#8a6840', borderRadius: '8px', padding: '4px 8px' }}>Đơn tối thiểu: {(c.minOrderValue || 0).toLocaleString('vi-VN')} đ</span>
                      </div>
                      <div style={{ color: '#8a7355', fontSize: '0.8rem', marginTop: '8px' }}>Đã dùng: {c.usedCount}/{c.usageLimit} lượt (còn {Math.max(c.usageLimit - c.usedCount, 0)})</div>
                      <div style={{ color: '#8a7355', fontSize: '0.8rem' }}>Hiệu lực: {new Date(c.startDate).toLocaleString('vi-VN')} đến {new Date(c.endDate).toLocaleString('vi-VN')}</div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px' }}>
                        <label style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
                          Kích hoạt
                          <input type="checkbox" checked={c.isActive} onChange={async () => {
                            try {
                              const updated = await adminCouponService.updateCoupon(c.id, { isActive: !c.isActive });
                              setCoupons((prev) => prev.map((item) => item.id === c.id ? updated : item));
                              success(updated.isActive ? 'Đã bật mã giảm giá.' : 'Đã tắt mã giảm giá.');
                            } catch { error('Không đổi được mã giảm giá.'); }
                          }} />
                        </label>
                        <button type="button" onClick={async () => {
                          if (!window.confirm(`Xóa mã ${c.code}?`)) return;
                          try {
                            await adminCouponService.deleteCoupon(c.id);
                            setCoupons((prev) => prev.filter((item) => item.id !== c.id));
                            success('Đã xóa mã giảm giá.');
                          } catch { error('Không xóa được mã giảm giá.'); }
                        }} style={{ border: 'none', background: 'transparent', color: '#d64545', cursor: 'pointer' }}>Xóa</button>
                      </div>
                    </div>
                  );
                })}
                {coupons.length === 0 && <p style={{ color: '#8a7355' }}>Chưa có mã giảm giá.</p>}
              </div>
            )}
          </div>
        )}

        {/* TAB 4: USERS CMS */}
        {activeTab === 'users' && (
          <div style={{ backgroundColor: 'rgba(14, 16, 23, 0.7)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px', padding: '28px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF' }}>
                  Quản lý khách hàng đăng ký
                </h3>
                <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.82rem', marginTop: '4px' }}>
                  Khóa tài khoản gian lận thanh toán hoặc nâng cấp vai trò ROLE_STAFF cho nhân viên
                </p>
              </div>
            </div>

            {loadingUsers ? (
              <div style={{ textAlign: 'center', padding: '60px 0' }}>
                <RefreshCw size={28} color="#ff6584" style={{ animation: 'spin 1.5s linear infinite' }} />
              </div>
            ) : (
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.86rem' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.1)', color: 'var(--color-sakura-300)' }}>
                    <th style={{ padding: '12px' }}>Họ Tên & Username</th>
                    <th style={{ padding: '12px' }}>Email</th>
                    <th style={{ padding: '12px' }}>Số Điện Thoại</th>
                    <th style={{ padding: '12px' }}>Vai Trò</th>
                    <th style={{ padding: '12px' }}>Trạng Thái</th>
                    <th style={{ padding: '12px', textAlign: 'right' }}>Thao Tác</th>
                  </tr>
                </thead>
                <tbody style={{ color: 'var(--color-text-secondary)' }}>
                  {users.map((u) => (
                    <tr key={u.id} style={{ borderBottom: '1px solid rgba(255, 255, 255, 0.05)' }}>
                      <td style={{ padding: '14px 12px', color: '#FFFFFF', fontWeight: 600 }}>
                        {u.fullName || u.username}
                        <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)' }}>@{u.username}</div>
                      </td>
                      <td style={{ padding: '14px 12px' }}>{u.email}</td>
                      <td style={{ padding: '14px 12px' }}>{u.phone || 'Chưa cập nhật'}</td>
                      <td style={{ padding: '14px 12px' }}>
                        <span
                          style={{
                            fontSize: '0.72rem',
                            fontWeight: 700,
                            padding: '3px 8px',
                            borderRadius: '4px',
                            backgroundColor: u.roles.includes('ROLE_ADMIN') ? 'rgba(212, 175, 55, 0.2)' : 'rgba(255, 101, 132, 0.15)',
                            color: u.roles.includes('ROLE_ADMIN') ? 'var(--color-gold-400)' : 'var(--color-sakura-300)',
                          }}
                        >
                          {u.roles.join(', ')}
                        </span>
                      </td>
                      <td style={{ padding: '14px 12px' }}>
                        <span
                          style={{
                            fontSize: '0.72rem',
                            fontWeight: 700,
                            padding: '3px 8px',
                            borderRadius: '4px',
                            backgroundColor: u.status === 'ACTIVE' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
                            color: u.status === 'ACTIVE' ? '#10B981' : '#EF4444',
                          }}
                        >
                          {u.status}
                        </span>
                      </td>
                      <td style={{ padding: '14px 12px', textAlign: 'right' }}>
                        {!u.roles.includes('ROLE_ADMIN') && (
                          <button
                            onClick={() => handleToggleUserStatus(u)}
                            style={{
                              padding: '6px 14px',
                              borderRadius: '9999px',
                              border: u.status === 'ACTIVE' ? '1px solid rgba(239, 68, 68, 0.3)' : '1px solid rgba(16, 185, 129, 0.3)',
                              backgroundColor: u.status === 'ACTIVE' ? 'rgba(239, 68, 68, 0.1)' : 'rgba(16, 185, 129, 0.1)',
                              color: u.status === 'ACTIVE' ? '#EF4444' : '#10B981',
                              fontSize: '0.76rem',
                              cursor: 'pointer',
                              display: 'inline-flex',
                              alignItems: 'center',
                              gap: '4px',
                            }}
                          >
                            {u.status === 'ACTIVE' ? <Lock size={12} /> : <Unlock size={12} />}
                            {u.status === 'ACTIVE' ? 'Khóa Tài Khoản' : 'Mở Khóa'}
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>

      {/* STOCK ADJUSTMENT MODAL */}
      {adjustModalItem && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            zIndex: 9998,
            backgroundColor: 'rgba(5, 5, 8, 0.85)',
            backdropFilter: 'blur(16px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
          }}
          onClick={() => setAdjustModalItem(null)}
        >
          <div
            style={{
              width: '100%',
              maxWidth: '460px',
              backgroundColor: '#0e1017',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              borderRadius: '16px',
              padding: '32px',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '8px' }}>
              Điều Chỉnh Tồn Kho Thực Tế
            </h3>
            <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.82rem', marginBottom: '20px' }}>
              {adjustModalItem.productName} ({adjustModalItem.sku})
            </p>

            <form onSubmit={handleStockAdjustmentSubmit}>
              <div style={{ marginBottom: '16px' }}>
                <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                  Loại thao tác nghiệp vụ:
                </label>
                <div style={{ display: 'flex', gap: '8px' }}>
                  {[
                    { type: 'ADD', label: 'Nhập Xưởng (+)' },
                    { type: 'DEDUCT', label: 'Xuất Hủy (-)' },
                    { type: 'SET', label: 'Cân Đối Kiểm Kê (=)' },
                  ].map((btn) => (
                    <button
                      key={btn.type}
                      type="button"
                      onClick={() => setAdjustType(btn.type as 'ADD' | 'DEDUCT' | 'SET')}
                      style={{
                        flex: 1,
                        padding: '8px 4px',
                        borderRadius: '8px',
                        border: adjustType === btn.type ? '1px solid #ff6584' : '1px solid rgba(255,255,255,0.08)',
                        backgroundColor: adjustType === btn.type ? 'rgba(255,101,132,0.15)' : 'rgba(255,255,255,0.02)',
                        color: adjustType === btn.type ? '#ff6584' : 'var(--color-text-secondary)',
                        fontSize: '0.78rem',
                        fontWeight: 600,
                        cursor: 'pointer',
                      }}
                    >
                      {btn.label}
                    </button>
                  ))}
                </div>
              </div>

              <div style={{ marginBottom: '24px' }}>
                <label style={{ display: 'block', fontSize: '0.8rem', color: 'var(--color-text-secondary)', marginBottom: '8px' }}>
                  Số lượng điều chỉnh:
                </label>
                <input
                  type="number"
                  min={1}
                  required
                  value={adjustQty}
                  onChange={(e) => setAdjustQty(parseInt(e.target.value, 10) || 1)}
                  style={{
                    width: '100%',
                    padding: '12px 14px',
                    borderRadius: '8px',
                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    color: '#FFFFFF',
                    fontSize: '1.1rem',
                    fontWeight: 700,
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => setAdjustModalItem(null)}
                  style={{ padding: '10px 18px', borderRadius: '9999px', backgroundColor: 'transparent', border: '1px solid rgba(255,255,255,0.1)', color: 'var(--color-text-secondary)', cursor: 'pointer' }}
                >
                  Đóng
                </button>
                <button type="submit" className="btn-pill-sakura" style={{ padding: '10px 22px' }}>
                  Xác Nhận Lưu Kho
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CREATE COUPON MODAL */}
      {showCreateCouponModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            zIndex: 9998,
            backgroundColor: 'rgba(5, 5, 8, 0.85)',
            backdropFilter: 'blur(16px)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
          }}
          onClick={() => setShowCreateCouponModal(false)}
        >
          <div
            style={{
              width: '100%',
              maxWidth: '480px',
              backgroundColor: '#0e1017',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              borderRadius: '16px',
              padding: '32px',
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginBottom: '18px' }}>
              Tạo mã giảm giá
            </h3>

            <form onSubmit={handleCreateCouponSubmit}>
              <div style={{ marginBottom: '14px' }}>
                <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Mã Khuyến Mãi (Code)</label>
                <input
                  type="text"
                  required
                  placeholder="VD: VIPDIAMOND20"
                  value={newCouponForm.code}
                  onChange={(e) => setNewCouponForm({ ...newCouponForm, code: e.target.value.toUpperCase() })}
                  style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none', textTransform: 'uppercase' }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '14px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Hình Thức Giảm</label>
                  <select
                    value={newCouponForm.discountType}
                    onChange={(e) => setNewCouponForm({ ...newCouponForm, discountType: e.target.value as 'PERCENTAGE' | 'FIXED_AMOUNT' })}
                    style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                  >
                    <option value="PERCENTAGE" style={{ backgroundColor: '#0e1017' }}>Phần Trăm (%)</option>
                    <option value="FIXED_AMOUNT" style={{ backgroundColor: '#0e1017' }}>Số Tiền Cố Định (₫)</option>
                  </select>
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Giá Trị Giảm</label>
                  <input
                    type="number"
                    required
                    value={newCouponForm.discountValue}
                    onChange={(e) => setNewCouponForm({ ...newCouponForm, discountValue: parseFloat(e.target.value) || 0 })}
                    style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '24px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Đơn Tối Thiểu (₫)</label>
                  <input
                    type="number"
                    value={newCouponForm.minOrderValue}
                    onChange={(e) => setNewCouponForm({ ...newCouponForm, minOrderValue: parseFloat(e.target.value) || 0 })}
                    style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '0.78rem', color: 'var(--color-text-secondary)', marginBottom: '6px' }}>Giới Hạn Lượt Dùng</label>
                  <input
                    type="number"
                    value={newCouponForm.usageLimit}
                    onChange={(e) => setNewCouponForm({ ...newCouponForm, usageLimit: parseInt(e.target.value, 10) || 100 })}
                    style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', backgroundColor: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.1)', color: '#FFFFFF', outline: 'none' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                <button
                  type="button"
                  onClick={() => setShowCreateCouponModal(false)}
                  style={{ padding: '10px 18px', borderRadius: '9999px', backgroundColor: 'transparent', border: '1px solid rgba(255,255,255,0.1)', color: 'var(--color-text-secondary)', cursor: 'pointer' }}
                >
                  Đóng
                </button>
                <button type="submit" className="btn-pill-sakura" style={{ padding: '10px 22px' }}>
                  Tạo Mã Ngay
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
