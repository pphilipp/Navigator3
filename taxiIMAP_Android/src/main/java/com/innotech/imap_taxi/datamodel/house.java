package com.innotech.imap_taxi.datamodel;

/**
 * Created by Sergey on 16.11.2015.
 */
public class house {

    String value, id, x, y;

    public house(String value, String id, String x, String y) {
        super();
        this.value = value;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

}
