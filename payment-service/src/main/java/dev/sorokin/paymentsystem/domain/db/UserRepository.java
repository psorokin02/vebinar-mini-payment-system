package dev.sorokin.paymentsystem.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

}
