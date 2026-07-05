package br.com.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;

@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Sao_Paulo")
public class Movimentacao {
    private int id;
    private int quantidade;
    private Timestamp dataHora;
    private String tipo;
    
    // Os campos de Snapshot (Histórico)
    private String nomeProduto; 
    private String nomeUsuario; 
    private String nomeCategoria; // AQUI ESTÁ A VARIÁVEL QUE FALTAVA!
    
    // Objetos de relacionamento
    private Produto produto; 
    private Usuario usuario; 

    // ==========================================
    // GETTERS E SETTERS
    // ==========================================
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public Timestamp getDataHora() { return dataHora; }
    public void setDataHora(Timestamp dataHora) { this.dataHora = dataHora; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    // AQUI ESTÃO OS MÉTODOS QUE O JAVA ESTAVA RECLAMANDO QUE NÃO EXISTIAM:
    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}