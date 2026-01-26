package com.beca.financial.user_service.api.dto;

public record ImportUserResponse(
        int totalRows,
        int imported,
        int failed,
        java.util.List<RowError> errors
) {
    public record RowError(int row, String message) { }
}
