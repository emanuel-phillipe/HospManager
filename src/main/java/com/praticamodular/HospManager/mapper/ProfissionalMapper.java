
package com.praticamodular.HospManager.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.praticamodular.HospManager.dto.DisponibilidadeRequest;
import com.praticamodular.HospManager.dto.ProfissionalRequest;
import com.praticamodular.HospManager.dto.ProfissionalResponse;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.Cirurgiao;
import com.praticamodular.HospManager.model.Disponibilidade;
import com.praticamodular.HospManager.model.MedicoClinicoGeral;
import com.praticamodular.HospManager.model.MedicoEspecialista;
import com.praticamodular.HospManager.model.ProfissionalSaude;

@Component
public class ProfissionalMapper {

    // Cria o tipo correto de profissional
    public ProfissionalSaude novo(ProfissionalRequest dados) {
        ProfissionalSaude profissional;

        if (dados.tipo() == null) {
            throw new RegraNegocioException(
                "Tipo de profissional obrigatório"
            );
        }

        String tipo = dados.tipo().trim()
                .toUpperCase(Locale.ROOT);

        switch (tipo) {
            case "CLINICO_GERAL":
                profissional = new MedicoClinicoGeral();
                break;

            case "ESPECIALISTA":
                profissional = new MedicoEspecialista();
                break;

            case "CIRURGIAO":
                profissional = new Cirurgiao();
                break;

            default:
                throw new RegraNegocioException(
                    "Tipo de profissional inválido"
                );
        }

        atualizar(profissional, dados);
        return profissional;
    }

    // Atualiza os dados de um profissional existente
    public void atualizar(
            ProfissionalSaude profissional,
            ProfissionalRequest dados) {

        profissional.setNome(dados.nome().trim());
        profissional.setRegistroProfissional(
            dados.registroProfissional().trim()
        );
        profissional.setTelefone(dados.telefone());
        profissional.setEmail(dados.email());
        profissional.setEspecialidade(dados.especialidade());

        List<Disponibilidade> horarios = new ArrayList<>();

        if (dados.disponibilidades() != null) {
            for (DisponibilidadeRequest horario : dados.disponibilidades()) {

                Disponibilidade disponibilidade =
                    new Disponibilidade();

                disponibilidade.setDiaSemana(
                    horario.diaSemana()
                );
                disponibilidade.setHoraInicio(
                    horario.horaInicio()
                );
                disponibilidade.setHoraFim(
                    horario.horaFim()
                );

                horarios.add(disponibilidade);
            }
        }

        profissional.setDisponibilidades(horarios);
    }

    // Converte o profissional em resposta da API
    public ProfissionalResponse resposta(
            ProfissionalSaude profissional) {

        String tipo;

        if (profissional instanceof MedicoClinicoGeral) {
            tipo = "CLINICO_GERAL";
        } else if (profissional instanceof MedicoEspecialista) {
            tipo = "ESPECIALISTA";
        } else if (profissional instanceof Cirurgiao) {
            tipo = "CIRURGIAO";
        } else {
            throw new RegraNegocioException(
                "Tipo de profissional desconhecido"
            );
        }

        List<DisponibilidadeRequest> horarios = new ArrayList<>();

        if (profissional.getDisponibilidades() != null) {
            for (Disponibilidade disponibilidade :
                    profissional.getDisponibilidades()) {

                horarios.add(new DisponibilidadeRequest(
                    disponibilidade.getDiaSemana(),
                    disponibilidade.getHoraInicio(),
                    disponibilidade.getHoraFim()
                ));
            }
        }

        return new ProfissionalResponse(
            profissional.getId(),
            profissional.getNome(),
            profissional.getRegistroProfissional(),
            profissional.getTelefone(),
            profissional.getEmail(),
            tipo,
            profissional.getEspecialidade(),
            horarios,
            profissional.isAtivo()
        );
    }
}
