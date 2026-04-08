package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Dto.MissaoMetricaDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.RankingAventureiroDto;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.StatusMissao;
import br.com.infnet.guilda_dos_aventureiros.Repository.MissaoRepository;
import br.com.infnet.guilda_dos_aventureiros.Repository.ParticipacaoMissaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/guilda/relatorios")
public class RelatorioController {

    private final ParticipacaoMissaoRepository participacaoRepository;
    private final MissaoRepository missaoRepository;

    public RelatorioController(ParticipacaoMissaoRepository participacaoRepository, MissaoRepository missaoRepository) {
        this.participacaoRepository = participacaoRepository;
        this.missaoRepository = missaoRepository;
    }

    @GetMapping("/ranking")
    public ResponseEntity<Page<RankingAventureiroDto>> obterRanking(
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataTermino,
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(participacaoRepository.gerarRanking(dataInicio, dataTermino, status, PageRequest.of(page, size)));
    }

    @GetMapping("/metricas-missoes")
    public ResponseEntity<Page<MissaoMetricaDto>> obterMetricasMissoes(
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataTermino,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(missaoRepository.gerarRelatorioMetricas(dataInicio, dataTermino, PageRequest.of(page, size)));
    }
}
