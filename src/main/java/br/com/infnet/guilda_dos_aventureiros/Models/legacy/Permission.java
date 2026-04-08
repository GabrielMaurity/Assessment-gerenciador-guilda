package br.com.infnet.guilda_dos_aventureiros.Models.legacy;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "permissions", schema = "audit")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String descricao;
}
