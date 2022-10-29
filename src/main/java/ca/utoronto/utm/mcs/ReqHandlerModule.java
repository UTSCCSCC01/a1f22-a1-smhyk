package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

@Module
public class ReqHandlerModule {
    // Created the Provides module for Driver to instantiate a driver with the valid URL and aAuthentication token
    Dotenv dotenv = Dotenv.load();
    private final String uri = dotenv.get("NEO4J_ADDR");
    private final String uriDb = "bolt://%s:7687";

    private final String connecUrl = String.format(uriDb, uri);
    //private final String uriDb = "bolt://neo4j:7687";

    private final String username = "neo4j";
    private final String password = "123456";

    @Provides
    Driver provideDriver() {
        return GraphDatabase.driver(this.connecUrl, AuthTokens.basic(this.username, this.password));
    }
}
