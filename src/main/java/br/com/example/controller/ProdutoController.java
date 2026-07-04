package br.com.example.controller;

import br.com.example.dao.ProdutoDAO;
import br.com.example.model.Produto;
import br.com.example.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> lista = produtoDAO.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<String> salvarProduto(@RequestBody Map<String, Object> dados) {
        String nome = (String) dados.get("nome");
        int qtd = Integer.parseInt(dados.get("quantidade").toString());
        double preco = Double.parseDouble(dados.get("preco").toString());
        
        Usuario logado = new Usuario();
        logado.setId(1); 

        boolean sucesso = cadastrarProduto(nome, qtd, preco, logado);
        
        if (sucesso) return ResponseEntity.ok("Produto cadastrado!");
        return ResponseEntity.badRequest().body("Erro ao cadastrar.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editarProduto(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        String nome = (String) dados.get("nome");
        int qtd = Integer.parseInt(dados.get("quantidade").toString());
        double preco = Double.parseDouble(dados.get("preco").toString());
        
        boolean sucesso = atualizarProduto(id, nome, qtd, preco);
        
        if (sucesso) return ResponseEntity.ok("Produto updated!");
        return ResponseEntity.badRequest().body("Erro ao atualizar.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProduto(@PathVariable int id) {
        boolean sucesso = excluirProduto(id);
        if (sucesso) return ResponseEntity.ok("Excluído com sucesso");
        return ResponseEntity.badRequest().body("Erro ao excluir.");
    }

    public boolean cadastrarProduto(String nome, int qtd, double preco, Usuario logado) {
        if (nome.isEmpty() || qtd < 0 || preco <= 0 || logado == null) return false;
        Produto p = new Produto();
        p.setNome(nome);
        p.setQuantidade(qtd);
        p.setPreco(preco);
        p.setUsuario(logado);
        return produtoDAO.salvar(p);
    }

    public boolean atualizarProduto(int id, String nome, int qtd, double preco) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        p.setQuantidade(qtd);
        p.setPreco(preco);
        return produtoDAO.atualizar(p);
    }

    public boolean excluirProduto(int id) {
        return produtoDAO.excluir(id);
    }
}