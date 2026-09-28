# Spring Boot 4 + Spring Security 7 + MapStruct + Thymeleaf

Bài tập 10 – thực hiện 3 ví dụ trong cùng một project, mỗi ví dụ là một commit.

| Commit | Ví dụ | Nội dung |
|---|---|---|
| Ví dụ 1 | HƯỚNG DẪN CHỨC NĂNG LOGIN BẰNG SPRING SECURITY 7 | Login bằng email, thông tin user hiển thị ở `header.html`, layout **không** dùng Layout Dialect |
| Ví dụ 2 | HƯỚNG DẪN CUSTOM LOGIN VỚI SPRING BOOT 4 | Custom login bằng username **hoặc** email, header hiển thị fullname + ảnh, layout dùng Thymeleaf Layout Dialect |

## Công nghệ

Spring Boot 4.1.1 · Spring Security 7 · JDK 26 · Spring Data JPA · SQL Server · Thymeleaf (+ Layout Dialect) · MapStruct 1.6.3 · Lombok

> Mỗi ví dụ thay đổi cấu trúc bảng `users`/`roles`. Khi chuyển từ commit ví dụ này sang ví dụ khác,
> hãy xóa và tạo lại database (hoặc đổi `databaseName` trong `.env`) để Hibernate tạo lại bảng.

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

## Ví dụ 2 – Custom login bằng username hoặc email

- `User` có thêm `username`, `images`; role lưu dạng `ROLE_USER`, `ROLE_ADMIN`.
- `CustomUserDetailsService` gọi `findByUsernameOrEmail(login, login)` nên nhập username hay email đều đăng nhập được.
- `CustomUserDetails` mang thêm `id`, `email`, `fullName`, `images`, `role` để view dùng `${#authentication.principal.fullName}`...
- `SecurityConfig` khai báo `DaoAuthenticationProvider`, `AuthenticationManager`; `/admin/**` chỉ cho ADMIN.
- View dùng Layout Dialect: `layouts/layout.html` + `layout:decorate`, `fragments/header.html` hiển thị avatar, họ tên, username, email, role.
- Dữ liệu mẫu tạo trong `SpringbootSecurity7Application` (CommandLineRunner).

Tài khoản mẫu:

| Username | Email | Mật khẩu | Role |
|---|---|---|---|
| `user01` | `user01@gmail.com` | `123456` | ROLE_USER |
| `admin` | `trungnh@hcmute.edu.vn` | `123456` | ROLE_ADMIN |
