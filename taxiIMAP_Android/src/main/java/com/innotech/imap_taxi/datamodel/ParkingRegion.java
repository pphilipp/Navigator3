/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.imap_taxi.datamodel;

/**
 * @author Kvest
 */
public class ParkingRegion {
    private String name;
    private int count;

    public ParkingRegion(String name) {
        this.name = name;
        count = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount(int delta) {
        count += delta;
    }
}
