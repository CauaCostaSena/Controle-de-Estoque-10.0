package br.com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.example.model.Movimentacao;
import br.com.example.model.Produto;
import br.com.example.util.Conexao;

public class MovimentacaoDAO {

    public boolean registrar(Movimentacao mov) {
        String sql = "INSERT INTO movimentacao (nome_produto, quantidade, tipo, id_produto, id_usuario) VALUES (?, ?, ?, ?, ?)";
        String sqlAtualizaEstoque = mov.getTipo().equalsIgnoreCase("ENTRADA") 
            ? "UPDATE produto SET quantidade = quantidade + ? WHERE id_produto = ?"
            : "UPDATE produto SET quantidade = quantidade - ? WHERE id_produto = ?";

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement stmtMov = conn.prepareStatement(sql);
                 PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizaEstoque)) {
                
                stmtMov.setString(1, mov.getProduto().getNome());
                stmtMov.setInt(2, mov.getQuantidade());
                stmtMov.setString(3, mov.getTipo().toUpperCase());
                stmtMov.setInt(4, mov.getProduto().getId());
                stmtMov.setInt(5, obterUsuarioId(conn, mov.getProduto().getId()));
                stmtMov.executeUpdate();

                stmtEstoque.setInt(1, mov.getQuantidade());
                stmtEstoque.setInt(2, mov.getProduto().getId());
                stmtEstoque.executeUpdate();

                conn.commit(); 
                return true;
            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Movimentacao> listarPorProduto(int produtoId) {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimentacao WHERE id_produto = ? ORDER BY data_hora DESC";
        try (Connection conn = Conexao.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movimentacao m = new Movimentacao();
                    m.setId(rs.getInt("id_movimentacao"));
                    m.setTipo(rs.getString("tipo"));
                    m.setQuantidade(rs.getInt("quantidade"));
                    m.setDataMovimentacao(rs.getTimestamp("data_hora"));

                    Produto p = new Produto();
                    p.setId(rs.getInt("id_produto"));
                    p.setNome(rs.getString("nome_produto"));
                    m.setProduto(p);

                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean registrarHistorico(Movimentacao mov, int usuarioId) {
        String sql = "INSERT INTO movimentacao (nome_produto, quantidade, tipo, id_produto, id_usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mov.getProduto().getNome());
            stmt.setInt(2, mov.getQuantidade());
            stmt.setString(3, mov.getTipo().toUpperCase());
            stmt.setInt(4, mov.getProduto().getId());
            stmt.setInt(5, usuarioId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Movimentacao> listarTodas() {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimentacao ORDER BY data_hora DESC";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movimentacao m = new Movimentacao();
                m.setId(rs.getInt("id_movimentacao"));
                m.setTipo(rs.getString("tipo"));
                m.setQuantidade(rs.getInt("quantidade"));
                m.setDataMovimentacao(rs.getTimestamp("data_hora"));

                Produto p = new Produto();
                p.setId(rs.getInt("id_produto"));
                p.setNome(rs.getString("nome_produto"));
                m.setProduto(p);

                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private int obterUsuarioId(Connection conn, int produtoId) throws SQLException {
        String sql = "SELECT id_usuario FROM produto WHERE id_produto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        }
        throw new SQLException("Produto nao encontrado para registrar movimentacao: " + produtoId);
    }
}
