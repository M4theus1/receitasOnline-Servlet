package br.com.receitasOnline.jetty.Services;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import br.com.receitasOnline.jetty.Entidades.Receita;
import br.com.receitasOnline.jetty.Entidades.Usuario;
import br.com.receitasOnline.jetty.Repository.ReceitaRepository;
import br.com.receitasOnline.jetty.Repository.AvaliacaoRepository;
import br.com.receitasOnline.jetty.Repository.UsuarioRepository;

import java.util.List;

public class ReceitaService {
    private final ReceitaRepository repository = new ReceitaRepository();
    private final AvaliacaoRepository avaliacaoRepository = new AvaliacaoRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

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

        if (avaliacao.getUsuario() == null || avaliacao.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }

        // 🔥 Busca o usuário completo usando o ID
        Usuario usuarioCompleto = usuarioRepository.buscarPorId(avaliacao.getUsuario().getId());
        if (usuarioCompleto == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        avaliacao.setUsuario(usuarioCompleto);
        avaliacao.setReceita(receita);
        receita.adicionarAvaliacao(avaliacao);
        repository.salvar(receita);

        return avaliacaoRepository.salvar(avaliacao);
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

    public List<Avaliacao> listarAvaliacoes(int receitaId) {
        return avaliacaoRepository.listarPorReceita(receitaId);
    }

}