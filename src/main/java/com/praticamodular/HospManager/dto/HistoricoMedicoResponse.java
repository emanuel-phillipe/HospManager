package com.praticamodular.HospManager.dto;

import java.util.List;
public record HistoricoMedicoResponse(String id, PacienteResumoResponse paciente,
        List<?> consultas, List<?> internacoes, List<RegistroAtendimentoResponse> registros) {}
