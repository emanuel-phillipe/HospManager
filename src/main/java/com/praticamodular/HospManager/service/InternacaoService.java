package com.praticamodular.HospManager.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.praticamodular.HospManager.dto.InternacaoRequest;
import com.praticamodular.HospManager.dto.InternacaoResponse;
import com.praticamodular.HospManager.exception.ConflitoException;
import com.praticamodular.HospManager.exception.RecursoNaoEncontradoException;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.Internacao;
import com.praticamodular.HospManager.model.Paciente;
import com.praticamodular.HospManager.model.Quarto;
import com.praticamodular.HospManager.model.enums.StatusInternacao;
import com.praticamodular.HospManager.repository.InternacaoRepository;
import com.praticamodular.HospManager.repository.PacienteRepository;
import com.praticamodular.HospManager.repository.QuartoRepository;
import com.praticamodular.HospManager.dto.AltaRequest;

@Service
public class InternacaoService implements InternacaoHistoricoProvider {
    private final com.praticamodular.HospManager.repository.ProfissionalRepository profissionais;
    private final InternacaoRepository internacoes;
    private final PacienteRepository pacientes;
    private final QuartoRepository quartos;
    private final QuartoService quartoService;

    public InternacaoService(InternacaoRepository internacoes, PacienteRepository pacientes,
            QuartoRepository quartos, QuartoService quartoService,
            com.praticamodular.HospManager.repository.ProfissionalRepository profissionais) {
        this.profissionais = profissionais;
        this.internacoes = internacoes;
        this.pacientes = pacientes;
        this.quartos = quartos;
        this.quartoService = quartoService;
    }

    public synchronized InternacaoResponse criar(InternacaoRequest dados) {
        validarDados(dados);
        Paciente paciente = encontrarPaciente(dados.pacienteId());
        if (!paciente.isAtivo()) {
            throw new RegraNegocioException("Não é possível internar um paciente inativo");
        }
        if (internacoes.existsByPacienteIdAndStatus(dados.pacienteId(), StatusInternacao.ATIVA)) {
            throw new ConflitoException("O paciente já possui uma internação ativa");
        }
        Quarto quarto = quartoService.encontrar(dados.quartoId());
        validarVaga(quarto);

        Internacao internacao = new Internacao();
        internacao.setId(UUID.randomUUID().toString());
        aplicarDados(internacao, dados);
        internacao.setStatus(StatusInternacao.ATIVA);
        Internacao salva = internacoes.insert(internacao);
        atualizarOcupacao(quarto);
        return resposta(salva);
    }

    public List<InternacaoResponse> listar(StatusInternacao status, String pacienteId) {
        List<Internacao> resultado;
        if (pacienteId != null && !pacienteId.isBlank()) {
            resultado = internacoes.findByPacienteIdOrderByDataEntradaDesc(pacienteId);
        } else if (status != null) {
            resultado = internacoes.findByStatusOrderByDataEntradaDesc(status);
        } else {
            resultado = internacoes.findAll();
        }
        return resultado.stream()
                .filter(i -> status == null || i.getStatus() == status)
                .sorted(Comparator.comparing(Internacao::getDataEntrada,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::resposta)
                .toList();
    }

    public InternacaoResponse buscar(String id) {
        return resposta(encontrar(id));
    }

    public synchronized InternacaoResponse atualizar(String id, InternacaoRequest dados) {
        validarDados(dados);
        Internacao internacao = encontrar(id);
        if (!internacao.estaAtiva()) {
            throw new ConflitoException("Não é possível alterar uma internação encerrada");
        }
        Paciente paciente = encontrarPaciente(dados.pacienteId());
        if (!paciente.isAtivo()) {
            throw new RegraNegocioException("Não é possível associar uma internação a um paciente inativo");
        }
        if (internacoes.existsByPacienteIdAndStatusAndIdNot(
                dados.pacienteId(), StatusInternacao.ATIVA, id)) {
            throw new ConflitoException("O paciente já possui outra internação ativa");
        }

        Quarto quartoAnterior = quartoService.encontrar(internacao.getQuartoId());
        Quarto novoQuarto = quartoService.encontrar(dados.quartoId());
        boolean mudouQuarto = !internacao.getQuartoId().equals(dados.quartoId());
        if (mudouQuarto) validarVaga(novoQuarto);

        aplicarDados(internacao, dados);
        Internacao salva = internacoes.save(internacao);
        if (mudouQuarto) atualizarOcupacao(quartoAnterior);
        atualizarOcupacao(novoQuarto);
        return resposta(salva);
    }

    public synchronized InternacaoResponse registrarAlta(String id, AltaRequest dados) {
        Internacao internacao = encontrar(id);
        internacao.registrarAlta(dados.dataEfetivaAlta());
        Internacao salva = internacoes.save(internacao);
        Quarto quarto = quartoService.encontrar(internacao.getQuartoId());
        atualizarOcupacao(quarto);
        return resposta(salva);
    }

    public synchronized void excluir(String id) {
        Internacao internacao = encontrar(id);
        if (internacao.estaAtiva()) {
            throw new ConflitoException("Registre a alta antes de excluir uma internação");
        }
        internacoes.delete(internacao);
    }

    @Override
    public List<?> listarPorPaciente(String pacienteId) {
        return internacoes.findByPacienteIdOrderByDataEntradaDesc(pacienteId)
                .stream().map(this::resposta).toList();
    }

    private void validarDados(InternacaoRequest dados) {
        profissionais.findById(dados.profissionalId()).filter(com.praticamodular.HospManager.model.ProfissionalSaude::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional responsável não encontrado ou inativo"));
        if (dados.dataPrevistaAlta().isBefore(dados.dataEntrada())) {
            throw new RegraNegocioException("A data prevista de alta não pode ser anterior à data de entrada");
        }
    }

    private void validarVaga(Quarto quarto) {
        long ocupacao = internacoes.countByQuartoIdAndStatus(quarto.getId(), StatusInternacao.ATIVA);
        if (ocupacao >= quarto.getCapacidadeMaxima()) {
            throw new ConflitoException("Quarto " + quarto.getNumero() + " está lotado");
        }
    }

    private void atualizarOcupacao(Quarto quarto) {
        quarto.setPacientesInternados(quartoService.ocupacaoAtual(quarto.getId()));
        quartos.save(quarto);
    }

    private void aplicarDados(Internacao internacao, InternacaoRequest dados) {
        internacao.setPacienteId(dados.pacienteId());
        internacao.setProfissionalId(dados.profissionalId());
        internacao.setQuartoId(dados.quartoId());
        internacao.setDataEntrada(dados.dataEntrada());
        internacao.setDataPrevistaAlta(dados.dataPrevistaAlta());
        internacao.setObservacoes(dados.observacoes());
    }

    private Paciente encontrarPaciente(String id) {
        return pacientes.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
    }

    private Internacao encontrar(String id) {
        return internacoes.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Internação não encontrada"));
    }

    private InternacaoResponse resposta(Internacao internacao) {
        String nomePaciente = pacientes.findById(internacao.getPacienteId())
                .map(Paciente::getNome).orElse(null);
        Integer numeroQuarto = quartos.findById(internacao.getQuartoId())
                .map(Quarto::getNumero).orElse(null);
        return new InternacaoResponse(internacao.getId(), internacao.getPacienteId(), nomePaciente,
                internacao.getProfissionalId(), internacao.getQuartoId(), numeroQuarto,
                internacao.getDataEntrada(), internacao.getDataPrevistaAlta(),
                internacao.getDataEfetivaAlta(), internacao.getObservacoes(), internacao.getStatus());
    }
}
