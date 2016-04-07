package com.innotech.imap_taxi.network;

import android.util.Log;
import com.innotech.imap_taxi.network.packet.Packet;

import java.util.*;

/**
 * @method getInstance - have critical sections
 * for initialisation/getting singleton instance of MultiPacketListener.
 */

public class MultiPacketListener implements OnNetworkPacketListener {
    public static final String LOG_TAG = MultiPacketListener.class.getSimpleName();
    private static MultiPacketListener instance;
    private static final Object lock = new Object();
    private Map<Integer, List<OnNetworkPacketListener>> mListeners;

    private MultiPacketListener() {
        mListeners = new HashMap<Integer, List<OnNetworkPacketListener>>();
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
        Log.d(LOG_TAG, String.valueOf(packet.getId()));

        List<OnNetworkPacketListener> callbacks = mListeners.get(packet.getId());
        if (callbacks != null) {
            Iterator<OnNetworkPacketListener> iterator = callbacks.iterator();
            while (iterator.hasNext()) {
                OnNetworkPacketListener item = iterator.next();
                item.onNetworkPacket(packet);
            }
        }
    }

    public void clear() {
        mListeners.clear();
    }

    public void addListener(int packetId, OnNetworkPacketListener listener) {
        if (mListeners.containsKey(packetId)) {
            List<OnNetworkPacketListener> callbacks = mListeners.get(packetId);
            if (!callbacks.contains(listener)) {
                callbacks.add(listener);
            }
        } else {
            List<OnNetworkPacketListener> callbacks = new LinkedList<OnNetworkPacketListener>();
            callbacks.add(listener);
            mListeners.put(packetId, callbacks);
        }
    }

    public void removeListener(OnNetworkPacketListener listener) {
        Set<Integer> keys = mListeners.keySet();
        Iterator<Integer> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            if (mListeners.get(key).remove(listener)){
                Log.d(LOG_TAG, String.valueOf(key));
            }
        }
    }

    public void removeListeners(int packetId) {
        List<OnNetworkPacketListener> callbacks = mListeners.get(packetId);
        if (callbacks != null) {
            callbacks.clear();
        }
    }
}
