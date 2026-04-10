package br.com.infnet.guilda_dos_aventureiros.Service;

import br.com.infnet.guilda_dos_aventureiros.Dto.MissaoMetricaDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.RankingAventureiroDto;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.StatusMissao;
import br.com.infnet.guilda_dos_aventureiros.Repository.MissaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.ParticipacaoMissaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RelatorioService {

    private final ParticipacaoMissaoRepository participacaoRepository;
    private final MissaoRepository missaoRepository;

    public RelatorioService(ParticipacaoMissaoRepository participacaoRepository,
                            MissaoRepository missaoRepository) {
        this.participacaoRepository = participacaoRepository;
        this.missaoRepository = missaoRepository;
    }

    public Page<RankingAventureiroDto> obterRanking(LocalDateTime dataInicio, LocalDateTime dataTermino,
                                                    StatusMissao status, Integer page, Integer size) {
        return participacaoRepository.gerarRanking(dataInicio, dataTermino, status, PageRequest.of(page, size));
    }

    public Page<MissaoMetricaDto> obterMetricasMissoes(LocalDateTime dataInicio, LocalDateTime dataTermino,
                                                       Integer page, Integer size) {
        return missaoRepository.gerarRelatorioMetricas(dataInicio, dataTermino, PageRequest.of(page, size));
    }
}