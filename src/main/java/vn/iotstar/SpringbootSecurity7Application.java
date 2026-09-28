package vn.iotstar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@SpringBootApplication
public class SpringbootSecurity7Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootSecurity7Application.class, args);
	}

	// Tạo dữ liệu mẫu: 2 role ROLE_USER, ROLE_ADMIN và 2 tài khoản user01, admin
	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			@Value("${ADMIN_EMAIL:trungnh@hcmute.edu.vn}") String adminEmail,
			@Value("${ADMIN_PASSWORD:123456}") String adminPassword) {
		return args -> {
			Role userRole = roleRepository.findByName("ROLE_USER")
					.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
			Role adminRole = roleRepository.findByName("ROLE_ADMIN")
					.orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

			if (userRepository.findByUsername("user01").isEmpty()) {
				User user = User.builder()
						.username("user01")
						.email("user01@gmail.com")
						.password(passwordEncoder.encode("123456"))
						.fullName("Nguyễn Hữu Trung")
						.images("/images/user.png")
						.role(userRole)
						.enabled(true)
						.build();
				userRepository.save(user);
			}

			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = User.builder()
						.username("admin")
						.email(adminEmail.toLowerCase())
						.password(passwordEncoder.encode(adminPassword))
						.fullName("System Administrator")
						.role(adminRole)
						.enabled(true)
						.build();
				userRepository.save(admin);
			}
		};
	}
}
