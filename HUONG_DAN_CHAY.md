# Hướng Dẫn Tự Khởi Chạy Dự Án Trang Sức AURA (Backend + Frontend)

Hệ thống bao gồm 2 phần:
1. **Backend**: Spring Boot 3.3.4 (Java 21), kết nối MySQL qua JPA / Hibernate, bảo mật JWT, Swagger OpenAPI.
2. **Frontend**: React 18, Vite, TypeScript, Lucide Icons, Vanilla CSS Luxury Design System.

---

## 1. Thông Tin Môi Trường & Cơ Sở Dữ Liệu
Trên máy tính của bạn đã được kiểm tra và sẵn sàng:
- **Java**: `Java 21.0.12.1 LTS`
- **Maven**: `Apache Maven 3.9.16`
- **Node.js**: `v25.2.1` / `npm 11.6.2`
- **MySQL**: Service `MySQL80` đang chạy cổng `3306`
  - Database: `jewelry_db`
  - Tài khoản: `root`
  - Mật khẩu mặc định: `123456` (cấu hình trong file [`.env`](.env) và [`application-dev.yml`](backend/src/main/resources/application-dev.yml))

---

## 2. Cách 1: Khởi Chạy Tự Động 1-Click (Khuyên Dùng)

Ở thư mục gốc của dự án, bạn chỉ cần click đúp vào file:
👉 **[`start-project.bat`](start-project.bat)**

Script sẽ tự động:
1. Kiểm tra và build file JAR Backend nếu chưa có.
2. Mở cửa sổ terminal riêng chạy Backend (cổng 8080).
3. Mở cửa sổ terminal riêng chạy Frontend (cổng 5173).

---

## 3. Cách 2: Tự Khởi Chạy Thủ Công Qua Terminal

### Bước 1: Khởi động Backend (Spring Boot)
Mở một cửa sổ PowerShell hoặc Command Prompt tại thư mục dự án và chạy:
```powershell
cd backend
mvn package -DskipTests
java -jar target/jewelry-store-backend-1.0.0-SNAPSHOT.jar
```
> **Mẹo kỹ thuật**: Do đường dẫn thư mục có ký tự tiếng Việt (`Web trang sức`), việc chạy trực tiếp file JAR đóng gói (`java -jar`) sẽ đảm bảo không bao giờ bị lỗi `ClassNotFoundException` do bảng mã console.

Khi thấy dòng sau là Backend đã sẵn sàng:
```
Tomcat started on port 8080 (http) with context path '/'
Started JewelryStoreApplication in ... seconds
```

### Bước 2: Khởi động Frontend (React Vite)
Mở thêm một cửa sổ PowerShell / Terminal thứ hai và chạy:
```powershell
cd frontend
npm run dev
```

Khi thấy dòng sau là Frontend đã sẵn sàng:
```
  VITE v5.4.x  ready in ... ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

---

## 4. Cách 3: Chạy Ngay Trong VS Code
1. **Chạy Backend**: 
   - Mở file [`JewelryStoreApplication.java`](backend/src/main/java/com/jewelry/store/JewelryStoreApplication.java).
   - Nhấn nút **Run** xuất hiện ngay phía trên `public static void main` (hoặc bấm **F5** để debug).
2. **Chạy Frontend**:
   - Nhấn tổ hợp phím `` Ctrl + ` `` để mở Terminal trong VS Code.
   - Gõ:
     ```powershell
     cd frontend
     npm run dev
     ```

---

## 5. Các Đường Dẫn Truy Cập Sau Khi Chạy
| Dịch vụ | Địa chỉ URL | Mô tả |
| :--- | :--- | :--- |
| **Website Trang Sức** | [http://localhost:5173](http://localhost:5173) | Giao diện mua sắm sang trọng, hiển thị live status Backend |
| **API Documentation** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Giao diện Swagger test các API endpoints |
| **Backend Health Check** | [http://localhost:8080/api/v1/health](http://localhost:8080/api/v1/health) | Kiểm tra trạng thái hoạt động của Spring Boot & DB |
