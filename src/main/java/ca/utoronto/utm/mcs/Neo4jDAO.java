package ca.utoronto.utm.mcs;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.inject.Inject;

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
}
