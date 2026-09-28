package vn.iotstar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpDTO {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 6, message = "OTP gồm 6 chữ số")
	private String otp;
}
