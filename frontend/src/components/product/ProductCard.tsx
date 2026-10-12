import React from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBag, Heart, Star, Gem } from 'lucide-react';
import { ProductSummary } from '../../types/product.types';
import { useCart } from '../../context/CartContext';
import { useToast } from '../../context/ToastContext';

interface ProductCardProps {
  product: ProductSummary;
  className?: string;
  style?: React.CSSProperties;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, className, style }) => {
  const { addToCart } = useCart();
  const { success } = useToast();

  const handleQuickAdd = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();

    // Default to the first available variant or generate representative variant
    const variantId = product.defaultVariantId || product.id;
    const price = product.minPrice || 15000000;
    const originalPrice = product.originalPrice || price * 1.15;

    await addToCart({
      variantId,
      productId: product.id,
      productName: product.name,
      productSlug: product.slug,
      sku: product.baseSku || `SKU-${product.id}`,
      sizeOption: 'Ni tiêu chuẩn',
      materialType: product.materialOverview || 'Vàng Ý 18K',
      unitPrice: price,
      originalPrice,
      thumbnailUrl: product.thumbnailUrl,
      availableStock: 10,
    });
  };

  const handleWishlistClick = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    success(`Đã thêm "${product.name}" vào danh sách tuyệt tác yêu thích.`, 'Wishlist');
  };

  const formatPrice = (price: number) => {
    return price.toLocaleString('vi-VN') + '₫';
  };

  const hasDiscount = product.originalPrice && product.originalPrice > product.minPrice;
  const discountPercent = hasDiscount
    ? Math.round(((product.originalPrice! - product.minPrice) / product.originalPrice!) * 100)
    : 0;

  return (
    <div
      className={`luxury-glass-card ${className || ''}`}
      style={{
        display: 'flex',
        flexDirection: 'column',
        position: 'relative',
        borderRadius: '16px',
        padding: '16px',
        height: '100%',
        ...style,
      }}
    >
      {/* Product Image Area */}
      <Link
        to={`/products/${product.slug}`}
        style={{
          position: 'relative',
          height: '240px',
          borderRadius: '12px',
          backgroundColor: '#0a0c12',
          overflow: 'hidden',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: '16px',
          border: '1px solid rgba(255, 255, 255, 0.05)',
        }}
      >
        {product.thumbnailUrl ? (
          <img
            src={product.thumbnailUrl}
            alt={product.name}
            style={{
              width: '100%',
              height: '100%',
              objectFit: 'cover',
              transition: 'transform 0.6s cubic-bezier(0.16, 1, 0.3, 1)',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.08)')}
            onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
          />
        ) : (
          <div
            style={{
              width: '100%',
              height: '100%',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              background: 'radial-gradient(circle at center, #1b1e2b 0%, #08090d 100%)',
            }}
          >
            <Gem size={56} color="#ff9bb2" style={{ filter: 'drop-shadow(0 0 20px rgba(255,101,132,0.4))' }} />
            <span style={{ fontSize: '0.72rem', color: 'var(--color-text-muted)', marginTop: '8px', letterSpacing: '0.1em' }}>
              AURA HIGH JEWELRY
            </span>
          </div>
        )}

        {/* Badges */}
        <div style={{ position: 'absolute', top: '12px', left: '12px', display: 'flex', flexDirection: 'column', gap: '6px' }}>
          {hasDiscount && (
            <span
              style={{
                backgroundColor: 'rgba(255, 101, 132, 0.9)',
                color: '#FFFFFF',
                fontSize: '0.7rem',
                fontWeight: 700,
                padding: '3px 8px',
                borderRadius: '6px',
                boxShadow: '0 0 10px rgba(255, 101, 132, 0.5)',
              }}
            >
              -{discountPercent}%
            </span>
          )}
          {product.categoryName && (
            <span
              style={{
                backgroundColor: 'rgba(5, 5, 8, 0.75)',
                backdropFilter: 'blur(8px)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                color: 'var(--color-sakura-300)',
                fontSize: '0.68rem',
                fontWeight: 600,
                padding: '3px 8px',
                borderRadius: '6px',
                textTransform: 'uppercase',
                letterSpacing: '0.05em',
              }}
            >
              {product.categoryName}
            </span>
          )}
        </div>

        {/* Wishlist Button */}
        <button
          onClick={handleWishlistClick}
          title="Thêm vào danh sách yêu thích"
          style={{
            position: 'absolute',
            top: '12px',
            right: '12px',
            width: '32px',
            height: '32px',
            borderRadius: '50%',
            backgroundColor: 'rgba(5, 5, 8, 0.65)',
            backdropFilter: 'blur(6px)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'var(--color-text-secondary)',
            cursor: 'pointer',
            transition: 'all 0.2s ease',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.color = '#ff6584';
            e.currentTarget.style.borderColor = '#ff6584';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.color = 'var(--color-text-secondary)';
            e.currentTarget.style.borderColor = 'rgba(255, 255, 255, 0.1)';
          }}
        >
          <Heart size={15} />
        </button>
      </Link>

      {/* Product Information */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginBottom: '6px' }}>
          {[1, 2, 3, 4, 5].map((s) => (
            <Star key={s} size={12} fill="#ff9bb2" color="#ff9bb2" />
          ))}
          <span style={{ fontSize: '0.72rem', color: 'var(--color-text-muted)', marginLeft: '4px' }}>(5.0)</span>
        </div>

        <Link to={`/products/${product.slug}`}>
          <h3
            style={{
              fontSize: '1.02rem',
              fontWeight: 600,
              color: '#FFFFFF',
              lineHeight: 1.4,
              marginBottom: '6px',
              transition: 'color 0.2s ease',
            }}
            onMouseEnter={(e) => (e.currentTarget.style.color = 'var(--color-sakura-300)')}
            onMouseLeave={(e) => (e.currentTarget.style.color = '#FFFFFF')}
          >
            {product.name}
          </h3>
        </Link>

        {product.materialOverview && (
          <p
            style={{
              fontSize: '0.78rem',
              color: 'var(--color-text-secondary)',
              marginBottom: '14px',
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
          >
            {product.materialOverview}
          </p>
        )}

        {/* Pricing & CTA Button */}
        <div
          style={{
            marginTop: 'auto',
            paddingTop: '12px',
            borderTop: '1px solid rgba(255, 255, 255, 0.06)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <div>
            <div style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--color-gold-400)' }}>
              {formatPrice(product.minPrice)}
            </div>
            {hasDiscount && (
              <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', textDecoration: 'line-through' }}>
                {formatPrice(product.originalPrice!)}
              </div>
            )}
          </div>

          <button
            onClick={handleQuickAdd}
            title="Thêm vào giỏ hàng"
            style={{
              width: '38px',
              height: '38px',
              borderRadius: '50%',
              backgroundColor: 'rgba(255, 101, 132, 0.15)',
              border: '1px solid rgba(255, 101, 132, 0.35)',
              color: 'var(--color-sakura-300)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer',
              transition: 'all 0.3s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#ff6584';
              e.currentTarget.style.color = '#FFFFFF';
              e.currentTarget.style.boxShadow = '0 0 15px rgba(255, 101, 132, 0.5)';
              e.currentTarget.style.transform = 'scale(1.06)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = 'rgba(255, 101, 132, 0.15)';
              e.currentTarget.style.color = 'var(--color-sakura-300)';
              e.currentTarget.style.boxShadow = 'none';
              e.currentTarget.style.transform = 'scale(1)';
            }}
          >
            <ShoppingBag size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};
