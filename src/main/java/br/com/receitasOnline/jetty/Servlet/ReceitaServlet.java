package br.com.receitasOnline.jetty.Servlet;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import br.com.receitasOnline.jetty.Entidades.Receita;
import br.com.receitasOnline.jetty.Services.ReceitaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/receitas/*")
public class ReceitaServlet extends HttpServlet {
    private final ReceitaService service = new ReceitaService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Receita> receitas = service.listarTodas();
                writeJson(resp, receitas);
                return;
            }

            String[] pathParts = pathInfo.split("/");

            if (pathParts.length == 2 && isNumeric(pathParts[1])) {
                int id = Integer.parseInt(pathParts[1]);
                Receita receita = service.buscarPorId(id);
                if (receita != null) {
                    writeJson(resp, receita);
                } else {
                    sendNotFound(resp, "Receita não encontrada");
                }
                return;
            }

            if (pathParts.length == 3 && isNumeric(pathParts[1]) && "avaliacoes".equals(pathParts[2])) {
                int receitaId = Integer.parseInt(pathParts[1]);
                List<Avaliacao> avaliacoes = service.listarAvaliacoes(receitaId);
                writeJson(resp, avaliacoes);
                return;
            }

            sendBadRequest(resp, "URL inválida");
        } catch (NumberFormatException e) {
            sendBadRequest(resp, "ID deve ser um número");
        } catch (Exception e) {
            sendServerError(resp, "Erro interno no servidor");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isJsonRequest(req)) {
            sendUnsupportedMedia(resp);
            return;
        }

        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                Receita receita = mapper.readValue(req.getReader(), Receita.class);
                Receita novaReceita = service.criarReceita(receita);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                writeJson(resp, novaReceita);
            } else {
                sendBadRequest(resp, "URL inválida");
            }
        } catch (Exception e) {
            sendBadRequest(resp, e.getMessage());
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
            Receita receita = mapper.readValue(req.getReader(), Receita.class);
            receita.setId(id);

            Receita receitaAtualizada = service.atualizarReceita(receita);
            if (receitaAtualizada != null) {
                writeJson(resp, receitaAtualizada);
            } else {
                sendNotFound(resp, "Receita não encontrada");
            }
        } catch (NumberFormatException e) {
            sendBadRequest(resp, "ID inválido");
        } catch (Exception e) {
            sendServerError(resp, "Erro ao atualizar receita");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = extractId(req.getPathInfo());
            boolean removido = service.removerReceita(id);

            if (removido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendNotFound(resp, "Receita não encontrada");
            }
        } catch (NumberFormatException e) {
            sendBadRequest(resp, "ID inválido");
        } catch (Exception e) {
            sendServerError(resp, "Erro ao remover receita");
        }
    }

    // ==== Métodos auxiliares reutilizáveis ====

    private boolean isJsonRequest(HttpServletRequest req) {
        return "application/json".equalsIgnoreCase(req.getContentType());
    }

    private int extractId(String pathInfo) throws NumberFormatException {
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            throw new NumberFormatException("ID malformado");
        }
        return Integer.parseInt(pathInfo.substring(1));
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
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
