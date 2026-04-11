package br.com.infnet.guilda_dos_aventureiros.Controllers;

import br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda.ContagemDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda.FaixaPrecoDto;
import br.com.infnet.guilda_dos_aventureiros.Dto.LojaGuilda.PrecoMedioDto;
import br.com.infnet.guilda_dos_aventureiros.Models.ProdutoLoja;
import br.com.infnet.guilda_dos_aventureiros.Service.ProdutoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loja")
@RequiredArgsConstructor
public class ProdutoLojaController {

    private final ProdutoLojaService produtoLojaService;

    // Busca Textual
    @GetMapping("/buscar/nome")
    public List<ProdutoLoja> buscarPorNome(@RequestParam String nome) {
        return produtoLojaService.buscarPorNome(nome);
    }

    @GetMapping("/buscar/descricao")
    public List<ProdutoLoja> buscarPorDescricao(@RequestParam String descricao) {
        return produtoLojaService.buscarPorDescricao(descricao);
    }

    @GetMapping("/buscar/frase")
    public List<ProdutoLoja> buscarPorFraseExata(@RequestParam String frase) {
        return produtoLojaService.buscarPorFraseExata(frase);
    }

    @GetMapping("/buscar/fuzzy")
    public List<ProdutoLoja> buscarFuzzy(@RequestParam String termo) {
        return produtoLojaService.buscarFuzzy(termo);
    }

    @GetMapping("/buscar/multiplos-campos")
    public List<ProdutoLoja> buscarEmMultiplosCampos(@RequestParam String termo) {
        return produtoLojaService.buscarEmMultiplosCampos(termo);
    }

    // Busca com filtros
    @GetMapping("/buscar/nome-categoria")
    public List<ProdutoLoja> buscarPorNomeECategoria(@RequestParam String nome,
                                                     @RequestParam String categoria) {
        return produtoLojaService.buscarPorNomeECategoria(nome, categoria);
    }

    @GetMapping("/buscar/preco")
    public List<ProdutoLoja> buscarPorFaixaDePreco(@RequestParam Double min,
                                                   @RequestParam Double max) {
        return produtoLojaService.buscarPorFaixaDePreco(min, max);
    }

    @GetMapping("/buscar/combinada")
    public List<ProdutoLoja> buscarCombinada(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String raridade,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax) {
        return produtoLojaService.buscarCombinada(categoria, raridade, precoMin, precoMax);
    }

    // Agregações
    @GetMapping("/agregacoes/categorias")
    public List<ContagemDto> contagemPorCategoria() {
        return produtoLojaService.contagemPorCategoria();
    }

    @GetMapping("/agregacoes/raridades")
    public List<ContagemDto> contagemPorRaridade() {
        return produtoLojaService.contagemPorRaridade();
    }

    @GetMapping("/agregacoes/preco-medio")
    public PrecoMedioDto precoMedio() {
        return produtoLojaService.precoMedio();
    }

    @GetMapping("/agregacoes/faixas-preco")
    public List<FaixaPrecoDto> faixasDePreco() {
        return produtoLojaService.faixasDePreco();
    }
}
