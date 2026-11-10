
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS review_images;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS product_reviews;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS wishlists;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS inventories;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS product_variants;
DROP TABLE IF EXISTS promotion_targets;
DROP TABLE IF EXISTS promotions;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS customer_notifications;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS password_reset_otps;
DROP TABLE IF EXISTS otp_sessions;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(30) NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  address TEXT NULL,
  is_locked TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE categories (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE products (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT NULL,
  price DECIMAL(15,2) NOT NULL,
  quantity INT NOT NULL,
  category_id BIGINT NOT NULL,
  material VARCHAR(255) NULL,
  weight VARCHAR(100) NULL,
  image_name VARCHAR(500) NULL,
  created_at DATETIME NULL,
  gem_type VARCHAR(255) NULL,
  purity VARCHAR(255) NULL,
  size VARCHAR(100) NULL,
  warranty VARCHAR(255) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cart_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE favorites (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_fav_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE coupons (
  id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL,
  title VARCHAR(255) NOT NULL,
  discount_type VARCHAR(30) NOT NULL,
  discount_value DECIMAL(15,2) NOT NULL,
  min_order_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  max_discount_amount DECIMAL(15,2) NULL,
  start_date DATETIME NOT NULL,
  end_date DATETIME NOT NULL,
  usage_limit INT NOT NULL DEFAULT 100,
  used_count INT NOT NULL DEFAULT 0,
  is_active TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_coupons_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE promotions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(80) NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT NULL,
  discount_type VARCHAR(30) NOT NULL,
  discount_value DECIMAL(15,2) NOT NULL,
  start_date DATETIME NOT NULL,
  end_date DATETIME NOT NULL,
  target_type VARCHAR(30) NOT NULL DEFAULT 'ALL',
  target_id BIGINT NULL,
  is_active TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_promotions_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE promotion_targets (
  id BIGINT NOT NULL AUTO_INCREMENT,
  promotion_id BIGINT NOT NULL,
  product_id BIGINT NULL,
  category_id BIGINT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_pt_promo FOREIGN KEY (promotion_id) REFERENCES promotions (id),
  CONSTRAINT fk_pt_product FOREIGN KEY (product_id) REFERENCES products (id),
  CONSTRAINT fk_pt_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  receiver_name VARCHAR(255) NOT NULL,
  phone VARCHAR(30) NOT NULL,
  shipping_address TEXT NOT NULL,
  total_amount DECIMAL(15,2) NOT NULL,
  payment_method VARCHAR(30) NOT NULL,
  payment_status VARCHAR(50) NOT NULL,
  order_status VARCHAR(50) NOT NULL,
  created_at DATETIME NOT NULL,
  discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  coupon_code VARCHAR(50) NOT NULL DEFAULT '',
  final_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  promotion_discount DECIMAL(15,2) NOT NULL DEFAULT 0,
  coupon_discount DECIMAL(15,2) NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_details (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NULL,
  product_name VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(15,2) NOT NULL,
  subtotal DECIMAL(15,2) NOT NULL,
  original_unit_price DECIMAL(15,2) NULL,
  promotion_discount DECIMAL(15,2) NOT NULL DEFAULT 0,
  promotion_name VARCHAR(255) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_details_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  type VARCHAR(50) NOT NULL DEFAULT 'ORDER_DELIVERED',
  order_id BIGINT NULL,
  product_id BIGINT NULL,
  is_read TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL,
  action_type VARCHAR(50) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE otp_sessions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  email VARCHAR(255) NOT NULL,
  purpose VARCHAR(50) NOT NULL,
  otp_hash VARCHAR(255) NOT NULL,
  otp_salt VARCHAR(255) NOT NULL,
  expires_at BIGINT NOT NULL,
  last_sent_at BIGINT NOT NULL,
  send_count INT NOT NULL DEFAULT 1,
  send_window_start BIGINT NOT NULL,
  failed_attempts INT NOT NULL DEFAULT 0,
  verified_at BIGINT NULL,
  reset_token_hash VARCHAR(255) NULL,
  used TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_otp_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE product_reviews (
  id BIGINT NOT NULL AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  order_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(255) NOT NULL,
  rating INT NOT NULL,
  comment TEXT NULL,
  is_visible TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products (id),
  CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token VARCHAR(500) NOT NULL,
  expiry_date DATETIME(6) NOT NULL,
  revoked TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_refresh_token (token),
  CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, username, full_name, email, phone, password_hash, role, address, is_locked) VALUES (1, 'admin', 'Quản Trị Viên Hệ Thống', 'duongtrinhan00@gmail.com', '0988888888', 'pbkdf2_sha256$60000$XXFg60GlpkIpLVqYHXIx0w==$fN1tgEkiAwrXiSWgQEwQOtzgjDy3LpYj87doQQcYT7k=', 'ADMIN', 'Phòng Quản Trị Tầng 8, Tòa Nhà Kim Cương, TP.HCM', 0);
INSERT INTO users (id, username, full_name, email, phone, password_hash, role, address, is_locked) VALUES (2, 'customer1', 'Nguyễn Văn An', 'customer1@gmail.com', '0901234567', 'pbkdf2_sha256$60000$1Fy6PUAtkKdrV8Msjodmxw==$FKnWRDUVhOfMhFsgMjotkDHxqxOsvCSD/nhU0/l4iec=', 'CUSTOMER', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 0);
INSERT INTO users (id, username, full_name, email, phone, password_hash, role, address, is_locked) VALUES (3, '__guest__', 'Khách vãng lai', 'guest@jewelry.local', NULL, 'pbkdf2_sha256$60000$vzVKWuuS0UKYzAvXvYLl8w==$5k2qZFyHAgxxCoZFCyHgjlJfU5hIP2MmTjp9NxHgob0=', 'GUEST', NULL, 0);
INSERT INTO categories (id, name, description) VALUES (1, 'Nhẫn Kim Cương', 'Bộ sưu tập nhẫn đính kim cương và đá quý tự nhiên sang trọng');
INSERT INTO categories (id, name, description) VALUES (2, 'Dây Chuyền & Mặt Dây', 'Dây chuyền vàng, bạch kim đính mặt đá tinh xảo');
INSERT INTO categories (id, name, description) VALUES (3, 'Vòng Tay & Lắc Tay', 'Vòng tay phong thủy, lắc tay vàng 18K và bạc Ý cao cấp');
INSERT INTO categories (id, name, description) VALUES (4, 'Bông Tai Quý Phái', 'Bông tai ngọc trai, nụ kim cương tỏa sáng quyến rũ');
INSERT INTO categories (id, name, description) VALUES (5, 'Trang Sức Vàng 18K', 'Trang sức chế tác từ vàng chuẩn 18K (750) đẳng cấp');
INSERT INTO categories (id, name, description) VALUES (6, 'Trang Sức Bạc Ý 925', 'Bạc Ý mạ Rhodium chống xỉn màu và sáng bóng bền lâu');
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (1, 'Nhẫn Kim Cương Solitaire Hoàng Gia 18K', 'Nhẫn đính kim cương tự nhiên 5.4 ly nước D, độ trong VVS1, ổ nhẫn chế tác từ vàng trắng 18K nguyên khối.', 38500000, 8, 1, 'Vàng trắng 18K & Kim Cương', '1.2 chỉ', 'jw_ring_solitaire', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (2, 'Nhẫn Ruby Huyết Bồ Câu Đính Kim Cương', 'Đá chủ Ruby huyết bồ câu tự nhiên 2.5 carat, giác cắt Oval lấp lánh bao quanh bởi 16 viên kim cương tấm.', 52000000, 4, 1, 'Vàng vàng 18K & Ruby', '1.5 chỉ', 'jw_ring_ruby', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (3, 'Dây Chuyền Vàng Ý Khắc Hoa Văn Cung Đình', 'Dây chuyền mắt xích kép thanh lịch, điểm xuyết họa tiết hoàng gia Ý sắc nét và quý phái.', 18200000, 15, 2, 'Vàng Ý 750', '2.8 chỉ', 'jw_necklace_italy', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (4, 'Mặt Dây Chuyền Trái Tim Sapphire Xanh Biển', 'Mặt dây hình trái tim đính đá Sapphire tự nhiên màu xanh sâu thẳm, viền kim cương nhân tạo Swarovski.', 14500000, 12, 2, 'Vàng trắng 14K & Sapphire', '0.9 chỉ', 'jw_pendant_sapphire', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (5, 'Lắc Tay Bạc Ý 925 Đính Đá Tinh Khôi', 'Lắc tay bạc Ý 925 phủ bạch kim sáng bóng, thiết kế bi tròn xen kẽ hoa tuyết lấp lánh.', 2450000, 25, 3, 'Bạc Ý 925 mạ Rhodium', '4.2 chỉ', 'jw_bracelet_silver', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (6, 'Vòng Tay Bản Liền Vàng 18K Chạm Rồng Phượng', 'Vòng tay truyền thống chạm nổi rồng phượng tinh xảo mang ý nghĩa may mắn, tài lộc cho gia chủ.', 46800000, 3, 3, 'Vàng 18K (75%)', '5.0 chỉ', 'jw_bangle_gold', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (7, 'Bông Tai Ngọc Trai Akoya Ánh Hồng', 'Cặp ngọc trai biển Akoya 8.5mm tròn hoàn hảo ánh xà cừ hồng dịu, chấu vàng trắng 18K cao cấp.', 9800000, 10, 4, 'Ngọc trai Akoya & Vàng trắng 18K', '0.6 chỉ', 'jw_earrings_pearl', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (8, 'Bông Tai Nụ Kim Cương 4 Chấu Cổ Điển', 'Bông tai nụ kim cương thiên nhiên 4.5 ly mỗi bên, thiết kế tinh giản tôn vinh vẻ đẹp thuần khiết.', 28900000, 7, 4, 'Bạch kim Platinum 950 & Kim cương', '0.8 chỉ', 'jw_earrings_stud', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (9, 'Kiềng Cưới Vàng 24K Hoa Mai May Mắn', 'Kiềng cưới cô dâu vàng ta 24K (999.9) chạm khắc chùm hoa mai 5 cánh sum vầy hạnh phúc.', 68000000, 5, 5, 'Vàng 24K (999.9)', '7.5 chỉ', 'jw_kieng_gold', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (10, 'Dây Chuyền Nam Vàng 18K Mắt Xích Cuba', 'Dây chuyền bản to đầm tay phong cách mạnh mẽ, khóa hộp kép chắc chắn và nam tính.', 62500000, 0, 5, 'Vàng 18K nguyên khối', '8.2 chỉ', 'jw_chain_men', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (11, 'Nhẫn Cặp Bạc Ý 925 Khắc Tên Vĩnh Cửu', 'Cặp nhẫn bạc đôi thiết kế tối giản trẻ trung, bề mặt phay xước hiện đại, miễn phí khắc tên theo yêu cầu.', 1850000, 28, 6, 'Bạc Ý 925', '2.1 chỉ', 'jw_rings_couple', '2026-10-23 16:08:53', NULL, NULL, NULL, NULL);
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (12, 'Lắc Chân Bạc Nữ Chuông Nhỏ Dễ Thương', 'Lắc chân bạc 925 phối chuông nhỏ ngân vang nhẹ nhàng khi di chuyển, tôn dáng cổ chân thanh mảnh.', 950000, 10, 6, 'Bạc Ý 925', '1.5 chỉ', 'jw_anklet_silver', '2026-10-23 16:08:53', '', '', '', '');
INSERT INTO products (id, name, description, price, quantity, category_id, material, weight, image_name, created_at, gem_type, purity, size, warranty) VALUES (13, 'Còng Tay', 'Mọi người đều ko muốn sử dụng', 200000, 42, 4, 'Bạc nguyên chất', '200gram', 'file:prod_1793865295390.jpg', '2026-11-05 14:55:10', '', 'Bạc 92,5%', '45cm', '6 Tháng, bao làm sạch');
INSERT INTO coupons (id, code, title, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, usage_limit, used_count, is_active, created_at) VALUES (1, 'JEWELRY10', 'Mã giảm giá Khách Hàng Thân Thiết (10%)', 'PERCENTAGE', 10, 5000000, 2000000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 100, 0, 1, '2026-10-29 13:44:23');
INSERT INTO coupons (id, code, title, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, usage_limit, used_count, is_active, created_at) VALUES (2, 'WELCOME100', 'Ưu đãi chào mừng thành viên mới - 500K', 'FIXED_AMOUNT', 500000, 2000000, 500000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 200, 1, 1, '2026-10-29 13:44:23');
INSERT INTO coupons (id, code, title, discount_type, discount_value, min_order_amount, max_discount_amount, start_date, end_date, usage_limit, used_count, is_active, created_at) VALUES (3, 'GOLDVIP', 'Đặc quyền VIP Giảm 2 Triệu VNĐ', 'FIXED_AMOUNT', 2000000, 20000000, 2000000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 50, 0, 1, '2026-10-29 13:44:23');
INSERT INTO promotions (id, code, name, description, discount_type, discount_value, start_date, end_date, target_type, target_id, is_active, created_at) VALUES (1, 'PROMO_YEAR_END_2026', 'Ưu đãi trang sức cuối năm', 'Giảm 15% cho toàn bộ sản phẩm thuộc danh mục Nhẫn Kim Cương', 'PERCENTAGE', 15, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'CATEGORY', 1, 1, '2026-10-29 13:44:23');
INSERT INTO promotions (id, code, name, description, discount_type, discount_value, start_date, end_date, target_type, target_id, is_active, created_at) VALUES (2, 'PROMO_GOLD_LUXURY', 'Tuần Lễ Vàng Trang Sức 18K', 'Giảm ngay 1.000.000 VNĐ cho các sản phẩm Trang Sức Vàng 18K', 'FIXED_AMOUNT', 1000000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'CATEGORY', 5, 1, '2026-10-29 13:44:23');
INSERT INTO promotions (id, code, name, description, discount_type, discount_value, start_date, end_date, target_type, target_id, is_active, created_at) VALUES (3, 'PROMO_FLASH_SALE', 'Flash Sale Giờ Vàng', 'Giảm 10% trên toàn bộ hệ thống trang sức', 'PERCENTAGE', 10, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 'ALL', NULL, 1, '2026-10-29 13:44:23');
INSERT INTO promotion_targets (id, promotion_id, product_id, category_id) VALUES (1, 1, NULL, 1);
INSERT INTO promotion_targets (id, promotion_id, product_id, category_id) VALUES (2, 2, NULL, 5);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (1, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 38500000, 'COD', 'Đã thanh toán', 'Đã giao hàng', '2026-10-23 16:08:53', 0, '', 38500000, 0, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (2, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 1850000, 'COD', 'Đã thanh toán', 'Đã giao hàng', '2026-10-23 16:09:52', 0, '', 1850000, 0, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (3, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 64350000, 'COD', 'Đã thanh toán', 'Đã giao hàng', '2026-10-30 16:07:51', 6435000, '', 57915000, 6435000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (4, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 62500000, 'COD', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-05 14:30:01', 6250000, '', 56250000, 6250000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (5, 3, 'Tri Nhân', '0378276079', 'HCM', 50000000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-05 16:24:30', 5000000, '', 45000000, 5000000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (6, 3, 'TriNhan', '0378276079', '4a Lê Văn Miến, An Khánh, Hồ Chí Minh 70000, Việt Nam', 50000000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-05 16:38:39', 5000000, '', 45000000, 5000000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (7, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-05 16:41:13', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (8, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-08 17:57:02', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (9, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-08 17:58:04', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (10, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'VNPay', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:07:55', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (11, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'VNPay', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:22:13', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (12, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'COD', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:23:17', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (13, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 2000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-08 18:25:23', 200, '', 1800, 200, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (14, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 400000, 'VNPay', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:40:32', 40000, '', 360000, 40000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (15, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 200000, 'COD', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:41:33', 20000, '', 180000, 20000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (16, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 200000, 'COD', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:48:32', 20000, '', 180000, 20000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (17, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 200000, 'VNPay', 'Chưa thanh toán', 'Chờ xác nhận', '2026-11-08 18:49:22', 20000, '', 180000, 20000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (18, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 200000, 'VNPay', 'Đã thanh toán', 'Chờ xác nhận', '2026-11-08 18:49:52', 20000, '', 180000, 20000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (19, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 200000, 'VNPay', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 18:59:22', 20000, '', 180000, 20000, 0);
INSERT INTO orders (id, user_id, receiver_name, phone, shipping_address, total_amount, payment_method, payment_status, order_status, created_at, discount_amount, coupon_code, final_amount, promotion_discount, coupon_discount) VALUES (20, 2, 'Nguyễn Văn An', '0901234567', '123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', 200000, 'VNPay', 'Chưa thanh toán', 'Đã hủy', '2026-11-08 19:00:03', 20000, '', 180000, 20000, 0);
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (1, 1, 1, 'Nhẫn Kim Cương Solitaire Hoàng Gia 18K', 1, 38500000, 38500000, 38500000, 0, NULL);
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (2, 2, 11, 'Nhẫn Cặp Bạc Ý 925 Khắc Tên Vĩnh Cửu', 1, 1850000, 1850000, 1850000, 0, NULL);
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (3, 3, 11, 'Nhẫn Cặp Bạc Ý 925 Khắc Tên Vĩnh Cửu', 1, 1665000, 1665000, 1850000, 185000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (4, 3, 10, 'Dây Chuyền Nam Vàng 18K Mắt Xích Cuba', 1, 56250000, 56250000, 62500000, 6250000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (5, 4, 10, 'Dây Chuyền Nam Vàng 18K Mắt Xích Cuba', 1, 56250000, 56250000, 62500000, 6250000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (6, 5, 13, 'Còng Tay', 1, 45000000, 45000000, 50000000, 5000000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (7, 6, 13, 'Còng Tay', 1, 45000000, 45000000, 50000000, 5000000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (8, 7, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (9, 8, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (10, 9, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (11, 10, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (12, 11, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (13, 12, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (14, 13, 13, 'Còng Tay', 1, 1800, 1800, 2000, 200, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (15, 14, 13, 'Còng Tay', 2, 180000, 360000, 200000, 40000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (16, 15, 13, 'Còng Tay', 1, 180000, 180000, 200000, 20000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (17, 16, 13, 'Còng Tay', 1, 180000, 180000, 200000, 20000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (18, 17, 13, 'Còng Tay', 1, 180000, 180000, 200000, 20000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (19, 18, 13, 'Còng Tay', 1, 180000, 180000, 200000, 20000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (20, 19, 13, 'Còng Tay', 1, 180000, 180000, 200000, 20000, 'Flash Sale Giờ Vàng');
INSERT INTO order_details (id, order_id, product_id, product_name, quantity, unit_price, subtotal, original_unit_price, promotion_discount, promotion_name) VALUES (21, 20, 13, 'Còng Tay', 1, 180000, 180000, 200000, 20000, 'Flash Sale Giờ Vàng');
INSERT INTO cart_items (id, user_id, product_id, quantity) VALUES (27, 2, 13, 1);
INSERT INTO favorites (id, user_id, product_id, created_at) VALUES (1, 2, 1, '2026-10-29 13:44:23');
INSERT INTO favorites (id, user_id, product_id, created_at) VALUES (2, 2, 3, '2026-10-29 13:44:23');
INSERT INTO notifications (id, user_id, title, content, type, order_id, product_id, is_read, created_at, action_type) VALUES (1, 2, 'Đơn hàng #1 đã được giao thành công!', 'Đơn hàng của bạn đã được giao thành công. Cảm ơn bạn đã mua sắm tại JewelryManagement! Bạn có muốn đánh giá sản phẩm vừa mua không?', 'ORDER_DELIVERED', 1, 1, 1, '2026-10-29 13:44:23', NULL);
INSERT INTO notifications (id, user_id, title, content, type, order_id, product_id, is_read, created_at, action_type) VALUES (2, 2, 'Đơn hàng #3 đã giao thành công!', 'Đơn hàng của bạn đã được giao thành công. Cảm ơn bạn đã mua sắm tại JewelryManagement! Bạn có muốn đánh giá sản phẩm vừa mua không?', 'ORDER_DELIVERED', 3, 11, 1, '2026-10-30 16:09:24', 'REVIEW_ORDER');
INSERT INTO otp_sessions (id, user_id, email, purpose, otp_hash, otp_salt, expires_at, last_sent_at, send_count, send_window_start, failed_attempts, verified_at, reset_token_hash, used) VALUES (1, 1, 'duongtrinhan00@gmail.com', 'RESET_PASSWORD', '+esxIKHpl22bTlJ6cMeM/df1YL2U/KdeQnebluLkcWs=', 'pzAx41-U0-9JnS2cqHNg2A', 1793351300553, 1793351000553, 1, 1793351000553, 0, 1793351036858, NULL, 1);
INSERT INTO product_reviews (id, product_id, order_id, user_id, user_name, rating, comment, is_visible, created_at) VALUES (1, 1, 1, 2, 'Nguyễn Văn An', 5, 'Nhẫn kim cương ngoài đời lấp lánh hơn cả trong hình, giác cắt xuất sắc, đóng gói hộp nhung rất cao cấp!', 1, '2026-10-29 13:44:23');
INSERT INTO product_reviews (id, product_id, order_id, user_id, user_name, rating, comment, is_visible, created_at) VALUES (2, 10, 3, 2, 'Nguyễn Văn An', 5, '', 1, '2026-10-30 16:10:21');
INSERT INTO product_reviews (id, product_id, order_id, user_id, user_name, rating, comment, is_visible, created_at) VALUES (3, 11, 3, 2, 'Nguyễn Văn An', 5, '', 1, '2026-10-30 16:10:27');

ALTER TABLE users AUTO_INCREMENT = 20;
ALTER TABLE categories AUTO_INCREMENT = 20;
ALTER TABLE products AUTO_INCREMENT = 30;
ALTER TABLE coupons AUTO_INCREMENT = 20;
ALTER TABLE promotions AUTO_INCREMENT = 20;
ALTER TABLE promotion_targets AUTO_INCREMENT = 20;
ALTER TABLE orders AUTO_INCREMENT = 40;
ALTER TABLE order_details AUTO_INCREMENT = 40;
ALTER TABLE cart_items AUTO_INCREMENT = 40;
ALTER TABLE favorites AUTO_INCREMENT = 20;
ALTER TABLE notifications AUTO_INCREMENT = 20;
ALTER TABLE otp_sessions AUTO_INCREMENT = 20;
ALTER TABLE product_reviews AUTO_INCREMENT = 20;
