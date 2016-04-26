package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.OrdersAdapterDisp4;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.datamodel.SettingsFromXml;
import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.model.UIData;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.DistanceOfOrderAnswer;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkErrorListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.SocketService;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi.network.packet.DriverBlockedPack;
import com.innotech.imap_taxi.network.packet.GetCSBalanceResponce;
import com.innotech.imap_taxi.network.packet.GetOrdersResponse;
import com.innotech.imap_taxi.network.packet.GetRoutesResponse;
import com.innotech.imap_taxi.network.packet.LoginResponse;
import com.innotech.imap_taxi.network.packet.PPSChangeStateResponse;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.network.packet.RelayCommunicationResponce;
import com.innotech.imap_taxi.network.packet.SearchInEtherOrderOverResponse;
import com.innotech.imap_taxi.network.packet.SetYourOrdersAnswer;
import com.innotech.imap_taxi.network.packet.SettingXmlResponse;
import com.innotech.imap_taxi.network.packet.SrvMessageResponce;
import com.innotech.imap_taxi.ping.PingHelper;
import com.innotech.imap_taxi.ping.RunPingAndGeo;
import com.innotech.imap_taxi.utile.AlertDHelper;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi3.R;

import java.util.ArrayList;

/**
 * Created by philipp on 25.04.16.
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener{
    private static final String LOG_TAG = MainMenuFragment.class.getSimpleName();
    private static final int RESULT_SETTINGS = 1;
    SharedPreferences sharedPrefs;
    public static boolean reCon = false;
    protected boolean xmlOk = false;
    private boolean isExit;
    private boolean isCancelProgressDialog;
    private String mAlertText;
    OrderManager orderManager;
    private LoginResponse loginResponse;
    private PlaySound play;
    ArrayList<DispOrder4> mOrders;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    OrdersAdapterDisp4 mAdapter;
    Context mContext;
    Button btnOrders;
    Button btnPrelim;
    Button myOwnOrder;
    Button parkings;
    Button btnMap;
    Button tvBalance;
    Button connection;
    Button btnCloseConnection;
    Button btnPrefs;
    Button btnTaxoMetr;
    Button btnSendCrash;
    Button btnBalance;
    TextView tvVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()" + "\n");
        try {
            play = PlaySound.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext = ContextHelper.getInstance().getCurrentContext();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mOrders = new ArrayList<DispOrder4>();
        mAdapter = new OrdersAdapterDisp4(mOrders, mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.main_menu_chapter_one_new,container,false);
        btnOrders = (Button) v.findViewById(R.id.btnOrders);




        Typeface menuTypeface = Typeface.createFromAsset(ContextHelper
                        .getInstance().getCurrentContext().getAssets(),
                "fonts/BebasNeueRegular.ttf");
        btnOrders.setTypeface(menuTypeface);
        btnOrders.setOnClickListener(this);

        btnPrelim = (Button) v.findViewById(R.id.btnPrelim);
        btnPrelim.setTypeface(menuTypeface);
        btnPrelim.setOnClickListener(this);

        myOwnOrder = (Button) v.findViewById(R.id.btn_do_my_order);
        myOwnOrder.setTypeface(menuTypeface);
        myOwnOrder.setOnClickListener(this);

        parkings = (Button) v.findViewById(R.id.parkingsButton);
        parkings.setTypeface(menuTypeface);
        parkings.setOnClickListener(this);

        btnMap = (Button) v.findViewById(R.id.btnmap);
        btnMap.setTypeface(menuTypeface);
        btnMap.setOnClickListener(this);

        tvBalance = (Button) v.findViewById(R.id.balanceButton);
        tvBalance.setTypeface(menuTypeface);

        tvVersion = (TextView) v.findViewById(R.id.version);

        connection = (Button) v.findViewById(R.id.connection);
        connection.setTypeface(menuTypeface);
        connection.setOnClickListener(this);
        isReConnect();

        btnCloseConnection = (Button) v.findViewById(R.id.exit);
        btnCloseConnection.setTypeface(menuTypeface);
        btnCloseConnection.setOnClickListener(this);

        btnPrefs = (Button) v.findViewById(R.id.btn_prefs);
        btnPrefs.setTypeface(menuTypeface);
        btnPrefs.setOnClickListener(this);

        btnTaxoMetr = (Button) v.findViewById(R.id.btn_taxometr);
        btnTaxoMetr.setTypeface(menuTypeface);
        btnTaxoMetr.setOnClickListener(this);

        btnSendCrash = (Button) v.findViewById(R.id.sendCrash);
        btnSendCrash.setOnClickListener(this);
        btnSendCrash.setTypeface(menuTypeface);

        btnBalance = (Button) v.findViewById(R.id.balanceButton);


        initButtonSyle();

        addPacketListeners();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        isExit = false;
    }

    private void initButtonSyle() {
        final PaintDrawable p = (PaintDrawable) GraphUtils.buttonStyle(connection);
        btnSendCrash.setBackground(p);
        connection.setBackground(p);
        btnOrders.setBackground(p);
        myOwnOrder.setBackground(p);
        btnMap.setBackground(p);
        btnBalance.setBackground(p);
        btnPrelim.setBackground(p);
        btnTaxoMetr.setBackground(p);
        parkings.setBackground(p);
        btnPrefs.setBackground(p);
        btnCloseConnection.setBackground(p);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connection:
//                connectionClick();
                break;
            case R.id.btnOrders:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ORDERS);
                break;
            case R.id.btn_do_my_order:
//                myOwnOrdersClick();
                break;
            case R.id.btnmap:
                MapFragmentWindow.orderId = -1;
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.MAP);
                break;
            case R.id.btnPrelim:
//                CurrentOrdersFragment
//                        .displayOrders(CurrentOrdersFragment.STATE_PRE);
//                FragmentTransactionManager.getInstance()
//                        .openFragment(CURRENTORDERS);
                break;
            case  R.id.parkingsButton:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.PARKINGS);
                break;
            case R.id.btn_busy:
                busyClick();
                break;
            case R.id.btn_taxometr:
//                taxometrClick();
                break;
            case R.id.btn_order:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ORDERS);
                break;
            case R.id.ethear_button:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ETHEAR);
                break;
            case R.id.test_test:
//                testClick();
                break;
            case R.id.exit:
                exitClick();
                break;
            case R.id.btn_prefs:
                ContextHelper
                        .getInstance()
                        .getCurrentActivity()
                        .startActivityForResult(
                                new Intent(ContextHelper.getInstance()
                                        .getCurrentContext(),
                                        UserSettingActivity.class),
                                RESULT_SETTINGS);
                break;
            case R.id.sendCrash :
//                showConfirmToast(false, "Address ");
                break;
            default:break;
        }

    }




    public void isReConnect() {
        if (reCon) {
            connection.performClick();
            reCon = false;
        }
    }

    private void addPacketListeners() {
        /** 1 */
        MultiPacketListener.getInstance().addListener(
                Packet.LOGIN_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        loginResponse = (LoginResponse) packet;
                        NavigatorMenuActivity.setLoginResponse(loginResponse);

                        Log.d(LOG_TAG, "loginResponse=" + loginResponse.answer + " - " + loginResponse.toString());
                        Log.d(LOG_TAG, "status '" + loginResponse.answer + "'");
                        Log.d(LOG_TAG, "LOGIN_RESPONCE answer=" + loginResponse.answer);

                        if (loginResponse.answer.equals(LoginResponse.ANSWER_OK)) {
                            Log.d(LOG_TAG, "login");

                            ServerData sData = ServerData.getInstance();
                            sData.setGuid(loginResponse.GUID);
                            sData.setPeopleID(loginResponse.peopleID);
                            sData.setSrvID(loginResponse.srvID);

                            // FragmentTransactionManager.getInstance().back();

                            Log.d(LOG_TAG, "ANSWER_OK createRegister");

                            byte[] body = RequestBuilder.createBodyRegisterOnRelay(ServerData
                                    .getInstance().getNick(), true, loginResponse.peopleID);
                            byte[] data = RequestBuilder
                                    .createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                            loginResponse.srvID,
                                            RequestBuilder.DEFAULT_DESTINATION_ID,
                                            loginResponse.GUID, true, body);
                            ConnectionHelper.getInstance().send(data);

                            byte[] body2 = RequestBuilder
                                    .createGetPPCSettingsXML(loginResponse.peopleID);
                            byte[] data2 = RequestBuilder
                                    .createSrvTransfereData(
                                            RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                            loginResponse.srvID,
                                            RequestBuilder.DEFAULT_DESTINATION_ID,
                                            loginResponse.GUID, true, body2);
                            ConnectionHelper.getInstance().send(data2);
                            // !!!???

                            byte[] body4 = RequestBuilder
                                    .createBodyGetTaxiParkings(loginResponse.peopleID);
                            byte[] data4 = RequestBuilder
                                    .createSrvTransfereData(
                                            RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                            loginResponse.srvID,
                                            RequestBuilder.DEFAULT_DESTINATION_ID,
                                            loginResponse.GUID, true, body4);
                            ConnectionHelper.getInstance().send(data4);

                            byte[] body5 = RequestBuilder
                                    .createBodyGetDriverState(loginResponse.peopleID);
                            byte[] data5 = RequestBuilder
                                    .createSrvTransfereData(
                                            RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                            loginResponse.srvID,
                                            RequestBuilder.DEFAULT_DESTINATION_ID,
                                            loginResponse.GUID, true, body5);
                            ConnectionHelper.getInstance().send(data5);
                            /**
                             *  @createBodyCarOnAddressRequest - вызывается когда
                             *  водитель посылает запрос "Выводить пассажиров"
                             *  @createBodyPPSOrderCancelRequest -  Запрос от
                             *  водителя на отмену заказа
                             *  @createBodyOrderCancel О - тмена заказа
                             *  @createBodyPickingUpCharge - Подача оплачена
                             *  @createMoveBackInQueue - Пакет Уступить очередь
                             *  @createDriverWaiting - Уведомление диспетчеру о том,
                             *  что водитель ожидает минут
                             *
                             *  @createSignPreliminaryOrder -
                             *  @createDriverDelay - Уведомление диспетчеру о том,
                             *  что водитель опаздывает на Delay минут
                             *  @createPassengerOut - если ожидание пассажира уже
                             *  более 10 минут, дисп он звонит пассажиру.
                             *  @PPCUnRegisterOnRelay - Снятие со смены
                             *
                             * */
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.server_working);
                                                StateObserver
                                                        .getInstance()
                                                        .setServer(
                                                                StateObserver.WORK);
                                                // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.btn_do_my_order).setVisibility(View.VISIBLE);
                                                // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.button_1).setVisibility(View.VISIBLE);
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                        } else if (loginResponse.answer.equals("NoRights")) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk(
                                                            "Нет прав. Проверьте, правильно ли введёны данные в полях Логин, Позывный, Пароль");
                                            // ConnectionHelper.getInstance().stop();
                                        }
                                    });

                        } else if (loginResponse.answer.equals("error2")) {

                        } else {
                            Log.d(LOG_TAG, "Error login");
                        }
                        if (!loginResponse.answer.equals(LoginResponse.ANSWER_OK)) {
                            // ConnectionHelper.getInstance().stop();
                            disconnect();
                            // ContextHelper.getInstance().getCurrentActivity().stopService(new
                            // Intent(ContextHelper.getInstance().getCurrentActivity(),SocketService.class));
                        }
                    }
                });

        ConnectionHelper.getInstance().setErrorListener(
                new OnNetworkErrorListener() {
                    @Override
                    public void onNetworkError(final int errorCode,
                                               final String errorMessage) {
                        Log.d(LOG_TAG, "onNetworkError[code=" + errorCode
                                + ", message=" + errorMessage + "]");

                        ContextHelper.getInstance().runOnCurrentUIThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (errorCode == 1) {
                                            // AlertDHelper.showDialogOk("Не удалось определить узел "
                                            // + "\n" + errorMessage);
                                        } else if (errorCode == 2) {
                                            // AlertDHelper.showDialogOk("Ошибка соединения с сервером"
                                            // + "\n" + errorMessage);
                                        } else if (errorCode == 3) {
                                            // AlertDHelper.showDialogOk("Ошибка пакета"
                                            // + "\n" + errorMessage);
                                        } else {
                                            // AlertDHelper.showDialogOk("Неопознанная ошибка"
                                            // + "\n" + errorMessage);
                                        }

                                        ConnectionHelper.getInstance().stop();
                                        PingHelper.getInstance().stop();
                                        play.play(R.raw.msg_stat_neg);
                                        // ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.no_server_working);
                                        StateObserver.getInstance().setServer(
                                                StateObserver.NO_WORK);
                                    }
                                });
                    }
                });
        /** 2 */
        MultiPacketListener.getInstance().addListener(
                Packet.RELAY_COMMUNICATION_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        RelayCommunicationResponce pack = (RelayCommunicationResponce) packet;
                        Log.d(LOG_TAG, "RELAY_COMMUNICATION_RESPONCE goted " + pack.toString()); // ok
                        isCancelProgressDialog = true;
                        Log.d(LOG_TAG, "RELAY_COMMUNICATION_RESPONCE = " + pack.getRelayAnswerType());

                        if (pack.getRelayAnswerType().equals("Registered")) {
                            // added a line
                            sharedPrefs.edit().putInt("currentRelayID", pack.getRelayID());
                            Log.d(LOG_TAG, "RELAY_COMMUNICATION_RESPONCE Registered on relay");

                            if (ServerData.getInstance().isShowAlertRegistr())
                                play.play(R.raw.msg_stat_pos);

                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if (ServerData.getInstance()
                                                    .isShowAlertRegistr())
                                                AlertDHelper
                                                        .showDialogOk("Вы зарегистрированы на смене");
											/*
											 * else
											 * Toast.makeText(ContextHelper.
											 * getInstance
											 * ().getCurrentContext(),
											 * "Вы зарегистрированы на смене"
											 * ,Toast.LENGTH_LONG).show();
											 */
                                            new Thread(RunPingAndGeo
                                                    .getInstance()).start();

                                            RequestHelper
                                                    .versionDeviceSoftware();
                                            RequestHelper.getOrders();
                                            RequestHelper.requestAir();

                                            ContextHelper.getInstance()
                                                    .runOnCurrentUIThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    // TextView
                                                                    // state_driver
                                                                    // =
                                                                    // (TextView)
                                                                    // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                                                    // //
                                                                    // state_driver.setText("СВОБОДЕН");
                                                                    // state_driver.setTextColor(Color.parseColor("#009900"));
                                                                    StateObserver
                                                                            .getInstance()
                                                                            .setDriverState(
                                                                                    StateObserver.DRIVER_FREE);

																	/*TextView busy2 = (TextView) ContextHelper
																			.getInstance()
																			.getCurrentActivity()
																			.findViewById(
																					R.id.btn_busy);
																	busy2.setText("Занят");*/

                                                                    ServerData
                                                                            .getInstance().isFree = true;

                                                                }
                                                            });
                                        }
                                    });

                        } else if (pack.getRelayAnswerType().equals("UnRegistered")) {
                            Log.d(LOG_TAG, "RELAY_COMMUNICATION_RESPONCE Unregistered on relay");
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            OrderManager.getInstance().clearAllOrders();
                                            if (!isExit)
                                                AlertDHelper.showDialogOk("Вы сняты со смены");
                                            RunPingAndGeo.getInstance().stop();
                                            disconnect();
                                            if (isExit) {
                                                ContextHelper.getInstance()
                                                        .getCurrentActivity()
                                                        .finish();
                                            }
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("CannotRegister")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Вы уже зарегистрированы на смене");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("PeopleNotFound")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Сотрудник не найден");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("InvalidCallSign")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Извините, вы не можете работать под данным позывным");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("DriverIsBlocked")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Вы заблокированы на сервере");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("CallSignAlreadyUsed")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Позывной уже используется другим водителем");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("NegativeBalance")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Отрицательный баланс");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("DataBaseError")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Ошибка на сервере");
                                            disconnect();
                                        }
                                    });
                        } else if (pack.getRelayAnswerType().equals("SubscriptionIsNotActive")) {
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDHelper
                                                    .showDialogOk("Подписка не активна");
                                            disconnect();
                                        }
                                    });
                        }
                    }
                });

        /** 3 */
        MultiPacketListener.getInstance().addListener(
                Packet.SETTINGS_XML_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        SettingXmlResponse pack = (SettingXmlResponse) packet;
                        Log.d(LOG_TAG, "SETTINGS_XML_RESPONCE goted SETTINGS_XML_RESPONCE "
                                + pack.getSettings()); // ok
                        if (pack.getSettings() == null) {
                            Log.d(LOG_TAG, "SETTINGS_XML_RESPONCE Settings error");
                            xmlOk = true;
                        } else Log.d(LOG_TAG, "SETTINGS_XML_RESPONCE Settings ok");

                        SettingsFromXml.getInstance().setSettingsFromXml(
                                pack.getSettings());

                        if (!SettingsFromXml.getInstance().isBalanceDrivers()) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            // balanceRefresh.setOnClickListener(null);
                                            // balanceRefresh.setVisibility(View.GONE);
//                                            tvBalance.setVisibility(View.GONE);

                                        }
                                    });
                        }

                        Log.d(LOG_TAG,
                                "SETTINGS_XML_RESPONCE Own_orders = "
                                        + SettingsFromXml.getInstance().isShowYourOrders());

                        if (!SettingsFromXml.getInstance().isShowYourOrders()) {
                            Log.d(LOG_TAG, "SETTINGS_XML_RESPONCE Own_orders = false");
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
//                                            myOwnOrder.setOnClickListener(null);
//                                            myOwnOrder.setVisibility(View.GONE);
//                                            StateObserver.getInstance()
//                                                    .setShowYourOrders(false);
                                        }
                                    });
                        } else {
                            Log.d(LOG_TAG, "SETTINGS_XML_RESPONCE Own_orders = true");
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            myOwnOrder
                                                    .setVisibility(View.VISIBLE);
                                            StateObserver.getInstance()
                                                    .setShowYourOrders(true);
                                        }
                                    });
                        }

                        if (!SettingsFromXml.getInstance().isUseParkings()) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
//                                            parkings.setOnClickListener(null);
//                                            parkings.setVisibility(View.GONE);
//                                            StateObserver.getInstance()
//                                                    .setShowParkings(false);
                                        }
                                    });
                        } else {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            parkings.setVisibility(View.VISIBLE);
                                            // first.findViewById(R.id.parkingsImage).setVisibility(View.VISIBLE);
                                            StateObserver.getInstance()
                                                    .setShowParkings(true);
                                        }
                                    });
                        }
                    }
                });

        /** 4 */
        MultiPacketListener.getInstance().addListener(
                Packet.SET_YOUR_ORDERS_ANSWER, new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        SetYourOrdersAnswer pack = (SetYourOrdersAnswer) packet;
                        Log.d(LOG_TAG, "SET_YOUR_ORDERS_ANSWER goted" + pack.driverState); // ok
                        play.play(R.raw.msg_warn);

                        if (pack.driverState.equals("Other")) {
                            ServerData.getInstance().doOwn = true;
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            myOwnOrder
                                                    .setText("Выполняю свой заказ");
                                            myOwnOrder
                                                    .setTextColor(getResources()
                                                            .getColor(
                                                                    R.color.orangeEditText));
                                            // TextView state_driver =
                                            // (TextView)
                                            // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                            // state_driver.setText("ЗАНЯТ");
                                            // state_driver.setTextColor(Color.RED);
                                            StateObserver
                                                    .getInstance()
                                                    .setDriverState(
                                                            StateObserver.DRIVER_BUSY);

                                            TextView busy2 = (TextView) ContextHelper
                                                    .getInstance()
                                                    .getCurrentActivity()
                                                    .findViewById(R.id.btn_busy);
                                            busy2.setEnabled(false);

                                            // ServerData.getInstance().isFree=;
                                        }
                                    });
                        } else if (pack.driverState.equals("Free")) {
                            ServerData.getInstance().doOwn = false;
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            myOwnOrder.setText("Cвой заказ");
                                            myOwnOrder.setTextColor(getResources()
                                                    .getColor(android.R.color.white));
                                            // TextView state_driver =
                                            // (TextView)
                                            // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                            // state_driver.setText("СВОБОДЕН");
                                            // state_driver.setTextColor(Color.parseColor("#009900"));
                                            StateObserver
                                                    .getInstance()
                                                    .setDriverState(
                                                            StateObserver.DRIVER_FREE);
                                            TextView busy2 = (TextView) ContextHelper
                                                    .getInstance()
                                                    .getCurrentActivity()
                                                    .findViewById(R.id.btn_busy);
                                            busy2.setEnabled(true);

                                            byte[] body = RequestBuilder
                                                    .createGetBalanceData(ServerData
                                                            .getInstance()
                                                            .getPeopleID());
                                            byte[] data = RequestBuilder
                                                    .createSrvTransfereData(
                                                            RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                                            ServerData
                                                                    .getInstance()
                                                                    .getSrvID(),
                                                            RequestBuilder.DEFAULT_DESTINATION_ID,
                                                            ServerData
                                                                    .getInstance()
                                                                    .getGuid(),
                                                            true, body);
                                            ConnectionHelper.getInstance()
                                                    .send(data);
                                        }
                                    });
                        }

                    }
                });

        /** 5 */
        //check ping response from server
        MultiPacketListener.getInstance().addListener(
                Packet.PING_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        Log.d(LOG_TAG, "PING_RESPONCE Test ping response.");
                    }
                });

        /** 6 */
        // вычитываем и обновляем запросы после подключения
        MultiPacketListener.getInstance().addListener(
                Packet.GET_ORDERS_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        final GetOrdersResponse pack = (GetOrdersResponse) packet;
                        Log.i(LOG_TAG, "GET_ORDERS_RESPONCE goted" + pack.count()); // ok
                        updateMyOrders(pack);
                        if (pack.count() > 0 && FragmentTransactionManager.getInstance()
                                .getId() != FragmentPacket.CURRENTORDERS) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            // 333
                                            int ordersAmount = pack.count();
                                            ordersAmount = OrderManager
                                                    .getInstance()
                                                    .getCountOfOrdersByState(
                                                            Order.STATE_PERFORMING);
                                            if (ordersAmount > 0) {
                                                mAlertText = "У вас "
                                                        + ordersAmount;
                                                if (ordersAmount == 1) {
                                                    mAlertText += " текущий заказ.";
                                                } else if (ordersAmount > 1
                                                        && ordersAmount < 5) {
                                                    mAlertText += " текущих заказа.";
                                                } else {
                                                    mAlertText += " текущих заказов.";
                                                }
                                                mAlertText += " Вы можете работать с ними через меню 'Заказ' -> 'Текущие'";
                                                AlertDHelper
                                                        .showDialogOk(mAlertText);
                                            }
                                        }
                                    });
                        }
                        // updateMyOrders(pack);
                    }
                });

        /** 7 */
        /** Getting distance packet from server */
        MultiPacketListener.getInstance().addListener(
                Packet.DISTANCE_ORDER_ANSWER_RESPONSE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        DistanceOfOrderAnswer pack = (DistanceOfOrderAnswer) packet;
                        if (pack.distance != 0) {
                            OrderManager.getInstance().getOrder(pack.orderID)
                                    .setDistanceToOrderPlace(pack.getDistance());
                        }
                    }
                });

        /** 8 */
        // блокировка водителя
        MultiPacketListener.getInstance().addListener(
                Packet.DRIVER_BLOCKED_PACK,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        final DriverBlockedPack pack = (DriverBlockedPack) packet;

                        Log.d(LOG_TAG,"goted DRIVER_BLOCKED_PACK " + pack.lockWith + " " + pack.lockTo); // ok

                        disconnect();

                        ContextHelper.getInstance().runOnCurrentUIThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mAlertText = "Водитель заблокирован";

                                        if (pack.isForever) {
                                            mAlertText += " навсегда";
                                        } else {
                                            mAlertText += " по "
                                                    + Utils.dateToTimeString(pack.lockTo)
                                                    + " "
                                                    + Utils.dateToDateString(pack.lockTo);
                                        }
                                        mAlertText += " по причине " + pack.lockDescription + ".";
                                        AlertDHelper.showDialogOk(mAlertText);
                                    }
                                });
                    }
                });

        /** 9 */
        MultiPacketListener.getInstance().addListener(
                Packet.PPSCHANGE_STATE_RESPONCE, new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        final PPSChangeStateResponse pack = (PPSChangeStateResponse) packet;

                        Log.d(LOG_TAG, "goted PPSCHANGE_STATE_RESPONCE orderid - "
                                + pack.orderID + " state " + pack.state);

                        // ContextHelper.getInstance().runOnCurrentUIThread(new
                        // Runnable() {
                        // @Override
                        // public void run() {
                        // Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
                        // "state ("
                        // + pack.state + ")", Toast.LENGTH_LONG).show();
                        // }
                        // });

                        if ((pack.state.equals("Break") || (pack.state
                                .equals("GetOrder")))) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            // TextView state_driver =
                                            // (TextView)
                                            // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                            // state_driver.setText("ЗАНЯТ");
                                            // state_driver.setTextColor(Color.RED);
                                            StateObserver
                                                    .getInstance()
                                                    .setDriverState(
                                                            StateObserver.DRIVER_BUSY);
                                            TextView busy2 = (TextView) ContextHelper
                                                    .getInstance()
                                                    .getCurrentActivity()
                                                    .findViewById(R.id.btn_busy);
                                            if (pack.state.equals("GetOrder")) {
                                                busy2.setEnabled(false);
                                            } else if (pack.state
                                                    .equals("Break")) {
                                                busy2.setText("СВОБОДЕН");
                                            }

                                            ServerData.getInstance().isFree = false;
                                        }
                                    });
                        } else if (pack.state.equals("Free")) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (OrderManager
                                                        .getInstance()
                                                        .getCountOfOrdersByState(
                                                                Order.STATE_PERFORMING) >= 1) {
													/*
													 * if
													 * (OrderManager.getInstance
													 * (
													 * ).getOrder(pack.orderID).
													 * getStatus() !=
													 * Order.STATE_PERFORMED)
													 * OrderManager
													 * .getInstance()
													 * .changeOrderState
													 * (pack.orderID,
													 * Order.STATE_CANCELLED);
													 */
                                                    if (pack.orderID > 0) {
                                                        FragmentTransactionManager
                                                                .getInstance()
                                                                .openFragment(
                                                                        FragmentPacket.ORDER_DETAILS);
                                                        OrderDetails.btnCancel
                                                                .setEnabled(false);
                                                        OrderDetails.btnDo
                                                                .setEnabled(false);
                                                        OrderDetails.btnArrived
                                                                .setEnabled(false);
                                                    }
                                                    // AlertDHelper.showDialogOk("Заказ снят диспетчером");
                                                }
                                                if (OrderManager
                                                        .getInstance()
                                                        .getCountOfOrdersByState(
                                                                Order.STATE_PERFORMING) < 1) {
                                                    // OrderDetails.btnAccept.setEnabled(false);//свежие
                                                    // правки
                                                    // TextView state_driver =
                                                    // (TextView)
                                                    // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                                    // state_driver.setText("СВОБОДЕН");
                                                    // state_driver.setTextColor(Color.parseColor("#009900"));
                                                    StateObserver
                                                            .getInstance()
                                                            .setDriverState(
                                                                    StateObserver.DRIVER_FREE);
                                                    TextView busy2 = (TextView) ContextHelper
                                                            .getInstance()
                                                            .getCurrentActivity()
                                                            .findViewById(
                                                                    R.id.btn_busy);
                                                    busy2.setEnabled(true);
                                                    busy2.setText("Занят");
                                                    ServerData.getInstance().isFree = true;
                                                    if (ServerData
                                                            .getInstance().doOwn) {
                                                        // state_driver.setText("ЗАНЯТ");
                                                        // state_driver.setTextColor(Color.RED);
                                                        StateObserver
                                                                .getInstance()
                                                                .setDriverState(
                                                                        StateObserver.DRIVER_BUSY);
                                                        busy2.setEnabled(false);
                                                    }
                                                    // else{
                                                    // }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        } else if (pack.state.equals("Other")) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                            ServerData.getInstance().doOwn = true;

                                            myOwnOrder
                                                    .setText("Выполняю свой заказ");
                                            myOwnOrder
                                                    .setTextColor(getResources()
                                                            .getColor(
                                                                    R.color.orangeEditText));

                                            // TextView state_driver =
                                            // (TextView)
                                            // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                            // state_driver.setText("ЗАНЯТ");
                                            // state_driver.setTextColor(Color.RED);
                                            StateObserver
                                                    .getInstance()
                                                    .setDriverState(
                                                            StateObserver.DRIVER_BUSY);
                                            TextView busy2 = (TextView) ContextHelper
                                                    .getInstance()
                                                    .getCurrentActivity()
                                                    .findViewById(R.id.btn_busy);
                                            // busy2.setText("СВОБОДЕН");
                                            busy2.setEnabled(false);

                                            ServerData.getInstance().doOwn = true;
                                        }
                                    });
                        } else if (pack.state.equals("DoOrder")) {
                            try {
                                if (OrderManager.getInstance()
                                        .getOrder(pack.orderID).getFolder()
                                        .equals("Направленный")) {
                                    OrderManager.getInstance()
                                            .changeOrderFolder(pack.orderID,
                                                    Order.FOLDER_DOING);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // play.play(R.raw.msg_stat_pos);
                        byte[] body = RequestBuilder
                                .createGetBalanceData(ServerData.getInstance()
                                        .getPeopleID());
                        byte[] data = RequestBuilder.createSrvTransfereData(
                                RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                ServerData.getInstance().getSrvID(),
                                RequestBuilder.DEFAULT_DESTINATION_ID,
                                ServerData.getInstance().getGuid(), true, body);
                        ConnectionHelper.getInstance().send(data);
                    }
                });

        /** 10 */
        MultiPacketListener.getInstance().addListener(
                Packet.SRV_MESSAGE_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        SrvMessageResponce pack = (SrvMessageResponce) packet;

                        Log.d(LOG_TAG, "goted SRV_MESSAGE_RESPONCE "
                                + pack.getMessage());
                    }
                });

        /** 11 */
        MultiPacketListener.getInstance().addListener(
                Packet.CSBALANCE_RESPONCE, new OnNetworkPacketListener() {

                    GetCSBalanceResponce getBalance;

                    @Override
                    public void onNetworkPacket(Packet packet) {
                        getBalance = (GetCSBalanceResponce) packet;
                        UIData.getInstance().setBalance(getBalance.getSum());
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                tvBalance.setText("Баланс: " + getBalance.getSum() + " грн.");
                            }
                        };

                        ContextHelper.getInstance().runOnCurrentUIThread(r);
                        Log.d("KVEST_TAG", getBalance.getSum());
                        // MultiPacketListener.getInstance().removeListeners(Packet.CSBALANCE_RESPONCE);
                    }
                });

        /** 12 */
        MultiPacketListener.getInstance().addListener(
                Packet.ETHEAR_ORDER_OVER_ANSWER, new OnNetworkPacketListener() {

                    @Override
                    public void onNetworkPacket(Packet packet) {
                        final SearchInEtherOrderOverResponse pack = (SearchInEtherOrderOverResponse) packet;

                        Log.i(LOG_TAG, "goted SearchInEtherOrderOverResponse " + pack.orderID);

                        ContextHelper.getInstance().runOnCurrentUIThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        int count = 0;
                                        int index = -1;
                                        for (DispOrder4 dispord : mOrders) {
                                            if (dispord.orderID == pack.orderID) {
                                                Log.w("ETHER ORDER ANSWER",
                                                        String.valueOf(pack.orderID));
                                                index = count;
                                                // mOrders.remove(dispord);
                                                break;
                                            }
                                            count++;
                                        }
                                        if (index > -1)
                                            mOrders.remove(index);

                                        mAdapter.updateMyList(mOrders);
                                        /** switchListView(mAdapter); */

                                        orderManager.changeOrderState(pack.orderID, Order.STATE_MISSED);

                                        mAdapter.updateMyList(mOrders);
                                        /** switchListView(mAdapter); */

                                        if (sharedPrefs.getBoolean(
                                                "prefIsAutoSearch", false)) // autosearch
                                            // 1
                                            // 2
                                            SwipeFragment.ethear.setText("ЭФИР("
                                                    + String.valueOf(orderManager
                                                    .getCountOfOrdersByState(Order.STATE_NEW)
                                                    + orderManager
                                                    .getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                    + ")");
                                        else
                                            SwipeFragment.ethear.setText("ЭФИР("
                                                    + orderManager
                                                    .getCountOfEfirOrders()
                                                    + ")");

                                        if (EfirOrder.orderId == pack.orderID) {
                                            EfirOrder.startTimer(0);
                                        }
                                    }
                                });
                    }

                });

    }//End addPacketListeners()

    protected void updateMyOrders(GetOrdersResponse pack) {
        for (int i = 0; i < pack.count(); i++) {
            Order order = new Order(pack.getOrder(i));

            String folder = order.getFolder();
            if ((folder.equals("НаОформлении"))
                    || (folder.equals("Направленный"))
                    || (folder.equals("ReceiveDriver"))
                    || (folder.equals(Order.FOLDER_DOING))
                    || (folder.equals(Order.FOLDER_RECEIVED))) {

                ContextHelper.getInstance().runOnCurrentUIThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                // TextView state_driver = (TextView)
                                // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
                                // state_driver.setText("ЗАНЯТ");
                                // state_driver.setTextColor(Color.RED);
                                StateObserver.getInstance().setDriverState(
                                        StateObserver.DRIVER_BUSY);
                                TextView busy2 = (TextView) ContextHelper
                                        .getInstance().getCurrentActivity()
                                        .findViewById(R.id.btn_busy);
                                busy2.setEnabled(false);

                                ServerData.getInstance().isFree = false;
                            }
                        });
                order.setStatus(Order.STATE_PERFORMING);

            }

            System.out.println("!!!!!!!!!!!!!!! " + folder);

            order.setFromServer(true);

            OrderManager.getInstance().addOrder(order);

            /** Request to server for distance packet order.getOrderID()*/
            byte[] body = RequestBuilder.getDistanceOfOrderAnswer(order.getOrderID());
            byte[] data = RequestBuilder.createSrvTransfereData(
                    RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
                            .getInstance().getSrvID(),
                    RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
                            .getInstance().getGuid(), true, body);
            ConnectionHelper.getInstance().send(data);
        }

    }

    private void exitClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ContextHelper.getInstance().getCurrentContext());

        builder.setMessage(getResources().getString(R.string.str_do_you_want_to_exit))
                .setTitle(getResources().getString(R.string.str_notif))
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (ConnectionHelper.getInstance()
                                        .isConnected()) {

                                    try {
                                        byte[] body2 = RequestBuilder
                                                .createPPCUnRegisterOnRelay(
                                                        true, "8",
                                                        loginResponse.peopleID);
                                        byte[] data2 = RequestBuilder
                                                .createSrvTransfereData(
                                                        RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                                        loginResponse.srvID,
                                                        RequestBuilder.DEFAULT_DESTINATION_ID,
                                                        loginResponse.GUID, true,
                                                        body2);
                                        ConnectionHelper.getInstance()
                                                .send(data2);
                                    } catch (Exception e) {e.printStackTrace();}
                                }
                                isExit = true;
                                ContextHelper.getInstance()
                                        .getCurrentActivity()
                                        .stopService(new Intent(ContextHelper
                                                .getInstance()
                                                .getCurrentActivity(),
                                                SocketService.class));
                                ContextHelper.getInstance()
                                        .getCurrentActivity()
                                        .finish();
                                System.exit(0);
                            }
                        });

        builder.setNegativeButton(getResources().getString(R.string.no),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }// end exitClick()

    private void busyClick() {
        if (ConnectionHelper.getInstance().isConnected()
                && !ServerData.getInstance().doOwn) {
            ServerData sData = ServerData.getInstance();
            if (sData.isFree) {
                System.out.println("send btnBusy");
                byte[] body2 = RequestBuilder.createBodyPPSChangeState(
                        "3", 0, loginResponse.peopleID);
                byte[] data2 = RequestBuilder.createSrvTransfereData(
                        RequestBuilder.DEFAULT_CONNECTION_TYPE,
                        loginResponse.srvID,
                        RequestBuilder.DEFAULT_DESTINATION_ID, loginResponse.GUID,
                        true, body2);
                ConnectionHelper.getInstance().send(data2);
            } else {
                System.out.println("send free");

                byte[] body2 = RequestBuilder.createBodyPPSChangeState(
                        "0", 0, loginResponse.peopleID);
                byte[] data2 = RequestBuilder.createSrvTransfereData(
                        RequestBuilder.DEFAULT_CONNECTION_TYPE,
                        loginResponse.srvID,
                        RequestBuilder.DEFAULT_DESTINATION_ID, loginResponse.GUID,
                        true, body2);
                ConnectionHelper.getInstance().send(data2);
            }
        } else {
            System.out.println("NO");
        }
    }//end busyClick()

    private void connect(final String host, final int port, final String login,
                         final String pass, final String nick) {

        Log.d(LOG_TAG, "2510 connect()");

        getActivity().stopService(new Intent(getActivity(), SocketService.class));
        ServerData.getInstance().setIp(host);
        ServerData.getInstance().setPort(port);

        final ProgressDialog progress =
                new ProgressDialog(ContextHelper.getInstance().getCurrentContext());
        progress.setTitle("Уведомление");
        progress.setMessage("Подключение к серверу");
        progress.setCancelable(false);
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean quit = false;
                for (int i = 0; i < 100; i++) {
                    if (isCancelProgressDialog) {
                        progress.dismiss();
                        return;
                    }
                    try {
                        Thread.sleep(100);
                        if (!ConnectionHelper.getInstance().isDisconnected()
                                && ConnectionHelper.getInstance().isConnected()) {
                            quit = true;
                            break;
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Log.d(LOG_TAG, "InterruptedException " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                progress.dismiss();
                if (!quit) {
                    ContextHelper.getInstance().runOnCurrentUIThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // ConnectionHelper.getInstance().stop();
                                    disconnect();
                                    Log.d(LOG_TAG,
                                            "не удаётся подключиться к серверу");
                                    AlertDHelper
                                            .showDialogOk("Не удается подключиться к серверу.");
                                    ServerData.getInstance().setMasterServer(
                                            !ServerData.getInstance()
                                                    .isMasterServer());
                                }
                            });
                }
                System.out.println("quit ok");
            }
        }).start();

        ConnectionHelper.getInstance().setDisconnected(false);
        ServerData.getInstance().setLogin(login);
        ServerData.getInstance().setPass(pass);
        ServerData.getInstance().setNick(nick);

        Log.d(LOG_TAG, "2574 стартуем сервис");

        getActivity().startService(new Intent(getActivity(), SocketService.class)
                .putExtra("host", host).putExtra("port", port));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Log.d(LOG_TAG, "2584 отправляем логин");

                // тут происходит коннект на сервак и отправка логина и пароля.
                ServerData.getInstance().setShowAlertRegistr(true);
                ConnectionHelper.getInstance().send(RequestBuilder
                        .createLogin(255, login, pass, true));

                Log.d(LOG_TAG, "2591" + "\n" + "login " + login + "\n" +"pass " + pass);

            }
        }, 1300l);

    } //END connect()

    private void myOwnOrdersClick() {
        if (ConnectionHelper.getInstance().isConnected()) {
            if (OrderManager.getInstance()
                    .getCountOfOrdersByState(Order.STATE_PERFORMING) == 0) {
                builder = new AlertDialog.Builder(ContextHelper
                        .getInstance().getCurrentContext());

                // перейти в состояние свои пассажиры. выйти из
                // состояния свои пассажиры?
                builder.setMessage(!ServerData.getInstance().doOwn ? "Перейти в состояние \"Свои пассажиры?\""
                        : "Выйти из состояния \"Свои пассажиры?\"");
                builder.setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                byte[] body = RequestBuilder
                                        .createBodySetYourOrders(ServerData
                                                .getInstance()
                                                .getPeopleID());
                                byte[] data = RequestBuilder
                                        .createSrvTransfereData(
                                                RequestBuilder.DEFAULT_CONNECTION_TYPE,
                                                ServerData
                                                        .getInstance()
                                                        .getSrvID(),
                                                RequestBuilder.DEFAULT_DESTINATION_ID,
                                                ServerData
                                                        .getInstance()
                                                        .getGuid(),
                                                true, body);
                                ConnectionHelper.getInstance().send(
                                        data);
                            }
                        });
                builder.setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();
            } else {
                AlertDHelper.showDialogOk("У вас есть текущие заказы!");
            }

        } else {
            AlertDHelper.showDialogOk("Вы не подключены к серверу!");
        }
    }// end myOwnOrdersClick()

    private void disconnect() {
        StateObserver.getInstance().setServer(StateObserver.NO_WORK);
        ConnectionHelper.getInstance().stop();
        StateObserver.getInstance().setDriverState(StateObserver.DRIVER_NO_CONNECT);
    }
}
