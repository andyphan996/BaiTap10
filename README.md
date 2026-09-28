# Spring Boot 4 + Spring Security 7 + MapStruct + Thymeleaf

Bài tập 10 – thực hiện 3 ví dụ trong cùng một project, mỗi ví dụ là một commit.

| Commit | Ví dụ | Nội dung |
|---|---|---|
| Ví dụ 1 | HƯỚNG DẪN CHỨC NĂNG LOGIN BẰNG SPRING SECURITY 7 | Login bằng email, thông tin user hiển thị ở `header.html`, layout **không** dùng Layout Dialect |
| Ví dụ 2 | HƯỚNG DẪN CUSTOM LOGIN VỚI SPRING BOOT 4 | Custom login bằng username **hoặc** email, header hiển thị fullname + ảnh, layout dùng Thymeleaf Layout Dialect |
| Ví dụ 3 | HƯỚNG DẪN SPRING BOOT-SECURITY7-MAPSTRUCT | Register + OTP qua mail, login lưu session, quên mật khẩu bằng OTP, CRUD + tìm kiếm + phân trang User/Product, upload ảnh Cloudinary |

Xem code từng ví dụ: `git checkout <mã commit>` (dùng `git log --oneline` để lấy mã commit).

## Công nghệ

Spring Boot 4.1.1 · Spring Security 7 · JDK 26 · Spring Data JPA / Hibernate · SQL Server · Thymeleaf (+ Layout Dialect) ·
MapStruct 1.6.3 · Lombok · Spring Mail · Cloudinary · Jakarta Validation · BCrypt

> Mỗi ví dụ thay đổi cấu trúc bảng `users`/`roles`. Khi chuyển từ commit ví dụ này sang ví dụ khác,
> hãy xóa và tạo lại database (hoặc đổi `databaseName` trong `.env`) để Hibernate tạo lại bảng.

## Cách chạy

1. Khởi động SQL Server và tạo database (ví dụ `webst4`).
2. Copy `.env.example` thành `.env` (ngang cấp với `pom.xml`) và sửa thông số cho phù hợp với máy mình:
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
   - `MAIL_USERNAME`, `MAIL_PASSWORD` (Gmail dùng **App Password**) – cần cho gửi OTP
   - `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` – cần cho upload ảnh sản phẩm

   File `.env` đã nằm trong `.gitignore` nên không bị đẩy lên GitHub.
3. Chạy:

   ```bash
   mvn spring-boot:run
   ```

4. Mở http://localhost:8088

Khi khởi động, `SpringbootSecurity7Application` tự tạo role `ROLE_USER`, `ROLE_ADMIN` và 2 tài khoản mẫu:

| Username | Email | Mật khẩu | Role |
|---|---|---|---|
| `user01` | `user01@gmail.com` | `123456` | ROLE_USER |
| `admin` | `trungnh@hcmute.edu.vn` (biến `ADMIN_EMAIL`) | `123456` (biến `ADMIN_PASSWORD`) | ROLE_ADMIN |

## Ví dụ 1 – Login bằng email

- `SecurityConfig`: form login tại `/login`, tham số `email`/`password`, đăng nhập xong chuyển tới `/dashboard` (chỉ ADMIN).
- `CustomUserDetailsService`: tìm user theo email (`JOIN FETCH role`).
- `DataInitializer`: tạo role `USER`, `ADMIN` và tài khoản admin mặc định.
- View: `layouts/layout.html` (fragment `head`), `fragments/header.html` (dùng `sec:authorize`, `sec:authentication`).

## Ví dụ 2 – Custom login bằng username hoặc email

- `User` có thêm `username`, `images`; role lưu dạng `ROLE_USER`, `ROLE_ADMIN`.
- `CustomUserDetailsService` gọi `findByUsernameOrEmail(login, login)` nên nhập username hay email đều đăng nhập được.
- `CustomUserDetails` mang thêm `id`, `email`, `fullName`, `images`, `role` để view dùng `${#authentication.principal.fullName}`...
- `SecurityConfig` khai báo `DaoAuthenticationProvider`, `AuthenticationManager`; `/admin/**` chỉ cho ADMIN.
- View dùng Layout Dialect: `layouts/layout.html` + `layout:decorate`, `fragments/header.html` hiển thị avatar, họ tên, username, email, role.

## Ví dụ 3 – Register OTP, Forgot password, CRUD User/Product, Cloudinary

**Authentication**

| URL | Chức năng |
|---|---|
| `GET/POST /register` | Đăng ký, tài khoản tạo ra ở trạng thái `enabled = false` và gửi OTP qua email |
| `GET/POST /verify-otp` | Nhập OTP 6 số để kích hoạt tài khoản |
| `POST /resend-register-otp` | Gửi lại OTP đăng ký |
| `POST /login`, `POST /logout` | Spring Security form login (username hoặc email), lưu trong session |
| `GET/POST /forgot-password` | Gửi OTP đặt lại mật khẩu |
| `GET/POST /reset-password` | Nhập OTP + mật khẩu mới |

- OTP lưu trong bảng `otp_tokens` dạng **băm BCrypt**, hết hạn sau 5 phút, tối đa 5 lần nhập sai, dùng một lần.
- Session: mỗi tài khoản chỉ giữ 1 session (`maximumSessions(1)`); đăng nhập ở nơi khác sẽ làm session cũ hết hạn.

**User (chỉ ADMIN – `/users`)**: CRUD, tìm kiếm theo username/email/họ tên, phân trang, phân quyền USER/ADMIN,
đếm tổng số user, đếm số product của từng user. User do admin tạo có mật khẩu mặc định `123456`.

**Product (`/products`)**: CRUD, tìm kiếm theo tên/mô tả, phân trang, upload ảnh lên Cloudinary (thư mục `shop/products`).
Product thuộc user đang đăng nhập; chỉ chủ sở hữu hoặc ADMIN mới được sửa/xóa. Xóa/đổi ảnh sẽ xóa ảnh cũ trên Cloudinary.

**Dashboard (`/`)**: tổng số user, tổng số product, số product của user đang đăng nhập.

**Cấu trúc chính**

```
vn.iotstar
├── config        SecurityConfig, CloudinaryConfig, EncodingConfig
├── controller    AuthController, HomeController, UserController, ProductController, GlobalExceptionHandler
├── dto           UserDTO, ProductDTO, RegisterDTO, LoginDTO, VerifyOtpDTO, ForgotPasswordDTO, ResetPasswordDTO
├── entity        User, Role, Product, OtpToken
├── mapper        UserMapper, ProductMapper (MapStruct: Entity <-> DTO)
├── repository    UserRepository, RoleRepository, ProductRepository, OtpTokenRepository
├── security      CustomUserDetails, CustomUserDetailsService
└── service       UserService, ProductService, AuthService, OtpService, EmailService, CloudinaryService (+ impl)
```
