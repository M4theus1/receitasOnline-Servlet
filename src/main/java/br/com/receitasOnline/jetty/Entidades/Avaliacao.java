package br.com.receitasOnline.jetty.Entidades;

public class Avaliacao {
    private Integer id;
    private Usuario usuario;
    private Receita receita;
    private int nota; // Exemplo: 1 a 5
    private String comentario;

    public Avaliacao() {}

    public Avaliacao(Integer id, Usuario usuario, Receita receita, int nota, String comentario) {
        this.id = id;
        this.usuario = usuario;
        this.receita = receita;
        this.nota = nota;
        this.comentario = comentario;
    }

    public String formatarAvaliacao() {
        String nomeUsuario = (usuario != null && usuario.getNome() != null) ? usuario.getNome() : "Usuário desconhecido";
        return nomeUsuario + " avaliou com " + nota + " estrelas: " + comentario;
    }


    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
