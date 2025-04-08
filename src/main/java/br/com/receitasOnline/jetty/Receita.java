package br.com.receitasOnline.jetty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Receita {
    private Integer id;
    private String titulo;
    private String descricao;
    private String modoPreparo;
    private List<String> ingredientes;
    private List<Avaliacao> avaliacoes;

    // Construtor padrão necessário para Jackson
    public Receita() {
        this.avaliacoes = new ArrayList<>();
        this.ingredientes = new ArrayList<>();
    }

    // Construtor completo com anotações Jackson
    @JsonCreator
    public Receita(@JsonProperty("id") Integer id,
                   @JsonProperty("titulo") String titulo,
                   @JsonProperty("descricao") String descricao,
                   @JsonProperty("modoPreparo") String modoPreparo,
                   @JsonProperty("ingredientes") List<String> ingredientes) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
        this.avaliacoes = new ArrayList<>();
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        if (this.avaliacoes == null) {
            this.avaliacoes = new ArrayList<>();
        }
        avaliacoes.add(avaliacao);
    }

    // Getters e Setters (mantidos os existentes)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getModoPreparo() {
        return modoPreparo;
    }

    public void setModoPreparo(String modoPreparo) {
        this.modoPreparo = modoPreparo;
    }

    public List<String> getIngredientes() {
        if (ingredientes == null) {
            ingredientes = new ArrayList<>();
        }
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<Avaliacao> getAvaliacoes() {
        if (avaliacoes == null) {
            avaliacoes = new ArrayList<>();
        }
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
}