package com.onboarding.handler;

import com.onboarding.model.Application;
import com.onboarding.store.ApplicationStore;
import com.onboarding.store.JobStore;
import com.onboarding.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles GET /api/applications (optionally ?jobId=), GET /api/applications/{id},
 * and POST /api/applications (candidate submits an application).
 */
public class ApplicationsHandler implements HttpHandler {

    private final ApplicationStore applicationStore;
    private final JobStore jobStore;

    public ApplicationsHandler(ApplicationStore applicationStore, JobStore jobStore) {
        this.applicationStore = applicationStore;
        this.jobStore = jobStore;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleGet(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            handlePost(exchange);
        } else {
            HttpUtil.sendError(exchange, 405, "Method not allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String remainder = path.substring("/api/applications".length()).replaceFirst("^/+", "");

        if (!remainder.isEmpty()) {
            try {
                int id = Integer.parseInt(remainder);
                Optional<Application> application = applicationStore.getById(id);
                if (application.isPresent()) {
                    HttpUtil.sendJson(exchange, 200, application.get().toJson());
                } else {
                    HttpUtil.sendError(exchange, 404, "Application not found");
                }
            } catch (NumberFormatException e) {
                HttpUtil.sendError(exchange, 400, "Invalid application id");
            }
            return;
        }

        Map<String, String> query = HttpUtil.parseQuery(exchange.getRequestURI().getQuery());
        List<Application> applications;
        if (query.containsKey("jobId")) {
            try {
                applications = applicationStore.getByJobId(Integer.parseInt(query.get("jobId")));
            } catch (NumberFormatException e) {
                HttpUtil.sendError(exchange, 400, "Invalid jobId filter");
                return;
            }
        } else {
            applications = applicationStore.getAll();
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < applications.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(applications.get(i).toJson());
        }
        sb.append("]");
        HttpUtil.sendJson(exchange, 200, sb.toString());
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = HttpUtil.readBody(exchange);
        Map<String, String> fields = com.onboarding.util.JsonUtil.parseFlatObject(body);

        String jobIdRaw = fields.get("jobId");
        String name = fields.getOrDefault("name", "").trim();
        String email = fields.getOrDefault("email", "").trim();
        String phone = fields.getOrDefault("phone", "").trim();
        String coverLetter = fields.getOrDefault("coverLetter", "").trim();

        if (jobIdRaw == null || jobIdRaw.isEmpty()) {
            HttpUtil.sendError(exchange, 400, "jobId is required");
            return;
        }
        if (name.isEmpty() || email.isEmpty()) {
            HttpUtil.sendError(exchange, 400, "name and email are required");
            return;
        }

        int jobId;
        try {
            jobId = Integer.parseInt(jobIdRaw);
        } catch (NumberFormatException e) {
            HttpUtil.sendError(exchange, 400, "Invalid jobId");
            return;
        }

        Optional<com.onboarding.model.Job> job = jobStore.getById(jobId);
        if (!job.isPresent()) {
            HttpUtil.sendError(exchange, 404, "Job not found");
            return;
        }

        Application application = applicationStore.add(jobId, name, email, phone, coverLetter);
        HttpUtil.sendJson(exchange, 201, application.toJson());
    }
}
