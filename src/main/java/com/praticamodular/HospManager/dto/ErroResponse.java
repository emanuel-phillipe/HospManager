package com.praticamodular.HospManager.dto;

import java.time.OffsetDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

public record ErroResponse(OffsetDateTime timestamp, int status, String erro, String mensagem,
        String path, @JsonInclude(JsonInclude.Include.NON_EMPTY) List<DetalheErro> detalhes) {
    public record DetalheErro(String campo, String mensagem) {}
}
