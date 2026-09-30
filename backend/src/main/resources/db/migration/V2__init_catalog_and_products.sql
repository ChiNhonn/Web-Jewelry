-- =============================================================================
-- V2: INITIALIZE CATALOG, CATEGORIES, PRODUCTS, VARIANTS & IMAGES
-- =============================================================================

-- 1. Categories Table (Supports Hierarchy & Self-referencing parent_id)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    parent_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories (id) ON DELETE SET NULL,
    INDEX idx_categories_slug (slug),
    INDEX idx_categories_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Products Master Table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    base_sku VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    short_description VARCHAR(500),
    material_overview VARCHAR(150),
    brand VARCHAR(100) DEFAULT 'AURA JEWELRY',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    INDEX idx_products_slug (slug),
    INDEX idx_products_base_sku (base_sku),
    INDEX idx_products_category_id (category_id),
    INDEX idx_products_status (status),
    INDEX idx_products_featured (is_featured)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Product Variants Table
CREATE TABLE IF NOT EXISTS product_variants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku VARCHAR(60) NOT NULL UNIQUE,
    size_option VARCHAR(50),
    material_type VARCHAR(100),
    original_price DECIMAL(15, 2) NOT NULL,
    sale_price DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    INDEX idx_product_variants_sku (sku),
    INDEX idx_product_variants_product_id (product_id),
    INDEX idx_product_variants_price (sale_price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Product Images Table
CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    public_id VARCHAR(100),
    is_thumbnail BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    INDEX idx_product_images_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed Sample Jewelry Categories
INSERT INTO categories (name, slug, description, status) VALUES
('Nhẫn Kim Cương', 'nhan-kim-cuong', 'Nhẫn kim cương tự nhiên đính chấu tinh xảo, nhẫn cầu hôn và nhẫn cưới cao cấp', 'ACTIVE'),
('Dây Chuyền & Mặt Dây', 'day-chuyen-mat-day', 'Dây chuyền vàng Ý 750, vàng 18K, mặt dây đính đá quý phong thủy', 'ACTIVE'),
('Vòng Tay & Lắc', 'vong-tay-lac', 'Vòng tay tennis kim cương, lắc tay phong cách hoàng gia sang trọng', 'ACTIVE'),
('Bông Tai', 'bong-tai', 'Bông tai nụ kim cương, bông tai ngọc trai Akoya tự nhiên', 'ACTIVE'),
('Bộ Trang Sức', 'bo-trang-suc', 'Set trang sức đồng bộ sang trọng dành cho dạ tiệc và ngày trọng đại', 'ACTIVE')
ON DUPLICATE KEY UPDATE description=VALUES(description);
