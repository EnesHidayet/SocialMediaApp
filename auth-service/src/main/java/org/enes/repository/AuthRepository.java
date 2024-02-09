package org.enes.repository;

import org.enes.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByUsernameAndPassword(String username,String password);
    Optional<Auth> findByActivationCode(String activationCode);
}
