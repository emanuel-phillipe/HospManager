package hospital;

/** Lançada quando uma mudança de status viola as regras de transição. */
public class TransicaoInvalidaException extends RegraNegocioException {
    public TransicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
