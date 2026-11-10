package com.praticamodular.HospManager.dto;

import java.time.LocalDateTime;
import com.praticamodular.HospManager.model.enums.TipoRegistro;
public record RegistroAtendimentoResponse(String id, LocalDateTime dataHora, TipoRegistro tipo,
        String descricao, String atendimentoId, String internacaoId) {}
