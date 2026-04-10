package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Dto.RankingAventureiroDto;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.ParticipacaoMissao;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.ParticipacaoMissaoId;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.StatusMissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipacaoMissaoRepository extends JpaRepository<ParticipacaoMissao, ParticipacaoMissaoId> {

    boolean existsByMissaoIdAndAventureiroId(Long missaoId, Long aventureiroId);

    List<ParticipacaoMissao> findByAventureiroId(UUID aventureiroId);

    @Query("SELECT p.aventureiro.id AS aventureiroId, p.aventureiro.nome AS nome, " +
            "COUNT(p) AS totalParticipacoes, SUM(p.recompensaOuro) AS totalRecompensas, " +
            "SUM(CASE WHEN p.destaque = true THEN 1 ELSE 0 END) AS totalDestaques " +
            "FROM ParticipacaoMissao p " +
            "WHERE (cast(:dataInicio as timestamp) IS NULL OR p.missao.dataInicio >= :dataInicio) " +
            "AND (cast(:dataFim as timestamp) IS NULL OR p.missao.dataFim <= :dataFim) " +
            "AND (cast(:status as string) IS NULL OR p.missao.status = :status) " +
            "GROUP BY p.aventureiro.id, p.aventureiro.nome " +
            "ORDER BY totalParticipacoes DESC")
    Page<RankingAventureiroDto> gerarRanking(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("status") StatusMissao status,
            Pageable pageable
    );
}
