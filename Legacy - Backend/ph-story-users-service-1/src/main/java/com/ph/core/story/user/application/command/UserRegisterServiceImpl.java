package com.ph.core.story.user.application.command;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import com.ph.core.story.user.application.command.dto.UserRegisterRequest;
import com.ph.core.story.user.domain.model.Profiles;
import com.ph.core.story.user.domain.model.Role;
import com.ph.core.story.user.domain.model.RoleConstants;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.ProfileRepositoryPort;
import com.ph.core.story.user.domain.repository.RoleRepositoryPort;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final ProfileRepositoryPort profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void register(UserRegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessValidationException("Username exists!", request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessValidationException("Email exists!", request.email());
        }

        Role userRole = roleRepository.findByName(RoleConstants.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found", null));

        // ===== Create User =====
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setUserType(request.userType());
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setTwoFactorEnabled(false);
        user.setAccountNonLocked(true);
        user.setRoles(Set.of(userRole));

        userRepository.save(user);

        // ===== CREATE PROFILE =====
        Profiles profile = new Profiles();
        profile.setUser(user);
        profile.setFullname(request.fullname());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setAddress(request.address());
        profile.setGender(request.gender());
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setIsDeceased(false);

        profileRepository.save(profile);
    }
}
