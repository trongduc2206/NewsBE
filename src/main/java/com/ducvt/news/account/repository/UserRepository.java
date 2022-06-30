package com.ducvt.news.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ducvt.news.account.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameAndStatus(String username, Integer status);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}