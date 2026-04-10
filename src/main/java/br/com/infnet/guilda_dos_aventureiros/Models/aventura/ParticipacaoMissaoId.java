package br.com.infnet.guilda_dos_aventureiros.Models.aventura;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ParticipacaoMissaoId implements Serializable {

    @Column(name = "missao_id")
    private Long missaoId;

    @Column(name = "aventureiro_id")
    private Long aventureiroId;
}
