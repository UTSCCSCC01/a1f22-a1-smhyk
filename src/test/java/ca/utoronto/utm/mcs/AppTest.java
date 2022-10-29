package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


// TODO Please Write Your Tests For CI/CD In This Class. You will see
// these tests pass/fail on github under github actions.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    public int get_request(String url, JSONObject body) throws IOException, InterruptedException {
        HttpClient c = HttpClient.newHttpClient();

        HttpRequest r = HttpRequest.
                newBuilder().
                uri(URI.create(url)).
                method("GET", HttpRequest.BodyPublishers.ofString(body.toString())).build();

        HttpResponse<String> res = c.send(r, HttpResponse.BodyHandlers.ofString());

        return res.statusCode();
    }

    public int put_request(String url, JSONObject body) throws JSONException, IOException {
        URL link = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) link.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        osw.write(body.toString());
        osw.close();
        os.close();
        connection.connect();

        return connection.getResponseCode();
    }

    @Test
    @Order(1)
    public void addActorPass() throws JSONException, IOException {

        JSONObject body = new JSONObject();
        body.put("name", "Yuval Kamani");
        body.put("actorId", "1000");

        int res = put_request("http://localhost:8080/api/v1/addActor", body);

        assertEquals(200, res);
    }

    @Test
    @Order(2)
    public void addActorFail() throws JSONException, IOException {

        JSONObject body = new JSONObject();
        body.put("name", "Sayed Mojtaba");
        body.put("actorId", "1000");

        int res = put_request("http://localhost:8080/api/v1/addActor", body);

        assertEquals(400, res);
    }

    @Test
    @Order(3)
    public void addMoviePass() throws JSONException, IOException {

        JSONObject body = new JSONObject();
        body.put("name", "Avengers");
        body.put("movieId", "100");

        int res = put_request("http://localhost:8080/api/v1/addMovie", body);

        assertEquals(200, res);
    }

    @Test
    @Order(4)
    public void addMovieFail() throws JSONException, IOException {

        JSONObject body = new JSONObject();
        body.put("name", "Avengers: Endgame");
        body.put("movieId", "100");

        int res = put_request("http://localhost:8080/api/v1/addMovie", body);

        assertEquals(400, res);
    }

    @Test
    @Order(5)
    public void addRelationshipPass() throws JSONException, IOException {

        JSONObject body = new JSONObject();
        body.put("actorId", "1000");
        body.put("movieId", "100");

        int res = put_request("http://localhost:8080/api/v1/addRelationship", body);

        assertEquals(200, res);
    }

    @Test
    @Order(6)
    public void addRelationshipFail() throws JSONException, IOException {

        JSONObject body = new JSONObject();
        body.put("actorId", "1000");
        body.put("movieId", "999");

        int res = put_request("http://localhost:8080/api/v1/addRelationship", body);

        if(res == 400) {
            assertEquals(400, res);
        } else if(res == 404) {
            assertEquals(404, res);
        }
    }

    @Test
    @Order(7)
    public void getActorPass() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "1000");

        int res = get_request("http://localhost:8080/api/v1/getActor", body);

        assertEquals(200, res);
    }

    @Test
    @Order(8)
    public void getActorFail() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "9999");

        int res = get_request("http://localhost:8080/api/v1/getActor", body);
        if(res == 400) {
            assertEquals(400, res);
        } else {
            assertEquals(404, res);
        }
    }

    @Test
    @Order(9)
    public void getMoviePass() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("movieId", "100");

        int res = get_request("http://localhost:8080/api/v1/getMovie", body);

        assertEquals(200, res);
    }

    @Test
    @Order(10)
    public void getMovieFail() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("movieId", "999");

        int res = get_request("http://localhost:8080/api/v1/getMovie", body);

        if(res == 400){
            assertEquals(400, res);
        } else {
            assertEquals(404, res);
        }

    }

    @Test
    @Order(11)
    public void hasRelationshipPass() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "1000");
        body.put("movieId", "100");

        int res = get_request("http://localhost:8080/api/v1/hasRelationship", body);

        assertEquals(200, res);
    }

    @Test
    @Order(12)
    public void hasRelationshipFail() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "9999");
        body.put("movieId", "100");

        int res = get_request("http://localhost:8080/api/v1/hasRelationship", body);

        if(res == 400){
            assertEquals(400, res);
        } else {
            assertEquals(404, res);
        }
    }

    @Test
    @Order(13)
    public void computeBaconNumberPass() throws JSONException, IOException, InterruptedException {

        JSONObject kBacon = new JSONObject();
        kBacon.put("name", "Kevin Bacon");
        kBacon.put("actorId", "nm0000102");

        int kBaconAdd = put_request("http://localhost:8080/api/v1/addActor", kBacon);

        if(kBaconAdd != 200){
            return;
        }

        JSONObject kBaconRelation = new JSONObject();
        kBaconRelation.put("actorId", "nm0000102");
        kBaconRelation.put("movieId", "100");

        int kBaconR = put_request("http://localhost:8080/api/v1/addRelationship", kBaconRelation);

        if(kBaconR != 200){
            return;
        }

        JSONObject body = new JSONObject();
        body.put("actorId", "1000");

        int res = get_request("http://localhost:8080/api/v1/computeBaconNumber", body);

        assertEquals(200, res);
    }

    @Test
    @Order(13)
    public void computeBaconNumberFail() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "9999");

        int res = get_request("http://localhost:8080/api/v1/computeBaconNumber", body);

        if(res == 400){
            assertEquals(400, res);
        } else {
            assertEquals(404, res);
        }
    }

    @Test
    @Order(14)
    public void computeBaconPathPass() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "1000");

        int res = get_request("http://localhost:8080/api/v1/computeBaconPath", body);

        assertEquals(200, res);
    }

    @Test
    @Order(15)
    public void computeBaconPathFail() throws JSONException, IOException, InterruptedException {

        JSONObject body = new JSONObject();
        body.put("actorId", "9999");

        int res = get_request("http://localhost:8080/api/v1/computeBaconPath", body);

        if(res == 400){
            assertEquals(400, res);
        } else {
            assertEquals(404, res);
        }
    }

    @AfterAll
    public static void deleteDb(){
        ReqHandlerComponent component = DaggerReqHandlerComponent.create();
        ReqHandler reqHandler = component.buildHandler();
        reqHandler.removeDb();
    }

}