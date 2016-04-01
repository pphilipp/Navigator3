package com.innotech.imap_taxi.datamodel;


public class ServerData {
    private static ServerData instance;
    private String ip;
    private int port;// = 6000;
    private String slaveIp;
    private int slavePort;
    private byte guid[];
    private int peopleID;
    private int srvID;
    private String login;
    private String pass;
    private String nick;
    private boolean isMasterServer = true;
    public boolean isFree;
    public boolean doOwn;
    public boolean isNight;
    public final boolean IS_TEST_BUILD = !true;
    public boolean gpsOrNetProv;
	public boolean showAlertRegistr;
    //public int textSize;

    public static ServerData getInstance() {
        if (instance == null)
            instance = new ServerData();

        return instance;
    }

    public boolean isShowAlertRegistr() {
		return showAlertRegistr;
	}

	public void setShowAlertRegistr(boolean showAlertRegistr) {
		this.showAlertRegistr = showAlertRegistr;
	}

	private ServerData() {
        ip = new String(/*"194.48.212.11"*/);
      //  textSize=ContextHelper.getInstance().getSharedPreferences().getInt("prefTextSize", 0);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getGuid() {
        return guid;
    }

    public void setGuid(byte[] guid) {
        this.guid = guid;
    }

    public int getPeopleID() {
        return peopleID;
    }

    public void setPeopleID(int peopleID) {
        this.peopleID = peopleID;
    }

    public int getSrvID() {
        return srvID;
    }

    public void setSrvID(int srvID) {
        this.srvID = srvID;
    }
    
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
    
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isMasterServer() {
        return isMasterServer;
    }

    public void setMasterServer(boolean isPrimeServer) {
        this.isMasterServer = isPrimeServer;
    }

    public String getSlaveIp() {
        return slaveIp;
    }

    public void setSlaveIp(String slaveIp) {
        this.slaveIp = slaveIp;
    }

    public int getSlavePort() {
        return slavePort;
    }

    public void setSlavePort(int slavePort) {
        this.slavePort = slavePort;
    }
}
