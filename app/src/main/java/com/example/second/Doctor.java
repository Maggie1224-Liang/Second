package com.example.second;

public class Doctor {
    private String name;
    private String deputy;

    public Doctor(String name, String deputy) {
        this.name = name;
        this.deputy = deputy;
    }

    public String getName() {
        return name;
    }

    public String getDeputy() {
        return deputy;
    }
}
