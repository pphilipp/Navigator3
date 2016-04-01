package com.innotech.imap_taxi.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.09.13
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public class DispOrder2 extends DispOrder {
    public String agentPay = "";
    public List<String> features = new ArrayList<String>();
    public String agentName = "";
    public String dispPhone = "";
    public int colorClass = 0;
    public String autoClass = "";
    public String clientName = "";
    public boolean receiptMustHave = false;
    public boolean preliminary = false;
}
