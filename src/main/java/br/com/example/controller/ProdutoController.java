package br.com.example.controller;

import br.com.example.dao.ProdutoDAO;
import br.com.example.model.Produto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin("*") // Libera acesso para o HTML
@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        return ResponseEntity.ok(produtoDAO.listarTodos());
    }

    @PostMapping
    // Mudamos de Map para o objeto Produto direto! O Spring Boot converte tudo sozinho.
    public ResponseEntity<String> salvarProduto(@RequestBody Produto p) {
        try {
            boolean sucesso = produtoDAO.salvar(p);
            
            if (sucesso) {
                return ResponseEntity.ok("Produto cadastrado com sucesso!");
            }
            return ResponseEntity.badRequest().body("Erro ao salvar produto no banco.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro de processamento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editarProduto(@PathVariable int id, @RequestBody Produto p) {
        try {
            p.setId(id); // Garante que estamos atualizando o ID correto da URL
            
            boolean sucesso = produtoDAO.atualizar(p);
            if (sucesso) {
                return ResponseEntity.ok("Produto atualizado com sucesso!");
            }
            return ResponseEntity.badRequest().body("Erro ao atualizar o produto.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarProduto(@PathVariable int id) {
        boolean sucesso = produtoDAO.excluir(id);
        if (sucesso) {
            return ResponseEntity.ok("Excluído com sucesso");
        }
        return ResponseEntity.badRequest().body("Erro ao excluir.");
    }
}