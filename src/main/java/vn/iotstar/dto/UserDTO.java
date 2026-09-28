package vn.iotstar.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

	private Long id;

	@Email
	@NotBlank
	private String email;

	@NotBlank
	private String fullName;

	@NotNull
	private Long roleId;

	private String roleName;
	private boolean enabled;
	private LocalDateTime createdAt;
}
