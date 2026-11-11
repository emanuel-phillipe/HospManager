package com.praticamodular.HospManager.mapper;
import org.springframework.stereotype.Component;
import com.praticamodular.HospManager.dto.ConsultaRequest;
import com.praticamodular.HospManager.dto.ConsultaResponse;
import com.praticamodular.HospManager.model.*;

@Component
public class ConsultaMapper {
    public Consulta nova(ConsultaRequest dados) {
        Consulta consulta;
        switch (dados.tipo()) {
            case ROTINA -> consulta = new ConsultaRotina();
            case ESPECIALIZADA -> {
                ConsultaEspecializada especializada = new ConsultaEspecializada();
                especializada.setEspecialidade(dados.especialidade());
                consulta = especializada;
            }
            default -> throw new IllegalArgumentException("Tipo de consulta desconhecido");
        }
        consulta.setPacienteId(dados.pacienteId());
        consulta.setData(dados.data());
        consulta.setHorario(dados.horario());
        consulta.setMotivo(dados.motivo().trim());
        return consulta;
    }

    public ConsultaResponse resposta(Consulta c) {
        return new ConsultaResponse(c.getId(), c.getTipo(), c.getPacienteId(), c.getResponsavelId(),
                c.getData(), c.getHorario(), c.getMotivo(),
                c instanceof ConsultaEspecializada e ? e.getEspecialidade() : null,
                c.getStatus(), c.getObservacoes());
    }
}
