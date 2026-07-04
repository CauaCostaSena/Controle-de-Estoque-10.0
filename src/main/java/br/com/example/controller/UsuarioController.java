package br.com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.example.dao.UsuarioDAO;
import br.com.example.model.Usuario;

@RestController // Avisa o Spring que esta classe responde a requisições da Web
@RequestMapping("/auth") // Prefixo para todas as rotas desta classe
public class UsuarioController {
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @PostMapping("/cadastro") // Mapeia a rota para criar conta
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario u) {
        if (u.getLogin() == null || u.getSenha() == null || u.getLogin().isEmpty() || u.getSenha().isEmpty()) {
            return ResponseEntity.badRequest().body("Login e senha são obrigatórios.");
        }
        
        boolean sucesso = usuarioDAO.cadastrar(u);
        
        if (sucesso) {
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Erro ao cadastrar usuário (usuário pode já existir).");
        }
    }

    @PostMapping("/login") // Mapeia a rota que o seu HTML está tentando acessar
    public ResponseEntity<?> autenticar(@RequestBody Usuario u) {
        if (u.getLogin() == null || u.getSenha() == null || u.getLogin().isEmpty() || u.getSenha().isEmpty()) {
            return ResponseEntity.badRequest().body("Login e senha são obrigatórios.");
        }
        
        Usuario logado = usuarioDAO.login(u.getLogin(), u.getSenha());
        
        if (logado != null) {
            // Retorna os dados do usuário em JSON (sucesso 200) para o HTML salvar no sessionStorage
            return ResponseEntity.ok(logado); 
        } else {
            // Retorna erro 401 (Não Autorizado) para acionar a mensagem vermelha correta
            return ResponseEntity.status(401).body("Login ou senha incorretos.");
        }
    }
}