package com.innotech.imap_taxi.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.content.Intent;

import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.packet.Packet;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 14.09.13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionHelper{// implements Runnable {
    private static ConnectionHelper instance;
    private static final Object lock = new Object();
    private OnNetworkPacketListener packetListener;
    private OnNetworkErrorListener errorListener;
    private PacketParser parser;
    private Socket socket;
    private InputStream fromServer;
    private OutputStream toServer;
    private String host;
    private int port;
    private volatile boolean z;//флаг основного цыкла
    private byte[] packetSize;
    private boolean working;
    private OnConnectionEstablishedListener connectionEstablishedListener;
    //private static boolean disconnected;

    public void setDisconnected(boolean disconnected) {
    	WatchSocket.disconnected = disconnected;
	}

	public boolean isDisconnected() {
		return WatchSocket.isDisconnected();
	}

	public static ConnectionHelper getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ConnectionHelper();
            }
        }

        return instance;
    }

    
    private ConnectionHelper() {
        connectionEstablishedListener = null;
        packetListener = MultiPacketListener.getInstance();
        socket = null;
        fromServer = null;
        toServer = null;
        z = false;
        packetSize = new byte[4];

        //Get parser
        parser = new PacketParser();
    }

  /*  public void start(String host, int port, OnConnectionEstablishedListener connectionEstablishedListener)//Метод запуска соединения
    {
        if (isConnected()) {
            stop();
        }

        this.host = host;
        this.port = port;
        
        Log.d("IMAP test", "Connecting to " + host + " " + port);
        
        this.connectionEstablishedListener = connectionEstablishedListener;
       // Thread thread = new Thread(this);
        z = true;
        //thread.start();
    }*/

    public void stop() {//Метод остоновки соединения
        z = false;
        disconnect();
        ContextHelper.getInstance().getCurrentActivity()
                .stopService(new Intent(ContextHelper
                    .getInstance().getCurrentContext(),
                        SocketService.class));
    }

    public void setPacketListener(OnNetworkPacketListener packetListener) {
        this.packetListener = packetListener;
    }

    public void setErrorListener(OnNetworkErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    private void disconnect() { //Метод разрыва соединения
    	WatchSocket.disconnect();
       /* try {
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
        WatchSocket.disconnected = true;*/
    }

    public boolean isConnected() {
    	return WatchSocket.isConnected();
       // return (socket != null && socket.isConnected() && z);
    }

   /* public void run() {
        //Create connection
        try {
            socket = new Socket(host, port);

            //Create streams
            toServer = new BufferedOutputStream(socket.getOutputStream());
            fromServer = new BufferedInputStream(socket.getInputStream());
        } catch (UnknownHostException unknownHostException) {
            disconnect();
            notifyConnectionErrorListener(OnNetworkErrorListener.UNKNOWN_HOST_ERROR, unknownHostException.getMessage());
            return;
        } catch (IOException ioException) {
            disconnect();
            notifyConnectionErrorListener(OnNetworkErrorListener.IO_ERROR, ioException.getMessage());
            return;
        } catch (Exception exception) {
            disconnect();
            notifyConnectionErrorListener(OnNetworkErrorListener.COMMON_ERROR, exception.getMessage());
            return;
        }

        if (connectionEstablishedListener != null) {
            connectionEstablishedListener.onConnectionEstablished();
        }
        connectionEstablishedListener = null;

        //start reading data from server
        while (z) {
            try {
            	Log.d("tag-tag-tag", "reading");
                //Читаем длинну пакета
                if (fromServer.read(packetSize) == -1) {
                    if (z) {
                        disconnect();
                        notifyConnectionErrorListener(OnNetworkErrorListener.CONNECTION_LOST_ERROR, "Error reading packet size");
                    }
                    return;
                }

                //Пропускаем версию протокода(4 байта)
                for (int i = 0; i < 4; ++i) {
                    fromServer.read(); 
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

                if (read == size) {
                    //Парсим пакет
                    Packet packet = parser.parsePacket(buff);

                    //Отдаем на обработку
                    notifyPacketListener(packet);
                } else {
                    if (z) {
                        disconnect();
                        notifyConnectionErrorListener(OnNetworkErrorListener.WRONG_PACKET_SIZE, "Read wrong packet size");
                    }
                    return;
                }
            } catch (Exception exception) {
                if (z) {
                    disconnect();
                    exception.printStackTrace();
                    notifyConnectionErrorListener(OnNetworkErrorListener.CONNECTION_LOST_ERROR, exception.getMessage());
                }
                return;
            }
        }

        //Log.i("NET", "thread finished");
    }
*/
    public synchronized boolean send(byte[] data){//Метод отправки данных по сети
    	return WatchSocket.send(data);
       /* try {
        	
            if (WatchSocket.toServer != null)//Если исходяший поток созданн,то отправляем данные
            {
            	WatchSocket.toServer.write(data);
            	WatchSocket.toServer.flush();
                return true;
            } else//Если не создан - возвращаем false
            {
                return false;
            }
        } catch (IOException ioe) {
            return false;
        }*/
    }

    private void notifyPacketListener(Packet packet) {
        if (packet != null && packetListener != null)
            packetListener.onNetworkPacket(packet);
    }

    private void notifyConnectionErrorListener(int errorCode, String errorMessage) {
        if (errorListener != null)
            errorListener.onNetworkError(errorCode, errorMessage);
    }

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}
}