package br.com.example.dao;

import br.com.example.model.Usuario;
import br.com.example.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    // Método para validar o login
    public Usuario autenticar(String login, String senha) {
        Usuario usuario = null;
        
        // Atualizado para a tabela 'usuario' e coluna 'login'
        String sql = "SELECT id, login FROM usuario WHERE login = ? AND senha = ?";
        
        // Usando o getConnection() correto
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, login);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setLogin(rs.getString("login"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuario; // Retorna nulo se o login ou senha estiverem errados
    }

    // (Opcional) Método para salvar novos usuários, caso você tenha uma tela de registro
    public boolean salvar(Usuario u) {
        String sql = "INSERT INTO usuario (login, senha) VALUES (?, ?)";
        
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getSenha());
            stmt.executeUpdate();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}