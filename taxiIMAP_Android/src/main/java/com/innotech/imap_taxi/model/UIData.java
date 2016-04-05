package com.innotech.imap_taxi.model;

public class UIData {
    private static UIData instance;
    private String balance;
    private String version;

    private UIData() {
        balance = new String("0,0");
        version = new String("v. 0.0");
    }


    public static UIData getInstance() {
        if(instance == null) {
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
