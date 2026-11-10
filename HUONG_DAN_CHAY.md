# Hướng dẫn chạy AURA

Tóm tắt chức năng và tài khoản mẫu nằm trong [README.md](README.md). File này chỉ ghi cách bật hệ thống trên máy local.

Hệ thống có hai phần:

1. **Backend**: Spring Boot 3.3.4, Java 21, MySQL, JWT, Flyway, Swagger.
2. **Frontend**: React 18, Vite, TypeScript.

## Môi trường

- Java 21
- Maven
- Node.js và npm
- MySQL 8, service `MySQL80`, cổng `3306`
- Database `jewelry_db`, user `root`, mật khẩu mặc định `123456`

Cấu hình local nằm trong [`application-dev.yml`](backend/src/main/resources/application-dev.yml). Hibernate không tự sửa bảng (`ddl-auto: none`). Flyway chạy migration `V1` đến `V8` lúc backend khởi động.

## Backend

Mở PowerShell tại thư mục dự án:

```powershell
cd backend
mvn -q "-Dmaven.test.skip=true" package
java -jar target\jewelry-store-backend-1.0.0-SNAPSHOT.jar
```

Chạy `java -jar` bằng đường dẫn tương đối, ngay trong thư mục `backend`. Đường dẫn tuyệt đối dễ lỗi vì tên thư mục có dấu tiếng Việt.

Backend sẵn sàng khi log có dòng:

```
Tomcat started on port 8080 (http) with context path '/'
Started JewelryStoreApplication
```

## Frontend

Mở một terminal khác:

```powershell
cd frontend
npm install
npm run dev
```

Frontend sẵn sàng khi Vite in:

```
Local: http://localhost:5173/
```

## Trong VS Code

1. Backend: mở `backend/src/main/java/com/jewelry/store/JewelryStoreApplication.java`, bấm Run phía trên `main`, hoặc F5.
2. Frontend: mở terminal (`Ctrl + \``), rồi `cd frontend` và `npm run dev`.

## Địa chỉ

| Dịch vụ | URL |
| :--- | :--- |
| Website | http://localhost:5173 |
| Swagger | http://localhost:8080/swagger-ui.html |
| Health | http://localhost:8080/api/v1/health |

Đăng nhập quản trị bằng `admin` hoặc `duongtrinhan00@gmail.com`. Khách mẫu là `customer1`. Trên database đang chạy, mật khẩu là `Password123!`.
