package br.com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // Configurações do Banco de Dados - Ajustadas para o seu ambiente local
    private static final String URL = "jdbc:mysql://localhost:3306/estoque_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Deixado em branco, já que seu banco não tem senha

    /**
     * Método para obter uma conexão ativa com o banco de dados.
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Garante que o driver do MySQL seja carregado na memória
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado no projeto. Verifique o pom.xml.", e);
        }
    }

    /**
     * Método utilitário para fechar a conexão com segurança de forma silenciosa.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}