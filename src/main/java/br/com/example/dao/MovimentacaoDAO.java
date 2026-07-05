package br.com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.example.model.Movimentacao;
import br.com.example.model.Produto;
import br.com.example.model.Usuario;
import br.com.example.util.Conexao;

public class MovimentacaoDAO {

    public boolean salvar(Movimentacao m) {
        String sql = "INSERT INTO movimentacao (quantidade, tipo, motivo, id_produto, id_usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getQuantidade());
            stmt.setString(2, m.getTipo());
            stmt.setString(3, m.getMotivo());
            stmt.setInt(4, m.getProduto() != null ? m.getProduto().getId() : 1);
            stmt.setInt(5, m.getUsuario() != null ? m.getUsuario().getId() : 1);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Movimentacao> listarTodas() {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "SELECT m.id AS m_id, m.quantidade, m.data_hora, m.tipo, m.motivo, " +
                     "p.id AS p_id, p.nome AS p_nome, " +
                     "u.id AS u_id, u.login " +
                     "FROM movimentacao m " +
                     "INNER JOIN produto p ON m.id_produto = p.id " +
                     "INNER JOIN usuario u ON m.id_usuario = u.id " +
                     "ORDER BY m.data_hora DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Movimentacao m = new Movimentacao();
                m.setId(rs.getInt("m_id"));
                m.setQuantidade(rs.getInt("quantidade"));
                m.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                m.setTipo(rs.getString("tipo"));
                m.setMotivo(rs.getString("motivo"));

                Produto p = new Produto();
                p.setId(rs.getInt("p_id"));
                p.setNome(rs.getString("p_nome"));
                m.setProduto(p);

                Usuario u = new Usuario();
                u.setId(rs.getInt("u_id"));
                u.setLogin(rs.getString("login"));
                m.setUsuario(u);

                lista.add(lista.size(), m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}