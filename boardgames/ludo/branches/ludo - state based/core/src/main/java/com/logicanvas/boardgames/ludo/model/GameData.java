package com.logicanvas.boardgames.ludo.model;

import com.logicanvas.boardgames.ludo.core.LudoPlayer;
import com.logicanvas.boardgames.ludo.intelligence.MoveSequence;
import com.logicanvas.boardgames.ludo.utility.LudoLogger;

/**
 * Created by amansoor on 24-08-2015.
 */
public class GameData {
    private LudoPlayer[] players;
    private MoveSequence moveSequence;
    private int turn;
    int diceRoll;

    public LudoPlayer[] getPlayers() {
        return players;
    }

    public void setPlayers(LudoPlayer[] players) {
        this.players = players;
    }

    public LudoPlayer getPlayer(int playerId) {
        if (players != null) {
            return players[playerId];
        } else {
            LudoLogger.log("Player data not set");
        }

        return null;
    }

    public void setMoveSequence(MoveSequence moveSequence) {
        this.moveSequence = moveSequence;
    }

    public MoveSequence getMoveSequence() {
        return moveSequence;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }
}
