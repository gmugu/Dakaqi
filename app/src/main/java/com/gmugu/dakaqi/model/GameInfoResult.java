package com.gmugu.dakaqi.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by mugu on 16/11/28.
 */

public class GameInfoResult implements Serializable {

    private String gameName;
    private Map<String, PlayerModel> playerInfos;
    private String RSAPrivateKey;

    public GameInfoResult() {
    }

    public Map<String, PlayerModel> getPlayerInfos() {
        return playerInfos;
    }

    public void setPlayerInfos(Map<String, PlayerModel> playerInfos) {
        this.playerInfos = playerInfos;
    }

    public String getRSAPrivateKey() {
        return RSAPrivateKey;
    }

    public void setRSAPrivateKey(String RSAPrivateKey) {
        this.RSAPrivateKey = RSAPrivateKey;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String toString() {
        return "GameInfoResult{" +
                "gameName='" + gameName + '\'' +
                ", playerInfos=" + playerInfos +
                ", RSAPrivateKey='" + RSAPrivateKey + '\'' +
                '}';
    }
}
