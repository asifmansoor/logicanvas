package com.logicanvas.boardgames.ludo.intelligence;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;
import com.logicanvas.boardgames.ludo.config.GameRules;
import com.logicanvas.boardgames.ludo.core.LudoPlayer;
import com.logicanvas.boardgames.ludo.model.GameData;
import com.logicanvas.boardgames.ludo.model.PlayerToken;
import com.logicanvas.boardgames.ludo.utility.LudoLogger;
import com.logicanvas.boardgames.ludo.utility.Utility;

import java.util.ArrayList;

/**
 * Created by amansoor on 24-08-2015.
 */
public class MoveEvaluator {
    private GameData gameData;

    public MoveEvaluator(GameData gameData) {
        this.gameData = gameData;
    }

    public GameMove evaluateMove() {
        ArrayList moves = new ArrayList(GameConfiguration.NO_OF_TOKENS_PER_PLAYER);
        GameMove move = null;
        LudoPlayer player = (LudoPlayer) gameData.getPlayer(gameData.getTurn());
        int highestScore = GameConfiguration.POINTS_IDLE_TOKEN;
        int maxMoveScoreTokenIndex = -1;    // Variable to track the index of the token with the maximum move score

        calculatePlayerRanks();

        // calculate the score for each token move and select the move which maximizes the player score
        for (int i = 0; i < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; i++) {
            move = calculateMove(i);
            if (move != null) {
                moves.add(move);
                if (move.getMoveScore() >= highestScore) {
                    maxMoveScoreTokenIndex = i;
                    highestScore = move.getMoveScore();
                }
            }
        }

        if (!moves.isEmpty()) {
            move = (GameMove) moves.get(maxMoveScoreTokenIndex);
        }
        return move;
    }

    /*
     * Method to set ranks of the players
     */
    private void calculatePlayerRanks() {
        for (int index = 0; index < GameConfiguration.NO_OF_PLAYERS; index++) {
            gameData.getPlayer(index).setRank(getPlayerRank(gameData.getPlayer(index)));
        }
    }

    /*
     * Method to get the rank of a player
     */
    private int getPlayerRank(LudoPlayer player) {
        int rank = GameConfiguration.NO_OF_PLAYERS;
        for (int index = 0; index < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; index++) {
            if (player.getPlayerId() != gameData.getPlayer(index).getPlayerId()
                    && gameData.getPlayer(index).getPlayerScore() <= player.getPlayerScore()) {
                rank--;
            }
        }

        return rank;
    }

    private boolean canTokenBeOpened

    private GameMove calculateMove(int tokenIndex) {
        GameMove gameMove = null;
        int playerId = gameData.getTurn();
        int diceRoll = gameData.getDiceRoll();
        int tokenLocation = gameData.getPlayer(playerId).getPlayerToken(tokenIndex).getLocation();
        int moveScore = 0;
        int opportunityScore = 0;
        int threatPerceptionScore = 0;

        // check if the token is closed and can be opened
        if (gameData.getPlayer(playerId).getPlayerToken(tokenIndex).getState() == GameConfiguration.TOKEN_STATE_UNOPEN && Utility.findInArray(GameRules
                .tokenOpeningMoves, diceRoll) != -1) {
            // can open token
            // TODO: can check for possibility of beating or getting beaten
            moveScore = GameConfiguration.POINTS_CAN_OPEN_TOKEN;
            gameMove = new GameMove(playerId, tokenIndex, GameMove.OPEN, 0, moveScore);
            LudoLogger.debug("evaluate: playerid : " + playerId + " tokenIndex: " + tokenIndex + " moveScore: " + moveScore + " " +
                    "outcome: " +
                    "OPEN");
            return gameMove;
        }

        // check for invalid moves
        if (!isValidMove(tokenIndex)) {
            moveScore = GameConfiguration.POINTS_IDLE_TOKEN;
            gameMove = new GameMove(playerId, tokenIndex, GameMove.IDLE, 0, moveScore);
            LudoLogger.debug("evaluate: playerid : " + playerId + " tokenIndex: " + tokenIndex + " moveScore: " + moveScore + " " +
                    "outcome: IDLE");
            return gameMove;
        } else {
            // check quadrant
            int quad = getPlayerTokenQuadrant(tokenIndex);

            // calculate scores
            // If rule is off player can enter.If rule is on player can only enter if it has already beaten another
            if ((!(GameRules.playerBeatToEnterHome && !gameData.getPlayer(playerId).isHasPlayerBeatenAToken())) &&
                    (getUpdatedLocation(tokenLocation, diceRoll) > GameConfiguration
                            .HOME_ROW_CHECK_INDEX_LIST[playerId]) && quad != 1) {
                // can enter home row
                opportunityScore += GameConfiguration.POINTS_CAN_ENTER_HOME_ROW;
                LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " opportunityScore: " +
                        opportunityScore + " outcome: ENTER HOME ROW");
            } else {

                // check other opponent proximity
                PlayerToken proximalToken;

                // check for safe spots and double up
                if (GameRules.safeSpotEnabled && Utility.findInArray(GameConfiguration.SAFE_SPOT_LIST,
                        (getUpdatedLocation(tokenLocation, diceRoll))) != -1) {
                    // can land on safe spot
                    opportunityScore += GameConfiguration.POINTS_CAN_LAND_ON_SAFE_SPOT * quad;
                    LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " opportunityScore:" +
                            " " +
                            "" + opportunityScore + " outcome: SAFE SPOT");
                } else if (GameRules.doubleUpAdvantageEnabled && checkDoubleUp(tokenIndex,
                        getUpdatedLocation(tokenLocation, diceRoll))) {
                    // can move to a double up position
                    opportunityScore += GameConfiguration.POINTS_CAN_DOUBLE_UP * quad;
                }

                // If opponent behind before move and after move
                // If opponent behind before move and no player behind after move
                // TODO: check for multiple token proximity
                if (checkPlayerProximity(tokenLocation, GameConfiguration.REVERSE) != null) {
                    if (checkPlayerProximity(getUpdatedLocation(tokenLocation, diceRoll), GameConfiguration
                            .REVERSE) !=
                            null) {
                        // If opponent behind before move and after move
                        threatPerceptionScore += GameConfiguration.POINTS_OPPONENT_BEHIND_BEFORE_AND_AFTER_MOVE * quad;
                        LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                "threatPerceptionScore: " + threatPerceptionScore + " situation: If " +
                                "opponent behind before move and after move");

                        // check if player is already on a safe spot
                        if (GameRules.safeSpotEnabled && Utility.findInArray
                                (GameConfiguration.SAFE_SPOT_LIST, tokenLocation) != -1) {
                            threatPerceptionScore += GameConfiguration.POINTS_PLAYER_ALREADY_SAFE_SPOT;
                            LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                    "threatPerceptionScore: " + threatPerceptionScore + " situation: If " +
                                    "player is already on a safe spot");
                        }

                    } else {
                        // If opponent behind before move and no player behind after move
                        opportunityScore += GameConfiguration.POINTS_OPPONENT_BEHIND_BEFORE_MOVE_AND_NOT_AFTER_MOVE *
                                quad;
                        LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                "opportunityScore: " + opportunityScore + " situation: If " +
                                "opponent behind before move and no player behind after move");
                    }
                }

                // If opponent ahead before move and after move
                // If opponent ahead before move and hit after move
                if ((proximalToken = checkPlayerProximity(tokenLocation, GameConfiguration.FORWARD)) !=
                        null) {
                    if (checkPlayerProximity
                            (getUpdatedLocation(tokenLocation, diceRoll), GameConfiguration
                                    .FORWARD) !=
                            null) {
                        // If opponent ahead before move and after move
                        opportunityScore += GameConfiguration.POINTS_OPPONENT_AHEAD_BEFORE_AND_AFTER_MOVE * quad;
                        LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                "opportunityScore: " + opportunityScore + " situation: If" +
                                " " +
                                "opponent ahead before move and after move");
                    } else if (getUpdatedLocation(tokenLocation, diceRoll) == proximalToken.getLocation()) {
                        // If opponent hit by the player after move
                        if (Utility.findInArray(GameConfiguration.SAFE_SPOT_LIST, proximalToken.getLocation()) == -1) {
                            // If opponent ahead before move and hit after move
                            opportunityScore += GameConfiguration
                                    .POINTS_OPPONENT_AHEAD_BEFORE_MOVE_AND_HIT_AFTER_MOVE * gameData.getPlayer(
                                    proximalToken.getPlayerId()).getRank() * quad;
                            LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                    "opportunityScore: " + opportunityScore + " situation:" +
                                    " If opponent ahead before move and hit after move");
                        }
                    } else {
                        // If opponent ahead before move and behind after move
                        threatPerceptionScore += GameConfiguration.POINTS_OPPONENT_BEHIND_BEFORE_AND_AFTER_MOVE * quad;
                        LudoLogger.debug("evaluate: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                "threatPerceptionScore: " + threatPerceptionScore + " situation: If " +
                                "opponent ahead before move and behind after move");

                        // check if player is already on a safe spot
                        if (GameRules.safeSpotEnabled && Utility.findInArray
                                (GameConfiguration.SAFE_SPOT_LIST, tokenLocation) != -1) {
                            threatPerceptionScore += GameConfiguration.POINTS_PLAYER_ALREADY_SAFE_SPOT;
                            LudoLogger.debug("situation: playerid : " + playerId + " tokenIndex: " + tokenIndex + " " +
                                    "threatPerceptionScore: " + threatPerceptionScore + " situation: If " +
                                    "player is already on a safe spot");
                        }

                    }
                }

            }

            // TODO: can check if player near an opponent opening path for threat


            // calculate total score by subtracting threat perception score from opportunity score
            moveScore = opportunityScore - threatPerceptionScore;
            if (moveScore == 0) {
                moveScore = quad;
            }

            // setup move
            LudoLogger.debug("evaluate: playerid : " + playerId + " tokenIndex: " + tokenIndex + " moveScore: " + moveScore + " " +
                    "outcome: normal move");
            gameMove = new GameMove(playerId, tokenIndex, GameMove.MOVE, diceRoll, moveScore);
            //LudoLogger.debug("evaluate: MOVE");
        }

        return gameMove;
    }

    private boolean isValidMove(int tokenIndex) {
        if (gameData.getPlayer(gameData.getTurn()).getPlayerToken(tokenIndex).getState() == GameConfiguration.TOKEN_STATE_HOME) {
            // token is home
            LudoLogger.debug("situation: playerid : " + gameData.getTurn() + " tokenIndex: " + tokenIndex + " outcome: token is already home");
            return false;
        } else if ((gameData.getPlayer(gameData.getTurn()).getPlayerToken(tokenIndex).getState() == GameConfiguration.TOKEN_STATE_UNOPEN) && Utility
                .findInArray(GameRules
                        .tokenOpeningMoves, gameData.getDiceRoll()) == -1) {
            // token is unopen and we don't have an opening move
            LudoLogger.debug("situation: playerid : " + gameData.getTurn() + " tokenIndex: " + tokenIndex + " outcome: token is unopen and we don't have an opening move");
            return false;
        } else if (!gameData.getPlayer(gameData.getTurn()).tokenHasValidMove(tokenIndex, gameData.getDiceRoll())) {
            // token has no valid move
            LudoLogger.debug("situation: playerid : " + gameData.getTurn() + " tokenIndex: " + tokenIndex + " outcome: token has no valid move");
            return false;
        } else {
            return true;
        }
    }

    /*
     * Method to check which quadrant is the player in. The quad index will change depending on the player since the
     * board positions are fixed hence we use the table in GameConfiguration to find the correct index
     */
    private int getPlayerTokenQuadrant(int tokenIndex) {
        int boardStartLocation = GameConfiguration.BLUE_OPEN_INDEX;
        int playerId = gameData.getTurn();
        int tokenLocation = gameData.getPlayer(playerId).getPlayerToken(tokenIndex).getLocation();
        if (tokenLocation >= boardStartLocation && tokenLocation <= boardStartLocation + 12) {
            return GameConfiguration.quadIndex[playerId][0];
        } else if (tokenLocation >= boardStartLocation + 13 && tokenLocation <= boardStartLocation + 25) {
            return GameConfiguration.quadIndex[playerId][1];
        } else if (tokenLocation >= boardStartLocation + 26 && tokenLocation <= boardStartLocation + 38) {
            return GameConfiguration.quadIndex[playerId][2];
        } else if ((tokenLocation >= boardStartLocation + 39 && tokenLocation <= boardStartLocation + 49) ||
                tokenLocation == 0 || tokenLocation == 1) {
            return GameConfiguration.quadIndex[playerId][3];
        }

        LudoLogger.error("Unable to get the correct quadrant");
        return 0;
    }

    private boolean checkDoubleUp(int tokenIndex, int proposedLocation) {
        for (int i = 0; i < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; i++) {
            if (i != tokenIndex) {
                if (gameData.getPlayer(gameData.getTurn()).getPlayerToken(i).getLocation() == proposedLocation) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Checks if there is another player behind
     */
    private PlayerToken checkPlayerProximity(int location, boolean direction) {
        ArrayList tokens = new ArrayList();

        // Find all tokens in proximity
        // TODO: consider the cyclical property of the board to limit the behind and ahead horizon
        // TODO: check if players are on the same safe spot
        for (int playerIdCounter = 0; playerIdCounter < GameConfiguration.NO_OF_PLAYERS; playerIdCounter++) {
            if (gameData.getTurn() != playerIdCounter) {
                for (int i = 0; i < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; i++) {
                    if (direction) {
                        // ahead
/*
                        if (location < gameData.getPlayer(playerIdCounter).getPlayerToken(i)
                                .getLocation() && getUpdatedLocation(location, 6) > gameData.getPlayer(playerIdCounter)
                                .getPlayerToken(i)
                                .getLocation()) {
*/
                        if (isPlayerInRangeInForwardDirection(location, gameData.getPlayer(playerIdCounter).getPlayerToken(i)
                                .getLocation())) {
                            tokens.add(gameData.getPlayer(playerIdCounter).getPlayerToken(i));
                        }
                    } else {
                        //behind
/*
                        if (location > gameData.getPlayer(playerIdCounter).getPlayerToken(i)
                                .getLocation() && getUpdatedLocation(location, -6) <= gameData.getPlayer
                                (playerIdCounter).getPlayerToken(i).getLocation()) {
*/
                        if (isPlayerInRangeInBackwardDirection(location, gameData.getPlayer(playerIdCounter).getPlayerToken(i)
                                .getLocation())) {
                            tokens.add(gameData.getPlayer(playerIdCounter).getPlayerToken(i));
                        }
                    }
                }
            }
        }

        if (!tokens.isEmpty()) {

            // Find the token with highest player value
            PlayerToken tokenInFocus = (PlayerToken) tokens.get(0);
            LudoPlayer playerInFocus = gameData.getPlayer(tokenInFocus.getPlayerId());
            int highestPlayerScoreInList = playerInFocus.getPlayerScore();
            int highestTokenScoreForTheSelectedPlayerInList = tokenInFocus.getTokenScore();
            for (int index = 1; index < tokens.size(); index++) {
                tokenInFocus = (PlayerToken) tokens.get(index);
                playerInFocus = gameData.getPlayer(tokenInFocus.getPlayerId());
                if (highestPlayerScoreInList == playerInFocus.getPlayerScore()) {
                    if (highestTokenScoreForTheSelectedPlayerInList < tokenInFocus.getTokenScore()) {
                        highestPlayerScoreInList = playerInFocus.getPlayerScore();
                        highestTokenScoreForTheSelectedPlayerInList = tokenInFocus.getTokenScore();
                    }
                } else if (highestPlayerScoreInList < playerInFocus.getPlayerScore()) {
                    highestPlayerScoreInList = playerInFocus.getPlayerScore();
                    highestTokenScoreForTheSelectedPlayerInList = tokenInFocus.getTokenScore();
                }
            }
            return tokenInFocus;
        }
        return null;
    }

    /*
     * to consider the cyclical property of the board to limit the behind and ahead horizon
     */
    private int getUpdatedLocation(int location, int numSteps) {
        if (location + numSteps < 0) {
            // cycle backwards
            return GameConfiguration.GAME_CYCLE_END_INDEX + location + numSteps;
        } else if (location + numSteps > GameConfiguration.GAME_CYCLE_END_INDEX) {
            // cycle forwards
            return location + numSteps - GameConfiguration.GAME_CYCLE_END_INDEX - 1;
        } else {
            return location + numSteps;
        }
    }

    private boolean isPlayerInRangeInBackwardDirection(int location1, int location2) {
        if (location1 - 6 >= 0) {
            // the checking range is not cycling backwards of the starting position
            if (location2 >= location1 - 6 && location2 <= location1) {
                return true;
            } else {
                return false;
            }
        } else {
            // the checking range is cycling backwards fo the starting position

            if (location2 >= 0 && location2 <= location1) {
                // first check ahead of the starting position
                return true;
            } else if (location2 <= GameConfiguration.GAME_CYCLE_END_INDEX && location2 >= GameConfiguration
                    .GAME_CYCLE_END_INDEX + location1 - 6 + 1) {       // added 1 since we start from 0
                // then check behind the starting position
                return true;
            } else {
                return false;
            }

        }
    }

    private boolean isPlayerInRangeInForwardDirection(int location1, int location2) {
        if (location1 + 6 <= GameConfiguration.GAME_CYCLE_END_INDEX) {
            // the checking range is not cycling forwards ahead of the starting position
            if (location2 <= location1 + 6 && location2 >= location1) {
                return true;
            } else {
                return false;
            }
        } else {
            // the checking range is cycling forwards ahead of the starting position

            if (location2 <= GameConfiguration.GAME_CYCLE_END_INDEX && location2 >= location1) {
                // first check behind of the starting position
                return true;
            } else if (location2 >= 0 && location2 <= location1 + 6 - 1 - GameConfiguration.GAME_CYCLE_END_INDEX) {
            //subtracted 1 since we start from 0
                // then check ahead of the starting position
                return true;
            } else {
                return false;
            }

        }
    }

}

