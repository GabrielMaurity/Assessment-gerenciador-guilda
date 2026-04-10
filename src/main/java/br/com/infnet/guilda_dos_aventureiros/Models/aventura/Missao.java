package br.com.infnet.guilda_dos_aventureiros.Models.aventura;

import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "missao", schema = "aventura")
public class Missao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @NotBlank
    @Column(length = 150, nullable = false)
    private String titulo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_perigo", nullable = false)
    private NivelPerigo nivelPerigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMissao status = StatusMissao.PLANEJADA;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @OneToMany(mappedBy = "missao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipacaoMissao> participacoes = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        dataCriacao = LocalDateTime.now();
    }
}
