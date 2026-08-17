package com.onboarding.store;

import com.onboarding.model.Application;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ApplicationStore {

    private final List<Application> applications = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public Application add(int jobId, String name, String email, String phone, String coverLetter) {
        int id = idGenerator.incrementAndGet();
        Application application = new Application(
                id, jobId, name, email, phone, coverLetter, "SUBMITTED",
                java.time.Instant.now().toString());
        applications.add(application);
        return application;
    }

    public List<Application> getAll() {
        return applications;
    }

    public List<Application> getByJobId(int jobId) {
        return applications.stream()
                .filter(a -> a.getJobId() == jobId)
                .collect(Collectors.toList());
    }

    public Optional<Application> getById(int id) {
        return applications.stream().filter(a -> a.getId() == id).findFirst();
    }
}
