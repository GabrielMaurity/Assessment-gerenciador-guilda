package br.com.infnet.guilda_dos_aventureiros.Models.legacy;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "usuarios", schema = "audit")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @ManyToMany
    @JoinTable(
            name = "user_roles", schema = "audit",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
