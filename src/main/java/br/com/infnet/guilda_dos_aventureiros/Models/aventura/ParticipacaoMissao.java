package br.com.infnet.guilda_dos_aventureiros.Models.aventura;

import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "papel_missao", nullable = false)
    private PapelMissao papelMissao;

    @Min(0)
    @Column(name = "recompensa_ouro")
    private Double recompensaOuro;

    @Column(nullable = false)
    private Boolean mvp = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
