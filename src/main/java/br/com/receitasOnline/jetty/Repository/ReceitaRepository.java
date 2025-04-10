package br.com.receitasOnline.jetty.Repository;

import br.com.receitasOnline.jetty.Entidades.Receita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceitaRepository {
    private final Map<Integer, Receita> receitas = new HashMap<>();
    private int idCounter = 1;

    public Receita salvar(Receita receita) {
        if (receita.getId() == null) {
            receita.setId(idCounter++);
        }
        receitas.put(receita.getId(), receita);
        return receita;
    }

    public Receita buscarPorId(Integer id) {
        return receitas.get(id);
    }

    public List<Receita> listarTodas() {
        return new ArrayList<>(receitas.values());
    }

    public boolean remover(Integer id) {
        return receitas.remove(id) != null;
    }
}