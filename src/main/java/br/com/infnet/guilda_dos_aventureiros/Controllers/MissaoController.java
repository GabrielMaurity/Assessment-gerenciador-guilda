package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Models.aventura.*;
import br.com.infnet.guilda_dos_aventureiros.Service.MissaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guilda/missoes")
public class MissaoController {

    private final MissaoService service;

    public MissaoController(MissaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Missao>> listarMissoes(
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(required = false) NivelPerigo nivelPerigo,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataTermino,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Missao> resultado = service.listarComFiltros(status, nivelPerigo, dataInicio, dataTermino, page, size);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultado.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(resultado.getTotalPages()))
                .body(resultado.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Missao> buscarMissao(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Missao> criarMissao(@Valid @RequestBody Missao missao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(missao));
    }

    @PostMapping("/{missaoId}/participantes/{aventureiroId}")
    public ResponseEntity<Void> adicionarParticipante(
            @PathVariable Long missaoId,
            @PathVariable Long aventureiroId,
            @Valid @RequestBody ParticipacaoMissao participacao
    ) {
        service.adicionarParticipante(missaoId, aventureiroId, participacao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{missaoId}/participantes/{aventureiroId}")
    public ResponseEntity<Void> removerParticipante(
            @PathVariable Long missaoId,
            @PathVariable Long aventureiroId
    ) {
        service.removerParticipante(missaoId, aventureiroId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Missao> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusMissao status
    ) {
        return ResponseEntity.ok(service.atualizarStatus(id, status));
    }
}