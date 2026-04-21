package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.ProdutoLoja;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProdutoLojaRepository
        extends ElasticsearchRepository<ProdutoLoja, String> {

    List<ProdutoLoja> findByNome(String nome);

    List<ProdutoLoja> findByCategoria(String categoria);

    List<ProdutoLoja> findByRaridade(String raridade);

    List<ProdutoLoja> findByPrecoBetween(Double min, Double max);

    List<ProdutoLoja> findByCategoriaAndRaridade(String categoria, String raridade);
}
