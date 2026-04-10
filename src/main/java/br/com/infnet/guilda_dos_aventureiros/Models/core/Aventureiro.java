package br.com.infnet.guilda_dos_aventureiros.Models.core;

import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Organizacao;
import br.com.infnet.guilda_dos_aventureiros.Models.legacy.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "aventureiro", schema = "aventura")
public class Aventureiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @ManyToOne
    @JoinColumn(name = "usuario_cadastro_id", nullable = false)
    private Usuario usuarioResponsavel;

    @NotBlank
    @Column(length = 120, nullable = false)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Classe classe;

    @Min(1)
    @Column(nullable = false)
    private Integer nivel;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToOne(mappedBy = "aventureiro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Companheiro companheiro;

    @PrePersist
    private void prePersist() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
