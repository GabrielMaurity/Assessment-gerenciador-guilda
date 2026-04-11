package br.com.infnet.guilda_dos_aventureiros.Repository;

import br.com.infnet.guilda_dos_aventureiros.Models.ProdutoLoja;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProdutoLojaRepository
        extends ElasticsearchRepository<ProdutoLoja, String> {

    // Busca por nome (text search)
    List<ProdutoLoja> findByNome(String nome);

    // Filtro exato por categoria (keyword)
    List<ProdutoLoja> findByCategoria(String categoria);

    // Filtro exato por raridade (keyword)
    List<ProdutoLoja> findByRaridade(String raridade);

    // Filtro por preço máximo
    List<ProdutoLoja> findByPrecoBetween(Double min, Double max);

    // Combinação de filtros
    List<ProdutoLoja> findByCategoriaAndRaridade(String categoria, String raridade);
}
