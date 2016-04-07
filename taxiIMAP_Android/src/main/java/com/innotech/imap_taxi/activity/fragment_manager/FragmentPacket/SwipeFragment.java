package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innotech.imap_taxi.activity.Main_taxometr;
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
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkErrorListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.SocketService;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi.network.packet.CallSignChangedResponce;
import com.innotech.imap_taxi.network.packet.DispOrderResponse4;
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
import com.innotech.imap_taxi.network.packet.TCPMessageResponce;
import com.innotech.imap_taxi.ping.PingHelper;
import com.innotech.imap_taxi.ping.PingTask;
import com.innotech.imap_taxi.ping.RunPingAndGeo;
import com.innotech.imap_taxi.utile.AlertDHelper;
import com.innotech.imap_taxi.utile.NotificationService;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi.utile.VerticalSeekBar;
import com.innotech.imap_taxi3.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SwipeFragment extends FragmentPacket
        implements OnTouchListener, OnClickListener {
    public static final String  LOG_TAG = SwipeFragment.class.getSimpleName();
    private static final int RESULT_SETTINGS = 1;
    public static boolean reCon = false;
    private boolean isExit;
    private boolean isCancelProgressDialog;
    protected boolean xmlOk = false;
    static public boolean open = false;
    private boolean isOrderYours = false;
    public static Button connection;
    static public Button ethear;
    static public Button myOwnOrder;
    static public Button parkings;
    Button btnBusy;
    Button btnOrder;
    Button btnCloseConnection;
    Button btnPrefs;
    Button btnTaxoMetr;
    Button btnMap;
    Button btnOrders;
    Button btnZoomIn;
    Button btnPrelim;
    Button btnZoomOut;
    Button btnTest;
    Button btnSendCrash;
    ToggleButton toggleBtnHide;
    TextView tvBalance;
    TextView tvVersion;
    TextView etherTxt;
    private String mAlertText;
    View view;
    View first = null;
    View second = null;
    View  vMap = null;
    private PlaySound play;
    private LoginResponse loginResponse;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    private GoogleMap mMap;
    MarkerOptions myLoMmarker;
    boolean updateMyLoc;
    ArrayList<LatLng> lg;
    ListView mapEther;
    LinearLayout llNoEther;
    LinearLayout llNoEtherSecond;
    OrdersAdapterDisp4 mAdapter;
    VerticalSeekBar zoomBar;
    List<DispOrder4> mOrders;
    SharedPreferences sharedPrefs;
    OrderManager orderManager;
    private ListView lvOrders;
    Context mContext;
    int i;
    private Button back, refresh;
    ServerData sData;
    Boolean etherIsVisible;
    AlertDialog alertDialog;
    float lat, lon;
    LatLng myLoc;

    public SwipeFragment() {
        super(SWIPE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()" + "\n");
        mContext = ContextHelper.getInstance().getCurrentContext();
        play = PlaySound.getInstance();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");

        view = inflater.inflate(R.layout.activity_swipe, container, false);

        mOrders = new ArrayList<DispOrder4>();
        mAdapter = new OrdersAdapterDisp4(mOrders, mContext);

       /**
        * Not clear what's happened on this @try block.
        *
        try {
            PackageInfo pInfo;
            pInfo = ContextHelper
                    .getInstance()
                    .getCurrentContext()
                    .getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            UIData.getInstance().setVersion(pInfo.versionName);
        } catch (NameNotFoundException e) {e.printStackTrace();}
        */

        addListeners();

        // via view
        List<View> pages = new ArrayList<View>();

        first = inflater.inflate(R.layout.main_menu_chapter_one_new, null);
        second = inflater.inflate(R.layout.main_menu_chapter_two_new, null);

        etherTxt = (TextView) second.findViewById(R.id.noEther);
        Typeface t = Typeface.createFromAsset(ContextHelper.getInstance()
                .getCurrentContext().getAssets(), "fonts/BebasNeueRegular.ttf");
        etherTxt.setTypeface(t);

        if (vMap != null) {
            ((ViewGroup) vMap.getParent()).removeAllViews();
        } else {
            vMap = inflater.inflate(R.layout.map_with_ether_fragment, null);
            mapEther = (ListView) vMap.findViewById(R.id.mapEther);
            mapEther.setAdapter(mAdapter);
            llNoEther = (LinearLayout) vMap.findViewById(R.id.noEtherLayout);
            TextView mapNoEtherTxt = (TextView) vMap.findViewById(R.id.noEther);
            Typeface tf = Typeface.createFromAsset(ContextHelper.getInstance()
                            .getCurrentContext().getAssets(),
                    "fonts/BebasNeueRegular.ttf");
            mapNoEtherTxt.setTypeface(tf);
            switchListView(mAdapter);

            if (mMap == null) {
                // !!!!
                mMap = ((SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.mapWithEther)).getMap();

                mapEther.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int i, long l) {
                        mMap.clear();
                        RequestHelper.getRoutes(mOrders.get(i).orderID);
                    }
                });

                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);

                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(new
                    // LatLng(48.6, 32)));

                    mMap.getUiSettings().setZoomControlsEnabled(false);

                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            zoomBar.setProgressAndThumb((int) cameraPosition.zoom);
                        }
                    });

                    myLoMmarker = new MarkerOptions()
                            .position(new LatLng(0, 0)).title("Это я");
                    myLoMmarker.visible(false);
                    mMap.addMarker(myLoMmarker);
                }
            }
        }

        pages.add(first);
        pages.add(second);
        pages.add(vMap);
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);

        PageIndicator mIndicator = (CirclePageIndicator) view
                .findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

        viewPager.setCurrentItem(1);
        Typeface menuTypeface = Typeface.createFromAsset(ContextHelper
                        .getInstance().getCurrentContext().getAssets(),
                "fonts/BebasNeueRegular.ttf");

        zoomBar = (VerticalSeekBar) vMap.findViewById(R.id.zoom_bar);

        if (mMap != null) {
            zoomBar.setMaximum((int) mMap.getMaxZoomLevel());
            zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            zoomBar.setProgressAndThumb((int) mMap.getCameraPosition().zoom);

        }

        btnZoomIn = (Button) vMap.findViewById(R.id.zoom_in);
        btnZoomOut = (Button) vMap.findViewById(R.id.zoom_out);
        btnZoomIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            }
        });
        btnZoomOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
            }
        });

        toggleBtnHide = (ToggleButton) vMap.findViewById(R.id.hide_ether);
        toggleBtnHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean b) {
                if (b) {
                    mapEther.setVisibility(View.GONE);
                    llNoEther.setVisibility(View.GONE);
                } else if (mAdapter.getCount() > 0) {
                    mapEther.setVisibility(View.VISIBLE);
                } else
                    llNoEther.setVisibility(View.VISIBLE);
            }
        });
        /**
         *
         * Buttons on menu list fragment
         *
         * **/
        btnOrders = (Button) first.findViewById(R.id.btnOrders);
        btnOrders.setTypeface(menuTypeface);
        btnOrders.setOnClickListener(this);

        btnPrelim = (Button) first.findViewById(R.id.btnPrelim);
        btnPrelim.setTypeface(menuTypeface);
        btnPrelim.setOnClickListener(this);

        myOwnOrder = (Button) first.findViewById(R.id.btn_do_my_order);
        myOwnOrder.setTypeface(menuTypeface);
        myOwnOrder.setOnClickListener(this);


        parkings = (Button) first.findViewById(R.id.parkingsButton);
        parkings.setTypeface(menuTypeface);

        //Parkings button
        parkings.setOnClickListener(this);

        btnMap = (Button) first.findViewById(R.id.btnmap);
        btnMap.setTypeface(menuTypeface);
        btnMap.setOnClickListener(this);

        btnBusy = (Button) second.findViewById(R.id.btn_busy);
        btnBusy.setTypeface(menuTypeface);
        btnBusy.setOnClickListener(this);

        btnOrder = (Button) second.findViewById(R.id.btn_order);
        btnOrders.setTypeface(menuTypeface);
        btnOrder.setOnClickListener(this);

        ethear = (Button) second.findViewById(R.id.ethear_button);
        ethear.setTypeface(menuTypeface);
        ethear.setOnClickListener(this);

        tvBalance = (Button) first.findViewById(R.id.balanceButton);
        tvBalance.setTypeface(menuTypeface);
        tvVersion = (TextView) first.findViewById(R.id.version);

        connection = (Button) first.findViewById(R.id.connection);
        connection.setTypeface(menuTypeface);
        //set connection to server on click button.
        connection.setOnClickListener(this);

        if (reCon) {
            connection.performClick();
            reCon = false;
        }

        btnTest = (Button) second.findViewById(R.id.test_test);
        if (ServerData.getInstance().IS_TEST_BUILD)
            btnTest.setOnClickListener(this);
        else
            btnTest.setVisibility(View.GONE);

        // 2

        // version2 = (TextView) second.findViewById(R.id.tvVersion);

        btnCloseConnection = (Button) first.findViewById(R.id.exit);
        btnCloseConnection.setTypeface(menuTypeface);
        btnCloseConnection.setOnClickListener(this);

        btnPrefs = (Button) first.findViewById(R.id.btn_prefs);
        btnPrefs.setTypeface(menuTypeface);
        btnPrefs.setOnClickListener(this);

        btnTaxoMetr = (Button) first.findViewById(R.id.btn_taxometr);
        btnTaxoMetr.setTypeface(menuTypeface);
        btnTaxoMetr.setOnClickListener(this);

        lvOrders = (ListView) second.findViewById(R.id.etherList);
        llNoEtherSecond = (LinearLayout) second.findViewById(R.id.noEtherLayout);
        lvOrders.setAdapter(mAdapter);
        switchListView(mAdapter);
        lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {

                Log.i(LOG_TAG, "Open description " + i + "\n");

                EfirOrder.setOrderId(mOrders.get(i).orderID);
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ORDER);
            }
        });

        final PaintDrawable p = (PaintDrawable) GraphUtils
                .buttonStyle(connection);

        btnSendCrash = (Button) first.findViewById(R.id.sendCrash);
        btnSendCrash.setOnClickListener(this);
        btnSendCrash.setTypeface(menuTypeface);

        // set buttons style

        btnSendCrash.setBackground((Drawable) p);
        connection.setBackground((Drawable) p);
        btnOrders.setBackground((Drawable) p);
        myOwnOrder.setBackground((Drawable) p);
        btnMap.setBackground((Drawable) p);
        ((Button) first.findViewById(R.id.balanceButton))
                .setBackground((Drawable) p);
        btnPrelim.setBackground((Drawable) p);
        btnTaxoMetr.setBackground((Drawable) p);
        parkings.setBackground((Drawable) p);
        btnPrefs.setBackground((Drawable) p);
        btnCloseConnection.setBackground((Drawable) p);

        btnSendCrash.setOnTouchListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart()");

        if (mMap != null) {
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(new LatLng(48.6, 32), 6),
                    100, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume()");

        isExit = false;
        tvBalance.setText(getResources().getString(R.string.str_balance)
                + UIData.getInstance().getBalance()
                + getResources().getString(R.string.str_UA_money));

        tvVersion.setText("v. " + UIData.getInstance().getVersion());
        // version2.setText("v. " + UIData.getInstance().getVersion());
        if (PreferenceManager.getDefaultSharedPreferences(
                ContextHelper.getInstance().getCurrentActivity()).getBoolean(
                "prefIsAutoEnter", false)) {
            if (isServiceRunning()) {
                if (!ConnectionHelper.getInstance().isConnected()) {
                    // ConnectionHelper.getInstance().send(RequestBuilder.createPing());
                    ContextHelper
                            .getInstance()
                            .getCurrentActivity()
                            .stopService(
                                    new Intent(ContextHelper.getInstance()
                                            .getCurrentActivity(),
                                            SocketService.class));
                    //
                    connection.performClick();
                    // ContextHelper.getInstance().getCurrentActivity().startService(new
                    // Intent(ContextHelper.getInstance().getCurrentActivity(),SocketService.class));
                }
            } else {
                connection.performClick();
                // ContextHelper.getInstance().getCurrentActivity().startService(new
                // Intent(ContextHelper.getInstance().getCurrentActivity(),SocketService.class));
            }
            // btnTest
            // connection.performClick();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy()");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof Button) {
            MediaPlayer mMediaPlayer = MediaPlayer.create(ContextHelper.getInstance().getCurrentContext(),
                    R.raw.msg_stat_pos);
            //TODO Fix: on android 5.1 mMediaPleyar random executed.
//            mMediaPlayer.start();
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
        setUserVisibleHint(true);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.d(LOG_TAG, "onViewStateRestored()");

        if (PreferenceManager.getDefaultSharedPreferences(
                ContextHelper.getInstance().getCurrentContext()).getBoolean(
                "prefIsAutoSearch", false)) // autosearch 1 2
            ethear.setText("ЭФИР("
                    + String.valueOf(OrderManager.getInstance()
                    .getCountOfOrdersByState(Order.STATE_NEW)
                    + OrderManager.getInstance()
                    .getCountOfOrdersByState(
                            Order.STATE_KRYG_ADA)) + ")");
        else
            ethear.setText("ЭФИР("
                    + OrderManager.getInstance().getCountOfEfirOrders() + ")");
        if (StateObserver.getInstance().getDriverState() == StateObserver.DRIVER_BUSY) {
            btnBusy.setEnabled(false);
        } else {
            btnBusy.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connection:
                connectionClick();
                break;
            case R.id.btnOrders:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.ORDERS);
                break;
            case R.id.btn_do_my_order:
                myOwnOrdersClick();
                break;
            case R.id.btnmap:
                MapFragment.orderId = -1;
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.MAP);
                break;
            case R.id.btnPrelim:
                 CurrentOrdersFragment
                         .displayOrders(CurrentOrdersFragment.STATE_PRE);
                        FragmentTransactionManager.getInstance()
                                .openFragment(CURRENTORDERS);
                break;
            case  R.id.parkingsButton:
                FragmentTransactionManager.getInstance().openFragment(
                        FragmentPacket.PARKINGS);
                break;
            case R.id.btn_busy:
                busyClick();
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
                testClick();
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
            case R.id.btn_taxometr:
                taxometrClick();
                break;
            case R.id.sendCrash :
                showConfirmToast(false, "Address");
                break;


            default:break;
        }

    }

    public void switchListView(OrdersAdapterDisp4 mAdapter) {
        if (mapEther != null && llNoEther != null && lvOrders != null
                && llNoEtherSecond != null) {
            if (mAdapter.getCount() > 0) {
                mapEther.setVisibility(View.VISIBLE);
                llNoEther.setVisibility(View.GONE);
                lvOrders.setVisibility(View.VISIBLE);
                llNoEtherSecond.setVisibility(View.GONE);
            } else {
                mapEther.setVisibility(View.GONE);
                llNoEther.setVisibility(View.VISIBLE);
                lvOrders.setVisibility(View.GONE);
                llNoEtherSecond.setVisibility(View.VISIBLE);
            }
        }
    }

    private void taxometrClick() {
        LocationManager locationManager;
        locationManager = (LocationManager) ContextHelper.getInstance()
                .getCurrentActivity()
                .getSystemService(Context.LOCATION_SERVICE);

        if (ConnectionHelper.getInstance().isConnected()) {
            if (locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER))
                ContextHelper
                        .getInstance()
                        .getCurrentActivity()
                        .startActivity(
                                new Intent(ContextHelper.getInstance()
                                        .getCurrentContext(),
                                        Main_taxometr.class));
            else
                AlertDHelper
                        .showDialogOk(getResources().getString(R.string.str_use_taxometer_is_not_possible));
        } else
            AlertDHelper
                    .showDialogOk(getResources().getString(R.string.str_use_taxometer_need_to_enter));
    }// end taxometrClick()

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

    private void testClick() {
        // String host = "194.48.212.10";
        String host_debug = "194.0.150.90";
        // int port = 2222;
        int port_debug = 1111;
        String host = "194.48.212.11";
        int port = 6000;
        // String host = "46.118.72.218";
        // int port = 1111;
        connect(host, port, "1", "1", "1");
    } //testClick()

    private void connectionClick() {
        Log.d(LOG_TAG, "601 нажата кнопка 'подключится'");
        if (ConnectionHelper.getInstance().isConnected()) {
                    /*
					 * AlertDHelper.showDialogOk("Повторное подключение запрещено!"
					 * ); return;
					 */
            RunPingAndGeo.getInstance().stop();
            ContextHelper.getInstance().getCurrentContext()
                    .stopService(new Intent(ContextHelper.getInstance()
                                    .getCurrentContext(),SocketService.class));
            try {
                Thread.sleep(500l);
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        if (!PingTask.isDataConnectionAvailable(ContextHelper
                .getInstance().getCurrentContext())) {
            AlertDHelper
                    .showDialogOk("Доступ к интернету отсутствует. Проверьте настройки передачи данных");
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(ContextHelper
                        .getInstance().getCurrentContext());

        String nick = sharedPrefs.getString("prefNick", "");

        String host = ServerData.getInstance().isMasterServer() ? sharedPrefs
                .getString("prefHost", "") : sharedPrefs.getString(
                "prefHostSlave", "").equals("") ? sharedPrefs
                .getString("prefHost", "") : sharedPrefs.getString(
                "prefHostSlave", "");

        String p = ServerData.getInstance().isMasterServer() ? sharedPrefs
                .getString("prefPort", "") : sharedPrefs.getString(
                "prefPortSlave", "").equals("") ? sharedPrefs
                .getString("prefPort", "") : sharedPrefs.getString(
                "prefPortSlave", "");
        // String slaveHost = sharedPrefs.getString("prefHostSlave",
        // "");
        // String slaveP = sharedPrefs.getString("prefPortSlave", "");

        // String host = sharedPrefs.getString("prefHost",
        // "194.0.150.90"); //сборка с тестовым сервером
        // String host = sharedPrefs.getString("prefHost",
        // "192.168.0.28"); //сборка с тестовым сервером во времена
        // краха интернетов
        // String host = sharedPrefs.getString("prefHost",
        // "193.178.228.37"); //сборка Сумы

        // String p = sharedPrefs.getString("prefPort", "15000");
        // //сборка с тестовым сервером
        // String p = sharedPrefs.getString("prefPort", "1111");
        // //сборка Сумы

        String login = sharedPrefs.getString("prefLogin", "");
        String pass = sharedPrefs.getString("prefPass", "");
        String message = "Введите данные подключения к серверу! ";
        if ((nick.equals("")) || (host.equals("")) || (p.equals(""))
                || (login.equals("")) || (pass.equals(""))) {
            message = message.concat("(");
            if (nick.equals(""))
                message = message.concat("Позывной ");
            if (login.equals(""))
                message = message.concat("Логин ");
            if (pass.equals(""))
                message = message.concat("Пароль ");
            if (host.equals(""))
                message = message.concat("Сервер ");
            if (p.equals(""))
                message = message.concat("Порт ");
            message = message.concat(")");
            AlertDHelper.showDialogOk(message);
            Log.d(LOG_TAG, "Введите данные подключения к серверу");

            return;
        }

        int port = 0;
        try {port = Integer.parseInt(p);}
        catch (Exception e) {e.printStackTrace();}

        System.out.println("port " + port);
        Log.w("PORT", String.valueOf(port));
        Log.w("HOST", host);

        connect(host, port, login, pass, nick);
    }// end connectionClick()

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

    private void addListeners() {
        MultiPacketListener.getInstance().addListener(Packet.LOGIN_RESPONCE,
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

                            byte[] body = RequestBuilder
                                    .createBodyRegisterOnRelay(ServerData
                                                    .getInstance().getNick(), true,
                                            loginResponse.peopleID);
                            byte[] data = RequestBuilder
                                    .createSrvTransfereData(
                                            RequestBuilder.DEFAULT_CONNECTION_TYPE,
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
                            // Log.d("tag-tag-tag","sended_getDriverState " +
                            // sended);

                            // createBodyCarOnAddressRequest вызывается когда
                            // водитель посылает запрос "Выводить пассажиров"
                            // createBodyPPSOrderCancelRequest Запрос от
                            // водителя на отмену заказа
                            // createBodyOrderCancel Отмена заказа
                            // createBodyPickingUpCharge Подача оплачена
                            // createMoveBackInQueue Пакет Уступить очередь
                            // createDriverWaiting Уведомление диспетчеру о том,
                            // что водитель ожидает минут
                            //
                            // createSignPreliminaryOrder
                            // createDriverDelay Уведомление диспетчеру о том,
                            // что водитель опаздывает на Delay минут
                            // createPassengerOut если ожидание пассажира уже
                            // более 10 минут, дисп он звонит пассажиру.
                            // PPCUnRegisterOnRelay Снятие со смены

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
                                                    .showDialogOk("Нет прав. Проверьте, правильно ли введёны данные в полях Логин, Позывный, Пароль");
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
                        // Log.w(TAG, "onNetworkError[code=" + errorCode +
                        // ", message=" + errorMessage + "]");

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
        // !!!
        MultiPacketListener.getInstance().addListener(
                Packet.RELAY_COMMUNICATION_RESPONCE,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        RelayCommunicationResponce pack = (RelayCommunicationResponce) packet;

                        Log.i(LOG_TAG, "goted " + pack.toString()); // ok

                        isCancelProgressDialog = true;

                        Log.d(LOG_TAG, "RELAY_RESPONSE = " + pack.getRelayAnswerType());

                        if (pack.getRelayAnswerType().equals("Registered")) {
                            // added a line
                            sharedPrefs.edit().putInt("currentRelayID", pack.getRelayID());

                            Log.d(LOG_TAG, "1089 Registered on relay");

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

                        } else if (pack.getRelayAnswerType().equals(
                                "UnRegistered")) {
                            Log.d(LOG_TAG, "Unregistered on relay");
                            play.play(R.raw.msg_stat_neg);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                            OrderManager.getInstance()
                                                    .clearAllOrders();

                                            if (!isExit) {
                                                AlertDHelper
                                                        .showDialogOk("Вы сняты со смены");
                                            }

                                            RunPingAndGeo.getInstance().stop();

                                            disconnect();
                                            if (isExit) {
                                                ContextHelper.getInstance()
                                                        .getCurrentActivity()
                                                        .finish();
                                            }
                                        }

                                    });
                        } else if (pack.getRelayAnswerType().equals(
                                "CannotRegister")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "PeopleNotFound")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "InvalidCallSign")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "DriverIsBlocked")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "CallSignAlreadyUsed")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "NegativeBalance")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "DataBaseError")) {
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
                        } else if (pack.getRelayAnswerType().equals(
                                "SubscriptionIsNotActive")) {
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

        // MultiPacketListener.getInstance().addListener(Packet.CSBALANCE_RESPONCE,
        // new OnNetworkPacketListener() {
        // @Override
        // public void onNetworkPacket(Packet packet) {
        // GetCSBalanceResponce pack = (GetCSBalanceResponce) packet;
        // Log.i(TAG, "goted " + pack.getSum()); //ok
        // }
        // });

        MultiPacketListener.getInstance().addListener(
                Packet.SETTINGS_XML_RESPONCE, new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        SettingXmlResponse pack = (SettingXmlResponse) packet;
                        Log.i(LOG_TAG,
                                "goted SETTINGS_XML_RESPONCE "
                                        + pack.getSettings()); // ok
                        Log.w(LOG_TAG,
                                "goted SETTINGS_XML_RESPONCE "
                                        + pack.getSettings()); // ok
                        if (pack.getSettings() == null) {
                            Log.d("XML", "Settings error");
                            xmlOk = true;
                        } else
                            Log.d("XML", "Settings ok");
                        SettingsFromXml.getInstance().setSettingsFromXml(
                                pack.getSettings());

                        if (!SettingsFromXml.getInstance().isBalanceDrivers()) {

                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            // balanceRefresh.setOnClickListener(null);
                                            // balanceRefresh.setVisibility(View.GONE);
                                            tvBalance.setVisibility(View.GONE);

                                        }
                                    });

                        }
                        Log.d("myLogs", "Own_orders = "
                                + SettingsFromXml.getInstance()
                                .isShowYourOrders());
                        if (!SettingsFromXml.getInstance().isShowYourOrders()) {
                            Log.d("myLogs", "Own_orders = false");
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            myOwnOrder.setOnClickListener(null);
                                            myOwnOrder.setVisibility(View.GONE);
                                            StateObserver.getInstance()
                                                    .setShowYourOrders(false);
                                        }
                                    });

                        } else {
                            Log.d("myLogs", "Own_orders = true");
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
                                            parkings.setOnClickListener(null);
                                            parkings.setVisibility(View.GONE);
                                            StateObserver.getInstance()
                                                    .setShowParkings(false);
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

        MultiPacketListener.getInstance().addListener(
                Packet.SET_YOUR_ORDERS_ANSWER, new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        SetYourOrdersAnswer pack = (SetYourOrdersAnswer) packet;

                        Log.d(LOG_TAG, "goted SET_YOUR_ORDERS_ANSWER " + pack.driverState); // ok

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
                                            myOwnOrder
                                                    .setTextColor(getResources()
                                                            .getColor(
                                                                    android.R.color.white));
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

        //check ping response from server
        MultiPacketListener.getInstance().addListener(Packet.PING_RESPONCE,
                new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                Log.d(LOG_TAG, "Test ping response.");
            }
        });

        // вычитываем и обновляем запросы после подключения
        MultiPacketListener.getInstance().addListener(
            Packet.GET_ORDERS_RESPONCE, new OnNetworkPacketListener() {
                @Override
                public void onNetworkPacket(Packet packet) {
                    final GetOrdersResponse pack = (GetOrdersResponse) packet;

                    Log.i(LOG_TAG, "1490 goted GET_ORDERS_RESPONCE " + pack.count()); // ok

                    updateMyOrders(pack);
                    if (pack.count() > 0
                            && FragmentTransactionManager.getInstance()
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

        // блокировка водителя
        MultiPacketListener.getInstance().addListener(
            Packet.DRIVER_BLOCKED_PACK, new OnNetworkPacketListener() {
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
                                                    Order.FOLDER_DOIN);
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

        MultiPacketListener.getInstance().addListener(
                Packet.SRV_MESSAGE_RESPONCE, new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        SrvMessageResponce pack = (SrvMessageResponce) packet;

                        Log.d(LOG_TAG, "goted SRV_MESSAGE_RESPONCE "
                                + pack.getMessage());
                    }
                });

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
                                tvBalance.setText("Баланс: "
                                        + getBalance.getSum() + " грн.");
                            }
                        };

                        ContextHelper.getInstance().runOnCurrentUIThread(r);
                        Log.d("KVEST_TAG", getBalance.getSum());
                        // MultiPacketListener.getInstance().removeListeners(Packet.CSBALANCE_RESPONCE);
                    }
                });

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

                                        //mAdapter.notifyDataSetChanged();
                                        mAdapter.updateMyList(mOrders);
                                        switchListView(mAdapter);

										/*System.out.println("state "
												+ orderManager.getOrder(
														pack.orderID)
														.getStatus());*/
                                        orderManager.changeOrderState(
                                                pack.orderID,
                                                Order.STATE_MISSED);

                                        //mAdapter.notifyDataSetChanged();
                                        mAdapter.updateMyList(mOrders);
                                        switchListView(mAdapter);

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

        MultiPacketListener.getInstance().addListener(
                Packet.TCPMESSAGE_RESPONCE, new OnNetworkPacketListener() {

                    @Override
                    public void onNetworkPacket(Packet packet) {
                        final TCPMessageResponce pack = (TCPMessageResponce) packet;

                        Log.i(LOG_TAG, "goted TCPMESSAGE_RESPONCE " + pack.orderID);
                        ContextHelper.getInstance().runOnCurrentUIThread(
                                new Runnable() {
                                    @Override
                                    public void run() {

                                        if (pack.orderID != -1) {
                                            if (!NotificationService
                                                    .sendNotific("notif",
                                                            pack.message, "")) {
                                                AlertDHelper
                                                        .showDialogOk(pack.message);
                                            }
                                        } else {
                                            if (!NotificationService
                                                    .sendNotific("notifSMS",
                                                            pack.message, "")) {
                                                AlertDHelper
                                                        .showDialogOk(pack.message);
                                                Log.d("Messages", "Message = " + pack.message);
                                            }
                                        }

                                        if (!pack.className
                                                .equals("IMAP.Net.OrderIsNotYours_mesbindata")) {
                                            if (pack.className
                                                    .equals("IMAP.Net.OrderIsYours_mesbindata")) {
                                                isOrderYours = true;
                                                // OrderManager.getInstance().changeOrderState(pack.orderID,
                                                // 2);
                                            }

                                            String address = "";
                                            if (orderManager.getOrder(pack.orderID) != null && orderManager.getOrder(pack.orderID) != null) {
                                                address = orderManager.getOrder(pack.orderID).getStreet();
                                                address += " " + orderManager.getOrder(pack.orderID).getAddressFact();
                                            }
                                            if (orderManager.getOrder(pack.orderID) != null && orderManager.getOrder(pack.orderID).getStatus() == Order.STATE_NEW) {
                                                showConfirmToast(isOrderYours, address);
                                            }

                                            return;
                                        }

                                        List<Order> taken = OrderManager
                                                .getInstance()
                                                .getOrdersByState(
                                                        Order.STATE_TAKEN);
                                        List<Order> perf = OrderManager
                                                .getInstance()
                                                .getOrdersByState(
                                                        Order.STATE_PERFORMING);

                                        System.out.println("1 " + taken.size());
                                        if ((taken.size() == 1)
                                                && (perf.size() == 0)) {
                                            System.out
                                                    .println("2 "
                                                            + pack.orderID
                                                            + " "
                                                            + taken.get(0)
                                                            .getOrderID());
                                            if (pack.orderID == taken.get(0)
                                                    .getOrderID()) {
                                                System.out.println("3 ok");

                                                StateObserver
                                                        .getInstance()
                                                        .setDriverState(
                                                                StateObserver.DRIVER_FREE);
                                                TextView busy2 = (TextView) ContextHelper
                                                        .getInstance()
                                                        .getCurrentActivity()
                                                        .findViewById(
                                                                R.id.btn_busy);
                                                busy2.setText("Занят");
                                                busy2.setEnabled(true);

                                                ServerData.getInstance().isFree = true;
                                            }
                                        }

                                        // временно !!!!!!!!!!!!!!!!!
                                        // label
                                        if (orderManager.getOrder(pack.orderID)
                                                .getStatus() == Order.STATE_TAKEN) {
                                            orderManager.changeOrderState(
                                                    pack.orderID,
                                                    Order.STATE_MISSED);
                                            NotificationService.cancelNotif(
                                                    pack.orderID, "f3");
                                        }

                                        if (EfirOrder.orderId == pack.orderID) {
                                            EfirOrder.startTimer(0);
                                        }
                                    }
                                });
                    }
                });

        MultiPacketListener.getInstance().addListener(Packet.ORDER_RESPONCE4,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        DispOrderResponse4 pack = (DispOrderResponse4) packet;

                        final DispOrder4 mOrder = pack.getOrder();

                        Log.d(LOG_TAG, "goted ORDER_RESPONCE4 " + mOrder.orderID
                                + " " + mOrder.orderType + " "
                                + mOrder.streetName + " !!!! " + mOrder.fare);

                        Order ord = new Order(mOrder);
                        orderManager = OrderManager.getInstance();

                        if (mOrder.orderType.equals("SendedByDispatcher")
                                || mOrder.orderType
                                .equals("SendedByDispatcherFromGetFolder")) {

                            play.play(R.raw.snd_msg);

                            // order add
                            ord.setStatus(Order.STATE_PERFORMING);
                            orderManager.addOrder(ord);
                            // orderManager.getOrder(mOrder.orderID).setStatus(Order.STATE_PERFORMING);
                            // open description window
                            OrderDetails.setOrderId(mOrder.orderID);

                            if (NotificationService.sendNotific(
                                    "SendedByDispatcher", mOrder.streetName
                                            + " " + mOrder.house, "")) {
                                return;
                            }

                            if (!mOrder.folder.equals(Order.FOLDER_NOT_DONE)
                                    && !mOrder.folder
                                    .equals(Order.FOLDER_TRASH)
                                    && !mOrder.folder.equals(Order.FOLDER_DONE)) {
                                ContextHelper.getInstance()
                                        .runOnCurrentUIThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                StateObserver
                                                        .getInstance()
                                                        .setDriverState(
                                                                StateObserver.DRIVER_BUSY);

                                                TextView busy2 = (TextView) ContextHelper
                                                        .getInstance()
                                                        .getCurrentActivity()
                                                        .findViewById(
                                                                R.id.btn_busy);
                                                busy2.setEnabled(false);

                                                ServerData.getInstance().isFree = false;

                                                if (isOrderYours) {
                                                    OrderManager
                                                            .getInstance()
                                                            .getOrder(
                                                                    mOrder.orderID).accepted = true;
                                                    OrderManager
                                                            .getInstance()
                                                            .getOrder(
                                                                    mOrder.orderID).arrived = false;
                                                    OrderDetails.btnArrived
                                                            .setEnabled(true);
                                                    OrderDetails.btnAccept
                                                            .setEnabled(false);
                                                    OrderDetails.btnDo
                                                            .setText("Выполняю");
                                                    isOrderYours = false;
                                                }
                                            }
                                        });
                            }

                            for (DispOrder4 orde : mOrders) {
                                if (orde.orderID == mOrder.orderID) {
                                    mOrders.remove(orde);
                                    ContextHelper.getInstance()
                                            .runOnCurrentUIThread(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //mAdapter.notifyDataSetChanged();
                                                            mAdapter.updateMyList(mOrders);
                                                            switchListView(mAdapter);
                                                            if (sharedPrefs
                                                                    .getBoolean(
                                                                            "prefIsAutoSearch",
                                                                            false))// autosearch
                                                                // 1
                                                                // 2
                                                                SwipeFragment.ethear
                                                                        .setText("ЭФИР("
                                                                                + String.valueOf(orderManager
                                                                                .getCountOfOrdersByState(Order.STATE_NEW)
                                                                                + orderManager
                                                                                .getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                                                + ")");
                                                            else
                                                                SwipeFragment.ethear
                                                                        .setText("ЭФИР("
                                                                                + orderManager
                                                                                .getCountOfEfirOrders()
                                                                                + ")");

                                                        }
                                                    });
                                    break;
                                }
                            }

                            if (FragmentTransactionManager.getInstance()
                                    .getId() != FragmentPacket.ORDER_DETAILS) {
                                FragmentTransactionManager.getInstance()
                                        .openFragment(
                                                FragmentPacket.ORDER_DETAILS);
                            } else {
                                Log.d(LOG_TAG, "!!!!!!!!! OK !!!!!!!!");
                            }

                        } else if (mOrder.orderType.equals("FindDriver1")
                                || mOrder.orderType.equals("FindDriver2")) {

                            play.play(R.raw.snd_msg);

                            ord.setStatus(Order.STATE_KRYG_ADA);
                            orderManager.addOrder(ord);
                            // !!!
                            EfirOrder.setOrderId(ord.getOrderID());

                            if (sharedPrefs.getBoolean("prefIsAutoSearch",
                                    false)) {// autosearch 1 2
                                // isEixist
                                boolean found = false;
                                int count = 0;
                                int index = -1;
                                for (DispOrder4 item : mOrders) {
                                    if (item.orderID == mOrder.orderID) {
                                        // mOrders.remove(item);
                                        // mOrders.add(0, mOrder);
                                        index = count;
                                        found = true;
                                        break;
                                    }
                                    count++;
                                }
                                if (index > -1) {
                                    mOrders.remove(index);
                                    mOrders.add(0, mOrder);
                                }
                                if (!found) {
                                    mOrders.add(0, mOrder);
                                }
                                ContextHelper.getInstance()
                                        .runOnCurrentUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //mAdapter.notifyDataSetChanged();
                                                mAdapter.updateMyList(mOrders);
                                                switchListView(mAdapter);
                                                SwipeFragment.ethear.setText("ЭФИР("
                                                        + String.valueOf(orderManager
                                                        .getCountOfOrdersByState(Order.STATE_NEW)
                                                        + orderManager
                                                        .getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                        + ")");
                                            }
                                        });
                            }

                            if (mOrder.orderType.equals("FindDriver1")) {
                                EfirOrder.startTimer(SettingsFromXml
                                        .getInstance().getFirstTimeSearch());
                                startTimerForNotification(SettingsFromXml
                                                .getInstance().getFirstTimeSearch(),
                                        mOrder.orderID, "f12");
                                if (sharedPrefs.getBoolean("prefAutoSearch1",
                                        true)) {
                                    NotificationService.sendNotific(
                                            "FindDriver1", mOrder.streetName
                                                    + " " + mOrder.house,
                                            mOrder.orderID + "");
                                }
                            } else if (mOrder.orderType.equals("FindDriver2")) {
                                EfirOrder.startTimer(SettingsFromXml
                                        .getInstance().getSecondTimeSearch());
                                startTimerForNotification(SettingsFromXml
                                                .getInstance().getSecondTimeSearch(),
                                        mOrder.orderID, "f12");
                                if (sharedPrefs.getBoolean("prefAutoSearch2",
                                        true)) {
                                    NotificationService.sendNotific(
                                            "FindDriver2", mOrder.streetName
                                                    + " " + mOrder.house,
                                            mOrder.orderID + "");
                                }
                            }

                            // !!!add condition
                            if (FragmentTransactionManager.getInstance()
                                    .getId() != 4) {
                                FragmentTransactionManager.getInstance()
                                        .openFragment(FragmentPacket.ORDER);
                            }
                        } else if (mOrder.orderType.equals("FindDriver3")) {
                            if (sharedPrefs.getBoolean("prefAutoSearchEfir",
                                    true)) {
                                NotificationService.sendNotific("FindDriver3",
                                        mOrder.streetName + " " + mOrder.house,
                                        mOrder.orderID + "");
                            }
                            play.play(R.raw.msg_new_ether_order);
                            // order add
                            orderManager.addOrder(ord);

                            // isEixist
                            boolean found = false;
                            for (DispOrder4 item : mOrders) {
                                if (item.orderID == mOrder.orderID) {
                                    mOrders.remove(item);
                                    mOrders.add(0, mOrder);
                                    found = true;
                                    break;
                                }
                            }

                            Log.d(LOG_TAG, "checkpoint1");

                            if (!found) {
                                mOrders.add(0, mOrder);
                            }

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(SettingsFromXml
                                                .getInstance()
                                                .getThirdTimeSearch());

                                        Log.d(LOG_TAG, "upd ethear adapter");

                                        ContextHelper.getInstance()
                                                .runOnCurrentUIThread(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                NotificationService
                                                                        .cancelNotif(
                                                                                mOrder.orderID,
                                                                                "f3");
                                                                mOrders.remove(mOrder);
                                                                //mAdapter.notifyDataSetChanged();
                                                                mAdapter.updateMyList(mOrders);
                                                                switchListView(mAdapter);

                                                                if (orderManager
                                                                        .getOrder(
                                                                                mOrder.orderID)
                                                                        .getStatus() == Order.STATE_TAKEN
                                                                        || orderManager
                                                                        .getOrder(
                                                                                mOrder.orderID)
                                                                        .getStatus() == Order.STATE_NEW) {
                                                                    Log.d(LOG_TAG, "checkpoint1");
                                                                    orderManager
                                                                            .changeOrderState(
                                                                                    mOrder.orderID,
                                                                                    Order.STATE_MISSED);
                                                                }
                                                                if (sharedPrefs
                                                                        .getBoolean(
                                                                                "prefIsAutoSearch",
                                                                                false))// autosearch
                                                                    // 1
                                                                    // 2
                                                                    SwipeFragment.ethear
                                                                            .setText("ЭФИР("
                                                                                    + String.valueOf(orderManager
                                                                                    .getCountOfOrdersByState(Order.STATE_NEW)
                                                                                    + orderManager
                                                                                    .getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                                                    + ")");
                                                                else
                                                                    SwipeFragment.ethear
                                                                            .setText("ЭФИР("
                                                                                    + orderManager
                                                                                    .getCountOfEfirOrders()
                                                                                    + ")");

                                                                if (EfirOrder.orderId == mOrder.orderID) {
                                                                    Log.d(LOG_TAG,"checkpoint2");
                                                                    EfirOrder.startTimer(0);
                                                                }
                                                            }
                                                        });
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                            Log.d(LOG_TAG, "checkpoint2");

                            // notify
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            //mAdapter.notifyDataSetChanged();
                                            mAdapter.updateMyList(mOrders);
                                            switchListView(mAdapter);
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
                                        }
                                    });
                            Log.d(LOG_TAG, "checkpoint3");
                        }

                    }

                });

        MultiPacketListener.getInstance().addListener(
                Packet.CALL_SIGNCHANGED_RESPONCE,
                new OnNetworkPacketListener() {

                    @Override
                    public void onNetworkPacket(Packet packet) {
                        CallSignChangedResponce responce = (CallSignChangedResponce) packet;

                        // Просто отменяем заказ
                        if (responce.getOrderID() != -1) {
                            cancelOrder(responce.getOrderID());
                        }
                    }

                    private void cancelOrder(int orderID) {
                        for (DispOrder4 ord : mOrders) {
                            if (ord.orderID == orderID) {
                                OrderManager.getInstance().changeOrderState(
                                        orderID, Order.STATE_CANCELLED);
                                FragmentTransactionManager.getInstance()
                                        .openFragment(
                                                FragmentPacket.ORDER_DETAILS);
                                OrderDetails.btnCancel.setEnabled(false);
                                OrderDetails.btnAccept.setEnabled(false);
                                OrderDetails.btnDo.setEnabled(false);
                                OrderDetails.btnArrived.setEnabled(false);
                                if (OrderManager.getInstance()
                                        .getCountOfOrdersByState(
                                                Order.STATE_PERFORMING) == 0) {
                                    AlertDHelper
                                            .showDialogOk("Заказ снят диспетчером");
                                    PlaySound.getInstance()
                                            .play(R.raw.msg_warn);
                                }
                                break;
                            }
                        }

                        List<Order> ord = OrderManager.getInstance()
                                .getOrdersByState(Order.STATE_PERFORMING);
                        for (Order or : ord) {
                            if (or.getOrderID() == orderID) {
                                OrderDetails.setOrderId(orderID);

                                OrderManager.getInstance().changeOrderState(
                                        orderID, Order.STATE_CANCELLED);

                                FragmentTransactionManager.getInstance()
                                        .openFragment(
                                                FragmentPacket.ORDER_DETAILS);
                                ContextHelper.getInstance()
                                        .runOnCurrentUIThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                OrderDetails.btnCancel
                                                        .setEnabled(false);
                                                OrderDetails.btnDo
                                                        .setEnabled(false);
                                                OrderDetails.btnAccept
                                                        .setEnabled(false);
                                                PlaySound.getInstance().play(
                                                        R.raw.msg_warn);
                                                OrderDetails.btnArrived
                                                        .setEnabled(false);
                                                AlertDHelper
                                                        .showDialogOk("Заказ снят диспетчером");
                                            }
                                        });
                                break;
                            }
                        }
                        // Свежие правки
                        if (OrderManager.getInstance().getCountOfOrdersByState(
                                Order.STATE_PERFORMING) < 1) {
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                            StateObserver
                                                    .getInstance()
                                                    .setDriverState(
                                                            StateObserver.DRIVER_FREE);
                                            TextView busy2 = (TextView) ContextHelper
                                                    .getInstance()
                                                    .getCurrentActivity()
                                                    .findViewById(R.id.btn_busy);
                                            busy2.setEnabled(true);
                                            busy2.setText("Занят");

                                            ServerData.getInstance().isFree = true;
                                            if (ServerData.getInstance().doOwn) {
                                                StateObserver
                                                        .getInstance()
                                                        .setDriverState(
                                                                StateObserver.DRIVER_BUSY);
                                                busy2.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                        // Генерируем событие изменения состояния
                    }
                });
    } // end addListeners()

    private void showConfirmToast(boolean isOrderYours, String address) {

        Context context = ContextHelper.getInstance().getCurrentContext();
        // Create layout inflator object to inflate toast.xml file
        LayoutInflater inflater = ContextHelper.getInstance().getCurrentActivity().getLayoutInflater();

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.custom_layout, null);

        LinearLayout container = (LinearLayout) toastRoot.findViewById(R.id.toast_container);
        TextView content = (TextView) toastRoot.findViewById(R.id.toast_content);

        if (isOrderYours) {
            container.setBackgroundColor(Color.GREEN);
            content.setText(address + ContextHelper.getInstance().getCurrentContext().getResources().getString(R.string.order_is_yours));
        } else {
            container.setBackgroundColor(Color.RED);
            content.setText(address + ContextHelper.getInstance().getCurrentContext().getResources().getString(R.string.order_is_not_yours));
        }


        Toast toast = new Toast(context);

        // Set layout to toast
        toast.setView(toastRoot);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM,
                0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }

    protected void updateMyOrders(GetOrdersResponse pack) {

        for (int i = 0; i < pack.count(); i++) {
            Order order = new Order(pack.getOrder(i));

            String folder = order.getFolder();
            if ((folder.equals("НаОформлении"))
                    || (folder.equals("Направленный"))
                    || (folder.equals("ReceiveDriver"))
                    || (folder.equals(Order.FOLDER_DOIN))
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
        }

        MultiPacketListener.getInstance().addListener(Packet.GET_ROUTES_ANSWER,
                new OnNetworkPacketListener() {
                    @Override
                    public void onNetworkPacket(Packet packet) {
                        GetRoutesResponse pack = (GetRoutesResponse) packet;
                        Log.e("IMAP", "!!! goted GET_ROUTES_ANSWER "
                                + pack.orderId + " x:" + pack.geoX.size()
                                + " y:" + pack.geoY.size());

                        if (pack.geoX.size() > 0) {

                            lg = new ArrayList<LatLng>();
                            for (int i = 0; i < pack.geoY.size(); i++) {
                                lg.add(new LatLng(pack.geoY.get(i), pack.geoX
                                        .get(i)));
                            }

                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        public void run() {
                                            updateMyLoc = false;

                                            drawRouteIfExist(lg);
                                        }
                                    });
                        }
                    }
                });
    }

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

    private void disconnect() {
        // ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
        // @Override
        // public void run() {
        // ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.server_indication)).setImageResource(R.drawable.no_server_working);
        // }
        // });
        StateObserver.getInstance().setServer(StateObserver.NO_WORK);

        ConnectionHelper.getInstance().stop();
        // ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
        //
        // @Override
        // public void run() {
        // TextView state_driver = (TextView)
        // ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
        // state_driver.setText("НЕ ПОДКЛЮЧЁН");
        // }
        // });
        StateObserver.getInstance().setDriverState(StateObserver.DRIVER_NO_CONNECT);
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) ContextHelper.getInstance()
                .getCurrentContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (SocketService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public class SamplePagerAdapter extends PagerAdapter {

        List<View> pages = null;

        public SamplePagerAdapter(List<View> pages) {
            this.pages = pages;
        }

        @Override
        public Object instantiateItem(View collection, int position) {
            View v = pages.get(position);
            ((ViewPager) collection).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            // ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }
    }

    public void drawRouteIfExist(ArrayList<LatLng> l) {

        Log.v("ABC", "size - " + l.size() + "first point - "
                + l.get(0).latitude + " " + l.get(0).longitude);

        mMap.addMarker(new MarkerOptions()
                .position(l.get(0))
                .title("Начало")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        mMap.addMarker(new MarkerOptions()
                .position(l.get(l.size() - 1))
                .title("Финиш")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addPolyline(new PolylineOptions().addAll(l).color(Color.BLUE));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(l.get(0)));
        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l.get(0),16),
        // 2000, null);
    }

    private void startTimerForNotification(final long time, final int orderID,
                                           final String string) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    ContextHelper.getInstance().runOnCurrentUIThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    NotificationService.cancelNotif(orderID,
                                            string);
                                }
                            });
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static class ListViewComparator implements Comparator<DispOrder4> {// autosearch
        // 1
        // 2

        @Override
        public int compare(DispOrder4 dispOrder4, DispOrder4 dispOrder42) {
            if (dispOrder4.orderType.equals(dispOrder42.orderType))
                return 0;
            else if (dispOrder4.orderType.equals("FindDriver1"))
                return -1;
            else if (dispOrder4.orderType.equals("FindDriver2")
                    && dispOrder42.orderType.equals("FindDriver3"))
                return -1;
            else
                return 1;
        }
    }

    private static class ViewHolder {
        TextView text;
    }
}
