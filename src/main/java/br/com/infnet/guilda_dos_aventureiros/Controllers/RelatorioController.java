package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Dto.MissaoMetricaDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.RankingAventureiroDto;
import br.com.infnet.guilda_dos_aventureiros.Models.aventura.StatusMissao;
import br.com.infnet.guilda_dos_aventureiros.Service.RelatorioService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/guilda/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/ranking")
    public ResponseEntity<Page<RankingAventureiroDto>> obterRanking(
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataTermino,
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(service.obterRanking(dataInicio, dataTermino, status, page, size));
    }

    @GetMapping("/metricas-missoes")
    public ResponseEntity<Page<MissaoMetricaDto>> obterMetricasMissoes(
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataTermino,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(service.obterMetricasMissoes(dataInicio, dataTermino, page, size));
    }
}