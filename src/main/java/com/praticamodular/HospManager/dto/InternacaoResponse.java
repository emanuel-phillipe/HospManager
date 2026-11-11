package com.praticamodular.HospManager.dto;

import java.time.LocalDate;
import com.praticamodular.HospManager.model.enums.StatusInternacao;

public record InternacaoResponse(
        String id,
        String pacienteId,
        String pacienteNome,
        String profissionalId,
        String quartoId,
        Integer numeroQuarto,
        LocalDate dataEntrada,
        LocalDate dataPrevistaAlta,
        LocalDate dataEfetivaAlta,
        String observacoes,
        StatusInternacao status) {}
