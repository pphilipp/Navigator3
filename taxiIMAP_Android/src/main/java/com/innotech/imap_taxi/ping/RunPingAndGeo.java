package com.innotech.imap_taxi.ping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.SwipeFragment;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.SocketService;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.network.packet.PingStateAnswer;
import com.innotech.imap_taxi.utile.AlertDHelper;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi3.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RunPingAndGeo implements Runnable {

    private static RunPingAndGeo instance;
    private final String TAG = "RunPingAndGeo";
    OnNetworkPacketListener ping_resp, ping_state;
    private boolean start;
    private ServerData sData;
//    private int sleep_interval = 5000;
    private int sleep_interval = 40000;
    private boolean noFake = true;
    private boolean reconnect = false;
    private boolean pingStateStatus = true;
    private int noPingState;
    private long reconnect_time = 2000l;
    private int attempts = 0;

    public static RunPingAndGeo getInstance() {
        if (instance == null) {
            instance = new RunPingAndGeo();
        }

        return instance;
    }

    public void stop() {
        start = false;
        instance = null;

        Log.d(TAG, "ping stop");

        MultiPacketListener.getInstance().removeListener(ping_resp);
        MultiPacketListener.getInstance().removeListener(ping_state);
    }

    public void run() {

        start = false;
        MultiPacketListener.getInstance().removeListener(ping_resp);
        MultiPacketListener.getInstance().removeListener(ping_state);

        Log.d(TAG, "ping start");

        sData = ServerData.getInstance();

        ping_resp = new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                Log.d(TAG, "goted PING_RESPONCE");
            }
        };

        ping_state = new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                PingStateAnswer pack = (PingStateAnswer) packet;
                Log.d(TAG, "goted PING_STATE_ANSWER + " + pack.isInRegisterLog);

                if (!pack.isInRegisterLog) {
                    Log.d(TAG,"isInRegisterLog=false  createRegister(pingstate)");
                    byte[] body = RequestBuilder.createBodyRegisterOnRelay(sData.getNick(), true, sData.getPeopleID());
                    byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, sData.getSrvID(),
                            RequestBuilder.DEFAULT_DESTINATION_ID, sData.getGuid(), true, body);
                    ConnectionHelper.getInstance().send(data);
                    body = null;
                    data = null;

                }
                Log.d(TAG, "listner PING_STATE_ANSWER ");

                pingStateStatus = true;
                noPingState = 0;
            }
        };

        MultiPacketListener.getInstance().addListener(Packet.PING_RESPONCE, ping_resp);
        MultiPacketListener.getInstance().addListener(Packet.PING_STATE_ANSWER, ping_state);

        if (!ServerData.getInstance().IS_TEST_BUILD) {
            if (!Settings.Secure.getString(ContextHelper.getInstance().getCurrentActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
                noFake = false; //comment for allow fake locs
                ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDHelper.showDialogOk("Выключите разрешение на фейковые координаты в настройках, иначе ваше местоположение отправляться не будет!");
                    }
                });
            }
        }

        start = true;
        final ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

//		scheduler.scheduleAtFixedRate
        scheduler.scheduleWithFixedDelay
                (
                        new Runnable() {
                            @Override
                            public void run() {
                                if (start) {
                                    if (isNetworkAvailable(ContextHelper.getInstance().getCurrentContext())) {
                                        //StateObserver.getInstance().setNetwork(StateObserver.WORK);
//                                        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.gprs_working);
//                                                } catch (Exception e) {
//                                                }
//                                            }
//                                        });

                                        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    StateObserver.getInstance().setNetwork(StateObserver.WORK);
//                                                    ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication).setBackgroundResource(R.drawable.gprs_working);
                                                } catch (Exception e) {
                                                }
                                            }
                                        });

                                        if (!ServerData.getInstance().IS_TEST_BUILD) {
                                            if (!Settings.Secure.getString(ContextHelper.getInstance().getCurrentActivity().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
                                                noFake = false; //comment for allow fake locs
                                            }
                                        }

                                        if (ConnectionHelper.getInstance().isConnected() && !ConnectionHelper.getInstance().isDisconnected() && ConnectionHelper.getInstance().isWorking()) {

                                            if (ConnectionHelper.getInstance().isWorking()) {
                                                if ((MyLocationListener.lon != 0.0) && (MyLocationListener.lat != 0.0) && noFake) {
                                                    Log.i(TAG, "ping " + MyLocationListener.lat + " " + MyLocationListener.lon);

//                                                    LogHelper.w_gps("ping " + MyLocationListener.lat + " " + MyLocationListener.lon);

                                                    //	byte[] body = RequestBuilder.createBodyPPCOnLineData((float)MyLocationListener.lon, (float)MyLocationListener.lat, sData.getPeopleID());
                                                    //byte[] body = RequestBuilder.createBodyPPCOnLineData((float)MyLocationListener.lon, (float)MyLocationListener.lat, sData.getPeopleID());
                                                    Time now = new Time();
                                                    now.setToNow();
                                                    byte[] body = RequestBuilder.createBodyP((byte) MyLocationListener.speed,
                                                            (int) MyLocationListener.speed,
                                                            now.toMillis(false),
                                                            (float) MyLocationListener.lon,
                                                            (float) MyLocationListener.lat,
                                                            sData.getPeopleID());
                                                    byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, sData.getSrvID(),
                                                            RequestBuilder.DEFAULT_DESTINATION_ID, sData.getGuid(), true, body);
                                                    if (ConnectionHelper.getInstance().send(data))
                                                    	Log.d("STATE", "gps sent");

                                                    MyLocationListener.lon = 0.0;
                                                    MyLocationListener.lat = 0.0;
                                                    MyLocationListener.direction = 0;
                                                    MyLocationListener.speed = 0;
                                                    body = null;
                                                    data = null;
                                                }

                                                byte[] body = RequestBuilder.createPing();
                                                ConnectionHelper.getInstance().send(body);
                                                Log.d(TAG,"ping");
                                                body = RequestBuilder.createPingState(sData.getPeopleID());
                                                byte[] data = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, sData.getSrvID(),
                                                        RequestBuilder.DEFAULT_DESTINATION_ID, sData.getGuid(), true, body);
                                                Log.d("pingstate " , String.valueOf(sData.getPeopleID()));
//                                                LogHelper.w_ping_state("pingstate " + sData.getPeopleID() + "");
                                                ConnectionHelper.getInstance().send(data);

                                                body = null;
                                                data = null;

                                                pingStateStatus = false;
                                            }
                                            if (!pingStateStatus) {

                                                noPingState++;

                                                Log.e(TAG, "!!!!!!! pingState NO " + noPingState);

                                                if (noPingState == 3) {
                                                    Log.e(TAG, "!!!!!!!!!!!!!!! PingStateServer not available");

                                                    if (Crashlytics.getInstance() != null) {
                                                        Crashlytics.getInstance().core.setString("waitingCarError", "lost connection");
                                                    }

//												ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
//				        				@Override
//				        				public void run() {
//				        					if (MyImapApp.getInstance().isVisible())
//												AlertDHelper.showDialogOk("PingStateServer не доступен");
//				        				}
//				            		});

                                                    noPingState = 0;
                                                }
                                            }

                                        } else {
                                            start = false;
                                            //	if (!ConnectionHelper.getInstance().isWorking()){
                                            PlaySound.getInstance().play(R.raw.msg_stat_neg);
//										ConnectionHelper.getInstance().stop();
//									ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
//										@Override
//										public void run() {
//											ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication).setBackgroundResource(R.drawable.no_gprs_working);
//											ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication).setBackgroundResource(R.drawable.no_server_working);
//
//											TextView state_driver = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
//											state_driver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
//											state_driver.setTextColor(Color.RED);
//											stop();
//										}
//									});
                                            if (Crashlytics.getInstance() != null) {
                                                Crashlytics.getInstance().core.setString("waitingCarError", "trying to reconnect");
                                            }

                                            tryReConnect();
                                            //	}
                                            //break;
                                            start = false;
                                            //this.sh
                                            scheduler.shutdown();
                                        }

                                    } else {
                                        PlaySound.getInstance().play(R.raw.msg_stat_neg);
//									ConnectionHelper.getInstance().stop();
//								ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
//									@Override
//									public void run() {
//										ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication).setBackgroundResource(R.drawable.no_gprs_working);
//										ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication).setBackgroundResource(R.drawable.no_server_working);
//
//										TextView state_driver = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
//										state_driver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
//										state_driver.setTextColor(Color.RED);
//										stop();
//									}
//								});
                                        if (Crashlytics.getInstance() != null) {
                                            Crashlytics.getInstance().core.setString("waitingCarError", "trying to reconnect");
                                        }
//
                                        tryReConnect();

                                        try {
                                            scheduler.shutdown();
                                        } catch (Exception e) {
                                            Log.d("sheduler_exception " , e.getMessage());
                                        }

                                    }
                                    try {
                                        if (!(SwipeFragment.parkings.getText().toString().equals("Стоянки"))) {
                                            RequestHelper.getTaxiParkings();
                                        }
                                    } catch (Exception e) {
                                    }

                                }
                            }
                        }, 0, sleep_interval, TimeUnit.MILLISECONDS);



		/*while(start) {


			try {
				Thread.sleep(sleep_interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}*/
    }

    public void setSleep(int sleep_interval) {
        this.sleep_interval = sleep_interval;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void tryReConnect() {
        Log.w("Reconect bef IF", String.valueOf(reconnect));
        if (!reconnect && start) {
            //ConnectionHelper.getInstance().stop();
            Log.w("Reconect aft IF", String.valueOf(reconnect));

            ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                @Override
                public void run() {
                    if (attempts > 1) {
//                        ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.no_gprs_working);
//                        ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.no_server_working);
                        StateObserver.getInstance().setNetwork(StateObserver.NO_WORK);
                        StateObserver.getInstance().setServer(StateObserver.NO_WORK);
//                        TextView state_driver = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
//                        state_driver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
//                        state_driver.setTextColor(Color.RED);
                        StateObserver.getInstance().setDriverState(StateObserver.DRIVER_LOST_CONNECTION);
                    }
                    //stop();

                    MultiPacketListener.getInstance().removeListener(ping_resp);
                    MultiPacketListener.getInstance().removeListener(ping_state);
                }
            });
            new Thread(new Runnable() {

                @Override
                public void run() {

                    reconnect = true;
                    while (reconnect && start) {
                        System.out.println("reconnecting");
                        Log.d("reconnecting","new Thread");

                        //

                        if (isNetworkAvailable(ContextHelper.getInstance().getCurrentContext())) {

                            System.out.println("network available");
                            Log.d("reconnecting", "network available");

                            ServerData.getInstance().setShowAlertRegistr(false);
                            if (attempts > 1) {
                                ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.no_gprs_working);
//                                        ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.no_server_working);
                                        StateObserver.getInstance().setNetwork(StateObserver.NO_WORK);
                                        StateObserver.getInstance().setServer(StateObserver.NO_WORK);
//                                        TextView state_driver = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
//                                        state_driver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
                                        ServerData.getInstance().setShowAlertRegistr(true);
                                        StateObserver.getInstance().setDriverState(StateObserver.DRIVER_LOST_CONNECTION);
//                                        state_driver.setTextColor(Color.RED);
                                    }
                                });
                            }

                            try {
                                Thread.sleep(reconnect_time);
                                reconnect_time += 100l;
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            ContextHelper.getInstance().getCurrentContext().stopService(new Intent(ContextHelper.getInstance().getCurrentContext(), SocketService.class));
                            Log.d("reconnecting", "service stoped sleep");

                            try {
                                Thread.sleep(reconnect_time);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            Log.d("reconnecting","service start");
                            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext());

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

                            ContextHelper.getInstance()
                                    .getCurrentContext()
                                    .startService(new Intent(ContextHelper.getInstance().getCurrentContext(), SocketService.class)
                                            .putExtra("host", host)
                                            .putExtra("port", port));
                            try {
                                Thread.sleep(1300L);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            if (ConnectionHelper.getInstance().isConnected()) {
                                Log.d("reconnecting","createLogin from ping");
                                ConnectionHelper.getInstance().send(RequestBuilder.createLogin(255, ServerData.getInstance().getLogin(), ServerData.getInstance().getPass(), true));
                                attempts = 0;
                                reconnect = false;
                            }
                        } else {
                            try {
                                ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ServerData.getInstance().setShowAlertRegistr(true);
//                                        ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.no_gprs_working);
//                                        ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.no_server_working);
                                        StateObserver.getInstance().setNetwork(StateObserver.NO_WORK);
                                        StateObserver.getInstance().setServer(StateObserver.NO_WORK);
//                                        TextView state_driver = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
//                                        state_driver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
//                                        state_driver.setTextColor(Color.RED);
                                        StateObserver.getInstance().setDriverState(StateObserver.DRIVER_LOST_CONNECTION);
                                    }
                                });
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        attempts++;
                        ServerData.getInstance().setMasterServer(!ServerData.getInstance().isMasterServer());
                    }
                    reconnect_time = 1000l;
                    reconnect = false;
                }
            }).start();

        }
    }

}
