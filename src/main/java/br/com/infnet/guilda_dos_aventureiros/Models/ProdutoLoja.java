package br.com.infnet.guilda_dos_aventureiros.Models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "guilda_loja", createIndex = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoLoja {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "portuguese_custom")
    private String nome;

    @Field(type = FieldType.Text, analyzer = "portuguese_custom")
    private String descricao;

    @Field(type = FieldType.Keyword)
    private String categoria;

    @Field(type = FieldType.Keyword)
    private String raridade;

    @Field(type = FieldType.Double)
    private Double preco;
}
