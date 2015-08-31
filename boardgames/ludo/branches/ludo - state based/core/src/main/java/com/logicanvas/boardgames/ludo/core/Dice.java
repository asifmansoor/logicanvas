package com.logicanvas.boardgames.ludo.core;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;
import com.logicanvas.boardgames.ludo.utility.LudoLogger;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by amansoor on 24-08-2015.
 *
 * This class is to get dice outcomes
 */
public class Dice {
    private int[] dice = {-1, -1};
    private Random rng = new Random();

    private ArrayList diceRig = new ArrayList();

    public Dice() {
        if (GameConfiguration.RIG_GAME) {
            for (int i = 0; i < GameConfiguration.rigList.length; i++) {
                diceRig.add(GameConfiguration.rigList[i]);
            }
        }
    }

    public int[] twoDiceOutcome() {
        dice[0] = rng.nextInt(6);
        dice[1] = rng.nextInt(6);
        return dice;
    }

    public int oneDiceOutcome() {
        if (diceRig != null && !diceRig.isEmpty() && GameConfiguration.RIG_GAME)
            dice[0] = (int) diceRig.remove(0);
        else
            dice[0] = rng.nextInt(6) + 1;
        LudoLogger.debug("Dice :" + dice[0]);
        return dice[0];
    }
}
