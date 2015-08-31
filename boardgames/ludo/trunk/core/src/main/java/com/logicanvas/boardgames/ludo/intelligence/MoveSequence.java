package com.logicanvas.boardgames.ludo.intelligence;

import java.util.ArrayList;

/**
 * Created by amansoor on 24-08-2015.
 *
 * This class is to keep track of the all player moves in one round of the board
 */
public class MoveSequence {
    private ArrayList sequenceArray;

    public MoveSequence() {
        sequenceArray = new ArrayList();
    }

    public void addSequence(GameMove move) {
        sequenceArray.add(move);
    }

    public GameMove popMove() {
        if (!isEmpty())
            return (GameMove) sequenceArray.remove(0);
        else
            return null;
    }

    public boolean isEmpty() {
        if (sequenceArray != null)
            return sequenceArray.isEmpty();
        else
            return true;
    }
}
