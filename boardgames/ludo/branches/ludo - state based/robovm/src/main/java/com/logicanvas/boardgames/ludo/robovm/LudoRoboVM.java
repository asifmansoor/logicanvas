package com.logicanvas.boardgames.ludo.robovm;

import com.logicanvas.boardgames.ludo.core.Ludo;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.*;
import playn.robovm.RoboPlatform;

public class LudoRoboVM extends UIApplicationDelegateAdapter {

    public static void main(String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, LudoRoboVM.class);
        pool.close();
    }

    @Override
    public boolean didFinishLaunching(UIApplication app, UIApplicationLaunchOptions launchOpts) {
        // create a full-screen window
        CGRect bounds = UIScreen.getMainScreen().getBounds();
        UIWindow window = new UIWindow(bounds);

        // configure and create the PlayN platform
        RoboPlatform.Config config = new RoboPlatform.Config();
        config.orients = UIInterfaceOrientationMask.All;
        RoboPlatform plat = RoboPlatform.create(window, config);

        // create and initialize our game
        new Ludo(plat);

        // make our main window visible (this starts the platform)
        window.makeKeyAndVisible();
        addStrongRef(window);
        return true;
    }
}
