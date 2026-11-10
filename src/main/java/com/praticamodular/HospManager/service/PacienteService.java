package com.praticamodular.HospManager.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.praticamodular.HospManager.dto.*;
import com.praticamodular.HospManager.exception.*;
import com.praticamodular.HospManager.mapper.PacienteMapper;
import com.praticamodular.HospManager.model.*;
import com.praticamodular.HospManager.repository.*;
import com.praticamodular.HospManager.validation.CpfValidator;

@Service
public class PacienteService {
    private final PacienteRepository pacientes;
    private final HistoricoMedicoRepository historicos;
    private final PacienteMapper mapper;
    private final AtendimentoHistoricoProvider atendimentos;
    private final InternacaoHistoricoProvider internacoes;

    public PacienteService(PacienteRepository pacientes, HistoricoMedicoRepository historicos,
            PacienteMapper mapper, AtendimentoHistoricoProvider atendimentos,
            InternacaoHistoricoProvider internacoes) {
        this.pacientes = pacientes;
        this.historicos = historicos;
        this.mapper = mapper;
        this.atendimentos = atendimentos;
        this.internacoes = internacoes;
    }

    public PacienteResponse criar(PacienteRequest dados) {
        String cpf = CpfValidator.normalizar(dados.cpf());
        if (pacientes.existsByCpf(cpf)) throw cpfDuplicado();
        Paciente paciente = mapper.novo(dados);
        paciente.validarContato();
        paciente.setId(UUID.randomUUID().toString());
        HistoricoMedico historico = new HistoricoMedico();
        historico.setId(UUID.randomUUID().toString());
        historico.setPacienteId(paciente.getId());
        // O histórico existe antes de tornar o paciente visível. Não exige replica set.
        historicos.insert(historico);
        try {
            return mapper.resposta(pacientes.insert(paciente));
        } catch (RuntimeException erro) {
            try {
                historicos.deleteById(historico.getId());
            } catch (RuntimeException falhaCompensacao) {
                erro.addSuppressed(falhaCompensacao);
            }
            if (erro instanceof DuplicateKeyException) throw cpfDuplicado();
            throw erro;
        }
    }

    public Page<PacienteResponse> listar(String nome, Pageable pageable) {
        Page<Paciente> pagina = nome == null || nome.isBlank()
                ? pacientes.findByAtivoTrue(pageable)
                : pacientes.findByAtivoTrueAndNomeContainingIgnoreCase(nome.trim(), pageable);
        return pagina.map(mapper::resposta);
    }

    public PacienteResponse buscar(String id) {
        return mapper.resposta(encontrar(id));
    }

    public PacienteResponse atualizar(String id, PacienteRequest dados) {
        Paciente paciente = encontrar(id);
        String cpf = CpfValidator.normalizar(dados.cpf());
        if (pacientes.existsByCpfAndIdNot(cpf, id)) throw cpfDuplicado();
        mapper.atualizar(paciente, dados);
        paciente.validarContato();
        try {
            return mapper.resposta(pacientes.save(paciente));
        } catch (DuplicateKeyException erro) {
            throw cpfDuplicado();
        }
    }

    public void excluir(String id) {
        Paciente paciente = encontrar(id);
        paciente.setAtivo(false);
        pacientes.save(paciente);
    }

    public HistoricoMedicoResponse historico(String id) {
        Paciente paciente = encontrar(id);
        HistoricoMedico historico = historicos.findByPacienteId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Histórico médico não encontrado"));
        return new HistoricoMedicoResponse(historico.getId(), mapper.resumo(paciente),
                atendimentos.listarPorPaciente(id), internacoes.listarPorPaciente(id),
                historico.getRegistros().stream().map(mapper::registro).toList());
    }

    private Paciente encontrar(String id) {
        return pacientes.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
    }

    private ConflitoException cpfDuplicado() {
        return new ConflitoException("Já existe um paciente com este CPF");
    }
}
