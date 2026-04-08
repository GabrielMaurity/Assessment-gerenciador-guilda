package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Models.aventura.*;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import br.com.infnet.guilda_dos_aventureiros.Repository.AventureiroRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.MissaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.OrganizacaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.ParticipacaoMissaoRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/guilda/missoes")
public class MissaoController {

    private final MissaoRepository missaoRepository;
    private final AventureiroRepository aventureiroRepository;
    private final ParticipacaoMissaoRepository participacaoRepository;
    private final OrganizacaoRepository organizacaoRepository;

    public MissaoController(MissaoRepository missaoRepository,
                            AventureiroRepository aventureiroRepository,
                            ParticipacaoMissaoRepository participacaoRepository,
                            OrganizacaoRepository organizacaoRepository) {
        this.missaoRepository = missaoRepository;
        this.aventureiroRepository = aventureiroRepository;
        this.participacaoRepository = participacaoRepository;
        this.organizacaoRepository = organizacaoRepository;
    }

    // Listar missões com filtros
    @GetMapping
    public ResponseEntity<List<Missao>> listarMissoes(
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(required = false) NivelPerigo nivelPerigo,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataTermino,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Missao> resultado = missaoRepository.buscarComFiltros(status, nivelPerigo, dataInicio, dataTermino, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultado.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(resultado.getTotalPages()))
                .body(resultado.getContent());
    }

    // Buscar missão por ID com participantes
    @GetMapping("/{id}")
    public ResponseEntity<Missao> buscarMissao(@PathVariable UUID id) {
        return missaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar missão
    @PostMapping
    public ResponseEntity<Missao> criarMissao(@Valid @RequestBody Missao missao) {
        Organizacao organizacao = organizacaoRepository.findById(missao.getOrganizacao().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organização não encontrada"));

        missao.setOrganizacao(organizacao);
        missao.setStatus(StatusMissao.PLANEJADA);

        return ResponseEntity.status(HttpStatus.CREATED).body(missaoRepository.save(missao));
    }

    // Adicionar participante à missão
    @PostMapping("/{missaoId}/participantes/{aventureiroId}")
    public ResponseEntity<Void> adicionarParticipante(
            @PathVariable UUID missaoId,
            @PathVariable UUID aventureiroId,
            @Valid @RequestBody ParticipacaoMissao participacao
    ) {
        Missao missao = missaoRepository.findById(missaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missão não encontrada"));

        if (missao.getStatus() == StatusMissao.CONCLUIDA || missao.getStatus() == StatusMissao.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missão não está aceitando participantes");
        }

        Aventureiro aventureiro = aventureiroRepository.findById(aventureiroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        if (!aventureiro.getAtivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aventureiro inativo não pode participar de missões");
        }

        if (!aventureiro.getOrganizacao().getId().equals(missao.getOrganizacao().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aventureiro não pertence à mesma organização da missão");
        }

        if (participacaoRepository.existsByMissaoIdAndAventureiroId(missaoId, aventureiroId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aventureiro já participa desta missão");
        }

        ParticipacaoMissaoId id = new ParticipacaoMissaoId(missaoId, aventureiroId);
        participacao.setId(id);
        participacao.setMissao(missao);
        participacao.setAventureiro(aventureiro);

        participacaoRepository.save(participacao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Remover participante da missão
    @DeleteMapping("/{missaoId}/participantes/{aventureiroId}")
    public ResponseEntity<Void> removerParticipante(
            @PathVariable UUID missaoId,
            @PathVariable UUID aventureiroId
    ) {
        ParticipacaoMissaoId id = new ParticipacaoMissaoId(missaoId, aventureiroId);

        if (!participacaoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        participacaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Atualizar status da missão
    @PatchMapping("/{id}/status")
    public ResponseEntity<Missao> atualizarStatus(
            @PathVariable UUID id,
            @RequestParam StatusMissao status
    ) {
        Missao missao = missaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missão não encontrada"));

        missao.setStatus(status);
        return ResponseEntity.ok(missaoRepository.save(missao));
    }
}
