package br.com.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Sao_Paulo")
public class Movimentacao {
    private int id;
    private int quantidade;
    private Timestamp dataHora; // O banco de dados vai preencher isso sozinho!
    private String tipo;
    private Produto produto; // Objeto para podermos enviar o nome para a tela
    private Usuario usuario; // Objeto para podermos enviar o login para a tela

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public Timestamp getDataHora() { return dataHora; }
    public void setDataHora(Timestamp dataHora) { this.dataHora = dataHora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}