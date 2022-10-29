package ca.utoronto.utm.mcs;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

// All your database transactions or queries should
// go in this class
public class Neo4jDAO {
    // TODO Complete This Class
    private final Session session;
    private final Driver driver;

    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
        this.session = driver.session();
    }

    public void insertActor(String name, String actorId) {
        String query;
        query = "CREATE (n:actor {name: \"%s\", actorId: \"%s\"})";
        query = String.format(query, name, actorId);
        this.session.run(query);
        return;
    }
    public void insertMovie(String name, String movieId) {
        String query;
        query = "CREATE (n:movie {name: \"%s\", movieId: \"%s\"})";
        query = String.format(query, name, movieId);
        this.session.run(query);
        return;
    }
    public void addRelationActed_In(String aid, String mid) {
        String query;
        query = "MATCH (a:actor),(m:movie)" +
                "WHERE a.actorId = \"%s\" AND m.movieId = \"%s\"" +
                "CREATE (a)-[r:ACTED_IN]->(m)" +
                "RETURN type(r)";
        query = String.format(query, aid, mid);
        this.session.run(query);
        return;
    }
    public boolean checkActorExists(String actorId) {
        String query;
        query = "OPTIONAL MATCH (n:actor{actorId:\"%s\"})\n" +
                "RETURN n IS NOT NULL AS Predicate";
        query = String.format(query, actorId);
        Result result = this.session.run(query);
        String bool = result.next().toString();

        if(bool.indexOf("TRUE") >= 0) {
            return true;
        }
        return false;
    }
    public boolean checkMovieExists(String movieId) {
        String query;
        query = "OPTIONAL MATCH (n:movie{movieId:\"%s\"})\n" +
                "RETURN n IS NOT NULL AS Predicate";
        query = String.format(query, movieId);
        Result result = this.session.run(query);
        String bool = result.next().toString();

        if(bool.indexOf("TRUE") >= 0) {
            return true;
        }
        return false;
    }


    public boolean hasRelationship(String actorId, String movieId) {
        String query;
        query = "MATCH (a:actor),(m:movie)" +
                "WHERE (a)-[:ACTED_IN]->(m) AND a.actorId = \"%s\" AND m.movieId = \"%s\"" +
                "RETURN a";

        query = String.format(query, actorId, movieId);
        Result result = this.session.run(query);

        return result.hasNext();
    }
        

    public boolean checkRelationshipExists(String actorId, String movieId) {
        String query;
        query = "MATCH ({actorId : \"%s\"})-[r]->({movieId : \"%s\"})\n" +
                "RETURN type(r) as type";
        query = String.format(query, actorId, movieId);
        Result result = this.session.run(query);
        if(!result.hasNext()) {
            return false;
        }
        String bool = result.next().toString();

        if(bool != null) {
            return true;
        }
        return false;

    }
    public String[] getActorMovies(String actorId) {
        String query;
        query = "MATCH (a:actor {actorId: \"%s\"})\n" +
                "OPTIONAL MATCH (a)-->(x)\n" +
                "RETURN x.name";
        query = String.format(query, actorId);
        Result result = this.session.run(query);

        String movieName, res;
        int startIndex, endIndex;
        String[] empty = new String[0];

        List<String> movie = new ArrayList<String>();
        //System.out.println("Length Start: "+movie.size());
        //System.out.println("Output: "+result.next().toString());
        while(result.hasNext()) {
            res = result.next().toString();
            startIndex = res.indexOf("\"") + 1;
            endIndex = res.indexOf("\"", startIndex);
            if(endIndex < startIndex) {
                break;
            }
            movieName = res.substring(startIndex, endIndex);
            //System.out.println("Output: "+movieName);
            movie.add(movieName);
        }
        //System.out.println("Length End: "+movie.size());
        String[] movies = new String[movie.size()];

        for (int i=0; i<movie.size(); i++) {
            movies[i] = movie.get(i);
        }
        return movies;
    }
    public String[] getMovieActors(String movieId) {
        String query;
        query = "MATCH (m:movie {movieId: \"%s\"})\n" +
                "OPTIONAL MATCH (x)-->(m)\n" +
                "RETURN x.actorId";
        query = String.format(query, movieId);
        Result result = this.session.run(query);

        String actorID, res;
        int startIndex, endIndex;

        List<String> actor = new ArrayList<String>();
        while(result.hasNext()) {
            res = result.next().toString();
            startIndex = res.indexOf("\"") + 1;
            endIndex = res.indexOf("\"", startIndex);
            if(endIndex < startIndex) {
                break;
            }
            actorID = res.substring(startIndex, endIndex);
            //System.out.println("Output: "+movieName);
            actor.add(actorID);
        }
        String[] actors = new String[actor.size()];

        for (int i=0; i<actor.size(); i++) {
            actors[i] = actor.get(i);
        }
        return actors;
    }
    public String getActorName(String actorId) {
        String query;
        query = "MATCH (a:actor {actorId: \"%s\"})\n" +
                "RETURN a.name";
        query = String.format(query, actorId);
        Result result = this.session.run(query);

        String res = result.next().toString();

        int startIndex, endIndex;
        startIndex = res.indexOf("\"") + 1;
        endIndex = res.indexOf("\"", startIndex);

        String actorName = res.substring(startIndex, endIndex);

        return actorName;
    }
    public String getMovieName(String movieId) {
        String query;
        query = "MATCH (m:movie {movieId: \"%s\"})\n" +
                "RETURN m.name";
        query = String.format(query, movieId);
        Result result = this.session.run(query);

        String res = result.next().toString();

        int startIndex, endIndex;
        startIndex = res.indexOf("\"") + 1;
        endIndex = res.indexOf("\"", startIndex);

        String movieName = res.substring(startIndex, endIndex);

        return movieName;
    }

    public int getBaconNumber(String actorId) {
        String query;
        query = "MATCH p = shortestPath((bacon: actor {actorId: \"nm0000102\"})-[*]-(a: actor {actorId: \"%s\"}))" +
                "RETURN length(p)";
        query = String.format(query, actorId);
        Result result = this.session.run(query);
        if(result.hasNext()){
            int value = Integer.parseInt(result.next().toString().replaceAll("[^0-9]", ""));
            return (value/2);
        }
        return 0;
    }

    public String[] getBaconPath(String actorId) {
        int num = getBaconNumber(actorId);
        String[] actors = new String[num + 1];

        String query;
        query = "MATCH p = shortestPath((a: actor {actorId: \"%s\"})-[*]-(b: actor {actorId: \"nm0000102\"})) UNWIND nodes(p) AS n RETURN COALESCE(n.actorId, n.movieId) AS list";
        query = String.format(query, actorId);
        Result result = this.session.run(query);

        if (result.hasNext()) {
            List<Record> record = result.list();
            for (int i = 0; i < record.size(); i += 2) {
                String val = record.get(i).values().toString();
                String newVal = val.substring(1, val.length() - 1);
                actors[i / 2] = newVal;
            }
        }
        return actors;
    }
}
