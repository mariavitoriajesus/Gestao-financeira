package com.beca.financial.user_service.repository;

import com.beca.financial.user_service.domain.User;
import com.beca.financial.user_service.domain.enums.StatusUsuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findAllByStatus(StatusUsuario status, Pageable pageable);

    boolean existsByCpfCnpj(@NotBlank String cpfCnpj);
}
