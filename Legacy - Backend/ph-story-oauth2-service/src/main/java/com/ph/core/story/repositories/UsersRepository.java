package com.ph.core.story.repositories;

import java.util.Optional;

import com.ph.core.story.models.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<Users> findByUsernameIgnoreCase(String username);
}
