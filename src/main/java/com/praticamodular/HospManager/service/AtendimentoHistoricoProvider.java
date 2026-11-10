package com.praticamodular.HospManager.service;

import java.util.List;

/**
 * Porta de integração do histórico com o módulo de atendimento.
 * Deve ser substituido por um service, por outros integrantes.
 */
public interface AtendimentoHistoricoProvider {
    List<?> listarPorPaciente(String pacienteId);
}
