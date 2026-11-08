ALTER TABLE products
    ADD COLUMN weight_grams DECIMAL(8, 2) NULL,
    ADD COLUMN stone_type VARCHAR(100) NULL,
    ADD COLUMN clarity VARCHAR(80) NULL,
    ADD COLUMN warranty VARCHAR(200) NULL;

ALTER TABLE orders
    ADD COLUMN guest_access_token VARCHAR(64) NULL;

UPDATE products
SET weight_grams = 3.20,
    stone_type = 'Kim cương tự nhiên',
    clarity = 'F / VVS1',
    warranty = 'Bảo hành trọn đời, làm mới miễn phí'
WHERE slug = 'nhan-solitaire-diamond-2ct';

UPDATE products
SET weight_grams = 8.50,
    stone_type = 'Kim cương baguette',
    clarity = 'G / VS1',
    warranty = 'Bảo hành 12 tháng, thu đổi lên đời'
WHERE slug = 'day-chuyen-royal-crown';

UPDATE products
SET weight_grams = 12.40,
    stone_type = 'Kim cương tròn',
    clarity = 'F / VS1',
    warranty = 'Bảo hành trọn đời'
WHERE slug = 'vong-tay-tennis-diamond-5ct';

UPDATE products
SET weight_grams = 4.10,
    stone_type = 'Ngọc trai Akoya',
    clarity = 'Ánh trắng hồng',
    warranty = 'Bảo hành 24 tháng'
WHERE slug = 'bong-tai-akoya-pearl-diamond';

UPDATE products
SET weight_grams = 28.00,
    stone_type = 'Kim cương tự nhiên',
    clarity = 'F / VVS2',
    warranty = 'Bảo hành trọn đời AURA Care'
WHERE slug = 'bo-trang-suc-empress-diamond';

UPDATE products
SET weight_grams = 2.80,
    stone_type = 'Kim cương tấm',
    clarity = 'G / VS2',
    warranty = 'Bảo hành trọn đời'
WHERE slug = 'nhan-cau-hon-eternity-pave';

UPDATE products
SET weight_grams = 6.40,
    stone_type = 'Kim cương tấm',
    clarity = 'H / VS2',
    warranty = 'Bảo hành trọn đời, chỉnh ni miễn phí'
WHERE slug = 'nhan-cuoi-cap-vinh-cuu';

UPDATE products
SET weight_grams = 4.60,
    stone_type = 'Không đính đá',
    clarity = 'Vàng nguyên khối',
    warranty = 'Bảo hành trọn đời, chỉnh ni miễn phí'
WHERE slug = 'nhan-cuoi-tron-vang-y';

UPDATE products
SET weight_grams = 15.20,
    stone_type = 'Không đính đá',
    clarity = 'Vàng Ý 18K',
    warranty = 'Bảo hành 24 tháng'
WHERE slug = 'kieng-vang-y-co-dien';

UPDATE products
SET weight_grams = 42.00,
    stone_type = 'Ngọc trai Nam Hải',
    clarity = 'Ánh trắng 10-11mm',
    warranty = 'Bảo hành 24 tháng'
WHERE slug = 'vong-co-ngoc-trai-nam-hai';

UPDATE products
SET weight_grams = 2.10,
    stone_type = 'Ruby mắt',
    clarity = 'Đá ruby tự nhiên',
    warranty = 'Bảo hành 12 tháng'
WHERE slug = 'charm-ty-huu-vang-18k';

UPDATE products
SET weight_grams = 5.30,
    stone_type = 'Garnet',
    clarity = 'Đá garnet mắt hồ ly',
    warranty = 'Bảo hành 12 tháng'
WHERE slug = 'vong-tay-charm-ho-ly';
