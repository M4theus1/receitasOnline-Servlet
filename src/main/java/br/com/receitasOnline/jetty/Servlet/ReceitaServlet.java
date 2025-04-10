package br.com.receitasOnline.jetty.Servlet;

import br.com.receitasOnline.jetty.Entidades.Avaliacao;
import br.com.receitasOnline.jetty.Entidades.Receita;
import br.com.receitasOnline.jetty.Services.ReceitaService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        try {
            String pathInfo = req.getPathInfo();
            resp.setContentType("application/json");

            if (pathInfo == null || pathInfo.equals("/")) {
                List<Receita> receitas = service.listarTodas();
                mapper.writeValue(resp.getWriter(), receitas);
                return;
            }

            String[] pathParts = pathInfo.split("/");

            if (pathParts.length == 2 && pathParts[1].matches("\\d+")) {
                int id = Integer.parseInt(pathParts[1]);
                Receita receita = service.buscarPorId(id);

                if (receita != null) {
                    mapper.writeValue(resp.getWriter(), receita);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Receita não encontrada");
                }
                return;
            }

            if (pathParts.length == 3 && pathParts[1].matches("\\d+") && "avaliacoes".equals(pathParts[2])) {
                int receitaId = Integer.parseInt(pathParts[1]);
                List<Avaliacao> avaliacoes = service.listarAvaliacoes(receitaId);
                mapper.writeValue(resp.getWriter(), avaliacoes);
                return;
            }

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL inválida");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID deve ser um número");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor");
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

            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                Receita receita = mapper.readValue(req.getReader(), Receita.class);
                Receita novaReceita = service.criarReceita(receita);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(resp.getWriter(), novaReceita);
                return;
            }

            String[] pathParts = pathInfo.split("/");

            if (pathParts.length == 3 && pathParts[1].matches("\\d+") && "avaliacoes".equals(pathParts[2])) {
                int receitaId = Integer.parseInt(pathParts[1]);
                Avaliacao avaliacao = mapper.readValue(req.getReader(), Avaliacao.class);

                Receita receita = service.buscarPorId(receitaId);
                if (receita == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Receita não encontrada");
                    return;
                }
                avaliacao.setReceita(receita);

                if (avaliacao.getUsuario() == null || avaliacao.getUsuario().getId() == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Usuário da avaliação é obrigatório");
                    return;
                }

                if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nota deve ser entre 1 e 5");
                    return;
                }

                Avaliacao novaAvaliacao = service.adicionarAvaliacao(receitaId, avaliacao);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(resp.getWriter(), novaAvaliacao);
                return;
            }

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL inválida");
        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "JSON inválido");
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar requisição");
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
            Receita receita = mapper.readValue(req.getReader(), Receita.class);
            receita.setId(id);

            Receita receitaAtualizada = service.atualizarReceita(receita);

            if (receitaAtualizada != null) {
                mapper.writeValue(resp.getWriter(), receitaAtualizada);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Receita não encontrada");
            }

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "JSON inválido");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao atualizar receita");
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
            boolean removido = service.removerReceita(id);

            if (removido) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Receita não encontrada");
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao remover receita");
        }
    }
}
