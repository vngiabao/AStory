package com.ph.core.story.user.infrastructure.persistence;

import com.ph.core.story.common.exception.ResourceNotFoundException;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    @Override
    public User save(User entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).filter(user -> !user.isDeleted());
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll((root, query, cb) -> cb.isFalse(root.get("deleted")), pageable);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User getReferenceIfExists(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }

        return repository.getReferenceById(id);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }
}
