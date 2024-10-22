package com.globex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.globex.model.UserPreferences;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    Optional<UserPreferences> findByUserId(Long userId);
}
