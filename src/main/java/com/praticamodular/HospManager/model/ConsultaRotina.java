package com.praticamodular.HospManager.model;
import org.springframework.data.annotation.Transient;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.enums.TipoConsulta;

public class ConsultaRotina extends Consulta {
    private String medicoId;
    @Transient private MedicoClinicoGeral medico;

    @Override
    public TipoConsulta getTipo() { return TipoConsulta.ROTINA; }

    @Override
    public void definirMedico(ProfissionalSaude profissional) {
        if (!(profissional instanceof MedicoClinicoGeral clinico)) {
            throw new RegraNegocioException("Consulta de rotina deve ser com um médico clínico geral");
        }
        this.medico = clinico;
        this.medicoId = clinico.getId();
    }

    @Override
    protected void validarModalidade() {
    }

    @Override
    public ProfissionalSaude getResponsavel() { return medico; }

    @Override
    public String getResponsavelId() { return medicoId; }

    public String getMedicoId() { return medicoId; }
}
