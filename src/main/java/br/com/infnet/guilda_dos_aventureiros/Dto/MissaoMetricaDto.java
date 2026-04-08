package br.com.infnet.guilda_dos_aventureiros.Dto;

import java.util.UUID;

public interface MissaoMetricaDto {
    UUID getMissaoId();
    String getTitulo();
    String getStatus();
    String getNivelPerigo();
    Long getQuantidadeParticipantes();
    Double getTotalRecompensasDistribuidas();
}
