SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM review_images;
DELETE FROM reviews;
DELETE FROM payments;
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM carts;
DELETE FROM wishlists;
DELETE FROM inventories;
DELETE FROM product_images;
DELETE FROM product_variants;
DELETE FROM products;
DELETE FROM customer_notifications;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO categories (name, slug, description, status) VALUES
('Nhẫn Kim Cương', 'nhan-kim-cuong', 'Nhẫn kim cương tự nhiên, nhẫn cầu hôn và nhẫn cưới', 'ACTIVE'),
('Dây Chuyền & Mặt Dây', 'day-chuyen-mat-day', 'Dây chuyền vàng Ý và mặt dây đá quý', 'ACTIVE'),
('Vòng Tay & Lắc', 'vong-tay-lac', 'Vòng tay tennis và lắc tay', 'ACTIVE'),
('Bông Tai', 'bong-tai', 'Bông tai kim cương và ngọc trai', 'ACTIVE'),
('Bộ Trang Sức', 'bo-trang-suc', 'Set trang sức đồng bộ', 'ACTIVE'),
('Nhẫn Cưới', 'nhan-cuoi', 'Nhẫn cưới cặp vàng Ý', 'ACTIVE'),
('Kiềng và Vòng Cổ', 'kieng-vong-co', 'Kiềng vàng và vòng cổ ngọc trai', 'ACTIVE'),
('Charm Phong Thủy', 'charm-phong-thuy', 'Charm tỳ hưu và hồ ly', 'ACTIVE')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), status = 'ACTIVE';

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status, weight_grams, stone_type, clarity, warranty)
SELECT c.id, 'Nhẫn Solitaire Diamond 2.0ct Chuẩn GIA', 'nhan-solitaire-diamond-2ct', 'AUR-RNG-001',
       'Nhẫn kim cương Solitaire 2.0 carat, vàng trắng Ý 18K, chứng nhận GIA.',
       'Kim cương tự nhiên GIA 2.0ct, nước F, độ tinh khiết VVS1.',
       'Kim cương tự nhiên GIA • Vàng trắng Ý 18K', 'AURA JEWELRY', TRUE, 'ACTIVE', 3.20, 'Kim cương tự nhiên', 'F / VVS1', 'Bảo hành trọn đời'
FROM categories c WHERE c.slug = 'nhan-kim-cuong'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'nhan-solitaire-diamond-2ct');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status, weight_grams, stone_type, clarity, warranty)
SELECT c.id, 'Dây Chuyền Royal Crown Diamond 18K', 'day-chuyen-royal-crown', 'AUR-NCK-002',
       'Dây chuyền vàng Ý 750, mặt vương miện đính kim cương baguette.',
       'Vàng Ý 750 phối kim cương baguette.',
       'Vàng Ý 750 nguyên khối • Kim cương baguette', 'AURA JEWELRY', TRUE, 'ACTIVE', 8.50, 'Kim cương baguette', 'G / VS1', 'Bảo hành 12 tháng'
FROM categories c WHERE c.slug = 'day-chuyen-mat-day'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'day-chuyen-royal-crown');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status, weight_grams, stone_type, clarity, warranty)
SELECT c.id, 'Nhẫn Cưới Cặp Vĩnh Cửu', 'nhan-cuoi-cap-vinh-cuu', 'AUR-WED-007',
       'Cặp nhẫn cưới vàng Ý 18K đính kim cương tấm.',
       'Cặp nhẫn cưới, khắc tên bên trong.',
       'Vàng Ý 18K • Kim cương tấm', 'AURA JEWELRY', TRUE, 'ACTIVE', 6.40, 'Kim cương tấm', 'H / VS2', 'Bảo hành trọn đời'
FROM categories c WHERE c.slug = 'nhan-cuoi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'nhan-cuoi-cap-vinh-cuu');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-RNG-001-NI12', 'Ni 12 (16.5mm)', 'Vàng trắng Ý 18K', 55000000, 48500000, 'ACTIVE' FROM products p
WHERE p.slug = 'nhan-solitaire-diamond-2ct' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-RNG-001-NI12');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-NCK-002-42CM', 'Chiều dài 42cm', 'Vàng Ý 750', 36000000, 32000000, 'ACTIVE' FROM products p
WHERE p.slug = 'day-chuyen-royal-crown' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-NCK-002-42CM');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-WED-007-YG', 'Cặp ni 12 và 16', 'Vàng Ý 18K', 28500000, 24800000, 'ACTIVE' FROM products p
WHERE p.slug = 'nhan-cuoi-cap-vinh-cuu' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-WED-007-YG');

INSERT INTO inventories (variant_id, quantity, reserved_quantity, low_stock_threshold, version)
SELECT v.id, 8, 0, 2, 0 FROM product_variants v
WHERE v.sku IN ('AUR-RNG-001-NI12', 'AUR-NCK-002-42CM', 'AUR-WED-007-YG')
  AND NOT EXISTS (SELECT 1 FROM inventories i WHERE i.variant_id = v.id);

INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=1000&q=80', TRUE, 0
FROM products p WHERE p.slug = 'nhan-solitaire-diamond-2ct'
  AND NOT EXISTS (SELECT 1 FROM product_images i WHERE i.product_id = p.id);

INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=1000&q=80', TRUE, 0
FROM products p WHERE p.slug = 'day-chuyen-royal-crown'
  AND NOT EXISTS (SELECT 1 FROM product_images i WHERE i.product_id = p.id);

INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order)
SELECT p.id, 'https://images.unsplash.com/photo-1543294001-f7cd5d7fb516?auto=format&fit=crop&w=1000&q=80', TRUE, 0
FROM products p WHERE p.slug = 'nhan-cuoi-cap-vinh-cuu'
  AND NOT EXISTS (SELECT 1 FROM product_images i WHERE i.product_id = p.id);
