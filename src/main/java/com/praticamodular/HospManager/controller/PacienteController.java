package com.praticamodular.HospManager.controller;

import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.praticamodular.HospManager.dto.*;
import com.praticamodular.HospManager.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
@Tag(name = "Pacientes", description = "Cadastro de pacientes e consulta ao histórico médico")
@ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
public class PacienteController {
    private final PacienteService service;

    public PacienteController(PacienteService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Cadastrar paciente e criar histórico vazio")
    @ApiResponse(responseCode = "201", description = "Paciente criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<PacienteResponse> criar(@Valid @RequestBody PacienteRequest dados) {
        PacienteResponse paciente = service.criar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(paciente.id()).toUri();
        return ResponseEntity.created(location).body(paciente);
    }

    @GetMapping
    @Operation(summary = "Listar pacientes ativos", description = "Paginação por page/size/sort e filtro por trecho do nome, sem diferenciar maiúsculas.")
    @ApiResponse(responseCode = "200", description = "Página de pacientes")
    @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public Page<PacienteResponse> listar(@RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por id", description = "Permite consultar também pacientes inativos.")
    @ApiResponse(responseCode = "200", description = "Paciente encontrado")
    @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public PacienteResponse buscar(@PathVariable String id) { return service.buscar(id); }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar todos os dados cadastrais", description = "Preserva o estado ativo/inativo e o histórico.")
    @ApiResponse(responseCode = "200", description = "Paciente atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "CPF de outro paciente", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public PacienteResponse atualizar(@PathVariable String id, @Valid @RequestBody PacienteRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar paciente", description = "Exclusão lógica: preserva o paciente e seu histórico.")
    @ApiResponse(responseCode = "204", description = "Paciente inativado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Consultar histórico médico", description = "Inclui consultas, internações e registros, mesmo para pacientes inativos.")
    @ApiResponse(responseCode = "200", description = "Histórico encontrado")
    @ApiResponse(responseCode = "404", description = "Paciente ou histórico não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public HistoricoMedicoResponse historico(@PathVariable String id) { return service.historico(id); }
}
