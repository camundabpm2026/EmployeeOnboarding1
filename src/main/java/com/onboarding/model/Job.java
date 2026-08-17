package com.onboarding.model;

import com.onboarding.util.JsonUtil;

public class Job {

    private final int id;
    private final String title;
    private final String department;
    private final String location;
    private final String description;
    private final String status;

    public Job(int id, String title, String department, String location, String description, String status) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"title\":\"" + JsonUtil.escape(title) + "\","
                + "\"department\":\"" + JsonUtil.escape(department) + "\","
                + "\"location\":\"" + JsonUtil.escape(location) + "\","
                + "\"description\":\"" + JsonUtil.escape(description) + "\","
                + "\"status\":\"" + JsonUtil.escape(status) + "\""
                + "}";
    }
}
