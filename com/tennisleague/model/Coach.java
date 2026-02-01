package com.tennisleague.model;

import java.util.Objects;

public class Coach {
    private int coachID;
    private String name;
    private String telephoneNumber;
    private int teamNumber;

    public Coach() {
    }

    public Coach(int coachID, String name, String telephoneNumber, int teamNumber) {
        this.coachID = coachID;
        this.name = name;
        this.telephoneNumber = telephoneNumber;
        this.teamNumber = teamNumber;
    }

    public int getCoachID() {
        return coachID;
    }

    public void setCoachID(int coachID) {
        this.coachID = coachID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coach coach = (Coach) o;
        return coachID == coach.coachID &&
               teamNumber == coach.teamNumber &&
               Objects.equals(name, coach.name) &&
               Objects.equals(telephoneNumber, coach.telephoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coachID, name, telephoneNumber, teamNumber);
    }

    @Override
    public String toString() {
        return "Coach{" +
               "coachID=" + coachID +
               ", name='" + name + '\'' +
               ", telephoneNumber='" + telephoneNumber + '\'' +
               ", teamNumber=" + teamNumber +
               '}';
    }
}
