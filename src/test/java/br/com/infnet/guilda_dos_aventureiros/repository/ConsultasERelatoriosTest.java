package br.com.infnet.guilda_dos_aventureiros.repository;

import br.com.infnet.guilda_dos_aventureiros.Dto.MissaoMetricaDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.RankingAventureiroDto;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.*;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Classe;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Usuario;

import br.com.infnet.guilda_dos_aventureiros.Repository.AventureiroRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.MissaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.OrganizacaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.ParticipacaoMissaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ConsultasERelatoriosTest {

    @Autowired private AventureiroRepository aventureiroRepository;
    @Autowired private MissaoRepository missaoRepository;
    @Autowired private ParticipacaoMissaoRepository participacaoRepository;
    @Autowired private OrganizacaoRepository organizacaoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private Aventureiro aventureiroSalvo;
    private Missao missaoSalva;

    @BeforeEach
    void setUp() {
        Organizacao org = new Organizacao();
        org.setNome("Guilda Teste " + UUID.randomUUID().toString());
        org.setAtivo(true);
        org.setCreatedAt(LocalDateTime.now());
        org = organizacaoRepository.save(org);

        Usuario user = new Usuario();
        user.setNome("Admin");
        user.setEmail("admin_" + UUID.randomUUID().toString().substring(0, 8) + "@guilda.com");
        user.setSenhaHash("admin");
        user.setStatus("ATIVO");
        user.setOrganizacao(org);
        user = usuarioRepository.save(user);

        Aventureiro aventureiro = new Aventureiro();
        aventureiro.setNome("Aragorn");
        aventureiro.setClasse(Classe.GUERREIRO);
        aventureiro.setNivel(10);
        aventureiro.setAtivo(true);
        aventureiro.setOrganizacao(org);
        aventureiro.setUsuarioResponsavel(user);
        aventureiroSalvo = aventureiroRepository.save(aventureiro);

        Missao missao = new Missao();
        missao.setTitulo("Caça ao Dragão");
        missao.setNivelPerigo(NivelPerigo.ALTO);
        missao.setStatus(StatusMissao.CONCLUIDA);
        missao.setOrganizacao(org);
        missao.setDataInicio(LocalDateTime.now().minusDays(2));
        missao.setDataFim(LocalDateTime.now().plusDays(2));
        missaoSalva = missaoRepository.save(missao);

        ParticipacaoMissao participacao = new ParticipacaoMissao();
        participacao.setId(new ParticipacaoMissaoId(missaoSalva.getId(), aventureiroSalvo.getId()));
        participacao.setMissao(missaoSalva);
        participacao.setAventureiro(aventureiroSalvo);
        participacao.setPapelMissao(PapelMissao.LIDER);
        participacao.setRecompensaOuro(500);
        participacao.setDestaque(true);
        participacaoRepository.save(participacao);
    }

    @Test
    void deveBuscarAventureiroPorNomeParcial() {
        Page<Aventureiro> resultado = aventureiroRepository.findByNomeContainingIgnoreCase("arago", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Aragorn");
    }

    @Test
    void deveBuscarUltimaMissaoDoAventureiro() {
        Optional<Missao> ultimaMissao = aventureiroRepository.buscarUltimaMissao(aventureiroSalvo.getId());

        assertThat(ultimaMissao).isPresent();
        assertThat(ultimaMissao.get().getTitulo()).isEqualTo("Caça ao Dragão");
    }

    @Test
    void deveGerarRankingDeAventureiros() {
        Page<RankingAventureiroDto> ranking = participacaoRepository.gerarRanking(
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now().plusYears(1),
                StatusMissao.CONCLUIDA,
                PageRequest.of(0, 10));

        assertThat(ranking.getContent()).isNotEmpty();
        assertThat(ranking.getContent().get(0).getNome()).contains("Aragorn");
    }

    @Test
    void deveGerarMetricasDeMissoes() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(5);
        LocalDateTime fim = LocalDateTime.now().plusDays(5);

        Page<MissaoMetricaDto> metricas = missaoRepository.gerarRelatorioMetricas(
                inicio,
                fim,
                PageRequest.of(0, 10));

        assertThat(metricas.getContent()).isNotEmpty();
        assertThat(metricas.getContent().get(0).getTitulo()).isEqualTo("Caça ao Dragão");
    }
}
