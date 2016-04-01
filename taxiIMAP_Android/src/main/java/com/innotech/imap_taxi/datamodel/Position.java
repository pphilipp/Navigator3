package com.innotech.imap_taxi.datamodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Position {
    private int id;
    private String parkingName;
    private String parkingRegion;
    private int carsCount;                                                  //Количество машин на данной парковке в данный момент
    private String queue;

    public Position(int id, String parkingName, String parkingRegion) {
        this.id = id;
        setParkingName(parkingName);
        this.parkingRegion = parkingRegion;
        carsCount = 0;
        queue = "";
    }

    public Position(int id) {
        this.id = id;
        parkingName = "";
        parkingRegion = ""; 
        carsCount = 0;
        queue = "";
    }

    public Position(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);

        try {
            id = dis.readInt();
            parkingName = dis.readUTF();
            parkingRegion = dis.readUTF();
        } finally {
            try {
                dis.close();
                bais.close();
            } catch (Exception ex) {
            }
        }

        carsCount = 0;
        queue = "";
    }

    public void setQueue(String newQueue) {
        queue = newQueue;
    }

    public String getQueue() {
        return queue;
    }

    public int getCarsCountInt() {
        return carsCount;
    }

    public String getCarsCount() {
        return Integer.toString(carsCount);
    }

    public void setCarsCount(int newCount) {
        carsCount = newCount;
    }

    public int getId() {
        return id;
    }

    public void setParkingName(String value) {
        parkingName = value;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingRegion(String value) {
        parkingRegion = value;
    }

    public String getParkingRegion() {
        return parkingRegion;
    }

    public byte[] toByte() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(result);

        try {
            dos.writeInt(id);
            dos.writeUTF(parkingName);
            dos.writeUTF(parkingRegion);

            return result.toByteArray();
        } catch (IOException ioex) {
            return (new byte[]{});
        } finally {
            try {
                dos.close();
                result.close();
            } catch (Exception ex) {
            }
        }
    }
}
