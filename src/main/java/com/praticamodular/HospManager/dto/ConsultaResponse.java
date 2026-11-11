package com.praticamodular.HospManager.dto;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.praticamodular.HospManager.model.enums.Especialidade;
import com.praticamodular.HospManager.model.enums.StatusConsulta;
import com.praticamodular.HospManager.model.enums.TipoConsulta;

public record ConsultaResponse(String id, TipoConsulta tipo, String pacienteId, String medicoId,
        LocalDate data, LocalTime horario, String motivo,
        @JsonInclude(JsonInclude.Include.NON_NULL) Especialidade especialidade,
        StatusConsulta status, String observacoes) {}
