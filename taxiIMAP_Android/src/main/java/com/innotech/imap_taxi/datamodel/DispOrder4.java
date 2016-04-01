package com.innotech.imap_taxi.datamodel;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public class DispOrder4 extends DispOrder3 {
    public String sourceWhence = "";
    public float orderCostForDriver = 0;
    public boolean canFirstForAnyParking = false;
    public float distanceToPointOfDelivery = 0;
    public boolean concessional = false;
    public float waitMinutes;
    public float waitMinutesPay;
}
