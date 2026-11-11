package com.praticamodular.HospManager.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.praticamodular.HospManager.dto.QuartoRequest;
import com.praticamodular.HospManager.dto.QuartoResponse;
import com.praticamodular.HospManager.exception.ConflitoException;
import com.praticamodular.HospManager.exception.RecursoNaoEncontradoException;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.Quarto;
import com.praticamodular.HospManager.model.enums.StatusInternacao;
import com.praticamodular.HospManager.repository.InternacaoRepository;
import com.praticamodular.HospManager.repository.QuartoRepository;

@Service
public class QuartoService {
    private final QuartoRepository quartos;
    private final InternacaoRepository internacoes;

    public QuartoService(QuartoRepository quartos, InternacaoRepository internacoes) {
        this.quartos = quartos;
        this.internacoes = internacoes;
    }

    public List<QuartoResponse> listar() {
        return quartos.findAll().stream().map(this::respostaAtualizada).toList();
    }

    public List<QuartoResponse> listarDisponiveis() {
        return quartos.findAll().stream()
                .map(this::respostaAtualizada)
                .filter(q -> q.vagasDisponiveis() > 0)
                .toList();
    }

    public QuartoResponse buscar(String id) {
        return respostaAtualizada(encontrar(id));
    }

    public synchronized QuartoResponse criar(QuartoRequest dados) {
        if (quartos.existsByNumero(dados.numero())) {
            throw new ConflitoException("Já existe um quarto com este número");
        }
        Quarto quarto = new Quarto();
        aplicarDados(quarto, dados);
        quarto.setPacientesInternados(0);
        try {
            return respostaAtualizada(quartos.insert(quarto));
        } catch (DuplicateKeyException erro) {
            throw new ConflitoException("Já existe um quarto com este número");
        }
    }

    public synchronized QuartoResponse atualizar(String id, QuartoRequest dados) {
        Quarto quarto = encontrar(id);
        if (quartos.existsByNumeroAndIdNot(dados.numero(), id)) {
            throw new ConflitoException("Já existe outro quarto com este número");
        }
        int ocupacao = ocupacaoAtual(quarto.getId());
        if (dados.capacidadeMaxima() < ocupacao) {
            throw new RegraNegocioException(
                    "A capacidade máxima não pode ser menor que a quantidade de pacientes internados (" + ocupacao + ")");
        }
        aplicarDados(quarto, dados);
        quarto.setPacientesInternados(ocupacao);
        try {
            return respostaAtualizada(quartos.save(quarto));
        } catch (DuplicateKeyException erro) {
            throw new ConflitoException("Já existe outro quarto com este número");
        }
    }

    public synchronized void excluir(String id) {
        Quarto quarto = encontrar(id);
        int ocupacao = ocupacaoAtual(id);
        if (ocupacao > 0) {
            throw new ConflitoException("Não é possível excluir um quarto que possui internações ativas");
        }
        quartos.delete(quarto);
    }

    public Quarto encontrar(String id) {
        return quartos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto não encontrado"));
    }

    public int ocupacaoAtual(String quartoId) {
        return Math.toIntExact(internacoes.countByQuartoIdAndStatus(quartoId, StatusInternacao.ATIVA));
    }

    public QuartoResponse respostaAtualizada(Quarto quarto) {
        quarto.setPacientesInternados(ocupacaoAtual(quarto.getId()));
        return new QuartoResponse(quarto.getId(), quarto.getNumero(), quarto.getAndar(),
                quarto.getCapacidadeMaxima(), quarto.pacientesInternados(),
                quarto.vagasDisponiveis(), quarto.getTipo(), quarto.getSituacao());
    }

    private void aplicarDados(Quarto quarto, QuartoRequest dados) {
        if (dados.capacidadeMaxima() < 1) {
            throw new RegraNegocioException("A capacidade máxima deve ser maior que zero");
        }
        quarto.setNumero(dados.numero());
        quarto.setAndar(dados.andar());
        quarto.setCapacidadeMaxima(dados.capacidadeMaxima());
        quarto.setTipo(dados.tipo());
    }
}
