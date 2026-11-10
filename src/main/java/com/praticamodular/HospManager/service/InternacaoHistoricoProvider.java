package com.praticamodular.HospManager.service;

import java.util.List;

/**
 * Porta de integração do histórico com o módulo de internacao.
 * Deve ser substituido por um service, por outros integrantes.
 */
public interface InternacaoHistoricoProvider {
    List<?> listarPorPaciente(String pacienteId);
}
