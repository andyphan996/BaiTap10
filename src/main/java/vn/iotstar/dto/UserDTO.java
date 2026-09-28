package vn.iotstar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

	private Long id;

	@NotBlank(message = "Username không được để trống")
	@Size(max = 50, message = "Username tối đa 50 ký tự")
	private String username;

	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;

	@NotBlank(message = "Họ tên không được để trống")
	private String fullName;

	private String images;
	private boolean enabled;
	private String roleName;
	private long productCount;
}
