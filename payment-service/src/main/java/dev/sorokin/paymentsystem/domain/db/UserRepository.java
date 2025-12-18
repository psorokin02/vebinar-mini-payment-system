package dev.sorokin.paymentsystem.domain.db;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(value = "payments")
    @Query("select u from UserEntity u")
    List<UserEntity> findAllWithUsers();

    boolean existsByEmailIgnoreCase(String email);

}
