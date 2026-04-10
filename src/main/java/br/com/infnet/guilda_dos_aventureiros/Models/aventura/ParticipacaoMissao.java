package br.com.infnet.guilda_dos_aventureiros.Models.aventura;

import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "participacao_missao",
        schema = "aventura",
        uniqueConstraints = @UniqueConstraint(columnNames = {"missao_id", "aventureiro_id"})
)
public class ParticipacaoMissao {

    @EmbeddedId
    private ParticipacaoMissaoId id;

    @ManyToOne
    @MapsId("missaoId")
    @JoinColumn(name = "missao_id", nullable = false)
    private Missao missao;

    @ManyToOne
    @MapsId("aventureiroId")
    @JoinColumn(name = "aventureiro_id", nullable = false)
    private Aventureiro aventureiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false)
    private PapelMissao papelMissao;

    @Column(name = "recompensa_ouro")
    private Integer recompensaOuro;

    @Column(nullable = false)
    private Boolean destaque = false;

    @Column(name = "data_registro", updatable = false)
    private LocalDateTime dataRegistro;

    @PrePersist
    private void prePersist() {
        dataRegistro = LocalDateTime.now();
    }
}
