
package com.praticamodular.HospManager.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.praticamodular.HospManager.dto.DisponibilidadeRequest;
import com.praticamodular.HospManager.dto.ProfissionalRequest;
import com.praticamodular.HospManager.dto.ProfissionalResponse;
import com.praticamodular.HospManager.exception.ConflitoException;
import com.praticamodular.HospManager.exception.RecursoNaoEncontradoException;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.mapper.ProfissionalMapper;
import com.praticamodular.HospManager.model.Cirurgiao;
import com.praticamodular.HospManager.model.MedicoClinicoGeral;
import com.praticamodular.HospManager.model.MedicoEspecialista;
import com.praticamodular.HospManager.model.ProfissionalSaude;
import com.praticamodular.HospManager.model.enums.Especialidade;
import com.praticamodular.HospManager.repository.ProfissionalRepository;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionais;
    private final ProfissionalMapper mapper;

    public ProfissionalService(
            ProfissionalRepository profissionais,
            ProfissionalMapper mapper) {
        this.profissionais = profissionais;
        this.mapper = mapper;
    }

    // Cadastra um novo profissional
    public ProfissionalResponse criar(ProfissionalRequest dados) {
        validarDados(dados);

        String registro = dados.registroProfissional().trim();

        if (profissionais.existsByRegistroProfissional(registro)) {
            throw registroDuplicado();
        }

        ProfissionalSaude profissional = mapper.novo(dados);
        profissional.validarContato();
        profissional.setId(UUID.randomUUID().toString());

        try {
            return mapper.resposta(profissionais.insert(profissional));
        } catch (DuplicateKeyException erro) {
            throw registroDuplicado();
        }
    }

    // Lista profissionais ativos, com filtro opcional
    public Page<ProfissionalResponse> listar(
            Especialidade especialidade,
            Pageable pageable) {

        Page<ProfissionalSaude> pagina =
                especialidade == null
                ? profissionais.findByAtivoTrue(pageable)
                : profissionais.findByAtivoTrueAndEspecialidade(
                    especialidade, pageable
                );

        return pagina.map(mapper::resposta);
    }

    // Busca um profissional pelo ID
    public ProfissionalResponse buscar(String id) {
        return mapper.resposta(encontrar(id));
    }

    // Atualiza um profissional
    public ProfissionalResponse atualizar(
            String id,
            ProfissionalRequest dados) {

        ProfissionalSaude profissional = encontrar(id);
        validarDados(dados);

        String registro = dados.registroProfissional().trim();

        if (profissionais.existsByRegistroProfissionalAndIdNot(
                registro, id)) {
            throw registroDuplicado();
        }

        validarTipoAtualizacao(profissional, dados);

        mapper.atualizar(profissional, dados);
        profissional.validarContato();

        try {
            return mapper.resposta(profissionais.save(profissional));
        } catch (DuplicateKeyException erro) {
            throw registroDuplicado();
        }
    }

    // Desativa um profissional sem apagar do banco
    public void excluir(String id) {
        ProfissionalSaude profissional = encontrar(id);
        profissional.setAtivo(false);
        profissionais.save(profissional);
    }

    // Associa o ID de um procedimento a um cirurgião
    public void registrarProcedimento(
            String cirurgiaoId,
            String procedimentoId) {

        Cirurgiao cirurgiao = encontrarCirurgiao(cirurgiaoId);

        if (procedimentoId == null || procedimentoId.isBlank()) {
            throw new RegraNegocioException(
                "ID do procedimento obrigatório"
            );
        }

        cirurgiao.registrarProcedimento(procedimentoId.trim());
        profissionais.save(cirurgiao);
    }

    // Lista os IDs dos procedimentos de um cirurgião
    public List<String> listarProcedimentos(String cirurgiaoId) {
        Cirurgiao cirurgiao = encontrarCirurgiao(cirurgiaoId);

        if (cirurgiao.getProcedimentosIds() == null) {
            return List.of();
        }

        return List.copyOf(cirurgiao.getProcedimentosIds());
    }

    // Busca um profissional ativo
    private ProfissionalSaude encontrar(String id) {
        ProfissionalSaude profissional = profissionais.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Profissional não encontrado"
            ));

        if (!profissional.isAtivo()) {
            throw new RecursoNaoEncontradoException(
                "Profissional não encontrado"
            );
        }

        return profissional;
    }

    // Confirma que o profissional é um cirurgião
    private Cirurgiao encontrarCirurgiao(String id) {
        ProfissionalSaude profissional = encontrar(id);

        if (!(profissional instanceof Cirurgiao cirurgiao)) {
            throw new RegraNegocioException(
                "O profissional informado não é um cirurgião"
            );
        }

        return cirurgiao;
    }

    // Valida tipo, especialidade e disponibilidades
    private void validarDados(ProfissionalRequest dados) {

        if (dados.tipo() == null || dados.tipo().isBlank()) {
            throw new RegraNegocioException(
                "Tipo de profissional obrigatório"
            );
        }

        String tipo = dados.tipo().trim()
                .toUpperCase(Locale.ROOT);

        if (!Set.of("CLINICO_GERAL", "ESPECIALISTA", "CIRURGIAO")
                .contains(tipo)) {
            throw new RegraNegocioException(
                "Tipo de profissional inválido"
            );
        }

        if (dados.especialidade() == null) {
            throw new RegraNegocioException(
                "Especialidade obrigatória"
            );
        }

        if (tipo.equals("CLINICO_GERAL")
                && dados.especialidade() != Especialidade.CLINICA_GERAL) {
            throw new RegraNegocioException(
                "Clínico geral deve possuir especialidade CLINICA_GERAL"
            );
        }

        if (tipo.equals("ESPECIALISTA")
                && dados.especialidade() == Especialidade.CLINICA_GERAL) {
            throw new RegraNegocioException(
                "Utilize CLINICO_GERAL para essa especialidade"
            );
        }

        if (tipo.equals("CIRURGIAO")
                && dados.especialidade() != Especialidade.CIRURGIA_GERAL) {
            throw new RegraNegocioException(
                "Cirurgião deve possuir especialidade CIRURGIA_GERAL"
            );
        }

        // Verifica horários inválidos e sobrepostos
        if (dados.disponibilidades() != null) {

            var horarios = dados.disponibilidades();

            for (int i = 0; i < horarios.size(); i++) {

                DisponibilidadeRequest atual = horarios.get(i);

                if (atual == null
                        || atual.diaSemana() == null
                        || atual.horaInicio() == null
                        || atual.horaFim() == null) {
                    throw new RegraNegocioException(
                        "Disponibilidade incompleta"
                    );
                }

                if (!atual.horaInicio().isBefore(atual.horaFim())) {
                    throw new RegraNegocioException(
                        "Horário inicial deve ser anterior ao final"
                    );
                }

                for (int j = 0; j < i; j++) {

                    DisponibilidadeRequest anterior = horarios.get(j);

                    boolean mesmoDia =
                        atual.diaSemana() == anterior.diaSemana();

                    boolean sobrepostos =
                        atual.horaInicio().isBefore(anterior.horaFim())
                        && anterior.horaInicio().isBefore(atual.horaFim());

                    if (mesmoDia && sobrepostos) {
                        throw new RegraNegocioException(
                            "Existem horários sobrepostos no mesmo dia"
                        );
                    }
                }
            }
        }
    }

    // Impede alterar a categoria do profissional
    private void validarTipoAtualizacao(
            ProfissionalSaude profissional,
            ProfissionalRequest dados) {

        String tipoAtual;

        if (profissional instanceof MedicoClinicoGeral) {
            tipoAtual = "CLINICO_GERAL";
        } else if (profissional instanceof MedicoEspecialista) {
            tipoAtual = "ESPECIALISTA";
        } else if (profissional instanceof Cirurgiao) {
            tipoAtual = "CIRURGIAO";
        } else {
            throw new RegraNegocioException(
                "Tipo de profissional desconhecido"
            );
        }

        if (!tipoAtual.equalsIgnoreCase(dados.tipo().trim())) {
            throw new RegraNegocioException(
                "Não é permitido alterar o tipo de profissional"
            );
        }
    }

    // Mensagem para registros duplicados
    private ConflitoException registroDuplicado() {
        return new ConflitoException(
            "Já existe um profissional com este registro"
        );
    }
}
