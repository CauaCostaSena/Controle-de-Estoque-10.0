package br.com.example.controller;

import br.com.example.dao.ProdutoDAO;
import br.com.example.model.Produto;
import br.com.example.model.Usuario;
import br.com.example.model.Categoria;
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
        return ResponseEntity.ok(produtoDAO.listarTodos());
    }

    @PostMapping
    public ResponseEntity<String> salvarProduto(@RequestBody Map<String, Object> dados) {
        try {
            String nome = (String) dados.get("nome");
            int qtd = Integer.parseInt(dados.get("quantidade").toString());
            double preco = Double.parseDouble(dados.get("preco").toString());
            
            int idUsuario = Integer.parseInt(dados.get("idUsuario").toString());
            int idCategoria = Integer.parseInt(dados.get("idCategoria").toString());
            
            Produto p = new Produto();
            p.setNome(nome);
            p.setQuantidade(qtd);
            p.setPreco(preco);
            
            Usuario u = new Usuario();
            u.setId(idUsuario);
            p.setUsuario(u);
            
            Categoria c = new Categoria();
            c.setId(idCategoria);
            p.setCategoria(c);

            boolean sucesso = produtoDAO.salvar(p);
            if (sucesso) return ResponseEntity.ok("Produto cadastrado com sucesso!");
            return ResponseEntity.badRequest().body("Erro ao cadastrar no banco de dados.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de processamento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editarProduto(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        try {
            String nome = (String) dados.get("nome");
            int qtd = Integer.parseInt(dados.get("quantidade").toString());
            double preco = Double.parseDouble(dados.get("preco").toString());
            int idCategoria = Integer.parseInt(dados.get("idCategoria").toString());
            
            Produto p = new Produto();
            p.setId(id);
            p.setNome(nome);
            p.setQuantidade(qtd);
            p.setPreco(preco);
            
            Categoria c = new Categoria();
            c.setId(idCategoria);
            p.setCategoria(c);
            
            boolean sucesso = produtoDAO.atualizar(p);
            if (sucesso) return ResponseEntity.ok("Produto atualizado com sucesso!");
            return ResponseEntity.badRequest().body("Erro ao atualizar o produto.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProduto(@PathVariable int id) {
        boolean sucesso = produtoDAO.excluir(id);
        if (sucesso) return ResponseEntity.ok("Excluído com sucesso");
        return ResponseEntity.badRequest().body("Erro ao excluir.");
    }
}