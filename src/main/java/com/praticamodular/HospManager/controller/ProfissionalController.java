
package com.praticamodular.HospManager.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.praticamodular.HospManager.dto.ErroResponse;
import com.praticamodular.HospManager.dto.ProcedimentoIdRequest;
import com.praticamodular.HospManager.dto.ProfissionalRequest;
import com.praticamodular.HospManager.dto.ProfissionalResponse;
import com.praticamodular.HospManager.model.enums.Especialidade;
import com.praticamodular.HospManager.service.ProfissionalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profissionais")
@Tag(name = "Profissionais", description = "Cadastro de profissionais de saúde e consulta aos procedimentos de cirurgiões")
@ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
public class ProfissionalController {

    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar profissional de saúde")
    @ApiResponse(responseCode = "201", description = "Profissional criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Registro profissional já cadastrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<ProfissionalResponse> criar(
            @Valid @RequestBody ProfissionalRequest dados) {

        ProfissionalResponse resposta = service.criar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    @GetMapping
    @Operation(summary = "Listar profissionais ativos", description = "Paginação por page/size/sort e filtro por especialidade.")
    @ApiResponse(responseCode = "200", description = "Página de profissionais")
    @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public Page<ProfissionalResponse> listar(
            @RequestParam(required = false)
            Especialidade especialidade,
            @ParameterObject Pageable pageable) {

        return service.listar(especialidade, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar profissional por id", description = "Permite consultar apenas profissionais ativos.")
    @ApiResponse(responseCode = "200", description = "Profissional encontrado")
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado ou inativo", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ProfissionalResponse buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar todos os dados cadastrais", description = "Permite atualizar apenas profissionais ativos, sem alterar o tipo de profissional.")
    @ApiResponse(responseCode = "200", description = "Profissional atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado ou inativo", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Registro profissional de outro profissional", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ProfissionalResponse atualizar(
            @PathVariable String id,
            @Valid @RequestBody ProfissionalRequest dados) {

        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar profissional", description = "Exclusão lógica: preserva o profissional e seus procedimentos.")
    @ApiResponse(responseCode = "204", description = "Profissional inativado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado ou inativo", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/procedimentos")
    @Operation(summary = "Registrar procedimento para cirurgião", description = "Associa o ID de um procedimento a um cirurgião ativo.")
    @ApiResponse(responseCode = "201", description = "Procedimento registrado", content = @Content)
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado ou inativo", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> registrarProcedimento(
            @PathVariable String id,
            @Valid @RequestBody ProcedimentoIdRequest dados) {

        service.registrarProcedimento(
            id,
            dados.procedimentoId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/{id}/procedimentos")
    @Operation(summary = "Listar procedimentos do cirurgião", description = "Retorna os IDs dos procedimentos associados a um cirurgião ativo.")
    @ApiResponse(responseCode = "200", description = "Lista de IDs dos procedimentos")
    @ApiResponse(responseCode = "404", description = "Profissional não encontrado ou inativo", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Profissional não é um cirurgião", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public java.util.List<String> listarProcedimentos(
            @PathVariable String id) {

        return service.listarProcedimentos(id);
    }

}
