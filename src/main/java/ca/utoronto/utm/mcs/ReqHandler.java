package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {
    public Neo4jDAO neo4jDAO;

    //Injected the Neo4jDAO object into ReqHandler constructor
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

        //Implementation of getActor
        if (exchange.getRequestURI().getPath().startsWith("/api/v1/getActor")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String actorId;

                if (deserialized.length() == 1 && deserialized.has("actorId")) { //Entry validation
                    actorId = deserialized.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                    return;
                }
                try {
                    if(!this.neo4jDAO.checkActorExists(actorId)) { //Return 404 if actor/movie doesn't exist
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    String actorName = this.neo4jDAO.getActorName(actorId);
                    String[] movies = this.neo4jDAO.getActorMovies(actorId);

                    JSONObject response = new JSONObject();
                    response.put("actorId", actorId);
                    response.put("name", actorName);
                    response.put("movies", new JSONArray(movies)); //Put entries in a JSON Object

                    byte[] val = response.toString().replace("\\\"","").getBytes(); //Converts JSON Object to String
                    if (val==null){
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    exchange.sendResponseHeaders(200, val.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(val);
                    os.close();
                    return;
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        //Implementation of getMovie
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/getMovie")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String movieId;

                if (deserialized.length() == 1 && deserialized.has("movieId")) { //Entry validation
                    movieId = deserialized.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                    return;
                }
                try {
                    if(!this.neo4jDAO.checkMovieExists(movieId)) { //Return 404 if actor/movie doesn't exist
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    String movieName = this.neo4jDAO.getMovieName(movieId);
                    String[] actors = this.neo4jDAO.getMovieActors(movieId);

                    JSONObject response = new JSONObject();
                    response.put("movieId", movieId);
                    response.put("name", movieName);
                    response.put("actors", new JSONArray(actors)); //Put entries in a JSON Object

                    byte[] val = response.toString().replace("\\\"","").getBytes(); //Converts JSON Object to String
                    if (val==null){
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    exchange.sendResponseHeaders(200, val.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(val);
                    os.close();
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        //Implementation of hasRelationship
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/hasRelationship")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String actorId, movieId;

                if (deserialized.length() == 2 && deserialized.has("actorId") && deserialized.has("movieId")) { //Entry validation
                    actorId = deserialized.getString("actorId");
                    movieId = deserialized.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                    return;
                }
                try {
                    if(this.neo4jDAO.checkActorExists(actorId) == false || this.neo4jDAO.checkMovieExists(movieId) == false){ //Return 404 if actor/movie doesn't exist
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    boolean retVal = this.neo4jDAO.hasRelationship(actorId, movieId);
                    JSONObject retObj = new JSONObject();
                    retObj.put("actorId", actorId);
                    retObj.put("movieId", movieId);
                    retObj.put("hasRelationship", retVal); //Put entries in a JSON Object

                    byte [] val = retObj.toString().replace("\\\"","").getBytes(); //Converts JSON Object to String
                    exchange.sendResponseHeaders(200, val.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(val);
                    os.close();
                    return;
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        //Implementation of computeBaconNumber
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/computeBaconNumber")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String actorId;

                if (deserialized.length() == 1 && deserialized.has("actorId")) { //Entry validation
                    actorId = deserialized.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                    return;
                }
                try {
                    int retVal;
                    if(actorId.equals("nm0000102")){
                        retVal = 0;
                    } else {
                        retVal = this.neo4jDAO.getBaconNumber(actorId);
                        if(this.neo4jDAO.checkActorExists(actorId) == false || retVal == 0){ //Return 404 if actor/movie doesn't exist
                            exchange.sendResponseHeaders(404, -1);
                            return;
                        }
                    }

                    JSONObject retObj = new JSONObject();
                    retObj.put("baconNumber", retVal); //Put entries in a JSON Object

                    byte[] val = retObj.toString().replace("\\\"", "").getBytes(); //Converts JSON Object to String
                    exchange.sendResponseHeaders(200, val.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(val);
                    os.close();
                    return;

                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        //Implementation of computeBaconPath
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/computeBaconPath")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String actorId;

                if (deserialized.length() == 1 && deserialized.has("actorId")) { //Entry validation
                    actorId = deserialized.getString("actorId");
                } else {
                    exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                    return;
                }
                try {
                    String [] path_arr;
                    if(actorId.equals("nm0000102")) {
                         path_arr = new String[]{actorId};
                    }
                    else {
                        path_arr = this.neo4jDAO.getBaconPath(actorId);
                        if (path_arr.length == 0 || this.neo4jDAO.checkActorExists(actorId) == false || path_arr[0] == null) { //Return 404 if actor/movie doesn't exist
                            exchange.sendResponseHeaders(404, -1);
                            return;
                        }
                    }

                    JSONObject retObj = new JSONObject();
                    retObj.put("baconPath", new JSONArray(path_arr)); //Put entries in a JSON Object

                    byte[] val = retObj.toString().replace("\\\"", "").getBytes(); //Converts JSON Object to String
                    exchange.sendResponseHeaders(200, val.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(val);
                    os.close();
                    return;

                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        else {
            throw new UnsupportedOperationException("Error: invalid http verb");
        }
    }
    public void handlePut(HttpExchange exchange) throws IOException, JSONException {
        //Implementation of addActor
        if (exchange.getRequestURI().getPath().startsWith("/api/v1/addActor")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String name, actorId;

                if (deserialized.length() == 2 && deserialized.has("name") && deserialized.has("actorId")) { //Entry validation
                    name = deserialized.getString("name");
                    actorId = deserialized.getString("actorId");
                    if(name.isEmpty() || actorId.isEmpty()) {
                        exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                        return;
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                try {
                    if(this.neo4jDAO.checkActorExists(actorId)) { //Return 40 if actor/movie already exist
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                    this.neo4jDAO.insertActor(name, actorId);
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                exchange.sendResponseHeaders(200, -1);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
        //Implementation of addMovie
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/addMovie")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String name, movieId;

                if (deserialized.length() == 2 && deserialized.has("name") && deserialized.has("movieId")) { //Entry validation
                    name = deserialized.getString("name");
                    movieId = deserialized.getString("movieId");
                    if(name.isEmpty() || movieId.isEmpty()) {
                        exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                        return;
                    }
                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                try {
                    if(this.neo4jDAO.checkMovieExists(movieId)) { //Return 40 if actor/movie already exist
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
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
        //Implementation of addRelationship
        else if (exchange.getRequestURI().getPath().startsWith("/api/v1/addRelationship")) {
            String body = Utils.convert(exchange.getRequestBody());
            try {
                JSONObject deserialized = new JSONObject(body);

                String actorId, movieId;

                if (deserialized.length() == 2 && deserialized.has("actorId") && deserialized.has("movieId")) { //Entry validation
                    actorId = deserialized.getString("actorId");
                    movieId = deserialized.getString("movieId");
                } else {
                    exchange.sendResponseHeaders(400, -1); //Return 400 if entry is not valid
                    return;
                }

                try {
                    if(!this.neo4jDAO.checkActorExists(actorId) ||  !this.neo4jDAO.checkMovieExists(movieId)) { //Return 404 if actor/movie doesn't exist
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    if(this.neo4jDAO.checkRelationshipExists(actorId, movieId)) {
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                    this.neo4jDAO.addRelationActed_In(actorId, movieId);
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
    //Clears database after every run
    public void removeDb(){
        this.neo4jDAO.removeDatabase();
    }
}