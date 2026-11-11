package com.praticamodular.HospManager.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InternacaoRequest(
        @NotBlank(message = "Paciente é obrigatório") String pacienteId,
        @NotBlank(message = "Profissional responsável é obrigatório") String profissionalId,
        @NotBlank(message = "Quarto é obrigatório") String quartoId,
        @NotNull(message = "Data de entrada é obrigatória") LocalDate dataEntrada,
        @NotNull(message = "Data prevista de alta é obrigatória") LocalDate dataPrevistaAlta,
        @Size(max = 2000, message = "Observações devem ter no máximo 2000 caracteres") String observacoes) {}
