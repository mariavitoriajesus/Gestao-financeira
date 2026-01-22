package com.beca.financial.user_service.api.dto;

import com.beca.financial.user_service.domain.enums.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String cpfCnpj,
        @NotNull TipoPessoa tipoPessoa,
        String phone,
        String endereco
) {
}
