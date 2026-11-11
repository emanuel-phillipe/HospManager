package com.praticamodular.HospManager.dto;

import com.praticamodular.HospManager.model.enums.SituacaoQuarto;
import com.praticamodular.HospManager.model.enums.TipoQuarto;

public record QuartoResponse(
        String id,
        int numero,
        int andar,
        int capacidadeMaxima,
        int pacientesInternados,
        int vagasDisponiveis,
        TipoQuarto tipo,
        SituacaoQuarto situacao) {}
