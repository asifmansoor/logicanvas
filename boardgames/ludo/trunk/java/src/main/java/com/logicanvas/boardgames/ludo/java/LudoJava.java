package com.logicanvas.boardgames.ludo.java;

import com.logicanvas.boardgames.ludo.core.Ludo;
import playn.java.LWJGLPlatform;

public class LudoJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        // use config to customize the Java platform, if needed
        config.width = 547;
        config.height = 547;
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new Ludo(plat);
        plat.start();
    }
}
