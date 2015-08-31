package com.logicanvas.boardgames.ludo.intelligence;

import com.logicanvas.boardgames.ludo.config.GameConfiguration;
import com.logicanvas.boardgames.ludo.model.GameData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by amansoor on 27-08-2015.
 */
public class GameOriginator {
    private GameData gameData;
    private String filePathLatest = "C:\\Users\\amansoor\\AppData\\Local\\LogiCanvas\\BoardGames\\Ludo\\LudoLatest" +
            ".json";
    private String fileBackUp = "C:\\Users\\amansoor\\AppData\\Local\\LogiCanvas\\BoardGames\\Ludo\\LudoBackup.json";

    public GameOriginator(GameData gameData) {
        this.gameData = gameData;
    }

    public void saveGame() {
        backUpFile();

        // write to JSON
        JSONObject obj = new JSONObject();
        FileWriter file;

        obj.put("turn", gameData.getTurn());
        obj.put("dice", gameData.getDiceRoll());

        JSONArray players = new JSONArray();
        for (int i = 0; i < GameConfiguration.NO_OF_PLAYERS; i++) {
            JSONObject player = new JSONObject();
            player.put("PlayerScore", gameData.getPlayer(i).getPlayerScore());
            player.put("playerBeaten", gameData.getPlayer(i).isHasPlayerBeatenAToken());
            JSONArray tokens = new JSONArray();
            for (int j = 0; j < GameConfiguration.NO_OF_TOKENS_PER_PLAYER; j++) {
                JSONObject tokenObject = new JSONObject();
                tokenObject.put("index", gameData.getPlayer(i).getPlayerToken(j).getIndex());
                tokenObject.put("location",gameData.getPlayer(i).getPlayerToken(j).getLocation());
                tokenObject.put("state",gameData.getPlayer(i).getPlayerToken(j).getState());
                tokenObject.put("score",gameData.getPlayer(i).getPlayerToken(j).getTokenScore());
                tokenObject.put("playerid",gameData.getPlayer(i).getPlayerToken(j).getPlayerId());
                tokens.add(tokenObject);
            }
            player.put("tokens", tokens);
            players.add(player);
        }

        obj.put("players", players);

        try {
            file = new FileWriter(filePathLatest);
            file.write(obj.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backUpFile() {
        try {
            FileWriter fileWriter = new FileWriter(fileBackUp);
            FileReader fileReader = new FileReader(filePathLatest);
            char element = (char) fileReader.read();
            while(element != '\uFFFF') {  //check for end of the file
                fileWriter.write(element);
                element = (char) fileReader.read();
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreGame() {
        // read from JSON
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            Object obj = parser.parse(new FileReader(fileBackUp));
            jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject);

            //gameData.setTurn(Integer.parseInt(jsonObject.get("turn").toString()));
            //gameData.setDiceRoll(Integer.parseInt(jsonObject.get("dice").toString()));

            int i = 0;
            JSONArray players = (JSONArray) jsonObject.get("players");
            Iterator<JSONObject> iterator = players.iterator();
            while (iterator.hasNext()) {
                JSONObject player = iterator.next();
                gameData.getPlayer(i).setPlayerScore(Integer.parseInt(player.get("PlayerScore").toString()));
                gameData.getPlayer(i).setHasPlayerBeatenAToken(Boolean.parseBoolean(player.get("playerBeaten").toString()));

                int j = 0;
                JSONArray tokens = (JSONArray) player.get("tokens");
                Iterator<JSONObject> tokenIterator = tokens.iterator();
                while (tokenIterator.hasNext()) {
                    JSONObject token = tokenIterator.next();
                    gameData.getPlayer(i).getPlayerToken(j).setIndex(Integer.parseInt(token.get("index").toString()));
                    gameData.getPlayer(i).getPlayerToken(j).setLocation(Integer.parseInt(token.get("location").toString()));
                    gameData.getPlayer(i).getPlayerToken(j).setState(Integer.parseInt(token.get("state").toString()));
                    gameData.getPlayer(i).getPlayerToken(j).setTokenScore(Integer.parseInt(token.get("score").toString()));
                    gameData.getPlayer(i).getPlayerToken(j).setPlayerId(Integer.parseInt(token.get("playerid").toString()));
                    j++;
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLatestDiceAndTurnValues();
    }

    private void setLatestDiceAndTurnValues() {
        // read from JSON
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            Object obj = parser.parse(new FileReader(filePathLatest));
            jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject);

            gameData.setTurn(Integer.parseInt(jsonObject.get("turn").toString()));
            gameData.setDiceRoll(Integer.parseInt(jsonObject.get("dice").toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
