package com.praticamodular.HospManager.dto;
import jakarta.validation.constraints.NotBlank;

public record ConclusaoConsultaRequest(
        @NotBlank(message = "Observações obrigatórias para concluir a consulta") String observacoes) {}
