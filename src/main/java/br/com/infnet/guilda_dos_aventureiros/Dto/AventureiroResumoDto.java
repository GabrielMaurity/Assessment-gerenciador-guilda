package br.com.infnet.guilda_dos_aventureiros.Dto;

import br.com.infnet.guilda_dos_aventureiros.Models.core.Aventureiro;
import br.com.infnet.guilda_dos_aventureiros.Models.core.Classe;
import lombok.Data;

import java.util.UUID;

@Data
public class AventureiroResumoDto {
    private Long id;
    private String nome;
    private Classe classe;
    private Integer nivel;
    private Boolean ativo;

    public AventureiroResumoDto(Aventureiro aventureiro) {
        this.id = aventureiro.getId();
        this.nome = aventureiro.getNome();
        this.classe = aventureiro.getClasse();
        this.nivel = aventureiro.getNivel();
        this.ativo = aventureiro.getAtivo();
    }
}
