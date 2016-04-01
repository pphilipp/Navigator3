package com.innotech.imap_taxi.core;

/**
 * Created by Ura on 16.09.2014.
 */
public class TaxometrManager {
    private static TaxometrManager instance;
    public long timeStart;
    public long timeInWait, timeInSimpleMode, timeInDownTime;  //millis
    public boolean wait;
    public boolean looper;
    public double price = 0;//grn
    public float allDistance = 0f;

    TaxometrManager() {
        timeInWait = 0;
        timeInSimpleMode = 0;
        timeInDownTime = 0;
        price = 0;
        timeStart = 0;
        allDistance = 0f;
        wait = false;
        looper = false;
    }

    public static TaxometrManager getInstance() {
        if (instance == null) {
            instance = new TaxometrManager();
        }

        return instance;
    }
}
