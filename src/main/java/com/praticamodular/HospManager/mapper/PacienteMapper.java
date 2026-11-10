package com.praticamodular.HospManager.mapper;

import java.util.Locale;
import org.springframework.stereotype.Component;
import com.praticamodular.HospManager.dto.*;
import com.praticamodular.HospManager.model.*;
import com.praticamodular.HospManager.validation.CpfValidator;

@Component
public class PacienteMapper {
    public Paciente novo(PacienteRequest dados) {
        Paciente paciente = new Paciente();
        atualizar(paciente, dados);
        return paciente;
    }

    public void atualizar(Paciente paciente, PacienteRequest dados) {
        paciente.setNome(dados.nome().trim());
        paciente.setCpf(CpfValidator.normalizar(dados.cpf()));
        paciente.setDataNascimento(dados.dataNascimento());
        paciente.setTelefone(dados.telefone());
        paciente.setEmail(dados.email());
        EnderecoRequest origem = dados.endereco();
        Endereco endereco = new Endereco();
        endereco.setLogradouro(origem.logradouro());
        endereco.setNumero(origem.numero());
        endereco.setComplemento(origem.complemento());
        endereco.setBairro(origem.bairro());
        endereco.setCidade(origem.cidade());
        endereco.setEstado(origem.estado().toUpperCase(Locale.ROOT));
        endereco.setCep(origem.cep());
        paciente.setEndereco(endereco);
    }

    public PacienteResponse resposta(Paciente paciente) {
        Endereco e = paciente.getEndereco();
        return new PacienteResponse(paciente.getId(), paciente.getNome(), paciente.getCpf(),
                paciente.getDataNascimento(), paciente.getTelefone(), paciente.getEmail(),
                new EnderecoResponse(e.getLogradouro(), e.getNumero(), e.getComplemento(),
                        e.getBairro(), e.getCidade(), e.getEstado(), e.getCep()),
                paciente.isAtivo(), paciente.calcularIdade());
    }

    public PacienteResumoResponse resumo(Paciente paciente) {
        return new PacienteResumoResponse(paciente.getId(), paciente.getNome(), paciente.getCpf(),
                paciente.getDataNascimento(), paciente.isAtivo());
    }

    public RegistroAtendimentoResponse registro(RegistroAtendimento r) {
        return new RegistroAtendimentoResponse(r.getId(), r.getDataHora(), r.getTipo(),
                r.getDescricao(), r.getAtendimentoId(), r.getInternacaoId());
    }
}
