# AURA — Cửa hàng trang sức

Website bán trang sức AURA. Khách xem sản phẩm, so sánh, đặt hàng và thanh toán trên trình duyệt. Quản trị viên quản lý kho, đơn, khách, khuyến mãi và nhận xét. Dữ liệu nằm trên MySQL, không dùng SQLite.

## Công nghệ

| Phần | Công nghệ |
| :--- | :--- |
| Giao diện | React 18, TypeScript, Vite 5, React Router 6 |
| Máy chủ | Spring Boot 3.3.4, Java 21, Spring Security, JWT |
| Dữ liệu | MySQL 8, JPA, Flyway (V1–V8), `ddl-auto: none` |
| Thanh toán | COD và VNPay sandbox |

Giao diện chạy ở `http://localhost:5173`. API chạy ở `http://localhost:8080`.

## Chức năng

**Khách vãng lai**

- Xem trang chủ, danh mục và chi tiết sản phẩm.
- So sánh tối đa 3 sản phẩm.
- Thêm giỏ hàng và đặt hàng.
- Đăng ký, đăng nhập.

**Khách hàng**

- Hồ sơ, đổi mật khẩu, địa chỉ nhận hàng.
- Danh sách yêu thích.
- Xem đơn và hủy đơn còn được phép hủy.
- Viết nhận xét sau khi đã mua. Nhận xét chỉ hiện công khai sau khi quản trị duyệt.
- Thông báo, gồm thông báo khi đơn được giao.

**Quản trị**

- Thống kê doanh thu.
- Sản phẩm, tồn kho và danh mục.
- Đơn hàng. Đơn đã hủy chỉ để xem, không đổi trạng thái.
- Khách hàng. Tài khoản kỹ thuật `__guest__` không nằm trong danh sách khách và không đăng nhập được.
- Khuyến mãi và mã giảm giá. Mã không cộng dồn với khuyến mãi đang chạy. Khi nhiều khuyến mãi cùng áp dụng, hệ thống lấy mức giá thấp nhất.
- Duyệt nhận xét (hiện hoặc ẩn).
- Xuất báo cáo.

Phí vận chuyển là 0. Thanh toán khi nhận hàng tạo đơn ở trạng thái chưa thanh toán cho đến khi đơn được giao. VNPay dùng cổng sandbox: trình duyệt chuyển sang VNPay, rồi quay về `/payment/vnpay-return`.

## Tài khoản mẫu

| Vai trò | Tên đăng nhập | Email |
| :--- | :--- | :--- |
| Quản trị | `admin` | `duongtrinhan00@gmail.com` |
| Khách | `customer1` | `customer1@gmail.com` |

Trên database đang chạy của máy này, mật khẩu hai tài khoản trên là `Password123!`.

## Chạy dự án

Cần Java 21, Maven, Node.js và MySQL 8 (service `MySQL80`, cổng `3306`). Database `jewelry_db`, tài khoản `root`, mật khẩu mặc định `123456`, cấu hình trong `backend/src/main/resources/application-dev.yml`.

Backend, chạy trong thư mục `backend` bằng đường dẫn tương đối vì tên thư mục có dấu:

```powershell
cd backend
mvn -q "-Dmaven.test.skip=true" package
java -jar target\jewelry-store-backend-1.0.0-SNAPSHOT.jar
```

Frontend, cửa sổ terminal thứ hai:

```powershell
cd frontend
npm install
npm run dev
```

Flyway tự áp migration khi backend khởi động. Không cần tạo bảng bằng tay.

## Đường dẫn

| Trang | URL |
| :--- | :--- |
| Trang chủ | http://localhost:5173 |
| Sản phẩm | http://localhost:5173/products |
| So sánh | http://localhost:5173/compare |
| Giỏ hàng | http://localhost:5173/cart |
| Thanh toán | http://localhost:5173/checkout |
| Tài khoản | http://localhost:5173/account |
| Quản trị | http://localhost:5173/admin |
| Swagger | http://localhost:8080/swagger-ui.html |
| Health | http://localhost:8080/api/v1/health |

## Không đưa lên Git

Giữ ngoài commit các thư mục và file cục bộ:

- `frontend/node_modules`
- `backend/target`
- `frontend/.env`
- `backend/src/main/resources/application-dev.yml` (mật khẩu cơ sở dữ liệu, khóa VNPay, mật khẩu email)

Trong repo hiện chưa có `.gitignore`, nên đừng dùng `git add .`. Thêm từng nhóm file nguồn, hoặc thêm `.gitignore` trước khi add toàn bộ.
