package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Dto.MissaoMetricaDto;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.Missao;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.NivelPerigo;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.StatusMissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface MissaoRepository extends JpaRepository<Missao, UUID> {

    @Query("SELECT m FROM Missao m WHERE " +
            "(:status IS NULL OR m.status = :status) AND " +
            "(:nivelPerigo IS NULL OR m.nivelPerigo = :nivelPerigo) AND " +
            "(:dataInicio IS NULL OR m.dataInicio >= :dataInicio) AND " +
            "(:dataTermino IS NULL OR m.dataTermino <= :dataTermino)")
    Page<Missao> buscarComFiltros(
            @Param("status") StatusMissao status,
            @Param("nivelPerigo") NivelPerigo nivelPerigo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataTermino") LocalDateTime dataTermino,
            Pageable pageable
    );

    @Query("SELECT m.id AS missaoId, m.titulo AS titulo, m.status AS status, m.nivelPerigo AS nivelPerigo, " +
            "COUNT(p) AS quantidadeParticipantes, COALESCE(SUM(p.recompensaOuro), 0) AS totalRecompensasDistribuidas " +
            "FROM Missao m LEFT JOIN m.participacoes p " +
            "WHERE (cast(:dataInicio as timestamp) IS NULL OR m.dataInicio >= :dataInicio) " +
            "AND (cast(:dataTermino as timestamp) IS NULL OR m.dataTermino <= :dataTermino) " +
            "GROUP BY m.id, m.titulo, m.status, m.nivelPerigo")
    Page<MissaoMetricaDto> gerarRelatorioMetricas(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataTermino") LocalDateTime dataTermino,
            Pageable pageable
    );
}
