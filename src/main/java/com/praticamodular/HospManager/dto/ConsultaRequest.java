package com.praticamodular.HospManager.dto;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.praticamodular.HospManager.model.enums.Especialidade;
import com.praticamodular.HospManager.model.enums.TipoConsulta;


public record ConsultaRequest(
        @NotNull(message = "Tipo da consulta obrigatório (ROTINA ou ESPECIALIZADA)") TipoConsulta tipo,
        @NotBlank(message = "Paciente obrigatório") String pacienteId,
        @NotBlank(message = "Médico obrigatório") String medicoId,
        @NotNull(message = "Data obrigatória") LocalDate data,
        @NotNull(message = "Horário obrigatório") LocalTime horario,
        @NotBlank(message = "Motivo obrigatório") String motivo,
        Especialidade especialidade) {}
