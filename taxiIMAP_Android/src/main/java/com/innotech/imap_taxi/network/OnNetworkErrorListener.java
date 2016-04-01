package com.innotech.imap_taxi.network;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.03.13
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public interface OnNetworkErrorListener {
    public static final int COMMON_ERROR = 0;
    public static final int UNKNOWN_HOST_ERROR = 1;
    public static final int IO_ERROR = 2;
    public static final int CONNECTION_LOST_ERROR = 3;
    public static final int WRONG_PACKET_SIZE = 4;

    public void onNetworkError(int errorCode, String errorMessage);
}
