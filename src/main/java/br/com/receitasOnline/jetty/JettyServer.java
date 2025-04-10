package br.com.receitasOnline.jetty;

import br.com.receitasOnline.jetty.Servlet.ReceitaServlet;
import br.com.receitasOnline.jetty.Servlet.UsuarioServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.FilterHolder;

import java.io.IOException;
import java.util.EnumSet;

public class JettyServer {
    private static final int DEFAULT_PORT = 8080;
    private Server server;

    public void start() throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("SERVER_PORT", String.valueOf(DEFAULT_PORT)));
        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/api");

        configureCORS(context);
        registerServlets(context);

        //context.setErrorHandler(new CustomErrorHandler());
        server.setHandler(context);

        server.start();
        System.out.println("Servidor Jetty iniciado na porta " + port);
        System.out.println("API disponível em: http://localhost:" + port + "/api");
        server.join();
    }

    private void configureCORS(ServletContextHandler context) {
        FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE,OPTIONS");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
    }

    private void registerServlets(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new ReceitaServlet()), "/receitas/*");
        context.addServlet(new ServletHolder(new UsuarioServlet()), "/usuarios/*");
        context.addServlet(new ServletHolder(new HealthCheckServlet()), "/health");
    }

    public void stop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
            System.out.println("Servidor Jetty parado");
        }
    }

    public static void main(String[] args) {
        JettyServer jettyServer = new JettyServer();
        try {
            jettyServer.start();
        } catch (Exception e) {
            System.err.println("Falha ao iniciar servidor: " + e.getMessage());
            try {
                jettyServer.stop();
            } catch (Exception ex) {
                System.err.println("Erro ao parar servidor: " + ex.getMessage());
            }
            System.exit(1);
        }
    }

    //static class CustomErrorHandler extends ErrorHandler {
       // @Override
       // public void handle(String target, Request baseRequest,
                          // HttpServletRequest request, HttpServletResponse response)
                //throws IOException {
            //response.setContentType("application/json");
            //response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            //response.getWriter().println("{\"error\":\"Ocorreu um erro no servidor\"}");
       // }
   // }

    static class HealthCheckServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("{\"status\":\"UP\"}");
        }
    }
}