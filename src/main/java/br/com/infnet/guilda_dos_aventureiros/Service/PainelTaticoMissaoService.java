package br.com.infnet.guilda_dos_aventureiros.Service;

import br.com.infnet.guilda_dos_aventureiros.Models.MaterializedView.PainelTaticoMissao;
import br.com.infnet.guilda_dos_aventureiros.Repository.PainelTaticoMissaoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PainelTaticoMissaoService {

    private final PainelTaticoMissaoRepository repository;

    public PainelTaticoMissaoService(PainelTaticoMissaoRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "topMissoes")
    public List<PainelTaticoMissao> buscarTopMissoesUltimos15Dias() {
        LocalDateTime dataFim = LocalDateTime.now();
        LocalDateTime dataInicio = dataFim.minusDays(15);
        return repository.buscarTop10Ultimos15Dias(dataInicio, dataFim);
    }

    @CacheEvict(value = "topMissoes", allEntries = true)
    @Scheduled(fixedRate = 1800000)
    public void forcarAtualizacaoDoCache() {}
}