package br.com.receitasOnline.jetty.Repository;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AvaliacaoRepository {
    private final Map<Integer, Avaliacao> avaliacoes = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Avaliacao salvar(Avaliacao avaliacao) {
        if (avaliacao.getId() == null) {
            avaliacao.setId(idCounter.getAndIncrement());
        }
        avaliacoes.put(avaliacao.getId(), avaliacao);
        return avaliacao;
    }

    public Avaliacao buscarPorId(Integer id) {
        return avaliacoes.get(id);
    }

    public List<Avaliacao> listarPorReceita(Integer receitaId) {
        return avaliacoes.values().stream()
                .filter(a -> a.getReceita().getId().equals(receitaId))
                .collect(Collectors.toList());
    }

    public List<Avaliacao> listarPorUsuario(Integer usuarioId) {
        return avaliacoes.values().stream()
                .filter(a -> a.getUsuario().getId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public List<Avaliacao> listarTodas() {
        return new ArrayList<>(avaliacoes.values());
    }

    public boolean remover(Integer id) {
        return avaliacoes.remove(id) != null;
    }

    public boolean removerTodasDaReceita(Integer receitaId) {
        List<Integer> idsParaRemover = avaliacoes.values().stream()
                .filter(a -> a.getReceita().getId().equals(receitaId))
                .map(Avaliacao::getId)
                .collect(Collectors.toList());

        idsParaRemover.forEach(avaliacoes::remove);
        return !idsParaRemover.isEmpty();
    }
}