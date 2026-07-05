package br.com.example.controller;

import java.util.Date;
// Forçando a importação para o VS Code não se perder
import br.com.example.model.Produto;
import br.com.example.model.Usuario;

public class Movimentacao {
    private int id;
    private Produto produto;
    private Usuario usuario;
    private String tipo;
    private int quantidade;
    private Date dataHora;
    private String motivo;

    public Movimentacao() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public Date getDataHora() { return dataHora; }
    public void setDataHora(Date dataHora) { this.dataHora = dataHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}