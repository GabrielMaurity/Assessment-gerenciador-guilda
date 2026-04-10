package br.com.infnet.guilda_dos_aventureiros.Service;

import br.com.infnet.guilda_dos_aventureiros.Models.aventura.*;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import br.com.infnet.guilda_dos_aventureiros.Repository.AventureiroRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.MissaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.OrganizacaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.ParticipacaoMissaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class MissaoService {

    private final MissaoRepository missaoRepository;
    private final AventureiroRepository aventureiroRepository;
    private final ParticipacaoMissaoRepository participacaoRepository;
    private final OrganizacaoRepository organizacaoRepository;

    public MissaoService(MissaoRepository missaoRepository,
                         AventureiroRepository aventureiroRepository,
                         ParticipacaoMissaoRepository participacaoRepository,
                         OrganizacaoRepository organizacaoRepository) {
        this.missaoRepository = missaoRepository;
        this.aventureiroRepository = aventureiroRepository;
        this.participacaoRepository = participacaoRepository;
        this.organizacaoRepository = organizacaoRepository;
    }

    public Page<Missao> listarComFiltros(StatusMissao status, NivelPerigo nivelPerigo,
                                         LocalDateTime dataInicio, LocalDateTime dataTermino,
                                         Integer page, Integer size) {
        return missaoRepository.buscarComFiltros(status, nivelPerigo, dataInicio, dataTermino,
                PageRequest.of(page, size));
    }

    // Alterado de UUID para Long
    public Missao buscarPorId(Long id) {
        return missaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missão não encontrada"));
    }

    public Missao criar(Missao missao) {
        Organizacao organizacao = organizacaoRepository.findById(missao.getOrganizacao().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organização não encontrada"));

        missao.setOrganizacao(organizacao);
        missao.setStatus(StatusMissao.PLANEJADA);

        return missaoRepository.save(missao);
    }

    // Alterado missaoId e aventureiroId de UUID para Long
    public void adicionarParticipante(Long missaoId, Long aventureiroId, ParticipacaoMissao participacao) {
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

        // Certifique-se que o construtor de ParticipacaoMissaoId agora aceita dois Longs
        ParticipacaoMissaoId id = new ParticipacaoMissaoId(missaoId, aventureiroId);
        participacao.setId(id);
        participacao.setMissao(missao);
        participacao.setAventureiro(aventureiro);

        participacaoRepository.save(participacao);
    }

    // Alterado missaoId e aventureiroId de UUID para Long
    public void removerParticipante(Long missaoId, Long aventureiroId) {
        ParticipacaoMissaoId id = new ParticipacaoMissaoId(missaoId, aventureiroId);

        if (!participacaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participação não encontrada");
        }

        participacaoRepository.deleteById(id);
    }

    // Alterado de UUID para Long
    public Missao atualizarStatus(Long id, StatusMissao status) {
        Missao missao = missaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Missão não encontrada"));

        missao.setStatus(status);
        return missaoRepository.save(missao);
    }
}