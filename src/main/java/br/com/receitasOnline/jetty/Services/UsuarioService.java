package br.com.receitasOnline.jetty.Services;

import br.com.receitasOnline.jetty.Entidades.Usuario;
import br.com.receitasOnline.jetty.Repository.UsuarioRepository;

import java.util.List;

// UsuarioService.java
public class UsuarioService {
    private final UsuarioRepository repository = new UsuarioRepository();

    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        return repository.salvar(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        if (repository.buscarPorId(usuario.getId()) == null) {
            return null;
        }
        return repository.salvar(usuario);
    }

    public boolean removerUsuario(int id) {
        return repository.remover(id);
    }
}
