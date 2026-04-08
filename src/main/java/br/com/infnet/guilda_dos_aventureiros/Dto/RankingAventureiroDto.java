package br.com.infnet.guilda_dos_aventureiros.Dto;

import java.util.UUID;

public interface RankingAventureiroDto {
    UUID getAventureiroId();
    String getNome();
    Long getTotalParticipacoes();
    Double getTotalRecompensas();
    Long getTotalMvps();
}
