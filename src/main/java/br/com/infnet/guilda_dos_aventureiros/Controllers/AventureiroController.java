package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Dto.AventureiroResumoDto;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Companheiro;
import br.com.infnet.guilda_dos_aventureiros.Service.AventureiroService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/guilda")
public class AventureiroController {

    private final AventureiroService service;

    public AventureiroController(AventureiroService service) {
        this.service = service;
    }

    @PostMapping("/recrutar-aventureiro")
    public ResponseEntity<Aventureiro> recrutarAventureiro(@Valid @RequestBody Aventureiro aventureiro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.recrutar(aventureiro));
    }

    @GetMapping("/consultar-aventureiro")
    public ResponseEntity<Page<AventureiroResumoDto>> consultarAventureiro(
            @RequestParam(required = false) String classe,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "1") Integer nivelMinimo,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 50) Integer size
    ) {
        var resultado = service.consultarComFiltros(classe, ativo, nivelMinimo, page, size);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultado.getTotalElements()))
                .header("X-Page", String.valueOf(resultado.getNumber()))
                .header("X-Size", String.valueOf(resultado.getSize()))
                .header("X-Total-Pages", String.valueOf(resultado.getTotalPages()))
                .body(resultado);
    }

    @GetMapping("/consultar-aventureiro/{id}")
    public ResponseEntity<Aventureiro> consultarAventureiroPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarPorId(id));
    }

    @PutMapping("/atualizar-aventureiro/{id}")
    public ResponseEntity<Aventureiro> atualizarAventureiro(@PathVariable Long id,
                                                            @Valid @RequestBody Aventureiro novosDados) {
        return ResponseEntity.ok(service.atualizar(id, novosDados));
    }

    @PatchMapping("/encerrar-vinculo/{id}")
    public ResponseEntity<Void> encerrarVinculo(@PathVariable Long id) {
        service.encerrarVinculo(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/recrutar/{id}")
    public ResponseEntity<Void> recrutarNovamente(@PathVariable Long id) {
        service.recrutarNovamente(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/aventureiro/{id}/companheiro")
    public ResponseEntity<Companheiro> definirCompanheiro(@PathVariable Long id,
                                                          @Valid @RequestBody Companheiro novoCompanheiro) {
        return ResponseEntity.ok(service.definirCompanheiro(id, novoCompanheiro));
    }

    @DeleteMapping("/aventureiro/{id}/companheiro")
    public ResponseEntity<Void> removerCompanheiro(@PathVariable Long id) {
        service.removerCompanheiro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-nome")
    public ResponseEntity<Page<AventureiroResumoDto>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "nome") String sort
    ) {
        return ResponseEntity.ok(service.buscarPorNome(nome, page, size, sort));
    }

    @GetMapping("/{id}/perfil-completo")
    public ResponseEntity<Map<String, Object>> buscarPerfilCompleto(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPerfilCompleto(id));
    }
}