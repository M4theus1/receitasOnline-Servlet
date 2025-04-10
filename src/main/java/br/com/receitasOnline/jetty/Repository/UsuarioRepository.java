package br.com.receitasOnline.jetty.Repository;

import br.com.receitasOnline.jetty.Entidades.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UsuarioRepository {
    private final Map<Integer, Usuario> usuarios = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(idCounter.getAndIncrement());
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    public Usuario buscarPorId(Integer id) {
        return usuarios.get(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarios.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }

    public boolean remover(Integer id) {
        return usuarios.remove(id) != null;
    }

    public boolean existeComEmail(String email) {
        return usuarios.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
}