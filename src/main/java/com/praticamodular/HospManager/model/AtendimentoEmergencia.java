package com.praticamodular.HospManager.model;

import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.exception.TransicaoInvalidaException;
import com.praticamodular.HospManager.model.enums.PrioridadeEmergencia;
import com.praticamodular.HospManager.model.enums.StatusEmergencia;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Atendimento de emergência. Data/horário herdados de Atendimento representam a CHEGADA do paciente.
 * Ordem natural (compareTo): maior prioridade primeiro; empate -> quem chegou antes; empate -> menor id.
 */
public class AtendimentoEmergencia extends Atendimento implements Comparable<AtendimentoEmergencia> {

    private final PrioridadeEmergencia prioridade;
    private StatusEmergencia status = StatusEmergencia.AGUARDANDO_AVALIACAO;
    private ProfissionalSaude medico; // 0..1: só definido ao iniciar o atendimento

    public AtendimentoEmergencia(String id, Paciente paciente, PrioridadeEmergencia prioridade,
                                 LocalDateTime chegada, String observacoes) {
        this.id = id;
        this.pacienteId = paciente == null ? null : paciente.getId();
        this.data = exigirChegada(chegada).toLocalDate();
        this.horario = chegada.toLocalTime();
        this.observacoes = observacoes;
        this.prioridade = prioridade;
        validarCondicoes();
    }

    public AtendimentoEmergencia(String id, Paciente paciente, PrioridadeEmergencia prioridade, String observacoes) {
        this(id, paciente, prioridade, LocalDateTime.now(), observacoes);
    }

    // ---------- regras de negócio ----------

    /**
     * Passa de AGUARDANDO_AVALIACAO para EM_ATENDIMENTO.
     * Exige profissional responsável e que ele não tenha outro atendimento no mesmo horário.
     */
    public void iniciarAtendimento(ProfissionalSaude medico) {
        validarTransicao(StatusEmergencia.EM_ATENDIMENTO);
        if (medico == null) {
            throw new RegraNegocioException("É necessário um profissional responsável para iniciar o atendimento");
        }
        if (medico.possuiConflito(LocalDate.now(), LocalTime.now())) {
            throw new RegraNegocioException("Profissional já possui outro atendimento neste horário");
        }
        this.status = StatusEmergencia.EM_ATENDIMENTO;
        this.medico = medico;
    }

    public void concluir(String obs) {
        validarTransicao(StatusEmergencia.CONCLUIDO);
        if (obs == null || obs.isBlank()) {
            throw new RegraNegocioException("Observações finais são obrigatórias para concluir o atendimento");
        }
        this.status = StatusEmergencia.CONCLUIDO;
        this.observacoes = (observacoes == null || observacoes.isBlank()) ? obs : observacoes + "\n" + obs;
    }

    public boolean estaAguardando() {
        return status == StatusEmergencia.AGUARDANDO_AVALIACAO;
    }

    @Override
    public void validarCondicoes() {
        if (pacienteId == null || pacienteId.isBlank()) throw new RegraNegocioException("Todo atendimento exige um paciente");
        if (prioridade == null) throw new RegraNegocioException("Prioridade é obrigatória");
        if (data == null || horario == null) throw new RegraNegocioException("Data/horário de chegada são obrigatórios");
        if (status != StatusEmergencia.AGUARDANDO_AVALIACAO && medico == null) {
            throw new RegraNegocioException("Atendimento em andamento/concluído exige profissional responsável");
        }
    }

    @Override
    public ProfissionalSaude getResponsavel() {
        return medico; // null enquanto aguarda
    }

    private void validarTransicao(StatusEmergencia novo) {
        if (!status.podeTransicionarPara(novo)) {
            throw new TransicaoInvalidaException("Transição inválida de emergência: " + status + " -> " + novo);
        }
    }

    private static LocalDateTime exigirChegada(LocalDateTime chegada) {
        if (chegada == null) throw new RegraNegocioException("Data/horário de chegada são obrigatórios");
        return chegada;
    }

    // ---------- ordenação da fila ----------

    public LocalDateTime getChegada() {
        return LocalDateTime.of(data, horario);
    }

    @Override
    public int compareTo(AtendimentoEmergencia o) {
        int c = Integer.compare(o.prioridade.getNivel(), this.prioridade.getNivel()); // maior nível primeiro
        if (c != 0) return c;
        c = this.getChegada().compareTo(o.getChegada());                               // FIFO dentro da prioridade
        if (c != 0) return c;
        return this.id.compareTo(o.id);
    }

    public PrioridadeEmergencia getPrioridade() { return prioridade; }
    public StatusEmergencia getStatus() { return status; }
}
