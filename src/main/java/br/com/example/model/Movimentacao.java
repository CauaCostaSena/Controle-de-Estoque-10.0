package br.com.example.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat; // Certifique-se de importar esta linha

public class Movimentacao {
    private int id;
    private int quantidade;

    // Esta anotação garante que o Java envie a data como texto limpo para o JavaScript
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;
    
    private String tipo;
    private String motivo;
    private Produto produto;
    private Usuario usuario;

    public Movimentacao() {}

    // ... Seus getters e setters permanecem idênticos abaixo ...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int Math) { this.quantidade = Math; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}