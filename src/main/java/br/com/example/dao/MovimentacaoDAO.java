package br.com.example.dao;

import br.com.example.model.Movimentacao;
import br.com.example.model.Produto;
import br.com.example.model.Usuario;
import br.com.example.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    public boolean salvar(Movimentacao m) {
        // Não enviamos o 'motivo' nem a 'data_hora' (o MySQL gera a data exata sozinho)
        String sqlMov = "INSERT INTO movimentacao (quantidade, tipo, id_produto, id_usuario) VALUES (?, ?, ?, ?)";
        
        // SQL para somar ou subtrair o estoque automaticamente!
        String operador = m.getTipo().equalsIgnoreCase("ENTRADA") ? "+" : "-";
        String sqlProd = "UPDATE produto SET quantidade = quantidade " + operador + " ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false); // Trava o banco para fazer as duas coisas juntas

            // 1. Salva o Log de Auditoria
            try (PreparedStatement stmtMov = conn.prepareStatement(sqlMov)) {
                stmtMov.setInt(1, m.getQuantidade());
                stmtMov.setString(2, m.getTipo().toUpperCase());
                stmtMov.setInt(3, m.getProduto().getId());
                stmtMov.setInt(4, m.getUsuario().getId());
                stmtMov.executeUpdate();
            }

            // 2. Atualiza o número no Estoque
            try (PreparedStatement stmtProd = conn.prepareStatement(sqlProd)) {
                stmtProd.setInt(1, m.getQuantidade());
                stmtProd.setInt(2, m.getProduto().getId());
                stmtProd.executeUpdate();
            }

            conn.commit(); // Confirma as duas operações
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {} // Desfaz se der erro
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (Exception ex) {}
        }
    }

    public List<Movimentacao> listarTodas() {
        List<Movimentacao> lista = new ArrayList<>();
        // Faz JOIN para buscar o Nome do Produto e o Login do Operador
        String sql = "SELECT m.id, m.quantidade, m.data_hora, m.tipo, " +
                     "p.id as id_produto, p.nome as nome_produto, " +
                     "u.id as id_usuario, u.login as login_usuario " +
                     "FROM movimentacao m " +
                     "LEFT JOIN produto p ON m.id_produto = p.id " +
                     "JOIN usuario u ON m.id_usuario = u.id " +
                     "ORDER BY m.data_hora DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movimentacao m = new Movimentacao();
                m.setId(rs.getInt("id"));
                m.setQuantidade(rs.getInt("quantidade"));
                m.setDataHora(rs.getTimestamp("data_hora"));
                m.setTipo(rs.getString("tipo"));

                // Produto (Pode ser nulo se foi deletado, graças à nossa regra SET NULL)
                if (rs.getObject("id_produto") != null) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("id_produto"));
                    p.setNome(rs.getString("nome_produto"));
                    m.setProduto(p);
                }

                // Usuario
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setLogin(rs.getString("login_usuario"));
                m.setUsuario(u);

                lista.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}