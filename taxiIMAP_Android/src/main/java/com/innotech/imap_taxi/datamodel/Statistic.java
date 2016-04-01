/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.innotech.imap_taxi.datamodel;

/**
 * @author Kvest
 */
public class Statistic {
    private int id;
    private int count;
    private String queue;

    public Statistic(int id) {
        this.id = id;
        count = 0;
        queue = "";
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int value) {
        count = value;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String value) {
        queue = value;
    }
}
