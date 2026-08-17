package com.onboarding.model;

import com.onboarding.util.JsonUtil;

public class Application {

    private final int id;
    private final int jobId;
    private final String name;
    private final String email;
    private final String phone;
    private final String coverLetter;
    private final String status;
    private final String submittedAt;

    public Application(int id, int jobId, String name, String email, String phone,
                        String coverLetter, String status, String submittedAt) {
        this.id = id;
        this.jobId = jobId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.coverLetter = coverLetter;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public int getId() {
        return id;
    }

    public int getJobId() {
        return jobId;
    }

    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"jobId\":" + jobId + ","
                + "\"name\":\"" + JsonUtil.escape(name) + "\","
                + "\"email\":\"" + JsonUtil.escape(email) + "\","
                + "\"phone\":\"" + JsonUtil.escape(phone) + "\","
                + "\"coverLetter\":\"" + JsonUtil.escape(coverLetter) + "\","
                + "\"status\":\"" + JsonUtil.escape(status) + "\","
                + "\"submittedAt\":\"" + JsonUtil.escape(submittedAt) + "\""
                + "}";
    }
}
