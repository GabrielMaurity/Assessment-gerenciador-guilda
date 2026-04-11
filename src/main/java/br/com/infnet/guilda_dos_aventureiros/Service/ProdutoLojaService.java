package br.com.infnet.guilda_dos_aventureiros.Service;

import br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda.ContagemDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda.FaixaPrecoDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda.PrecoMedioDto;
import br.com.infnet.guilda_dos_aventureiros.Models.ProdutoLoja;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoLojaService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<ProdutoLoja> buscarPorNome(String nome) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("nome")
                                .query(nome)))
                .build();

        return executarBusca(query);
    }

    public List<ProdutoLoja> buscarPorDescricao(String descricao) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("descricao")
                                .query(descricao)))
                .build();

        return executarBusca(query);
    }

    public List<ProdutoLoja> buscarPorFraseExata(String frase) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .matchPhrase(m -> m
                                .field("nome")
                                .query(frase)))
                .build();

        return executarBusca(query);
    }

    public List<ProdutoLoja> buscarFuzzy(String termo) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("nome")
                                .query(termo)
                                .fuzziness("AUTO")))
                .build();

        return executarBusca(query);
    }

    public List<ProdutoLoja> buscarEmMultiplosCampos(String termo) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("nome", "descricao")
                                .query(termo)))
                .build();

        return executarBusca(query);
    }


    private List<ProdutoLoja> executarBusca(Query query) {
        return elasticsearchOperations
                .search(query, ProdutoLoja.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    // BUSCAS COM FILTRO
    public List<ProdutoLoja> buscarPorNomeECategoria(String nome, String categoria) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> b
                                .must(m -> m
                                        .match(match -> match
                                                .field("nome")
                                                .query(nome)))
                                .filter(f -> f
                                        .term(t -> t
                                                .field("categoria")
                                                .value(categoria)))))
                .build();

        return executarBusca(query);
    }


    public List<ProdutoLoja> buscarPorFaixaDePreco(Double min, Double max) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .range(r -> r
                                                .number(n -> n
                                                        .field("preco")
                                                        .gte(min)
                                                        .lte(max))))))
                .build();

        return executarBusca(query);
    }


    public List<ProdutoLoja> buscarCombinada(String categoria, String raridade,
                                             Double precoMin, Double precoMax) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> {
                            if (categoria != null) {
                                b.filter(f -> f
                                        .term(t -> t
                                                .field("categoria")
                                                .value(categoria)));
                            }
                            if (raridade != null) {
                                b.filter(f -> f
                                        .term(t -> t
                                                .field("raridade")
                                                .value(raridade)));
                            }
                            if (precoMin != null && precoMax != null) {
                                b.filter(f -> f
                                        .range(r -> r
                                                .number(n -> n
                                                        .field("preco")
                                                        .gte(precoMin)
                                                        .lte(precoMax))));
                            }
                            return b;
                        }))
                .build();

        return executarBusca(query);
    }

    // AGREGAÇÕES
    public List<ContagemDto> contagemPorCategoria() {
        Query query = NativeQuery.builder()
                .withAggregation("por_categoria", Aggregation.of(a -> a
                        .terms(t -> t.field("categoria").size(50))))
                .build();

        return extrairContagemAgregacao(query, "por_categoria");
    }

    public List<ContagemDto> contagemPorRaridade() {
        Query query = NativeQuery.builder()
                .withAggregation("por_raridade", Aggregation.of(a -> a
                        .terms(t -> t.field("raridade").size(50))))
                .build();

        return extrairContagemAgregacao(query, "por_raridade");
    }

    public PrecoMedioDto precoMedio() {
        Query query = NativeQuery.builder()
                .withAggregation("preco_medio", Aggregation.of(a -> a
                        .avg(avg -> avg.field("preco"))))
                .build();

        SearchHits<ProdutoLoja> hits = elasticsearchOperations.search(query, ProdutoLoja.class);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();

        double valor = aggregations.aggregationsAsMap()
                .get("preco_medio")
                .aggregation()
                .getAggregate()
                .avg()
                .value();

        return PrecoMedioDto.builder().precoMedio(valor).build();
    }


    public List<FaixaPrecoDto> faixasDePreco() {
        Query query = NativeQuery.builder()
                .withAggregation("faixas_preco", Aggregation.of(a -> a
                        .range(r -> r
                                .field("preco")
                                .ranges(

                                        AggregationRange.of(faixa -> faixa.to(100.0)),
                                        AggregationRange.of(faixa -> faixa.from(100.0).to(300.0)),
                                        AggregationRange.of(faixa -> faixa.from(300.0).to(700.0)),
                                        AggregationRange.of(faixa -> faixa.from(700.0))
                                )
                        )))
                .build();

        List<String> nomes = List.of("Abaixo de 100", "De 100 a 300", "De 300 a 700", "Acima de 700");

        SearchHits<ProdutoLoja> hits = elasticsearchOperations.search(query, ProdutoLoja.class);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();

        List<RangeBucket> buckets = aggregations.aggregationsAsMap()
                .get("faixas_preco")
                .aggregation()
                .getAggregate()
                .range()
                .buckets()
                .array();

        List<FaixaPrecoDto> resultado = new ArrayList<>();
        for (int i = 0; i < buckets.size(); i++) {
            resultado.add(FaixaPrecoDto.builder()
                    .faixa(nomes.get(i))
                    .quantidade(buckets.get(i).docCount())
                    .build());
        }

        return resultado;
    }

    private List<ContagemDto> extrairContagemAgregacao(Query query, String nomeAgregacao) {
        SearchHits<ProdutoLoja> hits = elasticsearchOperations.search(query, ProdutoLoja.class);
        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) hits.getAggregations();

        return aggregations.aggregationsAsMap()
                .get(nomeAgregacao)
                .aggregation()
                .getAggregate()
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(bucket -> ContagemDto.builder()
                        .chave(bucket.key().stringValue())
                        .quantidade(bucket.docCount())
                        .build())
                .toList();
    }
}
