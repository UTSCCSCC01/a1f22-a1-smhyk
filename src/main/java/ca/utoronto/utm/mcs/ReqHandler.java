package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {
    public Neo4jDAO neo4jDAO;

    // TODO Complete This Class
    @Inject
    public ReqHandler(Neo4jDAO neo4jDAO) {
        this.neo4jDAO = neo4jDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    this.handleGet(exchange);
                    break;
                case "PUT":
                    this.handlePut(exchange);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleGet(HttpExchange exchange) throws IOException, JSONException {
        // To DO: Complete this function
    }
    public void handlePut(HttpExchange exchange) throws IOException, JSONException {
        if (exchange.getRequestURI().getPath().startsWith("/api/v1/addActor")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String name, actorId;

                if (deserialized.length() == 2 && deserialized.has("name") && deserialized.has("actorId")) {
                    name = deserialized.getString("name");
                    actorId = deserialized.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                try {
                    this.neo4jDAO.insertActor(name, actorId);
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                exchange.sendResponseHeaders(200, -1);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/addMovie")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String name, movieId;

                if (deserialized.length() == 2 && deserialized.has("name") && deserialized.has("movieId")) {
                    name = deserialized.getString("name");
                    movieId = deserialized.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                try {
                    this.neo4jDAO.insertMovie(name, movieId);
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                exchange.sendResponseHeaders(200, -1);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        else {
            throw new UnsupportedOperationException("Error: Invalid HTTP Verb");
        }
    }
}