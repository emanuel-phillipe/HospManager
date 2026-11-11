package com.praticamodular.HospManager.model;
import org.springframework.data.annotation.Transient;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.enums.Especialidade;
import com.praticamodular.HospManager.model.enums.TipoConsulta;

public class ConsultaEspecializada extends Consulta {
    private String medicoId;
    private Especialidade especialidade;
    @Transient private MedicoEspecialista medico;

    @Override
    public TipoConsulta getTipo() { return TipoConsulta.ESPECIALIZADA; }

    @Override
    public void definirMedico(ProfissionalSaude profissional) {
        if (!(profissional instanceof MedicoEspecialista especialista)) {
            throw new RegraNegocioException("Consulta especializada deve ser com um médico especialista");
        }
        this.medico = especialista;
        this.medicoId = especialista.getId();
    }

    @Override
    protected void validarModalidade() {
        if (especialidade == null) {
            throw new RegraNegocioException("Especialidade é obrigatória na consulta especializada");
        }
        if (medico.getEspecialidade() != especialidade) {
            throw new RegraNegocioException("O médico não atende a especialidade " + especialidade);
        }
    }

    @Override
    public ProfissionalSaude getResponsavel() { return medico; }

    @Override
    public String getResponsavelId() { return medicoId; }

    public String getMedicoId() { return medicoId; }
    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }
}
