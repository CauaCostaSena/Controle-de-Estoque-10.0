package br.com.example.dao;

import br.com.example.model.Categoria;
import br.com.example.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public boolean salvar(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome) VALUES (?)";
       try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Categoria> listarTodas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM categoria";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}