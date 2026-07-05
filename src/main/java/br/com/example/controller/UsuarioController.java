package br.com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.example.dao.UsuarioDAO;
import br.com.example.model.Usuario;

@CrossOrigin("*") // ESSENCIAL: Evita o erro de bloqueio (Connection Refused / CORS)
@RestController 
@RequestMapping("/auth")
public class UsuarioController {
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario u) {
        if (u.getLogin() == null || u.getSenha() == null || u.getLogin().isEmpty() || u.getSenha().isEmpty()) {
            return ResponseEntity.badRequest().body("Login e senha são obrigatórios.");
        }
        
        boolean sucesso = usuarioDAO.salvar(u);
        
        if (sucesso) {
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Erro ao cadastrar usuário (login já pode existir).");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody Usuario u) {
        if (u.getLogin() == null || u.getSenha() == null || u.getLogin().isEmpty() || u.getSenha().isEmpty()) {
            return ResponseEntity.badRequest().body("Login e senha são obrigatórios.");
        }
        
        Usuario logado = usuarioDAO.autenticar(u.getLogin(), u.getSenha());
        
        if (logado != null) {
            return ResponseEntity.ok(logado); 
        } else {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos.");
        }
    }
}