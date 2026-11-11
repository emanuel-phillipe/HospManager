package com.praticamodular.HospManager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.praticamodular.HospManager.model.enums.TipoQuarto;

public record QuartoRequest(
        @Min(value = 1, message = "Número do quarto deve ser maior que zero") int numero,
        @Min(value = 0, message = "Andar não pode ser negativo") int andar,
        @Min(value = 1, message = "Capacidade máxima deve ser pelo menos 1") int capacidadeMaxima,
        @NotNull(message = "Tipo do quarto é obrigatório") TipoQuarto tipo) {}
