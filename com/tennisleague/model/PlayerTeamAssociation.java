package com.tennisleague.model;

import java.util.Objects;

public class PlayerTeamAssociation {
    private int associationID;
    private int playerID;
    private int teamNumber;
    private int yearJoined;
    private Integer yearLeft;

    public PlayerTeamAssociation() {
    }

    public PlayerTeamAssociation(int associationID, int playerID, int teamNumber, int yearJoined, Integer yearLeft) {
        this.associationID = associationID;
        this.playerID = playerID;
        this.teamNumber = teamNumber;
        this.yearJoined = yearJoined;
        this.yearLeft = yearLeft;
    }

    public int getAssociationID() {
        return associationID;
    }

    public void setAssociationID(int associationID) {
        this.associationID = associationID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public int getYearJoined() {
        return yearJoined;
    }

    public void setYearJoined(int yearJoined) {
        this.yearJoined = yearJoined;
    }

    public Integer getYearLeft() {
        return yearLeft;
    }

    public void setYearLeft(Integer yearLeft) {
        this.yearLeft = yearLeft;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerTeamAssociation that = (PlayerTeamAssociation) o;
        return associationID == that.associationID &&
               playerID == that.playerID &&
               teamNumber == that.teamNumber &&
               yearJoined == that.yearJoined &&
               Objects.equals(yearLeft, that.yearLeft);
    }

    @Override
    public int hashCode() {
        return Objects.hash(associationID, playerID, teamNumber, yearJoined, yearLeft);
    }

    @Override
    public String toString() {
        return "PlayerTeamAssociation{" +
               "associationID=" + associationID +
               ", playerID=" + playerID +
               ", teamNumber=" + teamNumber +
               ", yearJoined=" + yearJoined +
               ", yearLeft=" + yearLeft +
               '}';
    }
}
