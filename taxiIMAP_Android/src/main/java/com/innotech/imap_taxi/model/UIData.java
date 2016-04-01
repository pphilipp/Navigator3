package com.innotech.imap_taxi.model;

/**
 * Created with IntelliJ IDEA.
 * User: u27
 * Date: 9/24/13
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIData {

    private static UIData instance;

    private String balance;
    private String version;

    private UIData() {
        balance = new String("0,0");
        version = new String("v. 0.0");
    }


    public static UIData getInstance() {

        if(instance==null) {
            instance = new UIData();
        }

        return instance;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
