package com.logicanvas.boardgames.ludo.core;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;
import com.logicanvas.boardgames.ludo.config.GameRules;
import com.logicanvas.boardgames.ludo.intelligence.GameMove;
import com.logicanvas.boardgames.ludo.model.PlayerToken;
import com.logicanvas.boardgames.ludo.utility.LudoLogger;

/**
 * Created by amansoor on 24-08-2015.
 * <p/>
 * This class is to track the various properties of a Ludo Player
 */
public class LudoPlayer {

    public final int startLocationIndex;                  // index of the board start location for the tokens
    public final int openLocationIndex;                   // index of the open location on board
    private int homeRowStartIndex;                        // index of the home row starting position
    private PlayerToken[] playerTokens;                   // token objects for the player
    private int playerId;                                 // id of the player
    private boolean hasPlayerBeatenAToken = false;        /* flag for check whether player has beaten another player
                                                             or not
                                                           */
    private int numTokensHome;                            // number of tokens already home
    private int numTokensUnOpened;                           // # of tokens at start which are not yet open
    private int playerScore;                              // total score of the player
    private int rank;                                     // rank of the player

    public LudoPlayer(int startLoc, int openLoc, int homeRowStartLoc, int id) {
        startLocationIndex = startLoc;
        openLocationIndex = openLoc;
        homeRowStartIndex = homeRowStartLoc;
        playerId = id;
        playerScore = 0;
        numTokensHome = 0;
        numTokensUnOpened = GameConfiguration.NO_OF_TOKENS_PER_PLAYER;

        playerTokens = new PlayerToken[GameConfiguration.NO_OF_TOKENS_PER_PLAYER];
        for (int i = 0; i < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; i++) {
            playerTokens[i] = new PlayerToken();
            playerTokens[i].setState(GameConfiguration.TOKEN_STATE_UNOPEN);
            playerTokens[i].setTokenScore(0);
            playerTokens[i].setLocation(startLocationIndex + i);
            playerTokens[i].setIndex(i);
            playerTokens[i].setPlayerId(playerId);
        }
    }

    public boolean isAllHomeForPlayer() {
        if (numTokensHome == GameConfiguration.NO_OF_TOKENS_PER_PLAYER) {
            return true;
        } else {
            return false;
        }
    }

    public void playMove(GameMove gameMove) {
        // TODO : use enter home and enter home row
        switch (gameMove.getMoveType()) {
            case GameMove.OPEN:
                LudoLogger.debug("move: OPEN : playerid: "+playerId+" tokenIndex: "+gameMove.getTokenIndex());
                openToken(gameMove.getTokenIndex());
                break;
            case GameMove.MOVE:
                LudoLogger.debug("move: MOVE : playerid: "+playerId+" tokenIndex: "+gameMove.getTokenIndex());
                moveToken(gameMove.getTokenIndex(), gameMove.getMoveSteps());
                break;
            case GameMove.RESET:
                LudoLogger.debug("move: RESET : playerid: "+playerId+" tokenIndex: "+gameMove.getTokenIndex());
                resetToken(gameMove.getTokenIndex());
            case GameMove.IDLE:
                LudoLogger.debug("move: IDLE : playerid: "+playerId+" tokenIndex: "+gameMove.getTokenIndex());
                break;
        }
    }

    public void resetToken(int tokenIndex) {
        playerTokens[tokenIndex].setLocation(startLocationIndex + tokenIndex);
        playerTokens[tokenIndex].setTokenScore(0);
        playerTokens[tokenIndex].setState(GameConfiguration.TOKEN_STATE_UNOPEN);
        numTokensUnOpened++;
    }

    public void openToken(int tokenIndex) {
        playerTokens[tokenIndex].setLocation(openLocationIndex);
        playerTokens[tokenIndex].setTokenScore(GameConfiguration.POINTS_OPEN_TOKEN);
        playerTokens[tokenIndex].setState(GameConfiguration.TOKEN_STATE_OPEN);
        numTokensUnOpened--;
    }

    public void enterLastRow(int tokenIndex, int numMoves) {
        playerTokens[tokenIndex].setLocation(homeRowStartIndex + numMoves);
        playerTokens[tokenIndex].updateTokenScore(numMoves + 100);
        playerTokens[tokenIndex].setState(GameConfiguration.TOKEN_STATE_IN_HOME_ROW);
    }

    private void enterTokenHome(int tokenIndex, int numMoves) {
        playerTokens[tokenIndex].setLocation(homeRowStartIndex + 5);
        playerTokens[tokenIndex].setTokenScore(numMoves+200);
        playerTokens[tokenIndex].setState(GameConfiguration.TOKEN_STATE_HOME);
    }

    private void restartCycle(int tokenIndex, int location) {
        playerTokens[tokenIndex].setLocation(location);
        playerTokens[tokenIndex].setTokenScore(location - startLocationIndex);
    }

    public void moveToken(int tokenIndex, int numMoves) {
        int currentLocation = playerTokens[tokenIndex].getLocation();
        int finalLocation = getFinalCyclicLocation(tokenIndex, numMoves);

        if (currentLocation > GameConfiguration.GAME_CYCLE_END_INDEX) {
            // inside the home row
            if (currentLocation + numMoves == homeRowStartIndex + 5) {
                LudoLogger.log("Enter Home");
                enterTokenHome(tokenIndex, numMoves);
                numTokensHome++;
            } else if (currentLocation + numMoves < homeRowStartIndex + 5) {
                LudoLogger.log("Move in home row");
                playerTokens[tokenIndex].updateLocation(numMoves);
                playerTokens[tokenIndex].setTokenScore(numMoves);
            } else {
                LudoLogger.log("No move");
            }
        } else if (canPlayerEnterHome() && finalLocation >= homeRowStartIndex) {
            int diff = finalLocation - (openLocationIndex - 2);
            enterLastRow(tokenIndex, diff - 1); // 1 is deducted since homeRowStartIndex would already mean the first position
            LudoLogger.log("Enter Home Row");
        } else if (currentLocation + numMoves > GameConfiguration.GAME_CYCLE_END_INDEX) {
            //  start second cycle around the board. Moving inside the home row would be detected and handled separately
            int diff = currentLocation + numMoves - GameConfiguration.GAME_CYCLE_END_INDEX - 1;
            restartCycle(tokenIndex, diff);
            LudoLogger.log("Restart Cycle");
        } else {
            playerTokens[tokenIndex].updateLocation(numMoves);
            playerTokens[tokenIndex].updateTokenScore(numMoves);
            LudoLogger.log("Normal Move");
        }
    }

/*    private boolean isEntryToHomeRow(int finalLocation, int currentLocation, int numMoves) {
        // The condition for this to be true is that the token is coming from behind the home row entry
        // point and after applying the move it goes beyond it.
        // To check if the token before the move application was behind the home row entry
        // we need to check for the normal case where the currentLocation would have a lesser value than the home row entry
        // as well as the case when the currentLocation after applying the move completes a cycle in which case it will
        // have a higher value than the home row entry
        if ((finalLocation - numMoves <= 0 || currentLocation <= (openLocationIndex - 2))
                && (finalLocation > (openLocationIndex - 2))) {
            return true;
        }

        return false;
    }*/

    protected boolean canPlayerEnterHome() {
        if (GameRules.playerBeatToEnterHome && !hasPlayerBeatenAToken) {
            return false;
        } else {
            return true;
        }
    }

    public boolean tokenHasValidMove(int tokenId, int numMoves) {
        //TODO: check for player beat rule
        int playedMoves = playerTokens[tokenId].getLocation();
        if (playedMoves > GameConfiguration.GAME_CYCLE_END_INDEX) {
            int homeRowMoves = playedMoves - homeRowStartIndex + 1;
            playedMoves = homeRowMoves + GameConfiguration.GAME_CYCLE_END_INDEX;
        }

        if (playedMoves + numMoves <= GameConfiguration.MAX_TOKEN_MOVE)
            return true;
        else
            return false;
    }

    private int getFinalCyclicLocation(int tokenIndex, int numMoves) {
        int currentLocation = playerTokens[tokenIndex].getLocation();
        if (currentLocation + numMoves > GameConfiguration.GAME_CYCLE_END_INDEX) {
            return currentLocation + numMoves - GameConfiguration.GAME_CYCLE_END_INDEX - 1;
        }

        return currentLocation + numMoves;
    }

    public int getStartLocationIndex() {
        return startLocationIndex;
    }

    public int getOpenLocationIndex() {
        return openLocationIndex;
    }

    public int getHomeRowStartIndex() {
        return homeRowStartIndex;
    }

    public boolean isHasPlayerBeatenAToken() {
        return hasPlayerBeatenAToken;
    }

    public PlayerToken[] getPlayerTokens() {
        return playerTokens;
    }

    public PlayerToken getPlayerToken(int tokenIndex) {
        return playerTokens[tokenIndex];
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getNumTokensHome() {
        return numTokensHome;
    }

    public int getNumTokensUnOpened() {
        return numTokensUnOpened;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public void setHasPlayerBeatenAToken(boolean hasPlayerBeatenAToken) {
        this.hasPlayerBeatenAToken = hasPlayerBeatenAToken;
    }
}
