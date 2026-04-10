package br.com.infnet.guilda_dos_aventureiros.Service;

import br.com.infnet.guilda_dos_aventureiros.Dto.AventureiroResumoDto;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Companheiro;
import br.com.infnet.guilda_dos_aventureiros.Repository.AventureiroRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.OrganizacaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.HashMap;

@Service
public class AventureiroService {

    private final AventureiroRepository repository;
    private final OrganizacaoRepository organizacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public AventureiroService(AventureiroRepository repository,
                              OrganizacaoRepository organizacaoRepository,
                              UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.organizacaoRepository = organizacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Aventureiro recrutar(Aventureiro aventureiro) {
        var organizacao = organizacaoRepository.findById(aventureiro.getOrganizacao().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organização não encontrada"));

        var usuario = usuarioRepository.findById(aventureiro.getUsuarioResponsavel().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado"));

        aventureiro.setOrganizacao(organizacao);
        aventureiro.setUsuarioResponsavel(usuario);
        aventureiro.setAtivo(true);

        return repository.save(aventureiro);
    }

    public Page<AventureiroResumoDto> consultarComFiltros(String classe, Boolean ativo,
                                                          Integer nivelMinimo, Integer page, Integer size) {
        var paginacao = PageRequest.of(page, size);
        return repository.buscarComFiltros(classe, ativo, nivelMinimo, paginacao)
                .map(AventureiroResumoDto::new);
    }

    public Aventureiro consultarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));
    }

    public Aventureiro atualizar(Long id, Aventureiro novosDados) {
        var existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        existente.setNome(novosDados.getNome());
        existente.setClasse(novosDados.getClasse());
        existente.setNivel(novosDados.getNivel());

        return repository.save(existente);
    }

    public void encerrarVinculo(Long id) {
        var aventureiro = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        aventureiro.setAtivo(false);
        repository.save(aventureiro);
    }

    public void recrutarNovamente(Long id) {
        var aventureiro = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        aventureiro.setAtivo(true);
        repository.save(aventureiro);
    }

    public Companheiro definirCompanheiro(Long id, Companheiro novoCompanheiro) {
        var aventureiro = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        aventureiro.setCompanheiro(novoCompanheiro);
        return repository.save(aventureiro).getCompanheiro();
    }

    public void removerCompanheiro(Long id) {
        var aventureiro = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        aventureiro.setCompanheiro(null);
        repository.save(aventureiro);
    }

    public Page<AventureiroResumoDto> buscarPorNome(String nome, Integer page, Integer size, String sort) {
        return repository.findByNomeContainingIgnoreCase(nome, PageRequest.of(page, size, Sort.by(sort)))
                .map(AventureiroResumoDto::new);
    }

    public Map<String, Object> buscarPerfilCompleto(Long id) {
        var aventureiro = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aventureiro não encontrado"));

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("aventureiro", aventureiro);
        perfil.put("totalParticipacoes", repository.contarParticipacoes(id));
        perfil.put("ultimaMissao", repository.buscarUltimaMissao(id).orElse(null));
        return perfil;
    }
}