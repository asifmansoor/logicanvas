package com.logicanvas.boardgames.ludo.core;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;
import com.logicanvas.boardgames.ludo.intelligence.GameMove;
import com.logicanvas.boardgames.ludo.intelligence.GameOriginator;
import com.logicanvas.boardgames.ludo.intelligence.MoveEvaluator;
import com.logicanvas.boardgames.ludo.intelligence.MoveSequence;
import com.logicanvas.boardgames.ludo.model.GameData;
import com.logicanvas.boardgames.ludo.utility.LudoLogger;
import com.logicanvas.boardgames.ludo.view.LudoView;
import playn.core.Clock;
import playn.core.Keyboard;
import playn.core.Mouse;
import playn.core.Platform;
import playn.scene.SceneGame;
import react.Slot;

public class Ludo extends SceneGame {

    private LudoView view;
    private GameData gameData;
    private MoveEvaluator moveEvaluator;
    private Dice dice;
    private boolean gameInProgress;
    private int lastUpdate = 0;
    private GameOriginator gameOriginator;

    public Ludo(Platform plat) {
        super(plat, 33); // update our "simulation" 33ms (30 times per second)

        // setup input
        plat.input().mouseEvents.connect(new Slot<Mouse.Event>() {
            @Override
            public void onEmit(Mouse.Event event) {
                //System.out.println("Mouse event : "+event.x());
            }
        });

        plat.input().keyboardEvents.connect(new Slot<Keyboard.Event>() {
            @Override
            public void onEmit(Keyboard.Event event) {
                try {
                    Keyboard.TypedEvent keyEvent = (Keyboard.TypedEvent) event;
                    if (keyEvent != null) {
                        if (keyEvent.typedChar == 's' || keyEvent.typedChar == 'S') {
                            // save game
                            LudoLogger.info("Saving Game...");
                            gameOriginator.saveGame();
                            LudoLogger.info("Saved Game...");
                        } else if (keyEvent.typedChar == 'r' || keyEvent.typedChar == 'R') {
                            // load game
                            LudoLogger.info("Restoring Game...");
                            gameOriginator.restoreGame();
                            restoreGame();
                            LudoLogger.info("Restored Game...");
                        } else {
                            gameInProgress = true;
                            gameUpdate();
                            //System.out.println("Key event : " + event.toString());
                            //gameUpdate();
                        }
                    }
                } catch (Exception e) {
                    //System.out.println("Key error:" + e);
                }
            }
        });


        //Setup the board
        view = new LudoView(rootLayer, plat);

        // setup players
        setupGame();

        // Update initial board display
        updateBoard();

    }

    private void setupGame() {
        gameData = new GameData();
        LudoPlayer[] players = new LudoPlayer[GameConfiguration.NO_OF_TOKENS_PER_PLAYER];

        // setup blue player data
        players[GameConfiguration.BLUE_PLAYER_ID] = new LudoPlayer(GameConfiguration.BLUE_START_INDEX,
                GameConfiguration.BLUE_OPEN_INDEX, GameConfiguration.BLUE_HOME_ROW_START_INDEX, GameConfiguration
                .BLUE_PLAYER_ID);
        players[GameConfiguration.RED_PLAYER_ID] = new LudoPlayer(GameConfiguration.RED_START_INDEX,
                GameConfiguration.RED_OPEN_INDEX, GameConfiguration.RED_HOME_ROW_START_INDEX, GameConfiguration
                .RED_PLAYER_ID);
        players[GameConfiguration.GREEN_PLAYER_ID] = new LudoPlayer(GameConfiguration.GREEN_START_INDEX,
                GameConfiguration.GREEN_OPEN_INDEX, GameConfiguration.GREEN_HOME_ROW_START_INDEX, GameConfiguration
                .GREEN_PLAYER_ID);
        players[GameConfiguration.YELLOW_PLAYER_ID] = new LudoPlayer(GameConfiguration.YELLOW_START_INDEX,
                GameConfiguration.YELLOW_OPEN_INDEX, GameConfiguration.YELLOW_HOME_ROW_START_INDEX, GameConfiguration
                .YELLOW_PLAYER_ID);

        gameData.setPlayers(players);

        // initialize the player moves tracker
        gameData.setMoveSequence(new MoveSequence());
        moveEvaluator = new MoveEvaluator(gameData);

        dice = new Dice();

        gameInProgress = false;
        gameData.setTurn(-1);

        gameOriginator = new GameOriginator(gameData);

    }

    /*
     * This method is to update the display of all tokens on the board with their updated locations
     */
    private void updateBoard() {
        for (int playerId = 0; playerId < GameConfiguration.NO_OF_PLAYERS; playerId++) {
            for (int tokenIndex = 0; tokenIndex < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; tokenIndex++) {
                int x = GameConfiguration.BOARD_POSITIONS[gameData.getPlayer(playerId).getPlayerToken(tokenIndex)
                        .getLocation()][0];
                int y = GameConfiguration.BOARD_POSITIONS[gameData.getPlayer(playerId).getPlayerToken(tokenIndex)
                        .getLocation()][1];
                //LudoLogger.log("Show token: playerid: "+playerId+" token: "+tokenIndex+" x: "+x+" y: "+y);
                view.updateTokenLocation(playerId, tokenIndex, x, y);
            }
        }
    }

    @Override
    public void update(Clock clock) {
        super.update(clock);
        int elapsed = clock.tick - lastUpdate;
        if (gameInProgress) {
            if (elapsed > 50) {
                gameUpdate();
                lastUpdate = clock.tick;
            }
        }

    }

    private void gameUpdate() {
        if (gameInProgress) {
            setupMove();
            playMove();
            gameOriginator.saveGame();
        }
    }

    /*
     * Method to setup current move
     */
    private void setupMove() {
        if (nextTurn()) {
            if (gameData.getTurn() == 0) {
                System.out.println("");
            }
            LudoLogger.debug("Turn :" + gameData.getTurn());

            gameData.setDiceRoll(dice.oneDiceOutcome());

            gameData.getMoveSequence().addSequence(moveEvaluator.evaluateMove());
        }
    }

    private void restoreGame() {
        LudoLogger.debug("Turn :" + gameData.getTurn());
        LudoLogger.debug("Dice :" + gameData.getDiceRoll());
        gameData.getMoveSequence().addSequence(moveEvaluator.evaluateMove());
        playMove();
    }

    private void playMove() {
        GameMove gameMove;
        while (!gameData.getMoveSequence().isEmpty()) {
            gameMove = gameData.getMoveSequence().popMove();
            gameData.getPlayer(gameMove.getPlayerId()).playMove(gameMove);
            updateBoard();
        }
    }

    private boolean nextTurn() {
        if (checkGameEnd()) {
            gameInProgress = false;
            LudoLogger.log("Game Over!");
            return false;
        }

        gameData.setTurn(gameData.getTurn() + 1);
        if (gameData.getTurn() > 3)
            gameData.setTurn(0);

        if (gameData.getPlayer(gameData.getTurn()).isAllHomeForPlayer()) {
            nextTurn();
        }

        return true;
    }

    private boolean checkGameEnd() {
        int numPlayersHome = 0;
        for (int i = 0; i < GameConfiguration.NO_OF_PLAYERS; i++) {
            if (gameData.getPlayer(i).isAllHomeForPlayer())
                numPlayersHome++;
        }

        if (numPlayersHome >= GameConfiguration.NO_OF_PLAYERS - 1)
            return true;
        else
            return false;
    }

}
