package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.MaterializedView.PainelTaticoMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PainelTaticoMissaoRepository
        extends JpaRepository<PainelTaticoMissao, Long> {

    @Query(value = """
        SELECT * FROM operacoes.vw_painel_tatico_missao
        WHERE ultima_atualizacao >= :dataInicio
          AND ultima_atualizacao <= :dataFim
        ORDER BY indice_prontidao DESC
        LIMIT 10
        """, nativeQuery = true)
    List<PainelTaticoMissao> buscarTop10Ultimos15Dias(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}