package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.aventura.Missao;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AventureiroRepository extends JpaRepository<Aventureiro, Long> {

    @Query("SELECT a FROM Aventureiro a WHERE " +
            "(:classe IS NULL OR a.classe = :classe) AND " +
            "(:ativo IS NULL OR a.ativo = :ativo) AND " +
            "(a.nivel >= :nivelMinimo)")
    Page<Aventureiro> buscarComFiltros(String classe, Boolean ativo, Integer nivelMinimo, Pageable pageable);

    Page<Aventureiro> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    @Query("SELECT p.missao FROM ParticipacaoMissao p WHERE p.aventureiro.id = :aventureiroId ORDER BY p.dataRegistro DESC LIMIT 1")
    Optional<Missao> buscarUltimaMissao(@Param("aventureiroId") Long aventureiroId);

    @Query("SELECT COUNT(p) FROM ParticipacaoMissao p WHERE p.aventureiro.id = :aventureiroId")
    Long contarParticipacoes(@Param("aventureiroId") Long aventureiroId);
}
