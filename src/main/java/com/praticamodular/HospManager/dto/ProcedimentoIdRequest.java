
package com.praticamodular.HospManager.dto;

import jakarta.validation.constraints.NotBlank;

public record ProcedimentoIdRequest(
    @NotBlank(message = "ID do procedimento obrigatório")
    String procedimentoId
) {}
