package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;

import java.net.InetSocketAddress;

@Module
public class ServerModule {
    //Created the Provides module for HttpServer to instantiate a httpserver with the valid hostname and Port number
    static int PORT = 8080;
    @Provides
    HttpServer provideHttpServer() throws RuntimeException {
        try {
            return HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create HTTPServer");
        }
    }
}
