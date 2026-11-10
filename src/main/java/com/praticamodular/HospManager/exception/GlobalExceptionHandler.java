package com.praticamodular.HospManager.exception;

import java.time.OffsetDateTime;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.praticamodular.HospManager.dto.ErroResponse;
import com.praticamodular.HospManager.dto.ErroResponse.DetalheErro;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException erro, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, erro.getMessage(), request, null);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoException erro, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, erro.getMessage(), request, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regra(RegraNegocioException erro, HttpServletRequest request) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, erro.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErroResponse> duplicidade(DuplicateKeyException erro, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, "Já existe um registro com os dados informados", request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException erro, HttpServletRequest request) {
        List<DetalheErro> detalhes = erro.getBindingResult().getFieldErrors().stream()
                .map(campo -> new DetalheErro(campo.getField(), campo.getDefaultMessage())).toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", request, detalhes);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> json(HttpMessageNotReadableException erro, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "JSON inválido: verifique os campos, tipos, datas e valores permitidos", request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> parametro(MethodArgumentTypeMismatchException erro, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + erro.getName(), request, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResponse> autenticacao(AuthenticationException erro, HttpServletRequest request) {
        return resposta(HttpStatus.UNAUTHORIZED, "Autenticação necessária", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> acessoNegado(AccessDeniedException erro, HttpServletRequest request) {
        return resposta(HttpStatus.FORBIDDEN, "Acesso negado", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception erro, HttpServletRequest request) {
        if (erro instanceof ErrorResponse erroHttp) {
            HttpStatus status = HttpStatus.valueOf(erroHttp.getStatusCode().value());
            return resposta(status, "Não foi possível processar a requisição", request, null);
        }
        log.error("Erro inesperado ao processar a requisição", erro);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", request, null);
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem,
            HttpServletRequest request, List<DetalheErro> detalhes) {
        String titulo = switch (status.value()) {
            case 400 -> "Requisição inválida";
            case 401 -> "Não autenticado";
            case 403 -> "Acesso negado";
            case 404 -> "Recurso não encontrado";
            case 409 -> "Conflito";
            case 422 -> "Regra de negócio violada";
            case 500 -> "Erro interno do servidor";
            default -> "Erro na requisição";
        };
        return ResponseEntity.status(status).body(new ErroResponse(OffsetDateTime.now(),
                status.value(), titulo, mensagem, request.getRequestURI(), detalhes));
    }
}
