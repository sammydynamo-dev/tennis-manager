package com.tennisleague.model;

import java.util.Objects;

public class WorkExperience {
    private int experienceID;
    private int coachID;
    private String experienceType;
    private int duration;

    public WorkExperience() {
    }

    public WorkExperience(int experienceID, int coachID, String experienceType, int duration) {
        this.experienceID = experienceID;
        this.coachID = coachID;
        this.experienceType = experienceType;
        this.duration = duration;
    }

    public int getExperienceID() {
        return experienceID;
    }

    public void setExperienceID(int experienceID) {
        this.experienceID = experienceID;
    }

    public int getCoachID() {
        return coachID;
    }

    public void setCoachID(int coachID) {
        this.coachID = coachID;
    }

    public String getExperienceType() {
        return experienceType;
    }

    public void setExperienceType(String experienceType) {
        this.experienceType = experienceType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkExperience that = (WorkExperience) o;
        return experienceID == that.experienceID &&
               coachID == that.coachID &&
               duration == that.duration &&
               Objects.equals(experienceType, that.experienceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experienceID, coachID, experienceType, duration);
    }

    @Override
    public String toString() {
        return "WorkExperience{" +
               "experienceID=" + experienceID +
               ", coachID=" + coachID +
               ", experienceType='" + experienceType + '\'' +
               ", duration=" + duration +
               '}';
    }
}
