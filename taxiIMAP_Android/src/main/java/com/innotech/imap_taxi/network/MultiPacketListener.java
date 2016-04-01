package com.innotech.imap_taxi.network;

import android.util.Log;
import com.innotech.imap_taxi.network.packet.Packet;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.03.13
 * Time: 20:02
 * To change this template use File | Settings | File Templates.
 */

public class MultiPacketListener implements OnNetworkPacketListener {
    private static MultiPacketListener instance;
    private static final Object lock = new Object();
    private Map<Integer, List<OnNetworkPacketListener>> listeners;

    private MultiPacketListener() {
        listeners = new HashMap<Integer, List<OnNetworkPacketListener>>();
    }

    public static MultiPacketListener getInstance() {
        synchronized (lock) {
            if (instance == null)
                instance = new MultiPacketListener();
        }

        return instance;
    }

    @Override
    public void onNetworkPacket(Packet packet) {
        Log.w("PACKET", String.valueOf(packet.getId()));

        List<OnNetworkPacketListener> callbacks = listeners.get(packet.getId());
        if (callbacks != null) {
            Iterator<OnNetworkPacketListener> iterator = callbacks.iterator();
            while (iterator.hasNext()) {
                OnNetworkPacketListener item = iterator.next();
                item.onNetworkPacket(packet);
            }
        }
    }

    public void clear() {
        listeners.clear();
    }

    public void addListener(int packetId, OnNetworkPacketListener listener) {
        if (listeners.containsKey(packetId)) {
            List<OnNetworkPacketListener> callbacks = listeners.get(packetId);
            if (!callbacks.contains(listener)) {
                callbacks.add(listener);
            }
        } else {
            List<OnNetworkPacketListener> callbacks = new LinkedList<OnNetworkPacketListener>();
            callbacks.add(listener);
            listeners.put(packetId, callbacks);
        }
    }

    public void removeListener(OnNetworkPacketListener listener) {
        Set<Integer> keys = listeners.keySet();
        Iterator<Integer> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            if (listeners.get(key).remove(listener)){
                Log.w("remove success", String.valueOf(key));
            }
        }
    }

    public void removeListeners(int packetId) {
        List<OnNetworkPacketListener> callbacks = listeners.get(packetId);
        if (callbacks != null) {
            callbacks.clear();
        }
    }
}
