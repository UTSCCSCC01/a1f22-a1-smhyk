package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;

public class Server {
    //Injected the HttpServer object into the constructor of Server
    HttpServer server;

    @Inject
    public Server(HttpServer server) {
        this.server = server;
    }
}
