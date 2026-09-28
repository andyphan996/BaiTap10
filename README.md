# Spring Boot 4 + Spring Security 7 + MapStruct + Thymeleaf

Bài tập 10 – thực hiện 3 ví dụ trong cùng một project, mỗi ví dụ là một commit.

| Commit | Ví dụ | Nội dung |
|---|---|---|
| Ví dụ 1 | HƯỚNG DẪN CHỨC NĂNG LOGIN BẰNG SPRING SECURITY 7 | Login bằng email, thông tin user hiển thị ở `header.html`, layout **không** dùng Layout Dialect |

## Công nghệ

Spring Boot 4.1.1 · Spring Security 7 · JDK 26 · Spring Data JPA · SQL Server · Thymeleaf · MapStruct 1.6.3 · Lombok

## Cách chạy

1. Tạo database trong SQL Server (ví dụ `webst4`).
2. Copy `.env.example` thành `.env` (ngang cấp với `pom.xml`) và sửa thông số DB/SMTP/Cloudinary cho phù hợp với máy mình.
   File `.env` đã nằm trong `.gitignore` nên không bị đẩy lên GitHub.
3. Chạy:

   ```bash
   mvn spring-boot:run
   ```

4. Mở http://localhost:8088/login

## Ví dụ 1 – Login bằng email

- `SecurityConfig`: form login tại `/login`, tham số `email`/`password`, đăng nhập xong chuyển tới `/dashboard` (chỉ ADMIN).
- `CustomUserDetailsService`: tìm user theo email (`JOIN FETCH role`).
- `DataInitializer`: tạo role `USER`, `ADMIN` và tài khoản admin mặc định.
- View: `layouts/layout.html` (fragment `head`), `fragments/header.html` (dùng `sec:authorize`, `sec:authentication`).

Tài khoản mẫu: `trungnh@hcmute.edu.vn` / `123456` (đổi bằng biến `ADMIN_EMAIL`, `ADMIN_PASSWORD`).
