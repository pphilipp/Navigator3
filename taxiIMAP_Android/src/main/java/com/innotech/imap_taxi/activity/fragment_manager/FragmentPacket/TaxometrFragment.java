package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManagerTaxometr;
import com.innotech.imap_taxi.core.TaxometrManager;
import com.innotech.imap_taxi.datamodel.Tarif;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

public class TaxometrFragment extends FragmentPacket {
    public static Tarif tarif;
    TaxometrManager taxometrManager;
    public final Integer SEAT = 0;
    public final Integer KILOMETR = 1;

    /*double priceToSeat=15;// grn
    double priceMetr=0.005;// grn/m   *1000
    double priceWaitForMillisecond=0.000018333; //grn/millis  grn/min 60*1000
    double priceDownTimeForMillisecond=0.00001; //grn/millis
    long timeFreeDowntime=20*1000; //millis
    float speedMinDownTime=8f; // km/h
    float distanceInPrice=-0.1f;
     */
    public final Integer WAIT = 2;
    public final Integer DOWNTIME = 3;
//    public double price = 0;//grn
    LocationManager locationManager;
    float curSpeed = 0;// km/h
    double priceToSeat = 2.9;// grn
    double priceMetr = 0.00299;// grn/m
    double priceWaitForMillisecond = 0.00001666666; //grn/millis  grn/min
    double priceDownTimeForMillisecond = 0.00001666666; //grn/millis
    long timeFreeDowntime = 0; //millis
    double speedMinDownTime = 15f; // km/h
    double distanceInPrice = -0.1f;
//    public long timeInWait, timeInSimpleMode, timeInDownTime;  //millis
    long timeLast; //millis
//    float allDistance = 0f;
//    public boolean wait;
//    public boolean looper;
//    public long timeStart;
    public long timeNow;
    Location lastLocation;
    View myView;
    LocationListener locListner;
    Thread thread;

    public TaxometrFragment() {
        super(TAXOMETR);
    }

    public static void setTarif(Tarif tar) {
        tarif = tar;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);*/
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            Log.wtf("JO",savedInstanceState.getString("tarif"));

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
    	Log.d("myLogs", "TaxometrFragment onResume");
        if (tarif != null) {
            timeFreeDowntime = (long) (tarif.freedowntime * 1000);
            speedMinDownTime = tarif.minspeedDowntime;
            distanceInPrice = tarif.distanceinseat;
            setPrices(tarif.pricetoseat, tarif.priceperkilometr / 1000, tarif.priceforwait / 60 / 1000, tarif.pricedowntime / 60 / 1000);
            ((Button) myView.findViewById(R.id.button_tarrif)).setText("ТАРИФ \"" + tarif.tariffname + "\"");
        }
        Log.w("TAXOMETR TAXFRAGMENT", "RESUME");
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		/*	byte[] body3 = RequestBuilder.createGetTaximeterRates();
		byte[] data3 = RequestBuilder.createSrvTransfereData(
				RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
				RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance().getGuid(), true,
				body3);
		ConnectionHelper.getInstance().send(data3);

		 */
        taxometrManager = TaxometrManager.getInstance();

        locationManager = (LocationManager) ContextHelper.getInstance().getCurrentActivity().getSystemService(Context.LOCATION_SERVICE);
        locListner = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                Log.w("COORDINATES TAXOMETR", location.getLongitude() + ":" + location.getLatitude());
                if (location != null) {
                    if (lastLocation != null) {
                        if (curSpeed != 0.0f) {
                            addPrice(priceMetr * location.distanceTo(lastLocation));
                            taxometrManager.allDistance += location.distanceTo(lastLocation);
                        }
                        setDistanceInView();
                        //((TextView)view.findViewById(R.id.textView_distance)).setText(allDistance+"");
                    }
                    if (location.hasSpeed()) {
                        setSpeed(location.getSpeed() * 3.6f);
                    }
                }
                lastLocation = location;
            }
        };
        if(!taxometrManager.looper)
            refreshValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        myView = inflater.inflate(R.layout.activity_main_taxometr, null, false);
        myView.findViewById(R.id.button_start_taxometr).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setEnabled(true);
                if (((Button) myView.findViewById(R.id.button_start_taxometr)).getText().equals("СТАРТ")) {
                    refreshValues();
                    addPrice(priceToSeat);
                    taxometrManager.timeStart = System.currentTimeMillis();
                    timeLast = System.currentTimeMillis();
                    taxometrManager.looper = true;
                    ((Button) myView.findViewById(R.id.button_start_taxometr)).setText("СТОП");
                    ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setText("ОЖИДАНИЕ");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListner);
                    thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (taxometrManager.looper) {
                                try {
                                    Thread.sleep(1000l);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    thread.interrupt();
                                }
                                speedCompare();
                            }

                        }
                    });
                    thread.start();

                } else {
                    taxometrManager.looper = false;
                    locationManager.removeUpdates(locListner);
                    ((Button) myView.findViewById(R.id.button_start_taxometr)).setText("СТАРТ");
                    ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setText("ОТМЕНА");
                    thread.interrupt();
                }
            }
        });

        myView.findViewById(R.id.button_cancel_taxometr).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //refreshValues();
                //looper=false;
                if (((Button) myView.findViewById(R.id.button_cancel_taxometr)).getText().equals("ОТМЕНА")) {
                    refreshValues();
                    taxometrManager.looper = false;
                    locationManager.removeUpdates(locListner);
                    //ContextHelper.getInstance().getCurrentActivity().startActivity(new Intent(ContextHelper.getInstance().getCurrentContext(), NavigatorMenuActivity.class));
                    ContextHelper.getInstance().getCurrentActivity().finish();
                } else {
                    taxometrManager.wait = true;
                    ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setEnabled(false);
                }
            }
        });

        myView.findViewById(R.id.button_tarrif).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(taxometrManager.looper){
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContextHelper.getInstance().getCurrentContext())
                            .setMessage("Все данные по заказу будут утеряны. Продолжить?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FragmentTransactionManagerTaxometr.getInstance().openFragment(FragmentPacket.TAXOMETR_ClASSES_TARIF);
                                    taxometrManager.looper = false;
                                    locationManager.removeUpdates(locListner);
                                    ((Button) myView.findViewById(R.id.button_start_taxometr)).setText("СТАРТ");
                                    ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setText("ОТМЕНА");
//                                    if(thread.isAlive())
                                    thread.interrupt();
                                    refreshValues();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setCancelable(false);
                    dialog = builder.create();
                    dialog.show();
                }else {
                    FragmentTransactionManagerTaxometr.getInstance().openFragment(FragmentPacket.TAXOMETR_ClASSES_TARIF);
                }
            }
        });
        setViews();
        if (taxometrManager.looper) {
            myView.findViewById(R.id.button_cancel_taxometr).setEnabled(!taxometrManager.wait);
            ((Button) myView.findViewById(R.id.button_start_taxometr)).setText("СТОП");
            ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setText("ОЖИДАНИЕ");
            timeLast = System.currentTimeMillis();
            ((Button) myView.findViewById(R.id.button_start_taxometr)).setText("СТОП");
            ((Button) myView.findViewById(R.id.button_cancel_taxometr)).setText("ОЖИДАНИЕ");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListner);
            thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (taxometrManager.looper) {
                        try {
                            Thread.sleep(1000l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        speedCompare();
                    }

                }
            });
            thread.start();
        }
        return myView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        outState.putLong("timeStart", TaxometrManager.timeStart);
//        outState.putLong("timeStart", TaxometrManager.timeInWait);
//        outState.putLong("timeStart", TaxometrManager.timeInSimpleMode);
//        outState.putDouble("timeStart", TaxometrManager.price);
//        outState.putLong("timeStart", timeNow);
//        outState.putFloat("timeStart", TaxometrManager.allDistance);
//        outState.putBoolean("looper", TaxometrManager.looper);
//        outState.putBoolean("wait", TaxometrManager.wait);
//        outState.putString("tarif", tarif.jo.toString());
        Log.wtf("FRAGMENT","DESTROY");
    }

    private void setTimeInView() {
        String time = new SimpleDateFormat("mm:ss").format(new Date(timeNow - taxometrManager.timeStart));
        int length = 0;
        if (time.charAt(0) == '0') {
            length = 1;
            if (time.charAt(1) == '0') {
                length = 3;
                if (time.charAt(3) == '0') {
                    length = 4;
                    if (time.charAt(4) == '0') {
                        length = 5;
                    }
                }
            }
        }
        time = "<font color='grey'>" + time.substring(0, length) + "</font><font color='#17E8E8'>" + time.substring(length, 5) + "</font>";
        //((TextView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.textView_)).setText(timeInDownTime+"");
        //((TextView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.textView_timeInSimple)).setText(timeInSimpleMode+"");
        //((TextView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.textView_timeInWait)).setText(timeInWait+"");
        ((TextView) myView.findViewById(R.id.textView_time_in_road)).setText(Html.fromHtml(time));
    }

    public void refreshValues() {
        taxometrManager.timeInWait = 0;
        taxometrManager.timeInSimpleMode = 0;
        taxometrManager.timeInDownTime = 0;
        taxometrManager.price = 0;
        taxometrManager.timeStart = 0;
        timeNow = 0;
        taxometrManager.allDistance = 0f;
        if (myView != null) {
            setViews();
        }
        taxometrManager.wait = false;
        taxometrManager.looper = false;
    }

    public void setViews() {
        setDistanceInView();
        setPricesInViews();
        setDistanceInView();
        setTimeInView();
        setPrice(taxometrManager.price);
        setSpeed(curSpeed);
    }

    public void setSpeed(float speed) {
        Log.w("TAXOMETR SPEED", String.valueOf(speed));
        curSpeed = speed;
        if (taxometrManager.wait) {
            if (curSpeed > 0)
                taxometrManager.wait = false;
        }
        String text = String.format("%.0f", curSpeed);
        if (text.equals("0"))
            text = "";
        String add = "";
        for (int i = 0; i < 3 - text.length(); i++)
            add += "0";
        text = "<font color='grey'>" + add + "</font><font color='#17E8E8'>" + text;
        ((TextView) myView.findViewById(R.id.textView_speed)).setText(Html.fromHtml(text));
    }

    public void addPrice(double p) {
        setPrice(taxometrManager.price + p);
    }

    public void setPrice(double p) {
        taxometrManager.price = p;
        String pr = String.format("%.2f", p);
        try {
            ((TextView) myView.findViewById(R.id.textView_price)).setText(pr.split(",")[0]);
            ((TextView) myView.findViewById(R.id.textview_price_after_dot)).setText(pr.split(",")[1]);
        } catch (Exception e) {
            ((TextView) myView.findViewById(R.id.textView_price)).setText(pr.split(".")[0]);
            ((TextView) myView.findViewById(R.id.textview_price_after_dot)).setText(pr.split(".")[1]);
        }
    }

    public void setPrices(double seat, double m, double wait, double downtime) {
        priceToSeat = seat;
        priceMetr = m;
        priceWaitForMillisecond = wait;
        priceDownTimeForMillisecond = downtime;
        setPricesInViews();
    }

    public void setDistanceInView() {
        String text = String.format(Locale.ENGLISH, "%.1f", taxometrManager.allDistance);
        while (text.length() < 5) {
            text = "0" + text;
        }
        int length = 0;
        if (text.charAt(0) == '0') {
            length = 1;
            if (text.charAt(1) == '0') {
                length = 2;
                if (text.charAt(2) == '0') {
                    length = 3;
                    if (text.charAt(4) == '0') {
                        length = 5;
                    }
                }
            }
        }
        text = "<font color='grey'>" + text.substring(0, length) + "</font><font color='#17E8E8'>" + text.substring(length, text.length()) + "</font>";

        ((TextView) myView.findViewById(R.id.textView_distance)).setText(Html.fromHtml(text));
    }

    private void setPricesInViews() {
        ((TextView) myView.findViewById(R.id.textView_price_downtime)).setText(String.format(Locale.ENGLISH, "%.2f", priceDownTimeForMillisecond * 60 * 1000));
        ((TextView) myView.findViewById(R.id.textView_price_kilometr)).setText(String.format(Locale.ENGLISH, "%.2f", priceMetr * 1000));
        ((TextView) myView.findViewById(R.id.textView_price_wait)).setText(String.format(Locale.ENGLISH, "%.2f", priceWaitForMillisecond * 60 * 1000));
        ((TextView) myView.findViewById(R.id.textView_price_enter)).setText(String.format(Locale.ENGLISH, "%.2f", priceToSeat));
        //TODO Понять
    }

    public void speedCompare() {
        ContextHelper.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long timeToCheck;
                timeNow = System.currentTimeMillis();
                long dt = (timeNow - timeLast);
                Log.d("TAXOMETR speedCompare", "time" + dt);
                if (taxometrManager.wait) {
                    setUseStroke(WAIT, true);
                    setUseStroke(DOWNTIME, false);
                    timeToCheck = taxometrManager.timeInWait;
                    taxometrManager.timeInWait += dt;
                    long payTime = taxometrManager.timeInWait - timeToCheck;
                    addPrice(payTime * priceWaitForMillisecond);
                } else {
                    setUseStroke(WAIT, false);
                    if (taxometrManager.allDistance > distanceInPrice) {
                        setUseStroke(SEAT, false);
                        Log.d("TAXOMETR speedCompare", "curSpeed"+curSpeed);
                        if (curSpeed <= speedMinDownTime) {
                            setUseStroke(DOWNTIME, true);
                            timeToCheck = taxometrManager.timeInDownTime;
                            taxometrManager.timeInDownTime += dt;
                            if (taxometrManager.timeInDownTime > timeFreeDowntime) {
                                long payTime = taxometrManager.timeInDownTime - timeToCheck;
                                addPrice(payTime * priceDownTimeForMillisecond);
                            }
                        } else {
                            setUseStroke(DOWNTIME, false);
                            taxometrManager.timeInSimpleMode += dt;
                        }
                    } else {
                        setUseStroke(SEAT, true);
                    }
                }
                timeLast = timeNow;
                setTimeInView();
            }
        });
    }

    private void setUseStroke(int index, boolean use) {
        Integer color;
        if (use)
            color = Color.parseColor("#f59f18");
        else
            color = Color.parseColor("#FFFFFF");
        if (index == SEAT) {
            ((TextView) myView.findViewById(R.id.textView_name_enter)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_price_enter)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_razm_enter)).setTextColor(color);
        } else if (index == KILOMETR) {
            ((TextView) myView.findViewById(R.id.textView_name_kilometr)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_price_kilometr)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_razm_kilometr)).setTextColor(color);
        } else if (index == WAIT) {
            ((TextView) myView.findViewById(R.id.textView_name_wait)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_price_wait)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_razm_wait)).setTextColor(color);
        } else if (index == DOWNTIME) {
            ((TextView) myView.findViewById(R.id.textView_name_downtime)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_price_downtime)).setTextColor(color);
            ((TextView) myView.findViewById(R.id.textView_razm_downtime)).setTextColor(color);
        }
    }


}
