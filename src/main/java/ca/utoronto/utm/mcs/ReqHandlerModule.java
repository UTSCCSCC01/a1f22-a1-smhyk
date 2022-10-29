package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

@Module
public class ReqHandlerModule {
    // Created the Provides module for Driver to instantiate a driver with the valid URL and aAuthentication token
    private final String uriDb = "bolt://localhost:7687";
    private final String username = "neo4j";
    private final String password = "123456";

    @Provides
    Driver provideDriver() {
        return GraphDatabase.driver(this.uriDb, AuthTokens.basic(this.username, this.password));
    }
}
