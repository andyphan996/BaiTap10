package vn.iotstar.controller;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.ForgotPasswordDTO;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.dto.VerifyOtpDTO;
import vn.iotstar.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private static final String MAIL_ERROR = "Không gửi được email OTP. Vui lòng kiểm tra cấu hình SMTP.";

	private final AuthService authService;

	// Phương thức POST /login do Spring Security tự sinh ra.
	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	// Nhận mọi method vì Spring Security forward cả request POST bị từ chối tới đây
	@RequestMapping("/access-denied")
	public String accessDenied() {
		return "auth/access-denied";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("registerDTO", new RegisterDTO());
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute RegisterDTO dto, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors())
			return "auth/register";
		try {
			authService.register(dto);
			redirect.addFlashAttribute("success", "OTP đã được gửi đến email.");
			redirect.addAttribute("email", dto.getEmail());
			return "redirect:/verify-otp";
		} catch (IllegalArgumentException e) {
			result.reject("register.error", e.getMessage());
		} catch (MailException e) {
			result.reject("register.error", MAIL_ERROR);
		}
		return "auth/register";
	}

	@GetMapping("/verify-otp")
	public String verifyPage(@RequestParam(required = false) String email, Model model) {
		VerifyOtpDTO dto = new VerifyOtpDTO();
		dto.setEmail(email);
		model.addAttribute("verifyOtpDTO", dto);
		return "auth/verify-otp";
	}

	@PostMapping("/verify-otp")
	public String verify(@Valid @ModelAttribute VerifyOtpDTO dto, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors())
			return "auth/verify-otp";
		if (!authService.verifyRegister(dto.getEmail(), dto.getOtp())) {
			result.reject("otp.error", "OTP không hợp lệ, hết hạn hoặc đã quá số lần thử.");
			return "auth/verify-otp";
		}
		redirect.addFlashAttribute("success", "Xác nhận thành công. Hãy đăng nhập.");
		return "redirect:/login";
	}

	@PostMapping("/resend-register-otp")
	public String resend(@RequestParam String email, RedirectAttributes redirect) {
		try {
			authService.resendRegisterOtp(email);
			redirect.addFlashAttribute("success", "Đã gửi lại OTP.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		} catch (MailException e) {
			redirect.addFlashAttribute("error", MAIL_ERROR);
		}
		redirect.addAttribute("email", email);
		return "redirect:/verify-otp";
	}

	@GetMapping("/forgot-password")
	public String forgot(Model model) {
		model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
		return "auth/forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgot(@Valid @ModelAttribute ForgotPasswordDTO dto, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors())
			return "auth/forgot-password";
		try {
			authService.forgotPassword(dto.getEmail());
			redirect.addFlashAttribute("email", dto.getEmail());
			redirect.addFlashAttribute("success", "OTP đã được gửi.");
			return "redirect:/reset-password";
		} catch (IllegalArgumentException e) {
			result.reject("forgot.error", e.getMessage());
		} catch (MailException e) {
			result.reject("forgot.error", MAIL_ERROR);
		}
		return "auth/forgot-password";
	}

	@GetMapping("/reset-password")
	public String reset(Model model) {
		ResetPasswordDTO dto = new ResetPasswordDTO();
		Object email = model.asMap().get("email");
		if (email != null)
			dto.setEmail(email.toString());
		model.addAttribute("resetPasswordDTO", dto);
		return "auth/reset-password";
	}

	@PostMapping("/reset-password")
	public String reset(@Valid @ModelAttribute ResetPasswordDTO dto, BindingResult result,
			@RequestParam String otp, RedirectAttributes redirect) {
		if (dto.getPassword() != null && !dto.getPassword().equals(dto.getConfirmPassword())) {
			result.reject("password.error", "Mật khẩu xác nhận không đúng.");
		}
		if (result.hasErrors())
			return "auth/reset-password";

		if (!authService.verifyResetOtp(dto.getEmail(), otp)) {
			result.reject("otp.error", "OTP không hợp lệ hoặc đã hết hạn.");
			return "auth/reset-password";
		}

		authService.resetPassword(dto.getEmail(), dto.getPassword());
		redirect.addFlashAttribute("success", "Đổi mật khẩu thành công.");
		return "redirect:/login";
	}
}
