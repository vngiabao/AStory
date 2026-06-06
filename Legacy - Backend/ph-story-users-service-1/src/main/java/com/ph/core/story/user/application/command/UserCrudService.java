package com.ph.core.story.user.application.command;

import com.ph.core.story.user.application.command.dto.ResetPasswordRequest;
import com.ph.core.story.user.application.command.dto.UserCreateRequest;
import com.ph.core.story.user.application.command.dto.UserUpdateRequest;
import com.ph.core.story.user.application.mapper.UserMapper;
import com.ph.core.story.user.application.query.dto.UserResponse;
import com.ph.core.story.common.base.BaseCrudServiceImpl;
import com.ph.core.story.common.base.BaseRepositoryPort;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserCrudService extends
        BaseCrudServiceImpl<User, UserCreateRequest, UserUpdateRequest, UserResponse, Long> {

    private final UserRepositoryPort repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected BaseRepositoryPort<User, Long> getRepository() {
        return repository;
    }

    @Override
    protected Function<UserCreateRequest, User> getCreateMapper() {
        return mapper::toEntity;
    }

    @Override
    protected BiConsumer<User, UserUpdateRequest> getUpdateMapper() {
        return mapper::update;
    }

    @Override
    protected Function<User, UserResponse> getResponseMapper() {
        return mapper::toResponse;
    }

    @Transactional
    public void resetPassword(String username, ResetPasswordRequest request) {

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", null));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessValidationException("Old password is incorrect", null);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessValidationException("New password must be different!", null);
        }

        if (!user.isEnabled() || !user.isAccountNonLocked()) {
            throw new BusinessValidationException("Account invalid", null);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        repository.save(user);
    }
}

