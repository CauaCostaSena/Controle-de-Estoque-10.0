package br.com.example.model;

import java.util.Date;

public class Movimentacao {
    private int id;
    private Produto produto;
    private String tipo; 
    private int quantidade;
    private Date dataMovimentacao;

    public Movimentacao() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public Date getDataMovimentacao() { return dataMovimentacao; }
    public void setDataMovimentacao(Date dataMovimentacao) { this.dataMovimentacao = dataMovimentacao; }
}
