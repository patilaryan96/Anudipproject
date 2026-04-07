package com.jobportal.model;

public class Profile {
    private int userId;
    private String skills;
    private int experienceYears;
    private String headline;

    public Profile() {
    }

    public Profile(int userId, String skills, int experienceYears, String headline) {
        this.userId = userId;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.headline = headline;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
}
