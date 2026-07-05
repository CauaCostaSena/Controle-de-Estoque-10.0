package br.com.example.controller;

import br.com.example.dao.CategoriaDAO;
import br.com.example.model.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*") // Libera a comunicação com o seu front-end local
@RequestMapping("/categorias")
public class CategoriaController {

    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    // 1. Listar todas as categorias
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        try {
            List<Categoria> lista = categoriaDAO.listarTodas();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 2. Cadastrar uma nova categoria
    @PostMapping
    public ResponseEntity<String> salvarCategoria(@RequestBody Map<String, Object> dados) {
        try {
            String nome = (String) dados.get("nome");

            if (nome == null || nome.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O nome da categoria é obrigatório.");
            }

            Categoria c = new Categoria();
            c.setNome(nome);

            boolean sucesso = categoriaDAO.salvar(c);
            
            if (sucesso) {
                return ResponseEntity.ok("Categoria cadastrada com sucesso!");
            } else {
                return ResponseEntity.badRequest().body("Erro ao cadastrar a categoria no banco de dados.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar requisição: " + e.getMessage());
        }
    }

    // 3. Atualizar uma categoria existente
    @PutMapping("/{id}")
    public ResponseEntity<String> editarCategoria(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        try {
            String nome = (String) dados.get("nome");

            if (nome == null || nome.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O nome da categoria não pode ser vazio.");
            }

            Categoria c = new Categoria();
            c.setId(id);
            c.setNome(nome);

            boolean sucesso = categoriaDAO.atualizar(c);

            if (sucesso) {
                return ResponseEntity.ok("Categoria atualizada com sucesso!");
            } else {
                return ResponseEntity.badRequest().body("Erro ao atualizar a categoria no banco de dados.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar atualização: " + e.getMessage());
        }
    }

    // 4. Deletar uma categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarCategoria(@PathVariable int id) {
        try {
            boolean sucesso = categoriaDAO.excluir(id);
            
            if (sucesso) {
                return ResponseEntity.ok("Categoria excluída com sucesso!");
            } else {
                return ResponseEntity.badRequest().body("Erro ao excluir a categoria. Certifique-se de que não existam produtos vinculados a ela.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar exclusão: " + e.getMessage());
        }
    }
}