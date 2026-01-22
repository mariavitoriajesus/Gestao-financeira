package com.beca.financial.user_service.api.dto;

import com.beca.financial.user_service.domain.enums.TipoPessoa;
import jakarta.validation.constraints.Email;

public record UpdateUserRequest(
        String nome,
        @Email String email,
        String password,
        String cpfCnpj,
        TipoPessoa tipoPessoa,
        String phone,
        String endereco
) {
}
