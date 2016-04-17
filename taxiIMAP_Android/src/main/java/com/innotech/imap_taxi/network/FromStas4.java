package com.innotech.imap_taxi.network;

import com.innotech.imap_taxi.network.packet.Packet;


public class FromStas4 extends Packet {
    public FromStas4(byte[] data) {
        super(DISTANCE_FROM_STAS);
    }

    @Override
    protected void parse(byte[] data) {

    }
}
