package com.beca.financial.user_service.api.dto;

public record LoginResponse(
        String toke,
        String tokenType
) {
}
