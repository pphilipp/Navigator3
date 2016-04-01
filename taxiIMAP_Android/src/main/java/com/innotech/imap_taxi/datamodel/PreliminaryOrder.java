package com.innotech.imap_taxi.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 14.11.13
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class PreliminaryOrder {
    public DispOrder4 order;
    public List<String> signed;

    public PreliminaryOrder(DispOrder4 order) {
        this.order = order;
        signed = new ArrayList<String>();
    }
}
