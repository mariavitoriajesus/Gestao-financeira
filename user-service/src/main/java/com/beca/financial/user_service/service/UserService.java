package com.beca.financial.user_service.service;


import com.beca.financial.user_service.api.dto.CreateUserRequest;
import com.beca.financial.user_service.api.dto.UpdateUserRequest;
import com.beca.financial.user_service.api.dto.UserResponse;
import com.beca.financial.user_service.domain.User;
import com.beca.financial.user_service.domain.enums.StatusUsuario;
import com.beca.financial.user_service.domain.enums.TipoPessoa;
import com.beca.financial.user_service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (request.cpfCnpj() != null && userRepository.existsByCpfCnpj(request.cpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCpfCnpj(request.cpfCnpj());
        user.setTipoPessoa(request.tipoPessoa() != null ? request.tipoPessoa() : TipoPessoa.FISICA);
        user.setPhone(request.phone());
        user.setEndereco(request.endereco());
        user.setStatus(StatusUsuario.ATIVO);
        user.setCreatedAt(LocalDateTime.now());

        User salved = userRepository.save(user);

        return toResponse(salved);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id){
        User user = userRepository.getReferenceById(id);
        return toResponse(user);
    }

    public Page<UserResponse> findAll(Boolean onlyActive, Pageable pageable) {
        Page<User> page = (onlyActive != null && onlyActive) ? userRepository.findAllByStatus(StatusUsuario.ATIVO, pageable)
                : userRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = getOrThrow(id);
        if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())){
                throw new IllegalArgumentException("Email already in use");
            }
            user.setCpfCnpj(request.cpfCnpj());
        }

        if (request.cpfCnpj() != null && !request.cpfCnpj().equals(user.getCpfCnpj())) {
            if (userRepository.existsByCpfCnpj(request.cpfCnpj())) {
                throw new IllegalArgumentException("CPF/CNPJ already in use");
            }
            user.setCpfCnpj(request.cpfCnpj());
        }

        if (request.nome() != null) user.setName(request.nome());
        if (request.password() != null) user.setPassword(request.password()); // hash depois
        if (request.tipoPessoa() != null) user.setTipoPessoa(request.tipoPessoa());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.endereco() != null) user.setEndereco(request.endereco());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional
    public void inactive(UUID id){
        User user = getOrThrow(id);
        user.getStatus(StatusUsuario.INATIVO);
        userRepository.save(user);
    }

    private User getOrThrow(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
    }

    private UserResponse toResponse(User u){
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getCpfCnpj(),
                u.getTipoPessoa(),
                u.getPhone(),
                u.getEndereco(),
                u.getStatus(StatusUsuario.ATIVO),
                u.getCreatedAt()
        );
    }

}
