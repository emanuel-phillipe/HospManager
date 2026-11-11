package com.praticamodular.HospManager.service;

import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.enums.StatusEmergencia;
import com.praticamodular.HospManager.model.AtendimentoEmergencia;
import com.praticamodular.HospManager.model.ProfissionalSaude;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Objects;

/**
 * Fila de emergência organizada por prioridade e, dentro dela, por ordem de chegada.
 * Mantém todos os atendimentos e os expõe por situação:
 * aguardando (AGUARDANDO_AVALIACAO), iniciados (EM_ATENDIMENTO) e concluídos (CONCLUIDO).
 */
public class FilaEmergencia {

    private final List<AtendimentoEmergencia> atendimentos = new ArrayList<>();

    public void registrar(AtendimentoEmergencia atendimento) {
        if (atendimento == null) throw new RegraNegocioException("Atendimento é obrigatório");
        if (!atendimento.estaAguardando()) {
            throw new RegraNegocioException("Só é possível entrar na fila um atendimento aguardando avaliação");
        }
        if (buscarPorId(atendimento.getId()).isPresent()) {
            throw new RegraNegocioException("Já existe atendimento com id " + atendimento.getId());
        }
        atendimentos.add(atendimento);
    }

    /** Próximo a ser chamado, sem retirá-lo da fila. */
    public Optional<AtendimentoEmergencia> proximo() {
        return aguardando().stream().findFirst();
    }

    /**
     * Chama o próximo da fila: inicia o atendimento com o profissional informado.
     * O profissional não pode estar em outro atendimento de emergência ainda em andamento.
     */
    public AtendimentoEmergencia chamarProximo(ProfissionalSaude medico) {
        AtendimentoEmergencia proximo = proximo()
                .orElseThrow(() -> new NoSuchElementException("Não há pacientes aguardando"));
        boolean ocupado = emAtendimento().stream().anyMatch(a -> (a.getResponsavel() == medico || (medico != null && medico.getId() != null
                && medico.getId().equals(a.getResponsavelId()))));
        if (ocupado) {
            throw new RegraNegocioException("Profissional já está em outro atendimento de emergência");
        }
        proximo.iniciarAtendimento(medico);
        return proximo;
    }

    public void concluir(String id, String obs) {
        buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Atendimento não encontrado: " + id))
                .concluir(obs);
    }

    public List<AtendimentoEmergencia> aguardando()   { return filtrar(StatusEmergencia.AGUARDANDO_AVALIACAO); }
    public List<AtendimentoEmergencia> emAtendimento() { return filtrar(StatusEmergencia.EM_ATENDIMENTO); }
    public List<AtendimentoEmergencia> concluidos()   { return filtrar(StatusEmergencia.CONCLUIDO); }

    /** Posição (1 = próximo) na fila de espera, ou -1 se não está aguardando. */
    public int posicao(String id) {
        List<AtendimentoEmergencia> fila = aguardando();
        for (int i = 0; i < fila.size(); i++) {
            if (Objects.equals(fila.get(i).getId(), id)) return i + 1;
        }
        return -1;
    }

    public Optional<AtendimentoEmergencia> buscarPorId(String id) {
        return atendimentos.stream().filter(a -> Objects.equals(a.getId(), id)).findFirst();
    }

    private List<AtendimentoEmergencia> filtrar(StatusEmergencia status) {
        return atendimentos.stream()
                .filter(a -> a.getStatus() == status)
                .sorted() // compareTo: prioridade desc, chegada asc
                .toList();
    }
}
