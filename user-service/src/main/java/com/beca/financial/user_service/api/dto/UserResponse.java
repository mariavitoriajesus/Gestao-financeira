package com.beca.financial.user_service.api.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String cpfCnpj,
        com.beca.financial.user_service.domain.enums.TipoPessoa tipoPessoa, String phone, String endereco,
        com.beca.financial.user_service.domain.enums.StatusUsuario status, java.time.LocalDateTime createdAt) {
}
