package com.onboarding;

import com.onboarding.handler.ApplicationsHandler;
import com.onboarding.handler.JobsHandler;
import com.onboarding.handler.StaticFileHandler;
import com.onboarding.store.ApplicationStore;
import com.onboarding.store.JobStore;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        JobStore jobStore = new JobStore();
        ApplicationStore applicationStore = new ApplicationStore();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/jobs", new JobsHandler(jobStore));
        server.createContext("/api/applications", new ApplicationsHandler(applicationStore, jobStore));
        server.createContext("/", new StaticFileHandler(Paths.get("web")));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("Employee hiring workflow server running at http://localhost:" + PORT);
    }
}
