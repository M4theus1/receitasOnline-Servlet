package br.com.receitasOnline.jetty.Servlet;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import br.com.receitasOnline.jetty.Entidades.Receita;
import br.com.receitasOnline.jetty.Entidades.Usuario;
import br.com.receitasOnline.jetty.Services.AvaliacaoService;
import br.com.receitasOnline.jetty.Services.ReceitaService;
import br.com.receitasOnline.jetty.Services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/avaliacoes/*")
public class AvaliacaoServlet extends HttpServlet {
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final ReceitaService receitaService = new ReceitaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isJsonRequest(req)) {
            sendUnsupportedMedia(resp);
            return;
        }

        try {
            Avaliacao avaliacao = mapper.readValue(req.getReader(), Avaliacao.class);

            if (avaliacao.getReceita() == null || avaliacao.getReceita().getId() == null ||
                    avaliacao.getUsuario() == null || avaliacao.getUsuario().getId() == null) {
                sendBadRequest(resp, "ID da receita e do usuário são obrigatórios");
                return;
            }

            Receita receita = receitaService.buscarPorId(avaliacao.getReceita().getId());
            Usuario usuario = usuarioService.buscarPorId(avaliacao.getUsuario().getId());

            if (receita == null || usuario == null) {
                sendNotFound(resp, "Receita ou usuário não encontrado");
                return;
            }

            avaliacao.setReceita(receita);
            avaliacao.setUsuario(usuario);

            Avaliacao novaAvaliacao = avaliacaoService.criarAvaliacao(avaliacao);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeJson(resp, novaAvaliacao);

        } catch (Exception e) {
            sendBadRequest(resp, "Erro ao processar requisição: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Avaliacao> avaliacoes = avaliacaoService.listarTodas();
            writeJson(resp, avaliacoes);
            return;
        }

        try {
            int id = extractId(pathInfo);
            Avaliacao avaliacao = avaliacaoService.buscarPorId(id);
            if (avaliacao == null) {
                sendNotFound(resp, "Avaliação não encontrada");
                return;
            }
            writeJson(resp, avaliacao);
        } catch (NumberFormatException e) {
            sendBadRequest(resp, "ID inválido");
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
            Avaliacao avaliacao = mapper.readValue(req.getReader(), Avaliacao.class);
            avaliacao.setId(id);

            Avaliacao atualizada = avaliacaoService.atualizarAvaliacao(avaliacao);
            if (atualizada == null) {
                sendNotFound(resp, "Avaliação não encontrada");
                return;
            }

            writeJson(resp, atualizada);
        } catch (NumberFormatException e) {
            sendBadRequest(resp, "ID inválido");
        } catch (Exception e) {
            sendBadRequest(resp, "Erro ao atualizar: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = extractId(req.getPathInfo());
            boolean removido = avaliacaoService.removerAvaliacao(id);

            if (removido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendNotFound(resp, "Avaliação não encontrada");
            }
        } catch (NumberFormatException e) {
            sendBadRequest(resp, "ID inválido");
        } catch (Exception e) {
            sendServerError(resp, "Erro ao remover avaliação");
        }
    }

    // ==== Métodos auxiliares ====

    private boolean isJsonRequest(HttpServletRequest req) {
        return "application/json".equalsIgnoreCase(req.getContentType());
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

    private void sendBadRequest(HttpServletResponse resp, String msg) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
    }

    private void sendNotFound(HttpServletResponse resp, String msg) throws IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
    }

    private void sendUnsupportedMedia(HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content-Type deve ser application/json");
    }

    private void sendServerError(HttpServletResponse resp, String msg) throws IOException {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
    }
}
