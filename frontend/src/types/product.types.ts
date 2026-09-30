export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  imageUrl?: string;
  status: 'ACTIVE' | 'INACTIVE';
  parentId?: number;
  children?: Category[];
}

export interface ProductImage {
  id: number;
  imageUrl: string;
  publicId?: string;
  isThumbnail: boolean;
  sortOrder: number;
}

export interface ProductVariant {
  id: number;
  sku: string;
  sizeOption?: string;
  materialType?: string;
  originalPrice: number;
  salePrice: number;
  status: 'ACTIVE' | 'INACTIVE';
  stockQuantity: number;
  availableQuantity: number;
}

export interface ProductSummary {
  id: number;
  name: string;
  slug: string;
  baseSku: string;
  categoryName: string;
  categorySlug: string;
  brand: string;
  materialOverview?: string;
  isFeatured: boolean;
  minPrice: number;
  maxPrice: number;
  thumbnailUrl?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'DRAFT' | 'OUT_OF_STOCK';
  createdAt: string;
}

export interface ProductDetail {
  id: number;
  name: string;
  slug: string;
  baseSku: string;
  category: Category;
  description?: string;
  shortDescription?: string;
  materialOverview?: string;
  brand: string;
  isFeatured: boolean;
  status: 'ACTIVE' | 'INACTIVE' | 'DRAFT' | 'OUT_OF_STOCK';
  images: ProductImage[];
  variants: ProductVariant[];
  createdAt: string;
  updatedAt?: string;
}
