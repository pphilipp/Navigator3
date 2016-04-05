package com.innotech.imap_taxi.network;

import com.innotech.imap_taxi.network.packet.Packet;

/**
 *@interface
 *
 */
public interface OnNetworkPacketListener {
    void onNetworkPacket(Packet packet);
}
