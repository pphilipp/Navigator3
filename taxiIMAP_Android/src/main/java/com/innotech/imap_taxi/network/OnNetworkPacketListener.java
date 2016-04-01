package com.innotech.imap_taxi.network;

import com.innotech.imap_taxi.network.packet.Packet;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.03.13
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public interface OnNetworkPacketListener {
    public void onNetworkPacket(Packet packet);
}
