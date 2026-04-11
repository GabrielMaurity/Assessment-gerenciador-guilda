package br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaixaPrecoDto {
    private String faixa;
    private Long quantidade;
}
