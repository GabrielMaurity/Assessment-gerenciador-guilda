package br.com.infnet.guilda_dos_aventureiros.Models.legacy;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "organizacoes", schema = "audit")
public class Organizacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nome;
    private Boolean ativo;
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
