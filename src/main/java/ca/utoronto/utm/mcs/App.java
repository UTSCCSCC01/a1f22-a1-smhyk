package ca.utoronto.utm.mcs;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;

public class App
{
    static int port = 8080;

    public static void main(String[] args) throws IOException
    {
        // Created the Server context using the dagger components for ReqHandler and ServerComponent
        ReqHandlerComponent component = DaggerReqHandlerComponent.create();
        ReqHandler reqHandler = component.buildHandler();

        ServerComponent serverComponent = DaggerServerComponent.create();
        Server server = serverComponent.buildServer();

        server.server.createContext("/api/v1", reqHandler);
        server.server.start();
        System.out.printf("Server started on port %d\n", port);

        // This code is used to get the neo4j address, you must use this so that we can mark :)
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        System.out.println(addr);
    }
}
