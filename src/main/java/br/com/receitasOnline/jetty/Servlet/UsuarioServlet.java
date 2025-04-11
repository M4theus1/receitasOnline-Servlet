package br.com.receitasOnline.jetty.Servlet;

import br.com.receitasOnline.jetty.Entidades.Usuario;
import br.com.receitasOnline.jetty.Services.UsuarioService;
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
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                writeJson(resp, service.listarTodos());
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2 && isNumeric(parts[1])) {
                Usuario usuario = service.buscarPorId(Integer.parseInt(parts[1]));
                if (usuario != null) {
                    writeJson(resp, usuario);
                } else {
                    sendNotFound(resp, "Usuário não encontrado");
                }
            } else {
                sendBadRequest(resp, "URL inválida");
            }
        } catch (Exception e) {
            sendServerError(resp, "Erro ao processar requisição");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isJsonRequest(req)) {
            sendUnsupportedMedia(resp);
            return;
        }

        try {
            Usuario usuario = mapper.readValue(req.getReader(), Usuario.class);

            if (usuario.getNome() == null || usuario.getNome().isBlank() ||
                    usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                sendBadRequest(resp, "Nome e email são obrigatórios");
            }

            Usuario novoUsuario = service.criarUsuario(usuario);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeJson(resp, novoUsuario);
        } catch (Exception e) {
            sendBadRequest(resp, "Erro ao processar JSON ou salvar usuário");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isJsonRequest(req)) {
            sendUnsupportedMedia(resp);
            return;
        }

        try {
            int id = extractId(req.getPathInfo());
            Usuario usuario = mapper.readValue(req.getReader(), Usuario.class);
            usuario.setId(id);

            Usuario atualizado = service.atualizarUsuario(usuario);
            if (atualizado != null) {
                writeJson(resp, atualizado);
            } else {
                sendNotFound(resp, "Usuário não encontrado");
            }
        } catch (Exception e) {
            sendServerError(resp, "Erro ao atualizar usuário");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = extractId(req.getPathInfo());
            boolean removido = service.removerUsuario(id);

            if (removido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendNotFound(resp, "Usuário não encontrado");
            }
        } catch (Exception e) {
            sendServerError(resp, "Erro ao remover usuário");
        }
    }

    // ==== Utilitários reutilizáveis ====

    private boolean isJsonRequest(HttpServletRequest req) {
        return "application/json".equalsIgnoreCase(req.getContentType());
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private int extractId(String pathInfo) throws NumberFormatException {
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            throw new NumberFormatException("ID malformado");
        }
        return Integer.parseInt(pathInfo.substring(1));
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        mapper.writeValue(resp.getWriter(), data);
    }

    private void sendBadRequest(HttpServletResponse resp, String message) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }

    private void sendNotFound(HttpServletResponse resp, String message) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
    }

    private void sendServerError(HttpServletResponse resp, String message) throws IOException {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    private void sendUnsupportedMedia(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content-Type deve ser application/json");
    }
}
