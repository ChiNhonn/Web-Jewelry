-- =============================================================================
-- V5: Sửa tiếng Việt bị mất dấu và bổ sung danh mục, sản phẩm
-- =============================================================================

-- Ẩn sản phẩm tạo từ bài test, không đưa ra cửa hàng
UPDATE products
SET status = 'INACTIVE'
WHERE slug IN ('nhan-test-concurrency', 'nhan-kim-cuong-test-1790809014708');

-- Sửa các sản phẩm đang hiển thị dấu ?
UPDATE products
SET
    name = 'Nhẫn Solitaire Diamond 2.0ct Chuẩn GIA',
    short_description = 'Kim cương tự nhiên GIA 2.0ct, nước F, độ tinh khiết VVS1, chế tác thủ công từ vàng trắng Ý 18K.',
    description = 'Tuyệt tác nhẫn kim cương Solitaire 2.0 carat đính chấu 6 ngạnh hoàng gia, tôn vinh độ phát quang và độ tinh khiết F/VVS1. Thiết kế thanh thoát từ vàng trắng Ý 750 nguyên khối, đạt chuẩn kiểm định Viện Đá Quý Hoa Kỳ GIA.',
    material_overview = 'Kim cương tự nhiên GIA • Vàng trắng Ý 18K'
WHERE slug = 'nhan-solitaire-diamond-2ct';

UPDATE products
SET
    name = 'Dây Chuyền Royal Crown Diamond 18K',
    short_description = 'Vàng Ý 750 nguyên khối phối kim cương baguette, phong cách quý tộc Châu Âu.',
    description = 'Dây chuyền vàng Ý 750 nguyên khối với mặt vương miện đính 32 viên kim cương baguette và brilliant-cut, lấp lánh như những vì sao.',
    material_overview = 'Vàng Ý 750 nguyên khối • Kim cương baguette'
WHERE slug = 'day-chuyen-royal-crown';

UPDATE products
SET
    name = 'Vòng Tay Tennis Diamond 5.0ct Platinum 950',
    short_description = 'Bạch kim Platinum 950 đính 48 viên kim cương tự nhiên, tổng trọng lượng 5.0 carat.',
    description = 'Vòng tay tennis với 48 viên kim cương tự nhiên đồng đều, liên kết trên nền bạch kim Platinum 950 đúc nguyên khối, chống oxy hóa.',
    material_overview = 'Bạch kim Platinum 950 • 48 viên kim cương tròn'
WHERE slug = 'vong-tay-tennis-diamond-5ct';

UPDATE products
SET
    name = 'Bông Tai Ngọc Trai Akoya và Kim Cương Halo',
    short_description = 'Ngọc trai Akoya Nhật Bản 8.5mm kết hợp kim cương tấm và vàng hồng 18K.',
    description = 'Đôi bông tai kết hợp vẻ dịu dàng của ngọc trai Akoya Nhật Bản thượng hạng 8.5mm và vòng hào quang kim cương giác cắt tròn.',
    material_overview = 'Ngọc trai Akoya Nhật Bản 8.5mm • Vàng hồng 18K'
WHERE slug = 'bong-tai-akoya-pearl-diamond';

UPDATE products
SET
    name = 'Bộ Trang Sức Dạ Vũ Hoàng Gia Empress',
    short_description = 'Set trọn bộ 4 món kim cương tự nhiên, vàng Ý 18K, dành cho dạ tiệc.',
    description = 'Bộ trang sức gồm 4 tác phẩm đồng điệu: dây chuyền vương quyền, nhẫn đính viên chủ 3ct, lắc tay và đôi hoa tai dạ hội.',
    material_overview = 'Set trọn bộ: Dây chuyền, Nhẫn, Vòng tay và Bông tai'
WHERE slug = 'bo-trang-suc-empress-diamond';

UPDATE products
SET
    name = 'Nhẫn Cầu Hôn Eternity Pavé Diamond Vàng Hồng',
    short_description = 'Vàng hồng Ý 18K đính kim cương tấm toàn đai, bảo hành trọn đời AURA Care.',
    description = 'Nhẫn cầu hôn với kỹ thuật đính kim cương vi thể Pavé thủ công trên thân nhẫn vàng hồng 18K.',
    material_overview = 'Vàng hồng Ý 18K • Kim cương tấm toàn đai'
WHERE slug = 'nhan-cau-hon-eternity-pave';

UPDATE product_variants SET material_type = 'Vàng trắng Ý 18K' WHERE sku IN ('AUR-RNG-001-NI10', 'AUR-RNG-001-NI12', 'AUR-RNG-001-NI14');
UPDATE product_variants SET size_option = 'Chiều dài 42cm', material_type = 'Vàng Ý 750' WHERE sku = 'AUR-NCK-002-42CM';
UPDATE product_variants SET size_option = 'Chiều dài 45cm', material_type = 'Vàng Ý 750' WHERE sku = 'AUR-NCK-002-45CM';
UPDATE product_variants SET size_option = 'Ngọc trai trắng ánh hồng', material_type = 'Vàng hồng 18K' WHERE sku = 'AUR-EAR-004-WHT';
UPDATE product_variants SET size_option = 'Ngọc trai ánh kim Champagne', material_type = 'Vàng hồng 18K' WHERE sku = 'AUR-EAR-004-GLD';
UPDATE product_variants SET size_option = 'Set vàng trắng 18K', material_type = 'Vàng trắng Ý 18K và kim cương' WHERE sku = 'AUR-SET-005-WG';
UPDATE product_variants SET size_option = 'Set vàng hồng 18K', material_type = 'Vàng hồng Ý 18K và kim cương' WHERE sku = 'AUR-SET-005-RG';
UPDATE product_variants SET material_type = 'Vàng hồng 18K' WHERE sku IN ('AUR-RNG-006-NI11', 'AUR-RNG-006-NI13');

-- Danh mục mới
INSERT INTO categories (name, slug, description, status) VALUES
('Nhẫn Cưới', 'nhan-cuoi', 'Nhẫn cưới cặp vàng Ý 18K, kiểu trơn hoặc đính kim cương tấm cho ngày trọng đại', 'ACTIVE'),
('Kiềng và Vòng Cổ', 'kieng-vong-co', 'Kiềng vàng cổ điển và vòng cổ ngọc trai dành cho dạ tiệc', 'ACTIVE'),
('Charm Phong Thủy', 'charm-phong-thuy', 'Charm và vòng tay phong thủy vàng 18K: tỳ hưu, hồ ly', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    status = 'ACTIVE';

-- Sản phẩm mới
INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status)
SELECT c.id,
       'Nhẫn Cưới Cặp Vĩnh Cửu',
       'nhan-cuoi-cap-vinh-cuu',
       'AUR-WED-007',
       'Cặp nhẫn cưới song hành, mặt ngoài đính kim cương tấm, lòng nhẫn có thể khắc tên theo yêu cầu.',
       'Cặp nhẫn cưới vàng Ý 18K đính kim cương tấm, khắc tên bên trong.',
       'Vàng Ý 18K • Kim cương tấm',
       'AURA JEWELRY',
       TRUE,
       'ACTIVE'
FROM categories c
WHERE c.slug = 'nhan-cuoi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'nhan-cuoi-cap-vinh-cuu');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status)
SELECT c.id,
       'Nhẫn Cưới Trơn Vàng Ý',
       'nhan-cuoi-tron-vang-y',
       'AUR-WED-008',
       'Nhẫn cưới trơn, bề mặt đánh bóng gương, vàng Ý đúc nguyên khối theo form cổ điển.',
       'Nhẫn cưới trơn bóng, vàng Ý nguyên khối.',
       'Vàng Ý 18K nguyên khối',
       'AURA JEWELRY',
       FALSE,
       'ACTIVE'
FROM categories c
WHERE c.slug = 'nhan-cuoi'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'nhan-cuoi-tron-vang-y');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status)
SELECT c.id,
       'Kiềng Vàng Ý Cổ Điển',
       'kieng-vang-y-co-dien',
       'AUR-CLL-009',
       'Kiềng cổ thanh mảnh lấy cảm hứng từ trang sức cung đình, khóa nam châm ẩn sau gáy.',
       'Kiềng cổ vàng Ý 18K bản mảnh, khóa nam châm ẩn.',
       'Vàng Ý 18K • Bản 3mm',
       'AURA JEWELRY',
       TRUE,
       'ACTIVE'
FROM categories c
WHERE c.slug = 'kieng-vong-co'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'kieng-vang-y-co-dien');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status)
SELECT c.id,
       'Vòng Cổ Ngọc Trai Nam Hải',
       'vong-co-ngoc-trai-nam-hai',
       'AUR-CLL-010',
       'Chuỗi ngọc trai Nam Hải kích thước đều 10-11mm, ánh trắng hồng, khóa hoa vàng trắng đính kim cương tấm.',
       'Vòng cổ ngọc trai Nam Hải ánh trắng, khóa vàng trắng 18K.',
       'Ngọc trai Nam Hải 10-11mm • Vàng trắng 18K',
       'AURA JEWELRY',
       TRUE,
       'ACTIVE'
FROM categories c
WHERE c.slug = 'kieng-vong-co'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'vong-co-ngoc-trai-nam-hai');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status)
SELECT c.id,
       'Charm Tỳ Hưu Vàng 18K',
       'charm-ty-huu-vang-18k',
       'AUR-CHM-011',
       'Tỳ hưu quay đầu, mắt ruby, thân đúc vàng 18K. Có thể đeo trên vòng tay hoặc dây chuyền.',
       'Charm tỳ hưu vàng 18K mắt ruby, móc đeo vòng hoặc dây chuyền.',
       'Vàng 18K • Đá ruby mắt',
       'AURA JEWELRY',
       FALSE,
       'ACTIVE'
FROM categories c
WHERE c.slug = 'charm-phong-thuy'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'charm-ty-huu-vang-18k');

INSERT INTO products (category_id, name, slug, base_sku, description, short_description, material_overview, brand, is_featured, status)
SELECT c.id,
       'Vòng Tay Charm Hồ Ly',
       'vong-tay-charm-ho-ly',
       'AUR-CHM-012',
       'Hồ ly chín đuôi đeo tay, đuôi đính đá garnet, dây xích vàng hồng điều chỉnh được độ dài.',
       'Vòng tay hồ ly chín đuôi vàng hồng 18K, mắt đá garnet.',
       'Vàng hồng 18K • Đá garnet',
       'AURA JEWELRY',
       FALSE,
       'ACTIVE'
FROM categories c
WHERE c.slug = 'charm-phong-thuy'
  AND NOT EXISTS (SELECT 1 FROM products p WHERE p.slug = 'vong-tay-charm-ho-ly');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-WED-007-YG', 'Cặp ni 12 và 16', 'Vàng Ý 18K', 28500000.00, 24800000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'nhan-cuoi-cap-vinh-cuu' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-WED-007-YG');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-WED-007-RG', 'Cặp ni 12 và 16', 'Vàng hồng 18K', 29500000.00, 25900000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'nhan-cuoi-cap-vinh-cuu' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-WED-007-RG');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-WED-008-NI14', 'Ni 14 (17.2mm)', 'Vàng Ý 18K', 18600000.00, 16200000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'nhan-cuoi-tron-vang-y' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-WED-008-NI14');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-WED-008-NI16', 'Ni 16 (17.8mm)', 'Vàng Ý 18K', 19800000.00, 17400000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'nhan-cuoi-tron-vang-y' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-WED-008-NI16');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-CLL-009-38', 'Dài 38cm', 'Vàng Ý 18K', 42000000.00, 38500000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'kieng-vang-y-co-dien' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-CLL-009-38');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-CLL-009-40', 'Dài 40cm', 'Vàng Ý 18K', 45000000.00, 41200000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'kieng-vang-y-co-dien' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-CLL-009-40');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-CLL-010-45', 'Dài 45cm', 'Ngọc trai Nam Hải', 128000000.00, 115000000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'vong-co-ngoc-trai-nam-hai' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-CLL-010-45');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-CHM-011-S', 'Size nhỏ', 'Vàng 18K', 8900000.00, 7600000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'charm-ty-huu-vang-18k' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-CHM-011-S');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-CHM-011-L', 'Size lớn', 'Vàng 18K', 12500000.00, 10900000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'charm-ty-huu-vang-18k' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-CHM-011-L');

INSERT INTO product_variants (product_id, sku, size_option, material_type, original_price, sale_price, status)
SELECT p.id, 'AUR-CHM-012-16', 'Ni 16cm', 'Vàng hồng 18K', 16800000.00, 14500000.00, 'ACTIVE'
FROM products p WHERE p.slug = 'vong-tay-charm-ho-ly' AND NOT EXISTS (SELECT 1 FROM product_variants v WHERE v.sku = 'AUR-CHM-012-16');

INSERT INTO inventories (variant_id, quantity, reserved_quantity, low_stock_threshold, version)
SELECT v.id, q.qty, 0, 2, 0
FROM product_variants v
JOIN (
    SELECT 'AUR-WED-007-YG' AS sku, 12 AS qty UNION ALL
    SELECT 'AUR-WED-007-RG', 8 UNION ALL
    SELECT 'AUR-WED-008-NI14', 20 UNION ALL
    SELECT 'AUR-WED-008-NI16', 15 UNION ALL
    SELECT 'AUR-CLL-009-38', 6 UNION ALL
    SELECT 'AUR-CLL-009-40', 6 UNION ALL
    SELECT 'AUR-CLL-010-45', 4 UNION ALL
    SELECT 'AUR-CHM-011-S', 25 UNION ALL
    SELECT 'AUR-CHM-011-L', 10 UNION ALL
    SELECT 'AUR-CHM-012-16', 9
) q ON q.sku = v.sku
WHERE NOT EXISTS (SELECT 1 FROM inventories i WHERE i.variant_id = v.id);

INSERT INTO product_images (product_id, image_url, is_thumbnail, sort_order)
SELECT p.id, img.url, TRUE, 0
FROM products p
JOIN (
    SELECT 'nhan-cuoi-cap-vinh-cuu' AS slug, 'https://images.unsplash.com/photo-1543294001-f7cd5d7fb516?auto=format&fit=crop&w=1000&q=80' AS url UNION ALL
    SELECT 'nhan-cuoi-tron-vang-y', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=1000&q=80' UNION ALL
    SELECT 'kieng-vang-y-co-dien', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=1000&q=80' UNION ALL
    SELECT 'vong-co-ngoc-trai-nam-hai', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=1000&q=80' UNION ALL
    SELECT 'charm-ty-huu-vang-18k', 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&w=1000&q=80' UNION ALL
    SELECT 'vong-tay-charm-ho-ly', 'https://images.unsplash.com/photo-1611591475836-8a719544256f?auto=format&fit=crop&w=1000&q=80'
) img ON img.slug = p.slug
WHERE NOT EXISTS (
    SELECT 1 FROM product_images i WHERE i.product_id = p.id AND i.is_thumbnail = TRUE
);
