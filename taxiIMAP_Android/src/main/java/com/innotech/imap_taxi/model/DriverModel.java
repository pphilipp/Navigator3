package com.innotech.imap_taxi.model;

/**
 * Created with IntelliJ IDEA.
 * User: SV_LTD
 * Date: 9/22/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DriverModel {

    private int id;
    private String callsign;
    private String password;
    private String name;
    private String lastName;

    public DriverModel(int id, String callsign, String password, String name, String lastName) {

        this.id = id;
        this.callsign = new String(callsign);
        this.password = new String(password);
        this.name = new String(name);
        this.lastName = new String(lastName);
    }

    public DriverModel(String callsign, String password, String name, String lastName) {

        this.callsign = new String(callsign);
        this.password = new String(password);
        this.name = new String(name);
        this.lastName = new String(lastName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "DriverModel{" +
                "id=" + id +
                ", login='" + callsign + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverModel that = (DriverModel) o;

        if (id != that.id) return false;
        if (callsign != null ? !callsign.equals(that.callsign) : that.callsign != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;

        return true;
    }

    public boolean isEmpty() {
        if (callsign.isEmpty()) return true;
        if (password.isEmpty()) return true;
        if (name.isEmpty()) return true;
        if (lastName.isEmpty()) return true;

        return false;
    }

}
