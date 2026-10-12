@echo off
chcp 65001 > nul
title Khởi chạy Dự án Trang Sức AURA

echo ================================================================
echo       HỆ THỐNG WEBSITE TRANG SỨC CAO CẤP - AURA JEWELRY
echo ================================================================
echo.

:: 1. Kiểm tra file JAR Backend
if not exist "%~dp0backend\target\jewelry-store-backend-1.0.0-SNAPSHOT.jar" (
    echo [1/3] Chưa tìm thấy file JAR Backend. Đang tiến hành build...
    cd /d "%~dp0backend"
    call mvn package -DskipTests
    if %errorlevel% neq 0 (
        echo [LỖI] Build Backend thất bại! Vui lòng kiểm tra lại Maven / Java.
        pause
        exit /b 1
    )
) else (
    echo [1/3] File JAR Backend đã sẵn sàng!
)

:: 2. Khởi chạy Backend trong cửa sổ riêng
echo [2/3] Đang khởi động Backend (Spring Boot - Port 8080)...
start "AURA Backend (Port 8080)" cmd /k "cd /d "%~dp0backend" && java -jar target/jewelry-store-backend-1.0.0-SNAPSHOT.jar"

:: 3. Khởi chạy Frontend trong cửa sổ riêng
echo [3/3] Đang khởi động Frontend (React Vite - Port 5173)...
start "AURA Frontend (Port 5173)" cmd /k "cd /d "%~dp0frontend" && npm run dev"

echo.
echo ================================================================
echo   KHỞI CHẠY THÀNH CÔNG!
echo.
echo   - Giao diện Web Khách Hàng : http://localhost:5173
echo   - Backend REST API         : http://localhost:8080
echo   - Swagger API Docs         : http://localhost:8080/swagger-ui.html
echo   - Kiểm tra Health Backend  : http://localhost:8080/api/v1/health
echo   - App Android Native APK   : android\app\build\outputs\apk\debug\app-debug.apk
echo ================================================================
echo.
pause
