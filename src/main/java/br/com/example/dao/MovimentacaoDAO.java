package br.com.example.dao;

import br.com.example.model.Movimentacao;
import br.com.example.model.Produto;
import br.com.example.model.Usuario;
import br.com.example.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    // ALTERADO: Agora retorna String para mandar mensagens de erro precisas (ex: falta de estoque)
    public String salvar(Movimentacao m) {
        // 1. Adicionado nome_categoria no INSERT (agora são 7 parâmetros)
        String sqlMov = "INSERT INTO movimentacao (quantidade, tipo, id_produto, id_usuario, nome_produto, nome_usuario, nome_categoria) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // 2. SQL inteligente para atualizar estoque com trava de segurança para SAÍDA
        String sqlProd;
        if (m.getTipo().equalsIgnoreCase("ENTRADA") || m.getTipo().equalsIgnoreCase("AJUSTE")) {
            sqlProd = "UPDATE produto SET quantidade = quantidade + ? WHERE id = ?";
        } else {
            // É SAÍDA: Só permite subtrair se o estoque atual aguentar a retirada! (quantidade >= ?)
            sqlProd = "UPDATE produto SET quantidade = quantidade - ? WHERE id = ? AND quantidade >= ?";
        }

        Connection conn = null;
        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false); // Trava o banco para transação segura

            // -----------------------------------------------------------------
            // AUTOPREENCHIMENTO INTELIGENTE DOS NOMES (SNAPSHOT)
            // -----------------------------------------------------------------
            if ((m.getNomeProduto() == null || m.getNomeProduto().trim().isEmpty()) && m.getProduto() != null && m.getProduto().getId() > 0) {
                // Busca Produto e Categoria de uma vez só usando JOIN!
                String sqlFetchProd = "SELECT p.nome as prod_nome, c.nome as cat_nome FROM produto p LEFT JOIN categoria c ON p.id_categoria = c.id WHERE p.id = ?";
                try (PreparedStatement stmtFP = conn.prepareStatement(sqlFetchProd)) {
                    stmtFP.setInt(1, m.getProduto().getId());
                    try (ResultSet rsFP = stmtFP.executeQuery()) {
                        if (rsFP.next()) {
                            m.setNomeProduto(rsFP.getString("prod_nome"));
                            m.setNomeCategoria(rsFP.getString("cat_nome")); // Salva a categoria também
                        }
                    }
                }
            }

            if ((m.getNomeUsuario() == null || m.getNomeUsuario().trim().isEmpty()) && m.getUsuario() != null && m.getUsuario().getId() > 0) {
                String sqlFetchUser = "SELECT login FROM usuario WHERE id = ?";
                try (PreparedStatement stmtFU = conn.prepareStatement(sqlFetchUser)) {
                    stmtFU.setInt(1, m.getUsuario().getId());
                    try (ResultSet rsFU = stmtFU.executeQuery()) {
                        if (rsFU.next()) {
                            m.setNomeUsuario(rsFU.getString("login"));
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 1. SALVA O HISTÓRICO DE AUDITORIA
            // -----------------------------------------------------------------
            try (PreparedStatement stmtMov = conn.prepareStatement(sqlMov)) {
                stmtMov.setInt(1, m.getQuantidade());
                stmtMov.setString(2, m.getTipo().toUpperCase());

                if (m.getProduto() != null && m.getProduto().getId() > 0) {
                    stmtMov.setInt(3, m.getProduto().getId());
                } else {
                    stmtMov.setNull(3, java.sql.Types.INTEGER);
                }

                stmtMov.setInt(4, m.getUsuario().getId());
                stmtMov.setString(5, m.getNomeProduto());
                stmtMov.setString(6, m.getNomeUsuario());
                stmtMov.setString(7, m.getNomeCategoria()); // Parâmetro 7: Categoria

                stmtMov.executeUpdate();
            }

            // -----------------------------------------------------------------
            // 2. ATUALIZA O ESTOQUE COM TRAVA DE SEGURANÇA
            // -----------------------------------------------------------------
            if (!m.getTipo().equalsIgnoreCase("EXCLUSAO") && m.getProduto() != null && m.getProduto().getId() > 0) {
                try (PreparedStatement stmtProd = conn.prepareStatement(sqlProd)) {
                    stmtProd.setInt(1, m.getQuantidade());
                    stmtProd.setInt(2, m.getProduto().getId());
                    
                    if (m.getTipo().equalsIgnoreCase("SAIDA")) {
                        stmtProd.setInt(3, m.getQuantidade()); // O 3º parâmetro é a trava da SAIDA (quantidade >= ?)
                    }

                    int linhasAfetadas = stmtProd.executeUpdate();
                    
                    // Se for saída e nenhuma linha atualizou, é porque não tinha estoque suficiente
                    if (linhasAfetadas == 0 && m.getTipo().equalsIgnoreCase("SAIDA")) {
                        conn.rollback(); // Desfaz a gravação do log de auditoria
                        return "Erro: Estoque insuficiente para realizar esta saída!";
                    }
                }
            }

            conn.commit(); // Confirma todas as operações com sucesso
            return "sucesso";

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz tudo em caso de qualquer falha catastrófica
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return "Erro interno no servidor ao registrar movimentação.";
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaura o comportamento padrão da conexão
                    Conexao.closeConnection(conn);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<Movimentacao> listarTodas() {
        List<Movimentacao> lista = new ArrayList<>();
        // Adicionada a leitura de nome_categoria
        String sql = "SELECT m.*, p.nome as prod_nome_atual, u.login as user_login_atual " +
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
                m.setNomeProduto(rs.getString("nome_produto"));
                m.setNomeUsuario(rs.getString("nome_usuario"));
                m.setNomeCategoria(rs.getString("nome_categoria")); // Lê a categoria do banco

                // Configura o objeto Produto para manter compatibilidade com o Javascript do site (m.produto.nome)
                String nomeFinalProduto = m.getNomeProduto();
                if (nomeFinalProduto == null || nomeFinalProduto.trim().isEmpty()) {
                    nomeFinalProduto = rs.getString("prod_nome_atual"); // Fallback para dados velhos
                }
                if (nomeFinalProduto == null || nomeFinalProduto.trim().isEmpty()) {
                    nomeFinalProduto = "Produto Removido"; // Caso o produto tenha sido deletado e não havia registro estático
                }

                Produto p = new Produto();
                p.setId(rs.getInt("id_produto"));
                p.setNome(nomeFinalProduto);
                m.setProduto(p);

                // Configura o objeto Usuario
                String loginFinalUsuario = m.getNomeUsuario();
                if (loginFinalUsuario == null || loginFinalUsuario.trim().isEmpty()) {
                    loginFinalUsuario = rs.getString("user_login_atual"); // Fallback para dados velhos
                }

                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                u.setLogin(loginFinalUsuario);
                m.setUsuario(u);

                lista.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}