package com.logicanvas.boardgames.ludo.model;

/**
 * Created by amansoor on 24-08-2015.
 * <p/>
 * This class is to track the various properties of a player token
 */
public class PlayerToken {
    private int location;       // location of token on board
    private int state;          // state of the token
    private int playerId;       // id of the player
    private int index;          // index of the token for a player id
    private int tokenScore;     // score of the token

    public void updateLocation(int steps) {
        location += steps;
    }

    public void updateTokenScore(int score) {
        tokenScore += score;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTokenScore() {
        return tokenScore;
    }

    public void setTokenScore(int tokenScore) {
        this.tokenScore = tokenScore;
    }
}
