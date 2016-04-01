package com.innotech.imap_taxi.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.CurrentOrdersFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.EfirOrder;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.FragmentPacket;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.MapFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.OrderDetails;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket.SwipeFragment;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi.customViews.CustomDigitalClock;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.model.UIData;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.packet.LoginResponse;
import com.innotech.imap_taxi.ping.MyLocationListener;
import com.innotech.imap_taxi.ping.PingHelper;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi3.R;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.fabric.sdk.android.Fabric;

public class NavigatorMenuActivity extends FragmentActivity implements Observer {

	// private ImageView serverConnection;
	static public Button parkings;
	static public ImageButton myOwnOrder;
	static public boolean gps;
	static public boolean doOwn = false;
	static public TextView activityTitle;
	// static public RelativeLayout iconLayout;
	static public LinearLayout iconLayout;
	static public LinearLayout arhivLayout;
	Listener GPSlistener;
	boolean reg = false;
	LocationManager mlocManager;
	boolean gpsProv, netProv; // fresh flags
	SharedPreferences sharedPrefs;
	AlertDialog.Builder builder;
	AlertDialog dialog;
	private int num_sat_connected, num_sat_all, i, y;
	private PowerManager mgr;
	private WakeLock wakeLock;
	private BroadcastReceiver mReceiver;
	// public static Activity act_navig;
	private boolean isScreenOn = true;
	private boolean onPause = false;
	Context mContext;

	private static LoginResponse lr;
	

	CustomDigitalClock digitalClock;
	LocationListener mlocListener, mlocListenerNetwork;

	ImageView gpsInd, networkInd, serverInd, parkingInd, balanceInd,
			stateDriverInd, theme;
	TextView stateDriver, driverNo;
	RelativeLayout parkingIco;

	public final static int GPS_REQUEST = 100;
	int orderAmount;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//Fabric.with(this, new Crashlytics());

		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		String tmDevice;
		tmDevice = "" + tm.getDeviceId();
		
		Log.d("myLogs", "deviceId = " + tmDevice);

		Crashlytics.setUserIdentifier(tmDevice);

		mContext = this;
		// Log.d("lifcycle", "OnCreate");

		//LogHelper.printLog(ContextHelper.getInstance().getCurrentContext());
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (sharedPrefs.getString(UserSettingActivity.KEY_TEXT_SIZE, "")
				.equals("")) {
			Editor ed = sharedPrefs.edit();
			ed.putString(UserSettingActivity.KEY_TEXT_SIZE, "0");
			ed.apply();
		}
		boolean isNight = sharedPrefs.getBoolean("prefIsNightTheme", false);

		// final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		// final String message = "Do you want open GPS setting?";
		//
		// builder = new AlertDialog.Builder(NavigatorMenuActivity.this);
		// builder.setMessage(message)
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface d, int id) {
		// startActivity(new Intent(action));
		// d.dismiss();
		// }
		// })
		// .setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface d, int id) {
		// d.cancel();
		// }
		// });
		// builder.create().show();
		Log.wtf("MultiPacketListener", "clear");
		MultiPacketListener.getInstance().clear();

		if (isNight) {
			ServerData.getInstance().isNight = true;
		} else {
			ServerData.getInstance().isNight = false;
		}

		if (ServerData.getInstance().isNight) {
			setTheme(R.style.Theme_NoTitle_Black);
		} else {
			setTheme(R.style.Theme_NoTitle);
		}

		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());

		Log.wtf("NavigationMenuActivity", "onCreate");
		// Log.d("lifcycle", "OnCreate");
		ContextHelper.getInstance().setCurrentContext(this);
		setContentView(R.layout.main_for_fragment_new);

		digitalClock = (CustomDigitalClock) findViewById(R.id.digitalClock);
		Typeface t = Typeface.createFromAsset(getAssets(),
				"fonts/TickingTimebombBB.ttf");
		digitalClock.setTypeface(t);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		networkInd = (ImageView) findViewById(R.id.internet_indication);
		serverInd = (ImageView) findViewById(R.id.server_indication);
		gpsInd = (ImageView) findViewById(R.id.gps_indication);
		stateDriverInd = (ImageView) findViewById(R.id.driverState);
		balanceInd = (ImageView) findViewById(R.id.balanceIcon);
		parkingIco = (RelativeLayout) findViewById(R.id.parkingIco);
		parkingIco = (RelativeLayout) findViewById(R.id.parkingIco);
		parkingInd = (ImageView) findViewById(R.id.parkingImg);

		stateDriver = (TextView) findViewById(R.id.state_driver);
		driverNo = (TextView) findViewById(R.id.driverNo);
		driverNo.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/digital-7.ttf"));
		activityTitle = (TextView) findViewById(R.id.activityTitle);

		// actionbar
		// iconLayout = (RelativeLayout) findViewById(R.id.iconLayout);
		iconLayout = (LinearLayout) findViewById(R.id.iconLayout);
		arhivLayout = (LinearLayout) findViewById(R.id.archivLayout);
		// actionbar

		StateObserver.getInstance().addObserver(this);

		stateDriverInd.setOnClickListener(driverClick);

		gpsInd.setOnClickListener(gpsClick);

		balanceInd.setOnClickListener(balanceClick);

		ImageView danger = (ImageView) findViewById(R.id.bt_danger);
		danger.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				builder = new AlertDialog.Builder(NavigatorMenuActivity.this);

				builder.setMessage("Синхронизировать сейчас?")
						.setTitle("Уведомление")
						.setPositiveButton("Да",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface d,
											int which) {
										AlertDialog dialog = builder.create();
										dialog.setCancelable(false);
										dialog.show();

										if (ConnectionHelper.getInstance()
												.isConnected()) {

											RequestHelper.dangerWarning();

											final ProgressDialog progress = new ProgressDialog(
													NavigatorMenuActivity.this);
											progress.setTitle("Уведомление");
											progress.setMessage("Синхронизация");
											progress.setCancelable(false);
											progress.show();

											new Thread(new Runnable() {

												@Override
												public void run() {

													/*
													 * ContextHelper.getInstance(
													 * )
													 * .runOnCurrentUIThread(new
													 * Runnable() {
													 * 
													 * @Override public void
													 * run() { } });
													 */

													try {
														Thread.sleep(3000);
														progress.dismiss();
													} catch (InterruptedException e) {
														// TODO Auto-generated
														// catch block
														e.printStackTrace();
													}

												}
											}).start();
										} else {
											Toast.makeText(
													ContextHelper
															.getInstance()
															.getCurrentContext(),
													"Вы не подключены к серверу!",
													Toast.LENGTH_LONG).show();
										}

										dialog.dismiss();
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

				AlertDialog dialog2 = builder.create();
				dialog2.setCancelable(false);
				dialog2.show();

			}
		});

		if (Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
			Log.d("NavigationMenuAct", "ALLOW_MOCK_LOCATION 0");
			getLocUpdates();
		} else {
			Log.d("NavigationMenuAct", "ALLOW_MOCK_LOCATION 1");
			// LogHelper.w_gps("ALLOW_MOCK_LOCATION 1");
			try {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setMessage(
						"Похоже что в настройках включен параметр 'фиктивное местоположение' (mock locations). Для работы приложения выключите его!")
						.setTitle("Уведомление")
						.setPositiveButton("OK", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});

				if (ServerData.getInstance().IS_TEST_BUILD) {
					Log.d("NavigationMenuAct", "test build");
					// LogHelper.w_gps("test build");
					builder.setNegativeButton("Нет", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							getLocUpdates();
						}
					});
				}

				dialog = builder.create();
				dialog.setCancelable(false);
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ConnectivityManager connectivityManager = (ConnectivityManager)
		// getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo activeNetworkInfo =
		// connectivityManager.getActiveNetworkInfo();
		// boolean connected = activeNetworkInfo != null &&
		// activeNetworkInfo.isConnected();

		// test
		if (ConnectionHelper.getInstance().isConnected()) {
			// if (connected) {
			StateObserver.getInstance().setNetwork(StateObserver.WORK);
			// ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			// @Override
			// public void run() {
			// ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.internet_indication)).setImageResource(R.drawable.gprs_working);
			//
			// }
			// });
		}
		PingHelper.getInstance().start();
		if (savedInstanceState == null) {
			FragmentTransactionManager.getInstance()
					.initializationFragmentTransaction(this);
		} else {
			/*
			 * FragmentTransactionManager.getInstance().openFragment(
			 * savedInstanceState.getInt("fragID")); Log.d("lifcycle",
			 * "fragID = " + FragmentTransactionManager.getInstance().getId());
			 */
		}
		Log.d("lifcycle", "OnCreate");
	} //END onCreate

	private void getLocUpdates() {
		mlocListener = new MyLocationListener();
		mlocListenerNetwork = new MyLocationListener();
		mlocManager = (LocationManager) ContextHelper.getInstance()
				.getCurrentActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		if (mlocManager.getAllProviders()
				.contains(LocationManager.GPS_PROVIDER))
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, mlocListener);
		if (mlocManager.getAllProviders().contains(
				LocationManager.NETWORK_PROVIDER))
			mlocManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							0, 0, mlocListenerNetwork);
		Log.d("NavigationMenuAct", "getLocUpdates");
		mlocManager.addGpsStatusListener(new Listener() {
			@Override
			public void onGpsStatusChanged(int event) {
				num_sat_connected = 0;
				num_sat_all = 0;
				Log.w("gps status", String.valueOf(event));

				GpsStatus gpsStatus = mlocManager.getGpsStatus(null);
				if (gpsStatus != null) {
					Iterable<GpsSatellite> satelitesList = gpsStatus
							.getSatellites();
					Iterator<GpsSatellite> sat = satelitesList.iterator();
					i = 0;
					y = 0;
					while (sat.hasNext()) {
						GpsSatellite satellite = sat.next();
						if (satellite.usedInFix()) {
							y++;
						}
						i++;
					}
					Log.w("num of satelites - " + y + "/" + i, Thread
							.currentThread().getName());
					Log.d("NavigationMenuAct", "num of satelites - " + y + "/"
							+ i);
					num_sat_connected = y;
					num_sat_all = i;
					if (num_sat_connected == 0 && gps) {
						gps = false;
						PlaySound.getInstance().play(R.raw.msg_stat_neg);
						StateObserver.getInstance().setGps(
								StateObserver.NO_WORK);
						// ContextHelper.getInstance().runOnCurrentUIThread(new
						// Runnable() {
						// @Override
						// public void run() {
						// try {
						// //
						// ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.gps_indication)).setImageResource(R.drawable.no_gps_working);
						// } catch (Exception e) {
						// Log.d("NavigationMenuActivity GetLocUpdates Exception1 ",
						// e.getMessage());
						// }
						// }
						// });
					} else if (num_sat_connected > 0 && !gps) {
						gps = true;
						ServerData.getInstance().gpsOrNetProv = true;
						PlaySound.getInstance().play(R.raw.msg_stat_pos);
						StateObserver.getInstance().setGps(StateObserver.WORK);

						// ContextHelper.getInstance().runOnCurrentUIThread(new
						// Runnable() {
						// @Override
						// public void run() {
						// try {
						// ((ImageView)ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.gps_indication)).setImageResource(R.drawable.gps_working);
						// } catch (Exception e) {
						// Log.d("NavigationMenuActivity GetLocUpdates Exception2 ",
						// e.getMessage());
						// }
						// }
						// });
					}
				} else
					Log.w("GPS", "else Listener");
				gpsStatus = null;
			}
		});
		reg = true;

		// LogHelper.w_gps("getLocUpdates");
		//
		// GPSlistener = new Listener() {
		//
		// @Override
		// public void onGpsStatusChanged(int event) {
		//
		// num_sat_connected = 0;
		// num_sat_all = 0;
		//
		// GpsStatus gpsStatus = mlocManager.getGpsStatus(null);
		// if(gpsStatus != null) {
		// Iterable<GpsSatellite> satelitesList = gpsStatus.getSatellites();
		// Iterator<GpsSatellite> sat = satelitesList.iterator();
		// i=0;
		// y=0;
		//
		// while (sat.hasNext()) {
		// GpsSatellite satellite = sat.next();
		// if (satellite.usedInFix()) {
		// y++;
		// }
		// i++;
		// //System.out.println(i + ": " + satellite.getPrn() + "," +
		// satellite.usedInFix() + "," + satellite.getSnr() + "," +
		// satellite.getAzimuth() + "," + satellite.getElevation());
		// }
		// //Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
		// "num of satelites - " + y + "/" + i, Toast.LENGTH_SHORT).show();
		// System.out.println("num of satelites - " + y + "/" + i);
		// LogHelper.w_gps("num of satelites - " + y + "/" + i);
		// num_sat_connected = y;
		// num_sat_all = i;
		//
		// if (num_sat_connected==0 && gps) {
		// gps = false;
		// PlaySound.getInstance().play(R.raw.msg_stat_neg);
		// ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
		// @Override
		// public void run() {
		// try{
		// ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.gps_indication).setBackgroundResource(R.drawable.no_gps_working);
		// }
		// catch (Exception e)
		// {
		//
		// }
		// }
		// });
		// } else if (num_sat_connected>0 && !gps) {
		// gps = true;
		// //Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
		// "num of satelites - " + y + "/" + i, Toast.LENGTH_SHORT).show();
		// PlaySound.getInstance().play(R.raw.msg_stat_pos);
		// ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
		// @Override
		// public void run() {
		// try{
		// ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.gps_indication).setBackgroundResource(R.drawable.gps_working);
		// }
		// catch (Exception e){}
		//
		// }
		// });
		// }
		//
		// }
		// gpsStatus=null;
		// }
		// };
		//
		// mlocManager = (LocationManager)
		// ContextHelper.getInstance().getCurrentActivity().getSystemService(Context.LOCATION_SERVICE);
		// try{
		// Looper.prepare();
		// }
		// catch (Exception e){}
		// mlocManager.addGpsStatusListener(GPSlistener);
		// reg = true;
		//
		// LocationListener mlocListener = new MyLocationListener();
		//
		// boolean gpsProv, netProv;
		//
		//
		// LocationManager manager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		// mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
		// 0, mlocListener);
		// gpsProv = true;
		// } else {
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!! no GPS_PROVIDER");
		// Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
		// "Включите GPS", Toast.LENGTH_LONG).show();
		// LogHelper.w_gps("no GPS_PROVIDER");
		// gpsProv = false;
		// }
		//
		// if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		// mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 0, 0, mlocListener);
		// netProv = true;
		// } else {
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!! no NETWORK_PROVIDER");
		// LogHelper.w_gps("no NETWORK_PROVIDER");
		// netProv = false;
		// }
		//
		// if (netProv || gpsProv) {
		// ServerData.getInstance().gpsOrNetProv = true;
		// Log.e("test", "gpsOrNetProv true");
		// LogHelper.w_gps("gpsOrNetProv true");
		// } else {
		// ServerData.getInstance().gpsOrNetProv = false;
		// Log.e("test", "gpsOrNetProv false");
		// LogHelper.w_gps("gpsOrNetProv false");
		// }
		//
		// if (!gps){
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// try{
		// Thread.sleep(60000l);
		// // Thread.sleep(30000l);
		// }
		// catch (Exception e){}
		// getLocUpdates();
		// }
		// }).start();
		// }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (FragmentTransactionManager.getInstance().getId() == FragmentPacket.ARCHIV) {
				iconLayout.setVisibility(View.VISIBLE);
				arhivLayout.setVisibility(View.GONE);
			}
			if (FragmentTransactionManager.getInstance().getId() != 0) {
				FragmentTransactionManager.getInstance().back();
			} else {
				moveTaskToBack(true);
			}
			return true;
		}
		/*
		 * else if (keyCode==KeyEvent.KEYCODE_POWER){ moveTaskToBack(true);
		 * return true; }
		 */
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("catchOnResume", "fragId = menu");
		Log.d("lifcycle", "onResume");
		ContextHelper.getInstance().setCurrentContext(mContext);
		// LogHelper.w_gps("onResume");
		Log.i("act", "onResume");

		// wakeLock.acquire();
		// PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		// isScreenOn = pm.isScreenOn();
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		processExtraData();
	}

	private void processExtraData() {
		Intent myIntent = getIntent();
		if (getIntent().getAction() != null) {
			Log.e("TAG", "@@@ action - " + getIntent().getAction());
			if (myIntent.getAction().equals("f1")
					|| myIntent.getAction().equals("f2")) {
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.ORDER);
			} else if (myIntent.getAction().equals("ef")) {
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.ETHEAR);
			} else if (myIntent.getAction().equals("sbd")
					|| myIntent.getAction().equals("upd")) {
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.ORDER_DETAILS);
			} else if (myIntent.getAction().equals("sms")) {
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.SWIPE);
			} else if (myIntent.getAction().equals("con")) {
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.SWIPE);
				SwipeFragment.reCon = true;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("lifcycle", "OnPause");
		if (dialog != null) {
			dialog.cancel();
			// dialog.get
		}
		/*
		 * Editor ed= ContextHelper.getInstance().getSharedPreferences().edit();
		 * ed.putBoolean("onpause", true); ed.commit();
		 */
		// LogHelper.w_gps("onPause");
		if (reg) {
			mlocManager.removeGpsStatusListener(GPSlistener);
			reg = false;
		}
		Log.i("act", "onPause");
		MyImapApp.getInstance().paused();
		// wakeLock.release();
		// PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		// if (!pm.isScreenOn() && isScreenOn) {
		// isScreenOn = false;
		// moveTaskToBack(false);
		/*
		 * IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON); //
		 * filter.addAction(Intent.ACTION_SCREEN_OFF);
		 * filter.addAction(Intent.ACTION_USER_PRESENT); //
		 * filter.addAction(Intent.ac); mReceiver = new ScreenReceiver();
		 * registerReceiver(mReceiver, filter);
		 */
		// }
		// FragmentTransactionManager.getInstance().setReba(false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// MyImapApp.getInstance().paused();
		if (reg) {
			mlocManager.removeGpsStatusListener(GPSlistener);
			reg = false;
		}

		// LogHelper.w_gps("onStop");
		Log.i("act", "onStop");
		Log.i("act", "isFinishing() = " + String.valueOf(isFinishing()));
		/*
		 * FragmentTransactionManager.getInstance().remove(
		 * NavigatorMenuActivity.this);
		 */
		/*
		 * if(isFinishing()) { ConnectionHelper.getInstance().stop();
		 * MultiPacketListener.getInstance().clear();
		 * PingHelper.getInstance().stop(); //wakeLock.release(); }
		 */
	}

	@Override
	protected void onDestroy() {
		// TODO порешать с остановкой сервиса
		// stopService(new
		// Intent(ContextHelper.getInstance().getCurrentContext(),
		// SocketService.class));
		super.onDestroy();
		if (mlocManager != null) {
			mlocManager.removeUpdates(mlocListener);
			mlocManager.removeUpdates(mlocListenerNetwork);
		}
		FragmentTransactionManager.getInstance().remove(
				NavigatorMenuActivity.this);
		Log.wtf("NavigationMenuActivity", "onDestroy");

	}

	@Override
	public void onBackPressed() {
		if (FragmentTransactionManager.getInstance().getId() == FragmentPacket.ORDERS) {
			FragmentTransactionManager.getInstance().openFragment(
					FragmentPacket.SWIPE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("lifcycle", "onSaveInstanceState");

		outState.putInt("orderForMap", MapFragment.orderId);
		outState.putInt("orderFromEther", EfirOrder.orderId);
		outState.putInt("fragID", FragmentTransactionManager.getInstance()
				.getId());
		Log.w("NavigationMenuActivity", "onSaveInstanceState");

		/*
		 * FragmentTransactionManager.getInstance().remove(
		 * NavigatorMenuActivity.this);
		 */
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		MapFragment.orderId = savedInstanceState.getInt("orderFromMap");
		EfirOrder.orderId = savedInstanceState.getInt("orderFromEther");

		Log.wtf("NavigationMenuActivity", "onRestoreInstanceState");
	}

	@Override
	public void update(Observable observable, Object o) {
		// TODO observer reaction
		StateObserver stateObserver = StateObserver.getInstance();
		if (StateObserver.getInstance().isGps()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gpsInd.setImageResource(R.drawable.gps_working);
				}
			});
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gpsInd.setImageResource(R.drawable.no_gps_working);
				}
			});
		}

		// if(StateObserver.getInstance().isServer()){
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// serverInd.setImageResource(R.drawable.server_working);
		// }
		// });
		// }else {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// serverInd.setImageResource(R.drawable.no_server_working);
		// }
		// });
		// }

		if (!StateObserver.getInstance().isNetwork()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					networkInd.setImageResource(R.drawable.no_gprs_working);
					networkInd.setOnClickListener(noInternetClick);
				}
			});
		} else if (StateObserver.getInstance().isNetwork()
				&& !StateObserver.getInstance().isServer()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					networkInd.setImageResource(R.drawable.no_server_working);
					networkInd.setOnClickListener(noConnectClick);
				}
			});
		} else if (StateObserver.getInstance().isServer()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					networkInd.setImageResource(R.drawable.server_working);
					networkInd.setOnClickListener(connectClick);
				}
			});
		}

		if (StateObserver.getInstance().isShowParkings()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					parkingIco.setVisibility(View.VISIBLE);
				}
			});
			if (StateObserver.getInstance().getParkingPosition() != 0) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						parkingInd.setImageResource(R.drawable.parking_no);
						driverNo.setVisibility(View.VISIBLE);
						driverNo.setText(String.valueOf(StateObserver
								.getInstance().getParkingPosition()));
					}
				});
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						parkingInd.setImageResource(R.drawable.parkings_stand);
						driverNo.setVisibility(View.GONE);
					}
				});
			}

		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					parkingIco.setVisibility(View.GONE);
				}
			});
		}

		switch (StateObserver.getInstance().getDriverState()) {
		case 0:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("СВОБОДЕН");
					stateDriver.setTextColor(Color.parseColor("#009900"));
					stateDriverInd.setImageResource(R.drawable.driver_green);
				}
			});

			break;
		case 1:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("ЗАНЯТ");
					stateDriver.setTextColor(Color.RED);
					stateDriverInd.setImageResource(R.drawable.driver_orange);
				}
			});

			break;
		case 2:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("НЕ ПОДКЛЮЧЁН");
					stateDriver.setTextColor(Color.WHITE);
				}
			});

			break;
		case 3:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
					stateDriver.setTextColor(Color.RED);
				}
			});

			break;
		}
		if (UIData.getInstance().getBalance().charAt(0) != '-') {
			balanceInd.setImageResource(R.drawable.balance_green);
		} else {
			balanceInd.setImageResource(R.drawable.balance_orange);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("lifcycle", "OnStart");
		Log.wtf("NavigationMenuActivity", "onStart");

		if (StateObserver.getInstance().isGps()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gpsInd.setImageResource(R.drawable.gps_working);
				}
			});
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gpsInd.setImageResource(R.drawable.no_gps_working);
				}
			});
		}

		// if(StateObserver.getInstance().isServer()){
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// serverInd.setImageResource(R.drawable.server_working);
		// }
		// });
		// }else {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// serverInd.setImageResource(R.drawable.no_server_working);
		// }
		// });
		// }

		if (!StateObserver.getInstance().isNetwork()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					networkInd.setImageResource(R.drawable.no_gprs_working);
					networkInd.setOnClickListener(noInternetClick);
				}
			});
		} else if (StateObserver.getInstance().isNetwork()
				&& !StateObserver.getInstance().isServer()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					networkInd.setImageResource(R.drawable.no_server_working);
					networkInd.setOnClickListener(noConnectClick);
				}
			});
		} else if (StateObserver.getInstance().isServer()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					networkInd.setImageResource(R.drawable.server_working);
					networkInd.setOnClickListener(connectClick);
				}
			});
		}

		if (StateObserver.getInstance().isShowParkings()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					parkingIco.setVisibility(View.VISIBLE);
				}
			});
			if (StateObserver.getInstance().getParkingPosition() != 0) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						parkingInd.setImageResource(R.drawable.parking_no);
						driverNo.setVisibility(View.VISIBLE);
						driverNo.setText(String.valueOf(StateObserver
								.getInstance().getParkingPosition()));
					}
				});
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						parkingInd.setImageResource(R.drawable.parkings_stand);
						driverNo.setVisibility(View.GONE);
					}
				});
			}
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					parkingIco.setVisibility(View.GONE);
				}
			});
		}

		switch (StateObserver.getInstance().getDriverState()) {
		case 0:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("СВОБОДЕН");
					stateDriver.setTextColor(Color.parseColor("#009900"));
					stateDriverInd.setImageResource(R.drawable.driver_green);
				}
			});

			break;
		case 1:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("ЗАНЯТ");
					stateDriver.setTextColor(Color.RED);
					stateDriverInd.setImageResource(R.drawable.driver_orange);
				}
			});

			break;
		case 2:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("НЕ ПОДКЛЮЧЁН");
					stateDriver.setTextColor(Color.WHITE);
				}
			});

			break;
		case 3:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					stateDriver.setText("ПОТЕРЯ СОЕДИНЕНИЯ");
					stateDriver.setTextColor(Color.RED);
				}
			});

			break;
		}
		if (UIData.getInstance().getBalance().charAt(0) != '-') {
			balanceInd.setImageResource(R.drawable.balance_green);
		} else {
			balanceInd.setImageResource(R.drawable.balance_orange);
		}
	}

	private View.OnClickListener driverClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			List<Order> ord = OrderManager.getInstance().getOrdersByState(
					Order.STATE_PERFORMING);
			switch (StateObserver.getInstance().getDriverState()) {
			case StateObserver.DRIVER_FREE:
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						//send busy message to server

						byte[] body2 = RequestBuilder.createBodyPPSChangeState("3", 0, lr.peopleID);
						byte[] data2 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, lr.srvID,
								RequestBuilder.DEFAULT_DESTINATION_ID, lr.GUID, true, body2);
						ConnectionHelper.getInstance().send(data2);

						stateDriver.setText("ЗАНЯТ");
						stateDriver.setTextColor(Color.RED);
						stateDriverInd
								.setImageResource(R.drawable.driver_orange);
						StateObserver.getInstance().setDriverState(
								StateObserver.DRIVER_BUSY);
						Toast.makeText(
								ContextHelper.getInstance().getCurrentContext(),
								ContextHelper
										.getInstance()
										.getCurrentContext()
										.getText(
												R.string.driver_status_changed_on_busy),
								Toast.LENGTH_SHORT).show();
					}
				});
				break;
			case StateObserver.DRIVER_BUSY:
				if (OrderManager.getInstance().getCountOfOrdersByState(
						Order.STATE_PERFORMING) < 1) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							//send free message to server
							byte[] body2 = RequestBuilder.createBodyPPSChangeState("0", 0, lr.peopleID);
							byte[] data2 = RequestBuilder.createSrvTransfereData(RequestBuilder.DEFAULT_CONNECTION_TYPE, lr.srvID,
									RequestBuilder.DEFAULT_DESTINATION_ID, lr.GUID, true, body2);
							ConnectionHelper.getInstance().send(data2);

							stateDriver.setText("СВОБОДЕН");
							stateDriver.setTextColor(Color
									.parseColor("#009900"));
							stateDriverInd
									.setImageResource(R.drawable.driver_green);
							StateObserver.getInstance().setDriverState(
									StateObserver.DRIVER_FREE);
						}
					});
				} else if (OrderManager.getInstance().getCountOfOrdersByState(
						Order.STATE_PERFORMING) == 1) {
					// there is only one current order and we`ll open it
					int orderID = ord.get(0).getOrderID();
					OrderDetails.dispOrderId(orderID);
					FragmentTransactionManager.getInstance().openFragment(
							FragmentPacket.ORDER_DETAILS);
				} else {
					// there are several orders and we`ll open list of orders
					CurrentOrdersFragment
							.setState(CurrentOrdersFragment.STATE_PERFORMING);
					CurrentOrdersFragment
							.displayOrders(CurrentOrdersFragment.STATE_PERFORMING);
					FragmentTransactionManager.getInstance().openFragment(
							FragmentPacket.CURRENTORDERS);
				}

				break;
			}
		}
	};

	private View.OnClickListener gpsClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					&& !mlocManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ContextHelper.getInstance().getCurrentContext());
				builder.setMessage(R.string.no_gps_connection)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										startActivityForResult(
												new Intent(
														Settings.ACTION_LOCATION_SOURCE_SETTINGS),
												GPS_REQUEST);
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User cancelled the dialog
									}
								});
				builder.create().show();
			} else {
			}
		}
	};

	private View.OnClickListener balanceClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
					"Ваш баланс: " + UIData.getInstance().getBalance(),
					Toast.LENGTH_LONG).show();
		}
	};

	private View.OnClickListener noInternetClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
					"Нет подключения к интернет", Toast.LENGTH_LONG).show();
		}
	};

	private View.OnClickListener noConnectClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			SwipeFragment.connection.performClick();
		}
	};

	private View.OnClickListener connectClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
					"Вы подключены к серверу", Toast.LENGTH_LONG).show();
		}
	};

	public static void setLr(LoginResponse login) {
		lr = login;
	}
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { menu.add(0, 0,
	 * 0, "Обновить"); return super.onCreateOptionsMenu(menu); }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { //
	 * ContextHelper.getInstance().getCurrentActivity().finish(); /*if
	 * (ConnectionHelper.getInstance().isConnected()){
	 * ConnectionHelper.getInstance(). }
	 */
	/*
	 * Intent intent = new
	 * Intent(ContextHelper.getInstance().getCurrentContext(),
	 * NavigatorMenuActivity.class);
	 * ContextHelper.getInstance().getCurrentActivity
	 * ().overridePendingTransition(0, 0);
	 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); finish();
	 * /*ContextHelper
	 * .getInstance().getCurrentActivity().overridePendingTransition(0, 0);
	 * 
	 * ContextHelper.getInstance().getCurrentActivity().startActivity(intent);
	 * 
	 * return super.onOptionsItemSelected(item); }
	 */
}
