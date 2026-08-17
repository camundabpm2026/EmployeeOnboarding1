package com.onboarding.handler;

import com.onboarding.model.Job;
import com.onboarding.store.JobStore;
import com.onboarding.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Handles GET /api/jobs and GET /api/jobs/{id}.
 */
public class JobsHandler implements HttpHandler {

    private final JobStore jobStore;

    public JobsHandler(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.sendError(exchange, 405, "Method not allowed");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String remainder = path.substring("/api/jobs".length());
        remainder = remainder.replaceFirst("^/+", "");

        if (remainder.isEmpty()) {
            List<Job> jobs = jobStore.getAll();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < jobs.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(jobs.get(i).toJson());
            }
            sb.append("]");
            HttpUtil.sendJson(exchange, 200, sb.toString());
            return;
        }

        try {
            int id = Integer.parseInt(remainder);
            Optional<Job> job = jobStore.getById(id);
            if (job.isPresent()) {
                HttpUtil.sendJson(exchange, 200, job.get().toJson());
            } else {
                HttpUtil.sendError(exchange, 404, "Job not found");
            }
        } catch (NumberFormatException e) {
            HttpUtil.sendError(exchange, 400, "Invalid job id");
        }
    }
}
