package com.praticamodular.HospManager.controller;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.praticamodular.HospManager.model.enums.StatusConsulta;
import com.praticamodular.HospManager.service.ConsultaService;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "Agendamento e andamento das consultas (rotina e especializada)")
@ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
public class ConsultaController {
    private final ConsultaService service;

    public ConsultaController(ConsultaService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Agendar consulta", description = "tipo ROTINA exige médico clínico geral; ESPECIALIZADA exige especialidade e médico especialista dessa área. Valida disponibilidade e conflito de horário.")
    @ApiResponse(responseCode = "201", description = "Consulta agendada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Paciente ou médico não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito de horário", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negócio violada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<ConsultaResponse> agendar(@Valid @RequestBody ConsultaRequest dados) {
        ConsultaResponse consulta = service.agendar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(consulta.id()).toUri();
        return ResponseEntity.created(location).body(consulta);
    }

    @GetMapping
    @Operation(summary = "Listar consultas", description = "Filtros opcionais: pacienteId, medicoId, status e data (yyyy-MM-dd). Paginação por page/size/sort.")
    @ApiResponse(responseCode = "200", description = "Página de consultas")
    @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public Page<ConsultaResponse> listar(@RequestParam(required = false) String pacienteId,
            @RequestParam(required = false) String medicoId,
            @RequestParam(required = false) StatusConsulta status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @ParameterObject @PageableDefault(size = 20, sort = { "data", "horario" }) Pageable pageable) {
        return service.listar(pacienteId, medicoId, status, data, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por id")
    @ApiResponse(responseCode = "200", description = "Consulta encontrada")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ConsultaResponse buscar(@PathVariable String id) { return service.buscar(id); }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar consulta", description = "AGENDADA -> CONFIRMADA")
    @ApiResponse(responseCode = "200", description = "Consulta confirmada")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Transição de status inválida", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ConsultaResponse confirmar(@PathVariable String id) { return service.confirmar(id); }

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar atendimento", description = "CONFIRMADA -> EM_ATENDIMENTO")
    @ApiResponse(responseCode = "200", description = "Atendimento iniciado")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Transição de status inválida", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ConsultaResponse iniciar(@PathVariable String id) { return service.iniciar(id); }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Concluir consulta", description = "EM_ATENDIMENTO -> CONCLUIDA. Exige observações e adiciona um registro ao histórico médico do paciente.")
    @ApiResponse(responseCode = "200", description = "Consulta concluída")
    @ApiResponse(responseCode = "400", description = "Observações ausentes", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Transição de status inválida", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ConsultaResponse concluir(@PathVariable String id, @Valid @RequestBody ConclusaoConsultaRequest dados) {
        return service.concluir(id, dados.observacoes());
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar consulta", description = "AGENDADA ou CONFIRMADA -> CANCELADA")
    @ApiResponse(responseCode = "200", description = "Consulta cancelada")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Transição de status inválida", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ConsultaResponse cancelar(@PathVariable String id) { return service.cancelar(id); }

    @PatchMapping("/{id}/nao-compareceu")
    @Operation(summary = "Registrar não comparecimento", description = "AGENDADA ou CONFIRMADA -> NAO_COMPARECEU")
    @ApiResponse(responseCode = "200", description = "Não comparecimento registrado")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "422", description = "Transição de status inválida", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ConsultaResponse naoCompareceu(@PathVariable String id) { return service.registrarNaoComparecimento(id); }
}
