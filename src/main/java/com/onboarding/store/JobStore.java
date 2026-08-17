package com.onboarding.store;

import com.onboarding.model.Job;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class JobStore {

    private final List<Job> jobs = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public JobStore() {
        seed();
    }

    private void seed() {
        add("Software Engineer", "Engineering", "Bengaluru (Hybrid)",
                "Build and maintain the employee onboarding platform.");
        add("HR Business Partner", "Human Resources", "Mumbai",
                "Support hiring managers through the full recruitment lifecycle.");
        add("Product Designer", "Design", "Remote",
                "Design candidate-facing and HR-facing experiences.");
    }

    private void add(String title, String department, String location, String description) {
        int id = idGenerator.incrementAndGet();
        jobs.add(new Job(id, title, department, location, description, "OPEN"));
    }

    public List<Job> getAll() {
        return jobs;
    }

    public Optional<Job> getById(int id) {
        return jobs.stream().filter(j -> j.getId() == id).findFirst();
    }
}
