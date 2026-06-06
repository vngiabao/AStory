package com.ph.core.story.user.application.command.dto;

import java.time.Instant;
import com.ph.core.story.common.validation.StrongPassword;
import com.ph.core.story.user.domain.model.Gender;
import com.ph.core.story.user.domain.model.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(

        // Không được để chống
        @NotBlank(message = "Username is required")
        // Độ dài chuỗi từ 8 đến 50 ký tự
        @Size(min = 8, max = 50, message = "Username must be between 8 and 50 characters")
        // Chỉ bao gồm chữ cái (hoa/thường), số, dấu ".", dấu "_" và dấu "-"
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username can only contain letters, numbers, dot, underscore, hyphen")
        // Tên người dùng
        String username,

        // Không được để chống
        @NotBlank(message = "Email is required")
        // Phải là cấu trúc email
        @Email(message = "Invalid email format")
        // Email người dùng
        String email,

        // Phải là mật khẩu mạnh
        @StrongPassword
        // Mật khẩu người dùng
        String password,

        // Không được null
        @NotNull UserType userType,

        // ===== PROFILE INFO =====
        // Tên đầy đủ của người dùng
        @NotBlank(message = "Fullname is required") @Size(max = 150) String fullname,
        // Số điện thoại của người dùng
        @Size(max = 50) String phoneNumber,
        // Địa chỉ của người dùng
        @Size(max = 255) String address,
        // Giới tính của người dùng
        @NotNull(message = "Gender is required") Gender gender,
        // Ngày sinh của người dùng
        Instant dateOfBirth

) {
}
