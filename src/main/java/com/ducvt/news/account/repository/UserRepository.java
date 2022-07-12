package com.ducvt.news.account.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ducvt.news.account.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameAndStatus(String username, Integer status);

  Optional<User> findByUsernameAndType(String username, String type);

  Optional<User> findByThirdPartyIdAndType(Long thirdPartyId, String type);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Page<User> findAllBy(Pageable pageable);

  Page<User> findByUsernameContains(String username, Pageable pageable);
}
