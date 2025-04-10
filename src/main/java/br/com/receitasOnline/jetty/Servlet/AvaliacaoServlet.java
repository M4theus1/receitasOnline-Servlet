package br.com.receitasOnline.jetty.Servlet;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import br.com.receitasOnline.jetty.Services.AvaliacaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@WebServlet("/avaliacoes/*")
public class AvaliacaoServlet extends HttpServlet {
    private final AvaliacaoService service = new AvaliacaoService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            resp.setContentType("application/json");

            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar todas as avaliações
                mapper.writeValue(resp.getWriter(), service.listarTodas());
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2 && parts[1].matches("\\d+")) {
                // Buscar avaliação por ID
                Avaliacao avaliacao = service.buscarPorId(Integer.parseInt(parts[1]));
                if (avaliacao != null) {
                    mapper.writeValue(resp.getWriter(), avaliacao);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Avaliação não encontrada");
                }
                return;
            }

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL inválida");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar requisição");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (!"application/json".equalsIgnoreCase(req.getContentType())) {
                resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                        "Content-Type deve ser application/json");
                return;
            }

            Avaliacao avaliacao = mapper.readValue(req.getReader(), Avaliacao.class);
            Avaliacao novaAvaliacao = service.criarAvaliacao(avaliacao);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), novaAvaliacao);
        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "JSON inválido");
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao criar avaliação");
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
            Avaliacao avaliacao = mapper.readValue(req.getReader(), Avaliacao.class);
            avaliacao.setId(id);

            Avaliacao avaliacaoAtualizada = service.atualizarAvaliacao(avaliacao);

            if (avaliacaoAtualizada != null) {
                mapper.writeValue(resp.getWriter(), avaliacaoAtualizada);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Avaliação não encontrada");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao atualizar avaliação");
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
            boolean removido = service.removerAvaliacao(id);

            if (removido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Avaliação não encontrada");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao remover avaliação");
        }
    }
}