package br.com.receitasOnline.jetty.Services;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import br.com.receitasOnline.jetty.Entidades.Receita;
import br.com.receitasOnline.jetty.Repository.ReceitaRepository;

import java.util.List;

public class ReceitaService {
    private final ReceitaRepository repository = new ReceitaRepository();

    public Receita criarReceita(Receita receita) {
        if (receita.getTitulo() == null || receita.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título da receita é obrigatório");
        }
        return repository.salvar(receita);
    }

    public List<Receita> listarTodas() {
        return repository.listarTodas();
    }

    public Receita buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public Avaliacao adicionarAvaliacao(int receitaId, Avaliacao avaliacao) {
        Receita receita = repository.buscarPorId(receitaId);
        if (receita == null) {
            throw new IllegalArgumentException("Receita não encontrada");
        }
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new IllegalArgumentException("A nota deve ser entre 1 e 5");
        }
        receita.adicionarAvaliacao(avaliacao);
        return avaliacao;
    }

    public List<Avaliacao> listarAvaliacoes(int receitaId) {
        Receita receita = repository.buscarPorId(receitaId);
        if (receita == null) {
            throw new IllegalArgumentException("Receita não encontrada");
        }
        return receita.getAvaliacoes();
    }

    public Receita atualizarReceita(Receita receita) {
        if (repository.buscarPorId(receita.getId()) == null) {
            return null;
        }
        return repository.salvar(receita);
    }

    public boolean removerReceita(int id) {
        return repository.remover(id);
    }
}