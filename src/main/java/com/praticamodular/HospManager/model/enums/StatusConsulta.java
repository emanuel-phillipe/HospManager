package com.praticamodular.HospManager.model.enums;
import java.util.EnumSet;
import java.util.Set;

public enum StatusConsulta {
    AGENDADA, CONFIRMADA, EM_ATENDIMENTO, CONCLUIDA, CANCELADA, NAO_COMPARECEU;

    public Set<StatusConsulta> proximosPermitidos() {
        return switch (this) {
            case AGENDADA -> EnumSet.of(CONFIRMADA, CANCELADA, NAO_COMPARECEU);
            case CONFIRMADA -> EnumSet.of(EM_ATENDIMENTO, CANCELADA, NAO_COMPARECEU);
            case EM_ATENDIMENTO -> EnumSet.of(CONCLUIDA);
            case CONCLUIDA, CANCELADA, NAO_COMPARECEU -> EnumSet.noneOf(StatusConsulta.class);
        };
    }

    public boolean podeTransicionarPara(StatusConsulta novo) {
        return proximosPermitidos().contains(novo);
    }

    public boolean isFinal() {
        return proximosPermitidos().isEmpty();
    }

    public static Set<StatusConsulta> ativos() {
        return EnumSet.of(AGENDADA, CONFIRMADA, EM_ATENDIMENTO);
    }
}
