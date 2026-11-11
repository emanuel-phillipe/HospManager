package com.praticamodular.HospManager.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

public record AltaRequest(
        @NotNull(message = "Data efetiva da alta é obrigatória") LocalDate dataEfetivaAlta) {}
