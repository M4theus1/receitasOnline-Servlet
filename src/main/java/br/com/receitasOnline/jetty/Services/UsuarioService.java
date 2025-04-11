package br.com.receitasOnline.jetty.Services;

import br.com.receitasOnline.jetty.Entidades.Usuario;
import br.com.receitasOnline.jetty.Repository.UsuarioRepository;

import java.util.List;

// UsuarioService.java
public class UsuarioService {
    private final UsuarioRepository repository = new UsuarioRepository();


    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (repository.existeComEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário com este email");
        }
        return repository.salvar(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public Usuario buscarPorEmail(String email) {
        return repository.buscarPorEmail(email);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        if (usuario.getId() == null || repository.buscarPorId(usuario.getId()) == null) {
            return null;
        }
        return repository.salvar(usuario);
    }

    public boolean removerUsuario(int id) {
        return repository.remover(id);
    }
}
