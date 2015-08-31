package com.logicanvas.boardgames.ludo.config;

/**
 * Created by amansoor on 19-06-2015.
 */
public class GameConfiguration {
    public static final boolean DEBUG = true;
    public static final boolean RIG_GAME = false;
    public static final int[] rigList = {6,1,1,1,6,1,1,1,5,1,1,1,5,1,1,1,36,1,1,1,5,1,1,1};

    public static final int[][] quadIndex = {{1,2,3,4},{4,1,2,3},{3,4,1,2},{2,3,4,1}};


    public static final int NO_OF_PLAYERS = 4;
    public static final int NO_OF_TOKENS_PER_PLAYER = 4;
    public static final int TOKEN_SIZE = 136;

    public static final boolean REVERSE = false;
    public static final boolean FORWARD = true;

    public static final int TOKEN_STATE_UNOPEN = 1;
    public static final int TOKEN_STATE_OPEN = 2;
    public static final int TOKEN_STATE_IN_HOME_ROW = 3;
    public static final int TOKEN_STATE_HOME = 4;

    public static final int BLUE_PLAYER_ID = 0;
    public static final int RED_PLAYER_ID = 1;
    public static final int GREEN_PLAYER_ID = 2;
    public static final int YELLOW_PLAYER_ID = 3;

    public static final int MAX_TOKEN_MOVE = 57;

    public static final int BLUE_START_INDEX = 77;
    public static final int BLUE_OPEN_INDEX = 2;
    public static final int BLUE_HOME_ROW_START_INDEX = 52;

    public static final int RED_START_INDEX = 81;
    public static final int RED_OPEN_INDEX = 15;
    public static final int RED_HOME_ROW_START_INDEX = 59;

    public static final int YELLOW_START_INDEX = 85;
    public static final int YELLOW_OPEN_INDEX = 41;
    public static final int YELLOW_HOME_ROW_START_INDEX = 71;

    public static final int GREEN_START_INDEX = 89;
    public static final int GREEN_OPEN_INDEX = 28;
    public static final int GREEN_HOME_ROW_START_INDEX = 65;

    public static final int GAME_CYCLE_END_INDEX = 51;

    public static final int[] SAFE_SPOT_LIST = {2, 15, 28, 41};
    public static final int[] HOME_ROW_CHECK_INDEX_LIST = {0, 13, 26, 39};

    public static final int POINTS_IDLE_TOKEN = -50000;
    public static final int POINTS_OPEN_TOKEN = 10;
    public static final int POINTS_CAN_ENTER_HOME_ROW = 100000;
    public static final int POINTS_CAN_LAND_ON_SAFE_SPOT = 500;
    public static final int POINTS_CAN_DOUBLE_UP = 300;
    public static final int POINTS_OPPONENT_BEHIND_BEFORE_AND_AFTER_MOVE = 300;
    public static final int POINTS_OPPONENT_BEHIND_BEFORE_MOVE_AND_NOT_AFTER_MOVE = 300;
    public static final int POINTS_OPPONENT_AHEAD_BEFORE_AND_AFTER_MOVE = 200;
    public static final int POINTS_OPPONENT_AHEAD_BEFORE_MOVE_AND_HIT_AFTER_MOVE = 500;
    public static final int POINTS_CAN_OPEN_TOKEN = 50;
    public static final int POINTS_PLAYER_ALREADY_SAFE_SPOT = 200;


    public static final int[][] BOARD_POSITIONS = new int[][]{
            {12, 258},  // 0 - yellow right turn
            {12, 223},  // 1
            {45, 221}, // 2 -   blue start
            {80, 221},
            {115, 221},
            {150, 221},
            {185, 221},
            {221, 186}, // 5 - blue left turn
            {221, 151},
            {221, 116},
            {221, 79},  //8
            {221, 44},
            {221, 9},
            {257, 9}, // 11 - blue right turn
            {292, 9},
            {292, 44},  // 13 - red starts
            {292, 79},
            {292, 114},
            {292, 149},
            {292, 184},  //17
            {327, 221}, // 18 - red left turn
            {362, 221},
            {397, 221},
            {432, 221},
            {467, 221},
            {503, 221},   //23
            {503, 256},  // 24 - red right turn
            {503, 292},  //25
            {468, 292},  // 26 - green starts
            {433, 292},
            {398, 292},
            {363, 292},
            {328, 292}, //30
            {292, 327}, //31 - green left turn
            {292, 362},
            {292, 397},
            {292, 432},
            {292, 467},
            {292, 504}, //36
            {257, 504},   // 37 - green right turn
            {222, 504},  //38
            {222, 468},  // 39 - yellow starts
            {222, 433},
            {222, 398},
            {222, 363},
            {222, 328}, //43
            {187, 293},  // 44 - yellow left turn
            {152, 293},
            {117, 293},
            {82, 293},
            {47, 293},
            {12, 293},  // 51 - cycle end
            {47, 258},  // 52 - inside blue last row
            {82, 258},
            {117, 258},
            {152, 258},
            {187, 258},
            {222, 258},
            {257, 258}, //58 - blue home
            {257, 44}, // 59 - inside red last row
            {257, 79},
            {257, 114},
            {257, 149},
            {257, 184},
            {257, 219}, // 64 - red home
            {468, 256}, // 65 - inside green last row
            {433, 256},
            {398, 256},
            {363, 256},
            {328, 256},
            {293, 256}, // 70 - green home
            {257, 469}, // 71 - inside yellow last row
            {257, 434},
            {257, 399},
            {257, 364},
            {257, 329},
            {257, 294}, // 76 - yellow home
            {66, 66}, // 77 - blue start 1
            {66, 128}, // 78 - blue start 2
            {129, 66},  // 79 - blue start 3
            {129, 128},  // 80 - blue start 4
            {384, 66}, // 81 - red start 1
            {384, 128}, // 82 - red start 2
            {447, 66},  // 83 - red start 3
            {447, 128},  // 84 - red start 4
            {66, 384}, // 85 - yellow start 1
            {66, 447}, // 86 - yellow start 2
            {129, 384},  // 87 - yellow start 3
            {129, 447},  // 88 - yellow start 4
            {384, 384}, // 89 - green start 1
            {384, 447}, // 90 - green start 2
            {447, 384},  // 91 - green start 3
            {447, 447}  // 92 - green start 4
    };

}
