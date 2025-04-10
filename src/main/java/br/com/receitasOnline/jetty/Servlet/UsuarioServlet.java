package br.com.receitasOnline.jetty.Servlet;

import br.com.receitasOnline.jetty.Entidades.Usuario;
import br.com.receitasOnline.jetty.Services.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    private final UsuarioService service = new UsuarioService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            resp.setContentType("application/json");

            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todos os usuários
                mapper.writeValue(resp.getWriter(), service.listarTodos());
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2 && parts[1].matches("\\d+")) {
                // Buscar usuário por ID
                Usuario usuario = service.buscarPorId(Integer.parseInt(parts[1]));
                if (usuario != null) {
                    mapper.writeValue(resp.getWriter(), usuario);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
                }
                return;
            }

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL inválida");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar requisição");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Usuario usuario = mapper.readValue(req.getReader(), Usuario.class);

            if (usuario.getNome() == null || usuario.getEmail() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nome e email são obrigatórios");
                return;
            }

            Usuario novoUsuario = service.criarUsuario(usuario);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), novoUsuario);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "JSON inválido");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (!"application/json".equalsIgnoreCase(req.getContentType())) {
                resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "Content-Type deve ser application/json");
                return;
            }

            String pathInfo = req.getPathInfo();
            if (pathInfo == null || !pathInfo.matches("/\\d+")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL inválida");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            Usuario usuario = mapper.readValue(req.getReader(), Usuario.class);
            usuario.setId(id);

            Usuario usuarioAtualizado = service.atualizarUsuario(usuario);

            if (usuarioAtualizado != null) {
                mapper.writeValue(resp.getWriter(), usuarioAtualizado);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao atualizar usuário");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || !pathInfo.matches("/\\d+")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL inválida");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean removido = service.removerUsuario(id);

            if (removido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao remover usuário");
        }
    }
}
