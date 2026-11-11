package hospital;

/**
 * Ciclo de vida de um procedimento cirúrgico:
 * AGENDADO -> EM_ANDAMENTO -> CONCLUIDO
 * AGENDADO / EM_ANDAMENTO -> CANCELADO
 */
public enum StatusProcedimento {
    AGENDADO,
    EM_ANDAMENTO,
    CONCLUIDO,
    CANCELADO;

    public boolean podeTransicionarPara(StatusProcedimento novo) {
        if (novo == null) return false;
        return switch (this) {
            case AGENDADO     -> novo == EM_ANDAMENTO || novo == CANCELADO;
            case EM_ANDAMENTO -> novo == CONCLUIDO || novo == CANCELADO;
            case CONCLUIDO, CANCELADO -> false;
        };
    }

    public boolean isFinal() {
        return this == CONCLUIDO || this == CANCELADO;
    }
}
