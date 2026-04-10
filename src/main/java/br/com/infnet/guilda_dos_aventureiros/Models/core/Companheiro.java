package br.com.infnet.guilda_dos_aventureiros.Models.core;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "companheiro", schema = "aventura")
public class Companheiro {

    @Id
    @Column(name = "aventureiro_id")
    private Long aventureiroId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "aventureiro_id")
    private Aventureiro aventureiro;

    @Column(length = 120, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    @Min(0) @Max(100)
    @Column(name = "indice_lealdade", nullable = false)
    private Integer lealdade;
}
