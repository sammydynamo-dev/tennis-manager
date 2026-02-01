package com.tennisleague.model;

import java.util.Objects;

public class Player {
    private int playerID;
    private int leagueWideNumber;
    private String name;
    private int age;

    public Player() {
    }

    public Player(int playerID, int leagueWideNumber, String name, int age) {
        this.playerID = playerID;
        this.leagueWideNumber = leagueWideNumber;
        this.name = name;
        this.age = age;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getLeagueWideNumber() {
        return leagueWideNumber;
    }

    public void setLeagueWideNumber(int leagueWideNumber) {
        this.leagueWideNumber = leagueWideNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerID == player.playerID &&
               leagueWideNumber == player.leagueWideNumber &&
               age == player.age &&
               Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerID, leagueWideNumber, name, age);
    }

    @Override
    public String toString() {
        return "Player{" +
               "playerID=" + playerID +
               ", leagueWideNumber=" + leagueWideNumber +
               ", name='" + name + '\'' +
               ", age=" + age +
               '}';
    }
}
