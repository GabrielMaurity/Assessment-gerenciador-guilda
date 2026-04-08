package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Dto.AventureiroResumoDto;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Companheiro;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Usuario;
import br.com.infnet.guilda_dos_aventureiros.Repository.AventureiroRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.OrganizacaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/guilda")
public class AventureiroController {

    private final AventureiroRepository repository;
    private final OrganizacaoRepository organizacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public AventureiroController(AventureiroRepository repository,
                                 OrganizacaoRepository organizacaoRepository,
                                 UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.organizacaoRepository = organizacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/recrutar-aventureiro")
    public ResponseEntity<Aventureiro> recrutarAventureiro(@Valid @RequestBody Aventureiro aventureiro) {
        Organizacao organizacao = organizacaoRepository.findById(aventureiro.getOrganizacao().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organização não encontrada"));

        Usuario usuario = usuarioRepository.findById(aventureiro.getUsuarioResponsavel().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado"));

        aventureiro.setOrganizacao(organizacao);
        aventureiro.setUsuarioResponsavel(usuario);
        aventureiro.setAtivo(true);

        Aventureiro salvo = repository.save(aventureiro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/consultar-aventureiro")
    public ResponseEntity<List<AventureiroResumoDto>> consultarAventureiro(
            @RequestParam(required = false) String classe,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(defaultValue = "1") Integer nivelMinimo,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Range(min = 1, max = 50) Integer size
    ) {
        Pageable paginacao = PageRequest.of(page, size);
        Page<Aventureiro> resultado = repository.buscarComFiltros(classe, ativo, nivelMinimo, paginacao);

        List<AventureiroResumoDto> dtos = resultado.getContent().stream()
                .map(AventureiroResumoDto::new).toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultado.getTotalElements()))
                .header("X-Page", String.valueOf(resultado.getNumber()))
                .header("X-Size", String.valueOf(resultado.getSize()))
                .header("X-Total-Pages", String.valueOf(resultado.getTotalPages()))
                .body(dtos);
    }

    @GetMapping("/consultar-aventureiro/{id}")
    public ResponseEntity<Aventureiro> consultarAventureiro(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/atualizar-aventureiro/{id}")
    public ResponseEntity<Aventureiro> atualizarAventureiro(@PathVariable UUID id, @Valid @RequestBody Aventureiro novosDados) {
        return repository.findById(id).map(aventureiroExistente -> {
            aventureiroExistente.setNome(novosDados.getNome());
            aventureiroExistente.setClasse(novosDados.getClasse());
            aventureiroExistente.setNivel(novosDados.getNivel());

            Aventureiro atualizado = repository.save(aventureiroExistente);
            return ResponseEntity.ok(atualizado);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Encerrar vínculo com a guilda
    @PatchMapping("/encerrar-vinculo/{id}")
    public ResponseEntity<Void> encerrarVinculo(@PathVariable UUID id) {
        Optional<Aventureiro> aventureiro = repository.findById(id);

        if (aventureiro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        aventureiro.get().setAtivo(false);
        repository.save(aventureiro.get());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/recrutar/{id}")
    public ResponseEntity<Void> recrutarNovamente(@PathVariable UUID id) {
        Optional<Aventureiro> aventureiro = repository.findById(id);

        if (aventureiro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        aventureiro.get().setAtivo(true);
        repository.save(aventureiro.get());
        return ResponseEntity.noContent().build();
    }

    // 7. Definir ou substituir companheiro
    @PutMapping("/aventureiro/{id}/companheiro")
    public ResponseEntity<Companheiro> definirCompanheiro(
            @PathVariable UUID id,
            @Valid @RequestBody Companheiro novoCompanheiro) {
        return repository.findById(id).map(aventureiro -> {
            aventureiro.setCompanheiro(novoCompanheiro);
            Aventureiro salvo = repository.save(aventureiro);
            return ResponseEntity.ok(salvo.getCompanheiro());
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/aventureiro/{id}/companheiro")
    public ResponseEntity<Void> removerCompanheiro(@PathVariable UUID id) {
        Optional<Aventureiro> aventureiro = repository.findById(id);

        if (aventureiro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        aventureiro.get().setCompanheiro(null);
        repository.save(aventureiro.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-nome")
    public ResponseEntity<Page<AventureiroResumoDto>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "nome") String sort
    ) {
        Page<Aventureiro> resultado = repository.findByNomeContainingIgnoreCase(nome, PageRequest.of(page, size, Sort.by(sort)));
        return ResponseEntity.ok(resultado.map(AventureiroResumoDto::new));
    }

    @GetMapping("/{id}/perfil-completo")
    public ResponseEntity<Map<String, Object>> buscarPerfilCompleto(@PathVariable UUID id) {
        return repository.findById(id).map(aventureiro -> {
            Map<String, Object> perfil = new HashMap<>();
            perfil.put("aventureiro", aventureiro);
            perfil.put("totalParticipacoes", repository.contarParticipacoes(id));
            perfil.put("ultimaMissao", repository.buscarUltimaMissao(id).orElse(null));
            return ResponseEntity.ok(perfil);
        }).orElse(ResponseEntity.notFound().build());
    }
}
