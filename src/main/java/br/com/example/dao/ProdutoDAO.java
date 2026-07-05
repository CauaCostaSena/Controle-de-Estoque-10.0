package br.com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.example.model.Produto;
import br.com.example.model.Usuario;
import br.com.example.model.Categoria;
import br.com.example.util.Conexao;

public class ProdutoDAO {

    public boolean salvar(Produto p) {
        String sql = "INSERT INTO produto (nome, quantidade, preco, id_usuario, id_categoria) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getQuantidade());
            stmt.setDouble(3, p.getPreco());
            stmt.setInt(4, p.getUsuario().getId());
            stmt.setInt(5, p.getCategoria().getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        // INNER JOIN para trazer os dados completos do Usuário e da Categoria de cada produto
        String sql = "SELECT p.id AS p_id, p.nome AS p_nome, p.quantidade, p.preco, " +
                     "u.id AS u_id, u.login AS u_login, " +
                     "c.id AS c_id, c.nome AS c_nome " +
                     "FROM produto p " +
                     "INNER JOIN usuario u ON p.id_usuario = u.id " +
                     "INNER JOIN categoria c ON p.id_categoria = c.id " +
                     "ORDER BY p.id DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getInt("p_id"));
                p.setNome(rs.getString("p_nome"));
                p.setQuantidade(rs.getInt("quantidade"));
                p.setPreco(rs.getDouble("preco"));

                Usuario u = new Usuario();
                u.setId(rs.getInt("u_id"));
                u.setLogin(rs.getString("u_login"));
                p.setUsuario(u);

                Categoria c = new Categoria();
                c.setId(rs.getInt("c_id"));
                c.setNome(rs.getString("c_nome"));
                p.setCategoria(c);

                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizar(Produto p) {
        String sql = "UPDATE produto SET nome = ?, quantidade = ?, preco = ?, id_categoria = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getQuantidade());
            stmt.setDouble(3, p.getPreco());
            stmt.setInt(4, p.getCategoria().getId());
            stmt.setInt(5, p.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}