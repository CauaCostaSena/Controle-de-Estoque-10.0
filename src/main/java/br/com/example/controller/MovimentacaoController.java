package br.com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.example.dao.MovimentacaoDAO;
import br.com.example.model.Movimentacao;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes") // Resolve o Erro 404!
@CrossOrigin("*") // Permite o acesso do HTML
public class MovimentacaoController {

    private MovimentacaoDAO dao = new MovimentacaoDAO();

    @GetMapping
    public ResponseEntity<List<Movimentacao>> listar() {
        return ResponseEntity.ok(dao.listarTodas());
    }

    @PostMapping
    public ResponseEntity<String> salvar(@RequestBody Movimentacao mov) {
        if (mov.getQuantidade() <= 0) {
            return ResponseEntity.badRequest().body("A quantidade deve ser maior que zero.");
        }
        
        boolean sucesso = dao.salvar(mov);
        
        if (sucesso) {
            return ResponseEntity.ok("Movimentação registrada com sucesso!");
        } else {
            return ResponseEntity.internalServerError().body("Erro ao registrar movimentação.");
        }
    }
}