package com.ph.core.story.user.application.query.dto;

//public record UserResponse(
//        Long id,
//        String username,
//        String email,
//        boolean active
//        ) {}

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    @NotBlank
    private Long id;

    @NotBlank
    private String username;

    @Email
    private String email;
}

