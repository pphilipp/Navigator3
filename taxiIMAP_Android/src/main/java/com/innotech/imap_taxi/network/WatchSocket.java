package com.innotech.imap_taxi.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.innotech.imap_taxi.activity.MyImapApp;
import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.LogHelper;
//import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.ping.RunPingAndGeo;
import com.innotech.imap_taxi.utile.MyApplication;

public class WatchSocket extends AsyncTask<Integer, Integer, Integer> {
    public static final String LOG_TAG = WatchSocket.class.getSimpleName();
    public static boolean disconnected;
    static PacketParser parser;
    //	private OnConnectionEstablishedListener connectionEstablishedListener;
    static InputStream fromServer;
    static OutputStream toServer;
    static boolean z;//флаг основного цыкла
    static boolean disc;
    private static OnNetworkPacketListener packetListener;
    private static OnNetworkErrorListener errorListener;
    private static Socket socket;
    private byte[] packetSize;

    public static boolean isConnected() {return (socket != null && socket.isConnected()/* && z*/);}

    public static boolean isDisconnected() {
        return disconnected;
    }

    public synchronized static boolean send(final byte[] data) {//Метод отправки данных по сети
        Log.d(LOG_TAG, "send_WATCHSOCKET");

        try {
            if (toServer != null) {//Если исходяший поток созданн,то отправляем данные
                toServer.write(data);
                toServer.flush();

                Log.d(LOG_TAG,StringUtils.bytesToStr(data));
                Log.d(LOG_TAG, "outcoming = " + StringUtils.bytesToStr(data));

                ConnectionHelper.getInstance().setWorking(true);

                return true;
            } else {return false;} //Если не создан - возвращаем false

        } catch (IOException ioe) {
            ioe.printStackTrace();
            ConnectionHelper.getInstance().setWorking(false);
            return false;
        }
    }


    static void disconnect()//Метод разрыва соединения
    {
        z = false;
        disc = true;
        new WatchSocket().cancel(false);
        try {
            if (socket != null) {
                socket.shutdownInput();
                socket.shutdownOutput();

                socket.close();
            }
            socket = null;
        } catch (Exception e) {
        }

        try {
            if (toServer != null) {
                toServer.close();
            }
            toServer = null;
        } catch (Exception e) {
        }
        try {
            if (fromServer != null) {
                fromServer.close();
            }
            fromServer = null;
        } catch (Exception e) {
        }
        Log.i("NET", "disconnected");
        disconnected = true;

    }

    static void notifyPacketListener(Packet packet) {
        if (packet != null && packetListener != null) {
            packetListener.onNetworkPacket(packet);
        }
    }

    static void notifyConnectionErrorListener(int errorCode, String errorMessage) {
        if (errorListener != null) {
            errorListener.onNetworkError(errorCode, errorMessage);
        }
    }

    private static void reconnect() {
        RunPingAndGeo.getInstance().tryReConnect();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        LogHelper.w_connection("onPreExecute");
        //connectionEstablishedListener = null;
        packetListener = MultiPacketListener.getInstance();
        Log.d("tag-tag-tag", "onPreExecute_WATCHSOCKET");
        socket = null;
        fromServer = null;
        toServer = null;
        z = true;
        packetSize = new byte[4];

        //Get parser
        parser = new PacketParser();

        LogHelper.w_connection("onPreExecute_2");

    }

    @Override
    protected Integer doInBackground(Integer... param) {
        Log.d(LOG_TAG, "doInBackground_WatchSocket");
        Log.d(LOG_TAG, "host_WatchSocket" + SocketService.host);

        InetAddress serverAddr;
        int serverPort;
        disc = false;
        try {
            while (true) {
                Log.d(LOG_TAG, "while(true) начало работы");
                if (disc) {
                    return 1;
                }
                //if (z){
                {
                    Log.d(LOG_TAG,
                            "WatchSocket_socket==null || toServer==null || fromServer==null = "
                            + (socket == null || toServer == null || fromServer == null));

                    if (socket == null || toServer == null || fromServer == null) {
                        SharedPreferences sharedPrefs = PreferenceManager
                                .getDefaultSharedPreferences(ContextHelper
                                        .getInstance().getCurrentContext());

                        String host = ServerData.getInstance().isMasterServer()
                                ? sharedPrefs.getString("prefHost", "")
                                : sharedPrefs.getString("prefHostSlave", "").equals("")
                                ? sharedPrefs.getString("prefHost", "")
                                : sharedPrefs.getString("prefHostSlave", "");

                        String port = ServerData.getInstance().isMasterServer()
                                ? sharedPrefs.getString("prefPort", "")
                                : sharedPrefs.getString("prefPortSlave", "").equals("")
                                ? sharedPrefs.getString("prefPort", "")
                                : sharedPrefs.getString("prefPortSlave", "");
                        try {
                            serverAddr = InetAddress.getByName(host);                            //свежие правки
                            serverPort = Integer.parseInt(port);
                        } catch (NullPointerException e) {

                            e.printStackTrace();
                            serverAddr = null;
                            serverPort = -1;
                            Log.d("WatchSocketException", e.getMessage());
                        }




//                        serverAddr = InetAddress.getByName(SocketService.host);

                        Log.d("Serv", String.valueOf(serverAddr));

                        //tyt oshibka
                        socket = new Socket(serverAddr, serverPort);             //свежие правки
                        Log.d("socket = ", String.valueOf(socket.getInetAddress()));

                        packetListener = MultiPacketListener.getInstance();
                        toServer = new BufferedOutputStream(socket.getOutputStream());
                        fromServer = new BufferedInputStream(socket.getInputStream());

                        Log.d("address = " , serverAddr + " " + socket.getInetAddress());
                        Log.d("myIp = " , socket.getLocalSocketAddress().toString());
                        // открываем сокет-соединение

                        Log.d(LOG_TAG, ("Swipe.act_swipe " + ((ContextHelper.getInstance().getCurrentActivity() != null) + "")));
                        Log.d(LOG_TAG, ("NotificatiServConnListner " + (SocketService.connectionEstablishedListener != null) + ""));

                        if (SocketService.connectionEstablishedListener != null) {
                            SocketService.connectionEstablishedListener.onConnectionEstablished();
                        }
                        SocketService.connectionEstablishedListener = null;
                    }
                    if (isCancelled()) {
                        return 1;
                    }
                    z = true;
                    disconnected = false;
                    Log.d("tag-tag-tag", "before  while (z)");
                    while (z) {
                        if (isCancelled()) {
                            return 1;
                        }
                        try {
                            Log.d("tag-tag-tag", "reading");
                            //Читаем длинну пакета
                            if (fromServer.read(packetSize) == -1) {
                                if (z) {
                                    //TODO Vova test
                                    reconnect();
                                    //	notifyConnectionErrorListener(OnNetworkErrorListener.CONNECTION_LOST_ERROR, "Error reading packet size");
                                }
                                Log.d("tag-tag-tag", "break");
                                //break;
                            }
                            //Пропускаем версию протокода(4 байта)
                            for (int i = 0; i < 4; ++i) {
                                Log.d("tag-tag-tag", "fromServer.read() = " + fromServer.read());
                            }

                            //Определяем размер
                            int size = Utils.byteToInt(packetSize);
                            byte[] buff = new byte[size];

                            //Читаем данные
                            int read = fromServer.read(buff, 0, size);

                            int reread = read;
                            while (read < size && reread != -1) {
                                reread = fromServer.read(buff, read, size - read);
                                read += reread;
                            }
                            Log.d("tag-tag-tag", "read == size =   " + ((read == size) + ""));
                            if (read == size) {
                                //Парсим пакет
                                Packet packet = parser.parsePacket(buff);
                                Log.d("tag-tag-tag", "parser.parsePacket");
                                //Отдаем на обработку
                                notifyPacketListener(packet);
                                Log.d("tag-tag-tag", "notifyPacketListener");
                            } else {
                                if (z) {
                                    disconnect();
                                    notifyConnectionErrorListener(OnNetworkErrorListener.WRONG_PACKET_SIZE, "Read wrong packet size");
                                }
                                break;
                            }
                        } catch (Resources.NotFoundException e){
                            e.printStackTrace();
                            Log.d("tag-tag-tag catch", e.getMessage());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            Log.d("tag-tag-tag catch", exception.getMessage());
                            z = false;

							/*	exception.printStackTrace();
							if (z) {
								disconnect();
								exception.printStackTrace();
								notifyConnectionErrorListener(OnNetworkErrorListener.CONNECTION_LOST_ERROR, exception.getMessage());
							}
							break;*/
                        }
                    }
                    reconnect();
                }
            }
            //	return -1;
        } catch (Exception e) {
            e.printStackTrace();

            Log.getStackTraceString(e);
            Log.d(LOG_TAG, "doInBackground_catch_MyImapApp.isVisible() = " + MyImapApp.getInstance().isVisible());

            return -1;
        }
    }


}
