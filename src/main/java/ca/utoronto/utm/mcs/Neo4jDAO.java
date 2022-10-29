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
    private final Session session;
    private final Driver driver;

    @Inject
    public Neo4jDAO(Driver driver) {
        // Injected Driver (org.neo4j.driver) into the constructor of Neo4jDAO and instantiated the session
        this.driver = driver;
        this.session = driver.session();
    }

   //Inserts actor into the database with the given name and actorId
    public void insertActor(String name, String actorId) {
        String query;
        query = "CREATE (n:actor {Name: \"%s\", id: \"%s\"})";
        query = String.format(query, name, actorId);
        this.session.run(query);
        return;
    }

    //Inserts movie into the database with the given name and movieId
    public void insertMovie(String name, String movieId) {
        String query;
        query = "CREATE (n:movie {Name: \"%s\", id: \"%s\"})";
        query = String.format(query, name, movieId);
        this.session.run(query);
        return;
    }

    //Adds a relation between the given actor id aid and movie id mid
    public void addRelationActed_In(String aid, String mid) {
        String query;
        query = "MATCH (a:actor),(m:movie)" +
                "WHERE a.id = \"%s\" AND m.id = \"%s\"" +
                "CREATE (a)-[r:ACTED_IN]->(m)" +
                "RETURN type(r)";
        query = String.format(query, aid, mid);
        this.session.run(query);
        return;
    }

    //Checks if an actor with id actorId already exists in the database
    public boolean checkActorExists(String actorId) {
        String query;
        query = "OPTIONAL MATCH (n:actor{id:\"%s\"})\n" +
                "RETURN n IS NOT NULL AS Predicate";
        query = String.format(query, actorId);
        Result result = this.session.run(query);
        String bool = result.next().toString();

        if(bool.indexOf("TRUE") >= 0) {
            return true;
        }
        return false;
    }

    //Checks if a movie with id movieId already exists in the database
    public boolean checkMovieExists(String movieId) {
        String query;
        query = "OPTIONAL MATCH (n:movie{id:\"%s\"})\n" +
                "RETURN n IS NOT NULL AS Predicate";
        query = String.format(query, movieId);
        Result result = this.session.run(query);
        String bool = result.next().toString();

        if(bool.indexOf("TRUE") >= 0) {
            return true;
        }
        return false;
    }

    //Checks if there is a relationship between an actor with id actorId and movie with id movieId
    public boolean hasRelationship(String actorId, String movieId) {
        String query;
        query = "MATCH (a:actor),(m:movie)" +
                "WHERE (a)-[:ACTED_IN]->(m) AND a.id = \"%s\" AND m.id = \"%s\"" +
                "RETURN a";

        query = String.format(query, actorId, movieId);
        Result result = this.session.run(query);

        return result.hasNext();
    }

    public boolean checkRelationshipExists(String actorId, String movieId) {
        String query;
        query = "MATCH ({id : \"%s\"})-[r]->({id : \"%s\"})\n" +
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

    //Returns a list of movies an actor with id actorId has acted in
    public String[] getActorMovies(String actorId) {
        String query;
        query = "MATCH (a:actor {id: \"%s\"})\n" +
                "OPTIONAL MATCH (a)-->(x)\n" +
                "RETURN x.Name";
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

    //Returns a list of actors in a movie with id movieId
    public String[] getMovieActors(String movieId) {
        String query;
        query = "MATCH (m:movie {id: \"%s\"})\n" +
                "OPTIONAL MATCH (x)-->(m)\n" +
                "RETURN x.id";
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

    //Returns the name of the actor with id actorId
    public String getActorName(String actorId) {
        String query;
        query = "MATCH (a:actor {id: \"%s\"})\n" +
                "RETURN a.Name";
        query = String.format(query, actorId);
        Result result = this.session.run(query);

        String res = result.next().toString();

        int startIndex, endIndex;
        startIndex = res.indexOf("\"") + 1;
        endIndex = res.indexOf("\"", startIndex);

        String actorName = res.substring(startIndex, endIndex);

        return actorName;
    }

    //Returns the name of the movie with id movieId
    public String getMovieName(String movieId) {
        String query;
        query = "MATCH (m:movie {id: \"%s\"})\n" +
                "RETURN m.Name";
        query = String.format(query, movieId);
        Result result = this.session.run(query);

        String res = result.next().toString();

        int startIndex, endIndex;
        startIndex = res.indexOf("\"") + 1;
        endIndex = res.indexOf("\"", startIndex);

        String movieName = res.substring(startIndex, endIndex);

        return movieName;
    }

    //Returns the Bacon number of an actor with id actorId
    public int getBaconNumber(String actorId) {
        String query;
        query = "MATCH p = shortestPath((bacon: actor {id: \"nm0000102\"})-[*]-(a: actor {id: \"%s\"}))" +
                "RETURN length(p)";
        query = String.format(query, actorId);
        Result result = this.session.run(query);
        if(result.hasNext()){
            int value = Integer.parseInt(result.next().toString().replaceAll("[^0-9]", ""));
            return (value/2);
        }
        return 0;
    }

    //Returns a list of actors in the Bacon path of actor with id actorId
    public String[] getBaconPath(String actorId) {
        int num = getBaconNumber(actorId);
        String[] actors = new String[num + 1];

        String query;
        query = "MATCH p = shortestPath((a: actor {id: \"%s\"})-[*]-(b: actor {id: \"nm0000102\"})) UNWIND nodes(p) AS n RETURN COALESCE(n.id) AS list";
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
