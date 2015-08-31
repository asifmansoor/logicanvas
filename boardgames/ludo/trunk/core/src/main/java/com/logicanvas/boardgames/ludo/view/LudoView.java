package com.logicanvas.boardgames.ludo.view;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;
import playn.core.Image;
import playn.core.Platform;
import playn.scene.ImageLayer;
import playn.scene.RootLayer;

/**
 * Created by amansoor on 17-06-2015.
 * <p/>
 * This class is for displaying the ludo board and the player tokens
 */
public class LudoView {
    private Image tokenImage;
    private ImageLayer[] blueTokenLayer;
    private ImageLayer[] redTokenLayer;
    private ImageLayer[] greenTokenLayer;
    private ImageLayer[] yellowTokenLayer;

    public LudoView(RootLayer rootLayer, Platform platform) {
        // setup variables
        Image boardImage = platform.assets().getImage("images/ludo_big.gif");
        ImageLayer boardLayer = new ImageLayer(boardImage);
        // scale the background to fill the screen
        boardLayer.setSize(platform.graphics().viewSize);
        rootLayer.add(boardLayer);

        tokenImage = platform.assets().getImage("images/tokens.png");

        blueTokenLayer = setUpTokens(GameConfiguration.TOKEN_SIZE, GameConfiguration.TOKEN_SIZE, rootLayer);
        redTokenLayer = setUpTokens(0, 0, rootLayer);
        greenTokenLayer = setUpTokens(GameConfiguration.TOKEN_SIZE, 0, rootLayer);
        yellowTokenLayer = setUpTokens(0, GameConfiguration.TOKEN_SIZE, rootLayer);
    }

    public ImageLayer[] setUpTokens(int imageX, int imageY, RootLayer rootLayer) {
        // setup tokens
        ImageLayer[] tokenLayer = new ImageLayer[GameConfiguration.NO_OF_TOKENS_PER_PLAYER];
        for (int i = 0; i < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; i++) {
            Image.Region tokenRegion = tokenImage.region(imageX, imageY, GameConfiguration.TOKEN_SIZE,
                    GameConfiguration.TOKEN_SIZE);
            tokenLayer[i] = new ImageLayer(tokenRegion);
            tokenLayer[i].setSize(35, 35);
            rootLayer.add(tokenLayer[i]);
        }

        return tokenLayer;
    }

    public void updateTokenLocation(int playerId, int index, int x, int y) {
        switch (playerId) {
            case GameConfiguration.BLUE_PLAYER_ID:
                blueTokenLayer[index].setTx(x);
                blueTokenLayer[index].setTy(y);
                break;
            case GameConfiguration.RED_PLAYER_ID:
                redTokenLayer[index].setTx(x);
                redTokenLayer[index].setTy(y);
                break;
            case GameConfiguration.GREEN_PLAYER_ID:
                greenTokenLayer[index].setTx(x);
                greenTokenLayer[index].setTy(y);
                break;
            case GameConfiguration.YELLOW_PLAYER_ID:
                yellowTokenLayer[index].setTx(x);
                yellowTokenLayer[index].setTy(y);
                break;
        }
    }

}
