package hospital;

/**
 * Ciclo de vida de um atendimento de emergência:
 * AGUARDANDO_AVALIACAO -> EM_ATENDIMENTO -> CONCLUIDO
 */
public enum StatusEmergencia {
    AGUARDANDO_AVALIACAO,
    EM_ATENDIMENTO,
    CONCLUIDO;

    public boolean podeTransicionarPara(StatusEmergencia novo) {
        if (novo == null) return false;
        return switch (this) {
            case AGUARDANDO_AVALIACAO -> novo == EM_ATENDIMENTO;
            case EM_ATENDIMENTO       -> novo == CONCLUIDO;
            case CONCLUIDO            -> false;
        };
    }

    public boolean isFinal() {
        return this == CONCLUIDO;
    }
}
