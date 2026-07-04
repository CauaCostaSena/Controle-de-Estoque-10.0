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
        return salvarERetornarId(p) > 0;
    }

    public int salvarERetornarId(Produto p) {
        String sql = "INSERT INTO produto (nome_produto, quantidade, preco, id_usuario, id_categoria) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getQuantidade());
            stmt.setDouble(3, p.getPreco());
            stmt.setInt(4, p.getUsuario().getId());
            stmt.setInt(5, obterCategoriaId(conn, p));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = """
                SELECT p.*, u.usuario_login, c.nome_categoria
                FROM produto p
                INNER JOIN usuario u ON p.id_usuario = u.id_usuario
                INNER JOIN categoria c ON p.id_categoria = c.id_categoria
                """;
        try (Connection conn = Conexao.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getInt("id_produto"));
                p.setNome(rs.getString("nome_produto"));
                p.setQuantidade(rs.getInt("quantidade"));
                p.setPreco(rs.getDouble("preco"));
                
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setLogin(rs.getString("usuario_login"));
                p.setUsuario(u);

                Categoria c = new Categoria();
                c.setId(rs.getInt("id_categoria"));
                c.setNome(rs.getString("nome_categoria"));
                p.setCategoria(c);
                
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean atualizar(Produto p) {
        String sql = "UPDATE produto SET nome_produto = ?, quantidade = ?, preco = ?, id_categoria = ? WHERE id_produto = ?";
        try (Connection conn = Conexao.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getQuantidade());
            stmt.setDouble(3, p.getPreco());
            stmt.setInt(4, obterCategoriaId(conn, p));
            stmt.setInt(5, p.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";
        try (Connection conn = Conexao.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Produto buscarPorId(int id) {
        String sql = """
                SELECT p.*, u.usuario_login, c.nome_categoria
                FROM produto p
                INNER JOIN usuario u ON p.id_usuario = u.id_usuario
                INNER JOIN categoria c ON p.id_categoria = c.id_categoria
                WHERE p.id_produto = ?
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("id_produto"));
                    p.setNome(rs.getString("nome_produto"));
                    p.setQuantidade(rs.getInt("quantidade"));
                    p.setPreco(rs.getDouble("preco"));

                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id_usuario"));
                    u.setLogin(rs.getString("usuario_login"));
                    p.setUsuario(u);

                    Categoria c = new Categoria();
                    c.setId(rs.getInt("id_categoria"));
                    c.setNome(rs.getString("nome_categoria"));
                    p.setCategoria(c);

                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int obterCategoriaId(Connection conn, Produto produto) throws SQLException {
        Categoria categoria = produto.getCategoria();
        if (categoria != null && categoria.getId() > 0) {
            return categoria.getId();
        }

        String nomeCategoria = categoria != null && categoria.getNome() != null && !categoria.getNome().isBlank()
                ? categoria.getNome()
                : "Geral";

        String selectSql = "SELECT id_categoria FROM categoria WHERE nome_categoria = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, nomeCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_categoria");
                }
            }
        }

        String insertSql = "INSERT INTO categoria (nome_categoria) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nomeCategoria);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Nao foi possivel obter a categoria do produto.");
    }
}
