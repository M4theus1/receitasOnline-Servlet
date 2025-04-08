package br.com.receitasOnline.jetty;

import br.com.receitasOnline.jetty.AvaliacaoRepository;
import java.util.List;

// AvaliacaoService.java
public class AvaliacaoService {
    private final AvaliacaoRepository repository = new AvaliacaoRepository();

    public Avaliacao criarAvaliacao(Avaliacao avaliacao) {
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new IllegalArgumentException("A nota deve ser entre 1 e 5");
        }
        return repository.salvar(avaliacao);
    }

    public List<Avaliacao> listarTodas() {
        return repository.listarTodas();
    }

    public Avaliacao buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public Avaliacao atualizarAvaliacao(Avaliacao avaliacao) {
        if (repository.buscarPorId(avaliacao.getId()) == null) {
            return null;
        }
        return repository.salvar(avaliacao);
    }

    public boolean removerAvaliacao(int id) {
        return repository.remover(id);
    }
}