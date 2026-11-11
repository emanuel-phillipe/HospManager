package com.praticamodular.HospManager.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import com.praticamodular.HospManager.dto.ConsultaRequest;
import com.praticamodular.HospManager.dto.ConsultaResponse;
import com.praticamodular.HospManager.exception.ConflitoException;
import com.praticamodular.HospManager.exception.RecursoNaoEncontradoException;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.mapper.ConsultaMapper;
import com.praticamodular.HospManager.model.*;
import com.praticamodular.HospManager.model.enums.StatusConsulta;
import com.praticamodular.HospManager.model.enums.TipoRegistro;
import com.praticamodular.HospManager.repository.*;

@Service
public class ConsultaService implements AtendimentoHistoricoProvider {
    private final ConsultaRepository consultas;
    private final PacienteRepository pacientes;
    private final HistoricoMedicoRepository historicos;
    private final ProfissionalRepository profissionais;
    private final ConsultaMapper mapper;
    private final MongoTemplate mongo;

    public ConsultaService(ConsultaRepository consultas, PacienteRepository pacientes,
            HistoricoMedicoRepository historicos, ProfissionalRepository profissionais,
            ConsultaMapper mapper, MongoTemplate mongo) {
        this.consultas = consultas;
        this.pacientes = pacientes;
        this.historicos = historicos;
        this.profissionais = profissionais;
        this.mapper = mapper;
        this.mongo = mongo;
    }

    public ConsultaResponse agendar(ConsultaRequest dados) {
        Paciente paciente = pacientes.findById(dados.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));
        if (!paciente.isAtivo()) {
            throw new RegraNegocioException("Paciente inativo não pode ter consulta agendada");
        }
        ProfissionalSaude medico = profissionais.findById(dados.medicoId())
                .filter(ProfissionalSaude::isAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado"));

        Consulta consulta = mapper.nova(dados);
        consulta.definirMedico(medico);
        consulta.validarCondicoes();
        if (consultas.existeConflitoMedico(medico.getId(), consulta.getData(), consulta.getHorario(),
                StatusConsulta.ativos())) {
            throw new ConflitoException("O médico já possui consulta nesse dia e horário");
        }
        if (consultas.existsByPacienteIdAndDataAndHorarioAndStatusIn(paciente.getId(), consulta.getData(),
                consulta.getHorario(), StatusConsulta.ativos())) {
            throw new ConflitoException("O paciente já possui consulta nesse dia e horário");
        }
        consulta.setId(UUID.randomUUID().toString());
        return mapper.resposta(consultas.insert(consulta));
    }

    public Page<ConsultaResponse> listar(String pacienteId, String medicoId, StatusConsulta status,
            LocalDate data, Pageable pageable) {
        List<Criteria> filtros = new ArrayList<>();
        if (pacienteId != null && !pacienteId.isBlank()) filtros.add(Criteria.where("pacienteId").is(pacienteId));
        if (medicoId != null && !medicoId.isBlank()) filtros.add(Criteria.where("medicoId").is(medicoId));
        if (status != null) filtros.add(Criteria.where("status").is(status));
        if (data != null) filtros.add(Criteria.where("data").is(data));
        Query filtro = new Query();
        if (!filtros.isEmpty()) filtro.addCriteria(new Criteria().andOperator(filtros));

        List<Consulta> pagina = mongo.find(Query.of(filtro).with(pageable), Consulta.class);
        return PageableExecutionUtils.getPage(pagina, pageable,
                () -> mongo.count(Query.of(filtro).limit(-1).skip(-1), Consulta.class))
                .map(mapper::resposta);
    }

    public ConsultaResponse buscar(String id) {
        return mapper.resposta(encontrar(id));
    }

    public ConsultaResponse confirmar(String id) { return alterar(id, Consulta::confirmar); }

    public ConsultaResponse iniciar(String id) { return alterar(id, Consulta::iniciar); }

    public ConsultaResponse cancelar(String id) { return alterar(id, Consulta::cancelar); }

    public ConsultaResponse registrarNaoComparecimento(String id) {
        return alterar(id, Consulta::registrarNaoComparecimento);
    }

    public ConsultaResponse concluir(String id, String observacoes) {
        Consulta consulta = encontrar(id);
        HistoricoMedico historico = historicos.findByPacienteId(consulta.getPacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Histórico médico não encontrado"));

        consulta.concluir(observacoes);

        RegistroAtendimento registro = new RegistroAtendimento();
        registro.setId(UUID.randomUUID().toString());
        registro.setDataHora(LocalDateTime.now());
        registro.setTipo(TipoRegistro.CONSULTA);
        registro.setDescricao(consulta.getObservacoes());
        registro.setAtendimentoId(consulta.getId());
        historico.getRegistros().add(registro);

        Consulta salva = consultas.save(consulta);
        historicos.save(historico);
        return mapper.resposta(salva);
    }

    @Override
    public List<ConsultaResponse> listarPorPaciente(String pacienteId) {
        return consultas.findByPacienteIdOrderByDataDescHorarioDesc(pacienteId).stream()
                .map(mapper::resposta).toList();
    }

    private ConsultaResponse alterar(String id, Consumer<Consulta> acao) {
        Consulta consulta = encontrar(id);
        acao.accept(consulta);
        return mapper.resposta(consultas.save(consulta));
    }

    private Consulta encontrar(String id) {
        return consultas.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada"));
    }
}
