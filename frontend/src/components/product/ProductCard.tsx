import React from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBag, Heart, Gem, Scale } from 'lucide-react';
import { ProductSummary } from '../../types/product.types';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { useCompare } from '../../context/CompareContext';
import { useToast } from '../../context/ToastContext';
import { wishlistService } from '../../services/wishlistService';

interface ProductCardProps {
  product: ProductSummary;
  className?: string;
  style?: React.CSSProperties;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, className, style }) => {
  const { addToCart } = useCart();
  const { isAuthenticated, openAuthModal } = useAuth();
  const { slugs: compareSlugs, add: addCompare, remove: removeCompare } = useCompare();
  const { success, error, info } = useToast();
  const [imageFailed, setImageFailed] = React.useState(false);
  const [liked, setLiked] = React.useState(false);
  const compared = compareSlugs.includes(product.slug);

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

  const handleWishlistClick = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isAuthenticated) {
      openAuthModal('login');
      return;
    }
    try {
      if (liked) {
        await wishlistService.removeFromWishlist(product.id);
        setLiked(false);
        info(`Đã bỏ "${product.name}" khỏi yêu thích.`);
      } else {
        await wishlistService.addToWishlist(product.id);
        setLiked(true);
        success(`Đã thêm "${product.name}" vào danh sách yêu thích.`, 'Yêu thích');
      }
    } catch {
      error('Không cập nhật được danh sách yêu thích.');
    }
  };

  const handleCompareClick = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (compared) {
      removeCompare(product.slug);
      info(`Đã bỏ "${product.name}" khỏi so sánh.`);
      return;
    }
    addCompare(product.slug, product.name);
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
      <div style={{ position: 'relative', height: '240px', marginBottom: '16px' }}>
      <Link
        to={`/products/${product.slug}`}
        aria-label={product.name}
        style={{
          position: 'relative',
          height: '100%',
          borderRadius: '12px',
          backgroundColor: '#0a0c12',
          overflow: 'hidden',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: '1px solid rgba(255, 255, 255, 0.05)',
        }}
      >
        {product.thumbnailUrl && !imageFailed ? (
          <img
            src={product.thumbnailUrl}
            alt=""
            style={{
              width: '100%',
              height: '100%',
              objectFit: 'cover',
              transition: 'transform 0.6s cubic-bezier(0.16, 1, 0.3, 1)',
            }}
            onError={() => setImageFailed(true)}
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
        <div style={{ position: 'absolute', top: '12px', left: '12px', right: '108px', display: 'flex', flexDirection: 'column', gap: '6px', alignItems: 'flex-start' }}>
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
                maxWidth: '100%',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {product.categoryName}
            </span>
          )}
        </div>

      </Link>
      <div style={{ position: 'absolute', top: '10px', right: '10px', zIndex: 3, display: 'flex', gap: '8px' }}>
        <button
          type="button"
          onClick={handleWishlistClick}
          title={liked ? 'Bỏ yêu thích' : 'Yêu thích'}
          aria-label={liked ? 'Bỏ yêu thích' : 'Yêu thích'}
          style={{
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            backgroundColor: liked ? 'rgba(255, 101, 132, 0.92)' : 'rgba(5, 5, 8, 0.78)',
            border: `1px solid ${liked ? '#ff6584' : 'rgba(255, 255, 255, 0.2)'}`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            cursor: 'pointer',
          }}
        >
          <Heart size={18} fill={liked ? '#fff' : 'none'} />
        </button>
        <button
          type="button"
          onClick={handleCompareClick}
          title={compared ? 'Bỏ so sánh' : 'So sánh'}
          aria-label={compared ? 'Bỏ so sánh' : 'So sánh'}
          style={{
            width: '40px',
            height: '40px',
            borderRadius: '50%',
            backgroundColor: compared ? 'rgba(232, 197, 107, 0.95)' : 'rgba(5, 5, 8, 0.78)',
            border: `1px solid ${compared ? '#e8c56b' : 'rgba(255, 255, 255, 0.2)'}`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: compared ? '#1a1408' : '#fff',
            cursor: 'pointer',
          }}
        >
          <Scale size={18} />
        </button>
      </div>
      </div>

      {/* Product Information */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        <Link to={`/products/${product.slug}`}>
          <h3
            style={{
              fontSize: '1.02rem',
              fontWeight: 600,
              color: '#FFFFFF',
              lineHeight: 1.4,
              marginBottom: '6px',
              minHeight: '2.8em',
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
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
