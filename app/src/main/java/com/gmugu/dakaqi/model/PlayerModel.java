package com.gmugu.dakaqi.model;

import java.io.Serializable;

/**
 * Created by mugu on 16/11/28.
 */

public class PlayerModel implements Serializable{

    private String cardMAC;
    private String ID;
    private String name;
    private String group;

    public PlayerModel() {
    }

    public String getCardMAC() {
        return cardMAC;
    }

    public void setCardMAC(String cardMAC) {
        this.cardMAC = cardMAC;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
