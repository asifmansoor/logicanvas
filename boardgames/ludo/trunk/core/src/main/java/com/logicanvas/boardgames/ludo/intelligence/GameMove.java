package com.logicanvas.boardgames.ludo.intelligence;

/**
 * Created by amansoor on 24-08-2015.
 *
 * This class is to define a game move
 */
public class GameMove {
    public static final int IDLE = 0;
    public static final int OPEN = 1;
    public static final int MOVE = 2;
    public static final int RESET = 3;
    public static final int ENTER = 4;
    public static final int HOME = 5;

    private int playerId;
    private int tokenIndex;
    private int moveType;
    private int moveSteps;
    private int moveScore;

    public GameMove(int playerId, int tokenIndex, int moveType, int moveSteps, int moveScore) {
        this.playerId = playerId;
        this.tokenIndex = tokenIndex;
        this.moveType = moveType;
        this.moveSteps = moveSteps;
        this.moveScore = moveScore;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public int getMoveType() {
        return moveType;
    }

    public int getMoveSteps() {
        return moveSteps;
    }

    public int getMoveScore() {
        return moveScore;
    }

    public void setMoveScore(int moveScore) {
        this.moveScore = moveScore;
    }
}
