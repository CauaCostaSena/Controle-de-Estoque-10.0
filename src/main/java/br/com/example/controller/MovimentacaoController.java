package br.com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.example.dao.MovimentacaoDAO;
import br.com.example.model.Movimentacao;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes") 
@CrossOrigin("*") 
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
        
        // ALTERADO: Agora recebemos uma String (texto) do DAO em vez de boolean!
        String resultado = dao.salvar(mov);
        
        // Verificamos o que o DAO nos respondeu
        if (resultado.equals("sucesso")) {
            return ResponseEntity.ok("Movimentação registrada com sucesso!");
        } else {
            // Se não for "sucesso", é porque a trava de estoque bloqueou ou deu outro erro.
            // Retornamos a mensagem exata que veio do DAO (ex: "Erro: Estoque insuficiente...")
            return ResponseEntity.badRequest().body(resultado);
        }
    }
}