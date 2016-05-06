package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.AddressAdapter;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi.datamodel.Address;
import com.innotech.imap_taxi.datamodel.DispOrder;
import com.innotech.imap_taxi.datamodel.DispOrder.DispSubOrder;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.datamodel.SettingsFromXml;
import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.graph_utils.RouteView;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi.utile.AlertDHelper;
import com.innotech.imap_taxi.utile.DbArchHelper;
import com.innotech.imap_taxi.utile.NotificationService;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi3.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class OrderDetails extends FragmentPacket {
	private static final String LOG_TAG = OrderDetails.class.getSimpleName();
	//possible states of order due to view and controls
	private static final int INCOMING = 1;
	private static final int ACCEPTED = 2;
	private static final int ARRIVED = 3;
	private static final int PERFORMING = 4;
	private static final int DONE = 5;
	private static final int UNDEFINED = -1;
	private static int curTenMin;
	private static int curMin;
	private static int curTenSec;
	private static int curSec;
	private static int oldOrderId = -1;
	private static int orderID = -1;
	private static long orderTime;
	private static long s;
	public static boolean isArch;
	private static boolean isArchiveOrder;
	protected static String comments;
	private static AlertDialog dialog;
	private static Timer timerStopWatch;
	private static TextView tvRegion;
	private static TextView tvDistance;
	private static TextView tvAdditionalTxt;
	private static TextView tvArriveTimerTxt;
	private static TextView tvAutoClassTxt;
	private static TextView tvTxtDetails;
	private static TextView tvAdressFrom1;
	private static TextView tvAdressFrom2;
	private static TextView tvAdressTo;
	private static TextView tvPrice;
	private static TextView tvTime;
	private static TextView tvDate;
	private static ImageView ivExtraInfoBtn;
	private static ImageView ivImageJoker;
	private static ImageView ivImageWebOrder;
	private static ImageView ivImageNoCash;
	private static ImageView ivImageEd;
	private static ImageView ivCommentsImage;
	private static RelativeLayout rlTenMinLayout;
	private static RelativeLayout rlMinLayout;
	private static RelativeLayout rlTenSecLayout;
	private static RelativeLayout rlSecLayout;
	private static LinearLayout llColorLayout;
	private static LinearLayout llFlipClock;
	private static LinearLayout llRoute;
	private static LinearLayout llRouteInfoLayout;
	private static ListView addressListView;
	private static ViewPager mPager;
	public static Button btnArrived;
	public static Button btnDo;
	public static Button btnMap;
	public static Button btnConnDrivCl;
	public static Button btnDetails;
	public static Button btnBack;
	public static Button btnCancel;
	public static Button btnAccept;
	public static Button btnNoClient;
	public static Button btnWellDone;
	private static RouteView routeView;
	private static CountDownTimer countDownTimer3;
	private static OnClickListener doListener;
	private static OnClickListener doneListener;
	private static OnClickListener arrivedListener;
	private static OnClickListener noClientListener;
	private static OnClickListener cancelListner;
	private static OnClickListener acceptListner;
	private static Order mOrder = null;
	private static View viewComments;
	private static View viewFeatures;
	private static PageIndicator mIndicator;

	private static Context mContext;
	//list of addresses in this order
	private static List<Address> route;
	private static AddressAdapter mAdapter;

	// private static Button btnArrived1;
	// private static RelativeLayout edditionalInfoLayout;
	private static TextView extraTxt;

	public OrderDetails() {
		super(ORDER_DETAILS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreateView()");
		View view = inflater.inflate(R.layout.fragment_order_details_new1, container, false);
		mContext = ContextHelper.getInstance().getCurrentContext();
		// define Layouts for FlipClock
		rlTenMinLayout = (RelativeLayout) view.findViewById(R.id.tenMinLayout);
		rlMinLayout = (RelativeLayout) view.findViewById(R.id.minLayout);
		rlTenSecLayout = (RelativeLayout) view.findViewById(R.id.tenSecLayout);
		rlSecLayout = (RelativeLayout) view.findViewById(R.id.secLayout);

		tvTxtDetails = (TextView) view.findViewById(R.id.txt_details);
		tvAutoClassTxt = (TextView) view.findViewById(R.id.autoClassTxt);
		llColorLayout = (LinearLayout) view.findViewById(R.id.colorLayout);
		llRouteInfoLayout = (LinearLayout) view.findViewById(R.id.routeInfoLayout);
		PaintDrawable paintDrawable = (PaintDrawable) GraphUtils.getEtherRouteGradient(llRouteInfoLayout);
		llRouteInfoLayout.setBackground(paintDrawable);

		tvRegion = (TextView) view.findViewById(R.id.regionTxt);
		tvDistance = (TextView) view.findViewById(R.id.distTxt);
		tvAdditionalTxt = (TextView) view.findViewById(R.id.edditionalTxt);
		ivExtraInfoBtn = (ImageView) view.findViewById(R.id.extraInfoBtn);

		tvArriveTimerTxt = (TextView) view.findViewById(R.id.timerTxt);
		llFlipClock = (LinearLayout) view.findViewById(R.id.flipClockLayout);

		ivImageJoker = (ImageView) view.findViewById(R.id.imageJoker);
		ivImageWebOrder = (ImageView) view.findViewById(R.id.imageWeb);
		ivImageNoCash = (ImageView) view.findViewById(R.id.imageNoCash);

		ivImageEd = (ImageView) view.findViewById(R.id.imageEd);
		ivCommentsImage = (ImageView) view.findViewById(R.id.commentImage);

		routeView = (RouteView) view.findViewById(R.id.routeCustom);
		extraTxt = (TextView) view.findViewById(R.id.extraTxt);

		tvAdressFrom1 = (TextView) view.findViewById(R.id.adrsFrom1);
		tvAdressFrom2 = (TextView) view.findViewById(R.id.adrsFrom2);
		tvAdressTo = (TextView) view.findViewById(R.id.adrsTo);
		tvDate = (TextView) view.findViewById(R.id.date);
		tvTime = (TextView) view.findViewById(R.id.time);
		tvTime.setText("");
		tvPrice = (TextView) view.findViewById(R.id.costTxt);
		llRoute = (LinearLayout) view.findViewById(R.id.routeLL);

		btnArrived = (Button) view.findViewById(R.id.btn_arrived);
		// btnArrived1 = (Button) view.findViewById(R.id.btn_arrived1);
		btnDo = (Button) view.findViewById(R.id.btn_do);
		btnConnDrivCl = (Button) view.findViewById(R.id.btn_connectDriverClient);

		// btnNoClient = (Button) view.findViewById(R.id.btn_noClient);
		// btnWellDone = (Button) view.findViewById(R.id.btnWellDone);

		btnDetails = (Button) view.findViewById(R.id.btn_details);
		// btnMap = (Button) view.findViewById(R.id.btn_map);
		btnBack = (Button) view.findViewById(R.id.btn_back);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel_ord);
		btnAccept = (Button) view.findViewById(R.id.btn_accept_order);

		// config btns
		btnConnDrivCl.setEnabled(false);
		// btnArrived1.setEnabled(true);
		// btnNoClient.setVisibility(View.GONE);
		btnDo.setVisibility(View.GONE);
		// btnWellDone.setVisibility(View.GONE);

		// arrive button action
		// btnArrived1.setOnClickListener(arrivedListener);

		Typeface t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");

//		tvAdressFrom1.setTypeface(t);
//		tvAdressTo.setTypeface(t);
//		tvRegion.setTypeface(t);
//		tvAdditionalTxt.setTypeface(t);

		t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/BebasNeueRegular.ttf");
		tvDistance.setTypeface(t);
		tvPrice.setTypeface(t);

		t = Typeface.createFromAsset(ContextHelper.getInstance()
						.getCurrentContext().getAssets(),
						"fonts/TickingTimebombBB.ttf");
		tvArriveTimerTxt.setTypeface(t);

		llRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MapFragmentWindow.orderId = -1;
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.MAP);
			}
		});

		llRoute.setEnabled(true);
		btnDetails.setEnabled(true);
		btnCancel.setEnabled(true);
		btnAccept.setEnabled(false);

		mPager = (ViewPager) view.findViewById(R.id.comment_container);
		viewComments = inflater.inflate(R.layout.order_comment_fragment, null);
		viewFeatures = inflater.inflate(R.layout.order_comment_fragment, null);
		mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);

		addressListView = (ListView) view.findViewById(R.id.address_listview);
		initRoutOrder(orderID);
		addressListView.setAdapter(mAdapter);
		Log.d(LOG_TAG, "route.size()------->" + route.size());

		//fill address listView
		if (mAdapter == null) {
			mAdapter = new AddressAdapter(mContext, route);
		} else {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.upDateList(route);
				}
			});
		}

		return view;
	}// end onCreateView

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onViewStateRestored()");
		// tells the fragment that all of the saved state of its view hierarchy has been restored
		super.onViewStateRestored(savedInstanceState);
		if (isArchiveOrder && mOrder != null) {
			dispArchOrder(mOrder);
		} else if (OrderManager.getInstance().getOrder(orderID) != null) {
			isArch = true;
			setOrderId(OrderDetails.orderID);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "onResume()");
		if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
				ContextHelper.getInstance().getCurrentContext()).getString(
				UserSettingActivity.KEY_TEXT_SIZE, "")) != 0) {
			tvTxtDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					Integer.parseInt(PreferenceManager
							.getDefaultSharedPreferences(
									ContextHelper.getInstance()
											.getCurrentContext())
							.getString(UserSettingActivity.KEY_TEXT_SIZE, "")) + 14);
		}

		//check if there are new addresses
		Address newAddress = GetAddressFragment.getCurrentAddress();
		if (newAddress != null && route != null) {
			route.add(newAddress);
			if (mAdapter != null) {
				mAdapter.upDateList(route);
			}
		}
		//upDate fragment
		initRoutOrder(orderID);

	}

	@Override
	public void onStop() {
		Log.d(LOG_TAG, "onStop()");
		super.onStop();
		if (countDownTimer3 != null) {
			countDownTimer3.cancel();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(LOG_TAG, "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	public static void setOrderId(final int orderID) {
		Log.d(LOG_TAG, "setOrderId()");
		OrderDetails.orderID = orderID;
		isArchiveOrder = false;

		if (!SettingsFromXml.getInstance().isAllowDriverCancelOrder())
			btnCancel.setVisibility(View.INVISIBLE);
		else
			btnCancel.setVisibility(View.VISIBLE);

		btnBack.setOnClickListener(null);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// FragmentTransactionManager.getInstance().back();
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.SWIPE);
			}
		});

		final Order ord = OrderManager.getInstance().getOrder(orderID);
		Log.d(LOG_TAG, "current order ----->" + ord.getOrderID());

		// for btnTest accept order without button
		/*OrderManager.getInstance().getOrder(orderID).accepted = true;
		OrderManager.getInstance().getOrder(orderID).arrived = false;
		btnArrived.setEnabled(true);
		btnAccept.setEnabled(false);*/

		llRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ----
				// RequestHelper.getRoutes(orderID);
				System.out.println("getRoutes" + orderID);
				// ----
				MapFragmentWindow.orderId = orderID;
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.MAP);
			}
		});

		btnConnDrivCl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RequestHelper.connectDriverClient(orderID);
				Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
						"Запрос отправлен", Toast.LENGTH_LONG).show();
				System.out.println("!!!!! SEND !!! connectDriverClient !!!!! "
						+ orderID);
			}
		});

		/**get distance */
		tvDistance.setText(Utils.intToSpannableStringKm(
				OrderManager.getInstance().getOrder(orderID).getDistanceToOrderPlace()));

		noClientListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				long wate = SettingsFromXml.getInstance().getClientWaitTime() * 60000;
				Date nn = new Date();
				long now = nn.getTime();

				long min = ((2 * 60 * 60000)
						+ now
						- OrderManager.getInstance().getOrder(orderID)
						.getDate() - wate) / 60000;

				System.out.println(Utils.dateToString(now)
						+ " + 2hr - "
						+ Utils.dateToString(OrderManager.getInstance()
						.getOrder(orderID).getDate()) + " = " + min
						+ " min");

				byte[] body = RequestBuilder.createPassengerOut(orderID, 0,
						ServerData.getInstance().getPeopleID());
				byte[] data = RequestBuilder.createSrvTransfereData(
						RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
								.getInstance().getSrvID(),
						RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
								.getInstance().getGuid(), true, body);
				ConnectionHelper.getInstance().send(data);

				if (countDownTimer3 != null) {
					btnArrived.setEnabled(false);
				}

			}
		};

		doneListener = new OnClickListener() {
			AlertDialog.Builder builder;
			@Override
			public void onClick(View v) {
				if (SettingsFromXml.getInstance().isDriverCanSendOrderCost()
						&& isBeznalOrKilometrazh(orderID)) {
					builder = new AlertDialog.Builder(ContextHelper
							.getInstance().getCurrentActivity());
					builder.setMessage("Укажите стоимость заказа").setTitle(
							"Уведомление");
					final EditText input = new EditText(ContextHelper
							.getInstance().getCurrentContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER
							| InputType.TYPE_NUMBER_FLAG_DECIMAL
							| InputType.TYPE_NUMBER_FLAG_SIGNED);
					builder.setView(input);
					builder.setPositiveButton("Отправить", null);
					dialog = builder.create();
					dialog.setCancelable(false);
					dialog.setOnShowListener(new DialogInterface.OnShowListener() {

						@Override
						public void onShow(DialogInterface d) {

							Button b = dialog
									.getButton(AlertDialog.BUTTON_POSITIVE);
							b.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view) {
									String value = input.getText() + "";
									if (!value.equals("")
											&& Float.parseFloat(value) > 0) {
										float d = 0;
										try {
											d = Float.parseFloat(value);
										} catch (NumberFormatException e) {
											e.printStackTrace();
										}
										int price = (int) Math.rint(100.0 * d);
										System.out.println("value " + price);

										byte[] body = RequestBuilder
												.createBodyCostOfDriverPack(
														orderID, price,
														ServerData
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

										sendDone(orderID);

										dialog.dismiss();
									} else {
										Toast.makeText(
												ContextHelper.getInstance()
														.getCurrentContext(),
												"Введите корректную сумму",
												Toast.LENGTH_LONG).show();
									}

								}
							});
						}
					});

					dialog.show();

				} else {
					btnDo.setText("Выполняю");// свежие правки
					sendDone(orderID);
				}

			}
		};

		doListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (countDownTimer3 != null) {
					countDownTimer3.cancel();
				}
				btnArrived.setEnabled(false);
				btnArrived.setText("На месте");
				btnArrived.setVisibility(View.GONE);
				btnDo.setBackgroundColor(0xFF9C3438);

				btnDo.setEnabled(false);
				btnDo.setOnClickListener(null);

				byte[] body = RequestBuilder.createBodyOrderAnswer(orderID,
						"12", "0", ServerData.getInstance().getPeopleID());
				byte[] data = RequestBuilder.createSrvTransfereData(
						RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
								.getInstance().getSrvID(),
						RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
								.getInstance().getGuid(), true, body);
				ConnectionHelper.getInstance().send(data);

				// OrderManager.getInstance().changeOrderFolder(orderID,
				// Order.FOLDER_DOIN);

				btnDo.setOnClickListener(doneListener);
				btnDo.setText("Выполнил");
				btnDo.setEnabled(true);
				btnAccept.setEnabled(false);

				btnArrived.setText("НЕТ КЛИЕНТА");

				// btnArrived1.setVisibility(View.GONE);
				btnConnDrivCl.setVisibility(View.GONE);
				// btnNoClient.setVisibility(View.GONE);
				// btnWellDone.setVisibility(View.VISIBLE);
			}

		};

		arrivedListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.i("orderDetails", "na MESTE");

				if (orderID != 0) {

					Log.i("orderDetails", "na MESTE != 0");

					byte[] body = RequestBuilder.createBodyCarOnAddressRequest(
							orderID, ServerData.getInstance().getPeopleID());
					byte[] data = RequestBuilder.createSrvTransfereData(
							RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
									.getInstance().getSrvID(),
							RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
									.getInstance().getGuid(), true, body);
					ConnectionHelper.getInstance().send(data);

					btnDo.setVisibility(View.VISIBLE);
					// btnNoClient.setVisibility(View.VISIBLE);
					// btnArrived1.setVisibility(View.GONE);

					btnDo.setEnabled(true);

					OrderManager.getInstance().setPressedArrived(orderID);
					// !!!
					btnArrived.setText("НЕТ КЛИЕНТА");
					timerStopWatch.purge();
					timerStopWatch.cancel();
					// displayTimer(orderID);

					// btnArrived.setOnClickListener(null);
					// btnArrived.setEnabled(false);
				}
			}
		};

		cancelListner = new OnClickListener() {

			@Override
			public void onClick(View v) {
				byte[] body = RequestBuilder.createBodyOrderCancel(orderID,
						RequestBuilder.REASON_REFUSE, ServerData.getInstance()
								.getPeopleID());
				byte[] data = RequestBuilder.createSrvTransfereData(
						RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
								.getInstance().getSrvID(),
						RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
								.getInstance().getGuid(), true, body);
				ConnectionHelper.getInstance().send(data);
				/*
				 * byte[] body = RequestBuilder.createBodyOrderAnswer(orderID,
				 * "12", "0", ServerData.getInstance().getPeopleID()); byte[]
				 * data = RequestBuilder.createSrvTransfereData(RequestBuilder.
				 * DEFAULT_CONNECTION_TYPE, ServerData.getInstance().getSrvID(),
				 * RequestBuilder.DEFAULT_DESTINATION_ID,
				 * ServerData.getInstance().getGuid(), true, body);
				 * ConnectionHelper.getInstance().send(data);
				 */
				// TODO
				// Toast.makeText(ContextHelper.getInstance().getCurrentContext(),
				// "in Process. Stas waiting", Toast.LENGTH_LONG).show();
			}
		};

		acceptListner = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				byte[] body = RequestBuilder.createBodyOrderAnswer(orderID,
						"AcceptOrder", "", ServerData.getInstance()
								.getPeopleID());
				byte[] data = RequestBuilder.createSrvTransfereData(
						RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
								.getInstance().getSrvID(),
						RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
								.getInstance().getGuid(), true, body);
				// Log.w("AccEpt", data.toString());
				ConnectionHelper.getInstance().send(data);
				OrderManager.getInstance().getOrder(orderID).accepted=true;
				OrderManager.getInstance().getOrder(orderID).arrived=false;
				Log.d(LOG_TAG, "Check status = " + OrderManager.getInstance().getOrder(orderID));
				ord.accepted = true;
				ord.arrived = false;
				setUpButtonsState(ord);
				/*btnArrived.setEnabled(true);
				btnAccept.setEnabled(false);*/
			}
		};

		if (countDownTimer3 != null) {
			countDownTimer3.cancel();
		}

		btnAccept.setOnClickListener(acceptListner);
		btnArrived.setOnClickListener(arrivedListener);
		btnDo.setOnClickListener(doListener);
		btnCancel.setOnClickListener(cancelListner);
		// btnArrived1.setOnClickListener(arrivedListener);


		System.out.println("f - " + ord.getFolder());

		Log.d(LOG_TAG, "Order info: state = " + ord.getStatus() + ", arrived = " + ord.arrived + ", accepted = " + ord.accepted);

		if (ord.getStatus() == Order.STATE_KRYG_ADA) {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					llRoute.setEnabled(false);

					btnArrived.setText("На месте");
					btnArrived.setEnabled(false);

					btnDo.setText("Выполняю");
					btnDo.setEnabled(false);

					btnConnDrivCl.setEnabled(false);
					btnCancel.setEnabled(false);
					btnAccept.setEnabled(false);
				}
			});
		} else if (ord.getStatus() == Order.STATE_PERFORMING) {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() { // свежие
				// правки
				@Override
				public void run() {

					if ((OrderManager.getInstance().getOrder(orderID).accepted
							&& OrderManager.getInstance()
							.getOrder(orderID).getFolder()
							.equals("Направленный")
							&& OrderManager.getInstance()
							.getOrder(orderID).getOrderType()
							.equals("SendedByDispatcher") && !OrderManager
							.getInstance().getOrder(orderID).arrived)
							|| OrderManager.getInstance()
							.getOrder(orderID).getFolder()
							.equals(Order.FOLDER_DOING)
							|| !OrderManager.getInstance()
							.getOrder(orderID).getOrderType()
							.equals("SendedByDispatcher")) {

						Log.d(LOG_TAG, "condition1");

						ContextHelper.getInstance()
								.runOnCurrentUIThread(new Runnable() {
									@Override
									public void run() {

										llRoute.setEnabled(true);

										btnArrived.setEnabled(true);
										btnArrived
												.setOnClickListener(arrivedListener);
										btnArrived.setText("На месте");

										btnDo.setEnabled(false);
										btnDo.setText("Выполняю");

										btnConnDrivCl.setEnabled(true);

										btnCancel.setEnabled(true);
										btnAccept.setEnabled(false);

									}
								});
					} else if ((OrderManager.getInstance().getOrder(
							orderID).accepted
							&& OrderManager.getInstance()
							.getOrder(orderID).getFolder()
							.equals("Направленный")
							&& OrderManager.getInstance()
							.getOrder(orderID).getOrderType()
							.equals("SendedByDispatcher") && OrderManager
							.getInstance().getOrder(orderID).arrived)
							|| OrderManager.getInstance()
							.getOrder(orderID).getFolder()
							.equals(Order.FOLDER_DOING)
							|| OrderManager.getInstance()
							.getOrder(orderID).getFolder()
							.equals("ReceiveDriver")// свежие
							// правки
							|| !OrderManager.getInstance()
							.getOrder(orderID).getOrderType()
							.equals("SendedByDispatcher")) {

						Log.d(LOG_TAG, "condition2");
						// btnArrived.setEnabled(false);
						btnArrived.setText("На месте");

						btnDo.setEnabled(true);
						btnDo.setOnClickListener(doListener);
						btnDo.setText("Выполняю");
						btnCancel.setEnabled(true);
						Log.d("STATE", "Order_ID = " + orderID);
						displayTimer(orderID);
					} else {
						Log.d(LOG_TAG, "condition3");

						btnAccept.setEnabled(true);
						btnArrived.setEnabled(false);
						btnDo.setEnabled(false);
					}
				}
			});
		}

		// начинается пздц

		if (ord.getFolder().equals(Order.FOLDER_DOING)) {

			OrderManager.getInstance().changeOrderState(orderID,
					Order.STATE_PERFORMING);
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					if (!ord.isFromServer() && !isArch) {
						AlertDHelper
								.showDialogOk("Заказ перенесен в папку выполняемые");
					}

					Log.d(LOG_TAG, "condition4");


					btnDo.setEnabled(true);
					btnDo.setText("Выполнил");
					btnDo.setVisibility(View.VISIBLE);
					btnDo.setOnClickListener(doneListener);
					btnAccept.setEnabled(false);
					btnCancel.setEnabled(true);

					btnArrived.setEnabled(false);
					btnArrived.setText("На месте");
					btnArrived.setVisibility(View.GONE);
					btnConnDrivCl.setVisibility(View.GONE);
					btnDo.setBackgroundColor(0xFF9C3438);

				}
			});
		} else if (ord.getFolder().equals(Order.FOLDER_DONE)) {

			Log.d(LOG_TAG, "condition5");
			OrderManager.getInstance().changeOrderState(orderID,
					Order.STATE_PERFORMED);

			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					if (!ord.isFromServer() && !isArch) {
						AlertDHelper
								.showDialogOk("Заказ перенесен в папку выполненные");
					}

					if (countDownTimer3 != null) {
						countDownTimer3.cancel();
					}

					btnArrived.setEnabled(false);
					btnArrived.setText("На месте");
					btnAccept.setEnabled(false);
					btnDo.setEnabled(false);
					btnCancel.setEnabled(false);
					writeToArch(orderID);
				}
			});
		} else if (ord.getFolder().equals(Order.FOLDER_NOT_DONE)) {

			Log.d(LOG_TAG, "condition5");
			OrderManager.getInstance().changeOrderState(orderID,
					Order.STATE_CANCELLED);

			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {

					ServerData.getInstance().isFree = true;

					if (!ord.isFromServer() && !isArch) {
						if (!NotificationService.sendNotific("notif",
								"Заказ снят диспетчером", ""))
							AlertDHelper
									.showDialogOk("Заказ снят диспетчером. Причина ("
											+ ord.getComments() + ")");

						PlaySound.getInstance().play(R.raw.msg_warn);
					}
					btnAccept.setEnabled(false);
					btnArrived.setEnabled(false);
					btnArrived.setText("На месте");
					btnDo.setEnabled(false);
					btnCancel.setEnabled(false);
				}
			});

		} else if ((ord.getFolder().equals(Order.FOLDER_TRASH))
				|| (ord.getFolder().equals(Order.FOLDER_INBOX))) {

			Log.d(LOG_TAG, "condition6");

			OrderManager.getInstance().changeOrderState(orderID,
					Order.STATE_CANCELLED);

			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {

					ServerData.getInstance().isFree = true;

					if (!ord.isFromServer() && !isArch) {
						if (!NotificationService.sendNotific("notif",
								"Заказ снят диспетчером", ""))
							AlertDHelper.showDialogOk("Заказ снят диспетчером");

						PlaySound.getInstance().play(R.raw.msg_warn);
					}
					if (countDownTimer3 != null) {
						countDownTimer3.cancel();
					}
					PlaySound.getInstance().play(R.raw.msg_warn);
					btnArrived.setEnabled(false);
					btnArrived.setText("На месте");

					btnDo.setEnabled(false);
					btnAccept.setEnabled(false);
					btnCancel.setEnabled(false);
				}
			});

		} else {
			if (OrderManager.getInstance().getOrder(orderID).accepted
					&& OrderManager.getInstance().getOrder(orderID).getFolder()
					.equals("Направленный")
					&& OrderManager.getInstance().getOrder(orderID)
					.getOrderType().equals("SendedByDispatcher")
					&& !OrderManager.getInstance().getOrder(orderID).arrived
					|| OrderManager.getInstance().getOrder(orderID).getFolder()
					.equals(Order.FOLDER_DOING)
					|| !OrderManager.getInstance().getOrder(orderID)
					.getOrderType().equals("SendedByDispatcher")) {

				Log.d(LOG_TAG, "condition7");
				OrderManager.getInstance().changeOrderState(orderID,
						Order.STATE_PERFORMING);

				if (OrderManager.getInstance().getOrder(orderID).arrived) {

					ContextHelper.getInstance().runOnCurrentUIThread(
							new Runnable() {
								@Override
								public void run() {

									btnArrived.setEnabled(false);
									btnArrived.setText("На месте");
									btnArrived.setVisibility(View.GONE);

									btnDo.setEnabled(true);
									btnDo.setOnClickListener(doListener);
									btnDo.setText("Выполняю");
									btnDo.setVisibility(View.VISIBLE);

									displayTimer(orderID);

								}
							});

				} else {

					ContextHelper.getInstance().runOnCurrentUIThread(
							new Runnable() {
								@Override
								public void run() {
									btnArrived.setEnabled(true);
									btnArrived
											.setOnClickListener(arrivedListener);
									btnArrived.setText("На месте");

									btnDo.setEnabled(false);
									btnDo.setText("Выполняю");

									btnCancel.setEnabled(true);

								}
							});

				}
			}



			if ((oldOrderId != orderID) || (oldOrderId == -1)) {

				Log.d(LOG_TAG, "condition8");

				// ContextHelper.getInstance().runOnCurrentUIThread(new
				// Runnable() {
				// @Override
				// public void run() {
				// if (!ord.isFromServer() && !isArch) {
				// AlertDHelper.showDialogOk("Заказ ваш. Счастливого пути!");
				// }
				// }
				// });

			} else {

				System.out.println("UPDATE ORDER ");

				ContextHelper.getInstance().runOnCurrentUIThread(
						new Runnable() {
							@Override
							public void run() {
								if (!ord.isFromServer() && !isArch) {
									if (!NotificationService.sendNotific(
											"notif", "Заказ был уточнен", ""))
										AlertDHelper
												.showDialogOk("Заказ был уточнен");
								}

							}
						});
			}
		}

		imFree();

		isArch = false;
		oldOrderId = orderID;

		if (oldOrderId != -1) {
			Order order = OrderManager.getInstance().getOrder(oldOrderId);
			fillViewsForOrder(order);
		} else {
			FragmentTransactionManager.getInstance().openFragment(SWIPE);
		}

		if (ord.isFromServer()) {
			OrderManager.getInstance()
					.setOrderFromServerFalse(ord.getOrderID());
		}

		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			@Override
			public void run() {
				setUpButtonsState(ord);
			}
		});

	}// end setOrderId()

	public static void flip(int changeNumber, final RelativeLayout myView) {
		Context mContext = ContextHelper.getInstance().getCurrentContext();
		int upperBackId = R.id.up_back;
		final ImageView up_back = (ImageView) myView.findViewById(upperBackId);
		Drawable img = up_back.getDrawable();
		final ImageView up = (ImageView) myView.findViewById(R.id.up);
		up.setImageDrawable(img);
		up.setVisibility(View.INVISIBLE);

		final ImageView down = (ImageView) myView.findViewById(R.id.down);
		// down.getLayoutParams().height = 0;
		down.setVisibility(View.INVISIBLE);

		int resId = mContext.getResources().getIdentifier("up_" + changeNumber,
				"drawable", mContext.getPackageName());
		Drawable image = mContext.getResources().getDrawable(resId);
		up_back.setImageDrawable(image);

		resId = mContext.getResources().getIdentifier("down_" + changeNumber,
				"drawable", mContext.getPackageName());
		image = mContext.getResources().getDrawable(resId);
		down.setImageDrawable(image);

		Animation anim = new ScaleAnimation(1f, 1f, // Start and end values for
				// the X axis scaling
				1f, 0f, // Start and end values for the Y axis scaling
				Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
				Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
		anim.setDuration(100);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Animation anim = new ScaleAnimation(1f, 1f, 0f, 1f,
						Animation.RELATIVE_TO_SELF, 0f,
						Animation.RELATIVE_TO_SELF, 0f);

				anim.setDuration(200);
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						ImageView back_down = (ImageView) myView.findViewById(R.id.down_back);
						back_down.setImageDrawable(down.getDrawable());
					}
				});
				down.startAnimation(anim);

			}
		});
		up.startAnimation(anim);

	}

	public static SpannableString getPriceFormat(String price) {
		SpannableString ss = null;
		String[] parts = null;
		int length;
		if (price.contains(".")) {
			price = price.replace(".", ",");
		}
		if (price.contains(",")) {
			parts = price.split(",");
			if (parts[0] == null)
				parts[0] = "00";
			if (parts[1] == null)
				parts[1] = "00";
			length = parts[0].length();
			price = parts[0] + "," + parts[1];
		} else {
			length = price.length();
			price = price + ",00";
		}
		ss = new SpannableString(price);
		ss.setSpan(new RelativeSizeSpan(0.6f), length + 1, price.length(), 0);
		return ss;
	}

	public static void dispOrderId(int orderID2) {
		oldOrderId = orderID2;
		if (oldOrderId != -1) {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					btnDo.setEnabled(false);
					// !!!
					btnArrived.setEnabled(true);
					btnConnDrivCl.setEnabled(false);
					btnArrived.setText("На месте");
					btnCancel.setEnabled(false);
					btnAccept.setEnabled(false);
					tvTxtDetails.setText(Html.fromHtml(OrderManager.getInstance()
							.getOrder(oldOrderId).getOrderFullDesc()));
					setDetails(OrderManager.getInstance().getOrder(oldOrderId)
							.getOrderFullDescOther());
				}
			});
		}
	}

	public static void dispArchOrder(final Order order) {
		Log.d(LOG_TAG,"dispArchOrder()");
		if (order != null) {
			isArchiveOrder = true;
			mOrder = order;
			tvTime.setText("00:00");
			tvTime.setVisibility(View.INVISIBLE);
			btnDo.setVisibility(View.GONE);
			btnArrived.setVisibility(View.GONE);
			btnConnDrivCl.setVisibility(View.GONE);

			if (mOrder != null) {
				fillViewsForOrder(mOrder);
			} else {
				Log.d(LOG_TAG, "dispArchOrder() ArchDisplay problems");
			}
			btnBack.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FragmentTransactionManager.getInstance().openFragment(
							FragmentPacket.ARCHIV);
				}
			});

		}
	}

	private static void sendDone(int orderID) {
		OrderManager.getInstance().changeOrderState(orderID,
				Order.STATE_PERFORMED);

		byte[] body = RequestBuilder.createBodyOrderAnswer(orderID, "13", "0",
				ServerData.getInstance().getPeopleID());
		byte[] data = RequestBuilder.createSrvTransfereData(
				RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
						.getInstance().getSrvID(),
				RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance()
						.getGuid(), true, body);
		ConnectionHelper.getInstance().send(data);

		btnDo.setEnabled(false);
		btnDo.setOnClickListener(null);
		btnAccept.setEnabled(false);
		btnConnDrivCl.setEnabled(false);

		// btnBack.setEnabled(false);

		writeToArch(orderID);

		FragmentTransactionManager.getInstance().openFragment(
				FragmentPacket.SWIPE);
	}

	private static void setUpButtonsState(Order currentOrder) {
		//toggleBtnHide all buttons and then show only needed ones
		Log.d(LOG_TAG, "setUpButtonsState() = " + getOrderState(currentOrder));
		switch (getOrderState(currentOrder)) {
			case INCOMING:
				hideAllButtons();
				//show accept and decline buttons
				btnAccept.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.VISIBLE);
				break;
			case ACCEPTED:
				hideAllButtons();
				//show arrived, connect with client
				btnArrived.setVisibility(View.VISIBLE);
				btnArrived.setText(ContextHelper.getInstance().getCurrentContext()
						.getResources().getString(R.string.str_on_here));
				btnArrived.setEnabled(true);
				btnArrived.setOnClickListener(arrivedListener);
				btnConnDrivCl.setVisibility(View.VISIBLE);
				break;
			case ARRIVED:
				hideAllButtons();
				//show do, no Client, connect with Client
				btnDo.setVisibility(View.VISIBLE);
				btnDo.setText(ContextHelper.getInstance().getCurrentContext()
						.getResources().getString(R.string.str_executing));
				btnConnDrivCl.setVisibility(View.VISIBLE);
				btnArrived.setVisibility(View.VISIBLE);
				btnArrived.setText(ContextHelper.getInstance().getCurrentContext()
						.getResources().getString(R.string.str_no_client));
				btnArrived.setOnClickListener(noClientListener);
				break;
			case PERFORMING:
				hideAllButtons();
				//show big red button DONE
				btnDo.setVisibility(View.VISIBLE);
				btnDo.setText(ContextHelper.getInstance().getCurrentContext()
						.getResources().getString(R.string.str_execute));
				btnDo.setOnClickListener(doneListener);
				break;
			case DONE:
				hideAllButtons();
				break;
			case UNDEFINED:
				hideAllButtons();
				break;
			default:
				hideAllButtons();
				break;
		}

	}

	private static void hideAllButtons() {
		if (buttonsNotNull()) {
			btnArrived.setVisibility(View.GONE);
			btnDo.setVisibility(View.GONE);
			btnMap.setVisibility(View.GONE);
			btnConnDrivCl.setVisibility(View.GONE);
			btnDetails.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			btnCancel.setVisibility(View.GONE);
			btnAccept.setVisibility(View.GONE);
			//sett all buttons enabled
			btnArrived.setEnabled(true);
			btnDo.setEnabled(true);
			btnMap.setEnabled(true);
			btnConnDrivCl.setEnabled(true);
			btnDetails.setEnabled(true);
			btnBack.setEnabled(true);
			btnCancel.setEnabled(true);
			btnAccept.setEnabled(true);
		}

	}

	private static boolean buttonsNotNull() {
		return (btnArrived != null &&
				btnDo != null &&
				btnMap != null &&
				btnConnDrivCl != null &&
				btnDetails != null &&
				btnBack != null &&
				btnCancel != null &&
				btnAccept != null);
	}

	private static int getOrderState(Order currentOrder) {
		int orderState = UNDEFINED;
		//check if order is DONE
		if (currentOrder.getFolder().equals(Order.FOLDER_DONE)) {
			return DONE;
		}
		//check if order is currently performing
		if (currentOrder.getFolder().equals(Order.FOLDER_DOING)) {
			return PERFORMING;
		}
		//check if ARRIVED
		if (currentOrder.arrived) {
			return ARRIVED;
		}
		//check if ACCEPTED
		if (currentOrder.getStatus() == Order.STATE_PERFORMING && !currentOrder.arrived) {
			return ACCEPTED;
		}
		//check if INCOMING
		if (currentOrder.getStatus() == Order.STATE_NEW) {
			return INCOMING;
		}
		if (currentOrder.getStatus() == Order.STATE_CANCELLED) {
			return UNDEFINED;
		}
		return orderState;
	}

	private static String convertToVerticalView(String input) {
		String result = "";
		String newLine = "\n";

		for (int i = 0; i < input.length(); i++)
			if (i != input.length()-1)
				result += input.charAt(i) + newLine;
			else
				result += input.charAt(i);

		return result;
	}

	private static void fillViewsForOrder(final Order order) {
		Log.d(LOG_TAG, "fillViewsForOrder()");
		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			@Override
			public void run() {
				tvAutoClassTxt.setText(convertToVerticalView(order.autoClass));
				llColorLayout.setBackgroundColor(order.colorClass);

//				tvAdressFrom1.setText(validationTextViewAddressFrom(order));
//				tvAdressTo.setText(validationTextViewAddressTo(order));

				String regionFromOrder = (order.getRegion() != null && !order
						.getRegion().equals("")) ? order.getRegion()
						: ContextHelper.getInstance().getCurrentContext()
								.getString(R.string.noRegion);
				tvRegion.setText(regionFromOrder);

				// set tvPrice
				float priceFloat = (order.getFare() == 0) ? 0 : order.getFare();
				String price = String.format("%.2f", priceFloat);
				OrderDetails.tvPrice.setText(getPriceFormat(price));

				/** print tvDistance (previos realisation with hardcod 22.4km)**/
//				tvDistance.setText(order.getDistanceToOrderPlace());

				// print additional and comments
				String featureList = "";
				if (order.getFeatures() != null
						&& order.getFeatures().size() != 0) {

					for (String dop : order.getFeatures())
						featureList += dop + "; ";
					tvAdditionalTxt.setText(featureList);
				} else {
					tvAdditionalTxt.setText(ContextHelper.getInstance()
							.getCurrentContext().getString(R.string.noFeature));
				}
				comments = (!order.getComments().equals("")) ? order.getComments() : "nothing";
//				ivExtraInfoBtn.setImageResource(R.drawable.pencil);

				// set additional info images
				Boolean joker = (order.agentName != null && !order.agentName
						.equals("")) ? true : false;
				Boolean noCash = order.isNonCashPay();
				Boolean webOrder = (!order.getSourceWhence().equals("Phone")
						&& !order.getSourceWhence().equals("Skype") && !order
						.getSourceWhence().equals("Mail")) ? true : false;
				Boolean edditional = (order.getFeatures() != null && order
						.getFeatures().size() > 0) ? true : false;

				// set extra info images
				ivImageJoker.setVisibility(joker ? View.VISIBLE : View.GONE);
				ivImageNoCash.setVisibility(noCash ? View.VISIBLE : View.GONE);
				ivImageWebOrder
						.setVisibility(webOrder ? View.VISIBLE : View.GONE);
				// ivImageEd.setVisibility(edditional ? View.VISIBLE : View.GONE);
				ivImageEd.setVisibility(View.VISIBLE);

				//swipe for comments
				tvAdditionalTxt.setVisibility(View.GONE);

				List<View> pages = new ArrayList<View>();
				if (!comments.equals("nothing") && viewComments != null) {
					((TextView) viewComments.findViewById(R.id.commentTxt)).setText(comments);
					pages.add(viewComments);
				}
				if (!featureList.equals("") && viewFeatures != null) {
					((TextView) viewFeatures.findViewById(R.id.commentTxt)).setText(featureList);
					pages.add(viewFeatures);
				}

				if (pages.size() > 0) {
					SamplePagerAdapter mFragmentAdapter = new SamplePagerAdapter(pages);
					mPager.setOffscreenPageLimit(2);
					mPager.setAdapter(mFragmentAdapter);
					mIndicator.setViewPager(mPager);
				}

				mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

					}

					@Override
					public void onPageSelected(int position) {
						switch (position) {
							case 0:
								ivCommentsImage
										.setImageResource(R.drawable.comment_active);
								ivImageEd.setImageResource(R.drawable.additional_not_active);
								break;
							case 1:
								ivCommentsImage
										.setImageResource(R.drawable.comment_not_active);
								ivImageEd.setImageResource(R.drawable.additional_active);
								break;
						}
					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});

				mPager.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						mPager.getParent().requestDisallowInterceptTouchEvent(true);
						return false;
					}
				});

				//end swipe block

				ivExtraInfoBtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d("myLogs", "Click-Click");
						/*if (extraTxt.getVisibility() == View.GONE) {
							if (!comments.equals("nothing")) {
								extraTxt.setText(comments);
								tvAdditionalTxt.setVisibility(View.GONE);
								extraTxt.setVisibility(View.VISIBLE);
								ivCommentsImage
										.setImageResource(R.drawable.comment_active);
								ivImageEd.setImageResource(R.drawable.additional_not_active);
							}

						} else {
							tvAdditionalTxt.setVisibility(View.VISIBLE);
							extraTxt.setVisibility(View.GONE);
							ivCommentsImage
									.setImageResource(R.drawable.comment_not_active);
							ivImageEd.setImageResource(R.drawable.additional_active);

						}*/
						OwnOrder.setCurrentOrder(order);
						FragmentTransactionManager.getInstance().openFragment(FragmentPacket.OWN_ORDER);

					}
				});
				if (!isArchiveOrder) {
					// start timer
					//tvArriveTimerTxt.setVisibility(View.VISIBLE);
					llFlipClock.setVisibility(View.VISIBLE);
					orderTime = order.getDate();
					Log.d("myLogs",
							"orderTimer = " + Utils.dateToTimeString(orderTime));
					timerStopWatch = new Timer();
					timerStopWatch.schedule(new ArriveTimer(), 0, 500);
					tvDate.setText(Utils.dateToDateString(OrderManager
							.getInstance().getOrder(oldOrderId).getDate()));
					tvArriveTimerTxt.setText(Utils.dateToTimeString(OrderManager
							.getInstance().getOrder(oldOrderId).getDate()));

					setDetails(OrderManager.getInstance().getOrder(orderID)
							.getOrderFullDescOther());
					btnArrived.setVisibility(View.VISIBLE);
					btnConnDrivCl.setVisibility(View.VISIBLE);
					btnDo.setVisibility(View.VISIBLE);
				} else {
					//tvArriveTimerTxt.setVisibility(View.INVISIBLE);
					llFlipClock.setVisibility(View.INVISIBLE);
					tvArriveTimerTxt.setText("00:00");
					btnArrived.setVisibility(View.GONE);
					btnConnDrivCl.setVisibility(View.GONE);
					btnDo.setVisibility(View.GONE);
				}

				// set buttons
				if (order.getStatus() == Order.STATE_PERFORMING) {
					btnDo.setVisibility(order.arrived ? View.VISIBLE
							: View.GONE);
					btnConnDrivCl.setVisibility(View.VISIBLE);
					btnConnDrivCl.setEnabled((System.currentTimeMillis() - orderTime) > 0);

				}

				int routeType = 0;
				if (order.getAddress().size() < 1) {
					routeType = 1;
				}
				//there are start and end points
				if (order.getAddress().size() == 1) {
					routeType = 2;
				}
				//there are a lot of points in route
				if (order.getAddress().size() > 1) {
					routeType = 3;
				}
			}
		});

		/**
		 * Init rout from order and fill data to listView.
		 * */
		initRoutOrder(order.getOrderID());
		addressListView.setAdapter(mAdapter);
		//fill address listView
		if (mAdapter == null) {
			mAdapter = new AddressAdapter(mContext, route);
		}
		else {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.upDateList(route);
				}
			});
		}
	} //end fillViewsForOrders()

	private static void initRoutOrder(int orderID) {
		/**
		 * Getting all rout from current order and fill collection for adapter.
		 * */
		Log.d(LOG_TAG, "initRoutOrder()");
		Log.d(LOG_TAG, "orderId = " + orderID);
		Order currentOrder = OrderManager.getInstance().getOrder(orderID);

		//initialize routes list
		if (route == null) {
			route = new ArrayList<>();
		}

		if (currentOrder != null && route.size() == 0) {

			/** get Address from and setUp it */
			final Address fromAddress = new Address();
			fromAddress.setStreetName(currentOrder.getStreet());

			//get house number
			final String addressFact = (currentOrder.getAddressFact() != null
					|| !currentOrder.getAddressFact().equals("0")
					|| !currentOrder.getAddressFact().equals(""))
					? currentOrder.getAddressFact()
					: "";
			fromAddress.setHouse(addressFact);

			//Entrance
			fromAddress.setEntrance((currentOrder.getParade() != null
					&& !currentOrder.getParade().equals(""))
					? currentOrder.getParade()
					: "");

			//flat
			fromAddress.setFlat((currentOrder.getFlat() != null
					&& !currentOrder.getFlat().equals(""))
					? currentOrder.getFlat()
					: "");

			route.add(fromAddress);

			/** get subOrders */
			for (DispOrder.DispSubOrder subOrder : currentOrder.getAddress()) {
				Log.d(LOG_TAG, "SubOrder");
				final Address toAddress = new Address();
				String[] splitAddr = subOrder.to.split(",");
				final String curStreet = splitAddr[0];
				final String curHouse = splitAddr[1].trim();
				toAddress.setStreetName(curStreet);
				Log.d(LOG_TAG, curStreet);
				//getStreetThread.start();
				toAddress.setHouse(curHouse);

				route.add(toAddress);
			}
		}
	}

	private static String validationTextViewAddressTo(Order order) {
		String addressTo = ContextHelper.getInstance().getCurrentContext().getResources()
				.getString(R.string.str_address_to);

		if (order.getAddress().size() > 0) {
			addressTo = order.getAddress().get(order.getAddress().size() - 1).to;
		}

		StringBuilder address = new StringBuilder(
				ContextHelper.getInstance().getCurrentContext().getResources()
						.getString(R.string.str_address_to));

		return addressTo;
	}

	@NonNull
	private static String validationTextViewAddressFrom(Order order) {
		StringBuffer addressFrom = new StringBuffer();
//		addressFrom.append(order.getOrderID());
		addressFrom.append(order.getStreet());
		addressFrom.append(order.getHouse().equals("0")
				|| order.getHouse().equals("")
				|| order.getHouse() == null
				? "" : ", д." + order.getHouse());
		addressFrom.append(order.getFlat().equals("00")
				|| order.getFlat().equals("")
				|| order.getFlat() == null
				? "" : ", кв." + order.getFlat());
		addressFrom.append(order.getEntrance().equals("00")
				|| order.getEntrance().equals("")
				|| order.getEntrance() == null
				? "" : ", пар." + order.getEntrance());
		addressFrom.append(order.getBuilding().equals("00")
				|| order.getBuilding().equals("")
				|| order.getBuilding() == null
				? "" : ", корп." + order.getBuilding());

		return addressFrom.toString();
	}

	// arrive timer
	private static class ArriveTimer extends TimerTask {

		@Override
		public void run() {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {

				@Override
				public void run() {
					TimeZone mTimezone = Calendar.getInstance().getTimeZone();
					long currentTime = System.currentTimeMillis();
					long tz = mTimezone.getOffset(currentTime);
					currentTime += tz;
					long millis = Math.abs(currentTime - orderTime);
					int seconds = (int) (millis / 1000);
					int minutes = seconds / 60;
					seconds = seconds % 60;

					tvArriveTimerTxt.setText(String.format("%d:%02d", minutes,
							seconds));

					try {
						final int tenMin = Math.round(minutes / 10);
						if (tenMin != curTenMin) {
							curTenMin = tenMin;
							flip(tenMin, rlTenMinLayout);
							}
						int min = minutes - tenMin * 10;
						if (min != curMin) {
							curMin = min;
							flip(min, rlMinLayout);
						}
						int tenSec = Math.round(seconds / 10);
						if (tenSec != curTenSec) {
							curTenSec = tenSec;
							flip(tenSec, rlTenSecLayout);
						}
						int secForFlip = seconds - tenSec * 10;
						if (secForFlip != curSec) {
							curSec = secForFlip;
							flip(secForFlip, rlSecLayout);
						}
					} catch (Exception e) {
						Log.d("myLogs", "FlipClock error = " + e.getMessage());
						e.printStackTrace();
					}

				}
			});

		}

	}



	private static void displayTimer(final int orderID) {
		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			@Override
			public void run() {
				if (countDownTimer3 != null) {
					countDownTimer3.cancel();
				}

				Order order = OrderManager.getInstance().getOrder(orderID);
				long raznica = 0;
				if (order.getDate3() != 0) {
					raznica = order.getDate3() - new Date().getTime();
				} else {
					Log.d(LOG_TAG, "TimeZone.getDefault().getOffset(new Date().getTime()) = "
									+ TimeZone.getDefault().getOffset(new Date().getTime()));
					raznica = order.getDate()
							- new Date().getTime()
							- TimeZone.getDefault().getOffset(
									new Date().getTime());// *60*1000;//
															// (2*60*60000);
															// //отнимаем 2
															// часа, для украины
				}

				Log.d(LOG_TAG, "!!!! raznica " + raznica);

				s = SettingsFromXml.getInstance().getClientWaitTime() * 60000
						+ raznica;

				if (s <= 0) {
					btnArrived.setEnabled(true);
					btnArrived.setText("НЕТ КЛИЕНТА");
					btnConnDrivCl.setEnabled(true);

					// /////////////////

					if (OrderManager.getInstance().getOrder(orderID).dateNoClient == 0) {
						long dateNow = new Date().getTime();
						OrderManager.getInstance().setDateNoClient(orderID,
								(dateNow + s));
					}

					countDownTimer3 = new CountDownTimer(1000000, 1000) {
						public void onTick(long millisUntilFinished) {
							long ss;
							Order ord = OrderManager.getInstance().getOrder(orderID);
							long dateNow = new Date().getTime();
							// System.out.println("dnc - " + ord.dateNoClient +
							// " now - " + dateNow);

							ss = (new Date().getTime() - ord.dateNoClient) / 1000;

							// Log.e("tag", "ct1");

							/*
							 * if (ss >= 60) { long q = ss / 60; long w = ss -
							 * (60 * q); if (Long.toString(w).length() == 1) {
							 * btnArrived.setText("НЕТ КЛИЕНТА (" + q + ":0" + w
							 * + ")"); } else {
							 * btnArrived.setText("НЕТ КЛИЕНТА (" + q + ":" + w
							 * + ")"); } } else if (ss < 60) { if
							 * (Long.toString(ss).length() == 1) {
							 * btnArrived.setText("НЕТ КЛИЕНТА (0:0" + ss +
							 * ")"); } else {
							 * btnArrived.setText("НЕТ КЛИЕНТА (0:" + ss + ")");
							 * } }
							 */
							// !!!
							btnArrived.setText("НЕТ КЛИЕНТА");

						}

						public void onFinish() {
							btnArrived.setEnabled(false);
							btnArrived.setText("НЕТ КЛИЕНТА");

							btnArrived.setOnClickListener(null);
						}
					}.start();

					// /////////////////////

					btnArrived.setOnClickListener(noClientListener);

					return;
				} else {
					btnConnDrivCl.setEnabled(false);
				}

				// Log.d("timer", "btnTest - " + s + " s iss - " + s*1000 + " ms");
				countDownTimer3 = new CountDownTimer(s, 1000) {

					public void onTick(long millisUntilFinished) {

						btnArrived.setEnabled(false);

						long ss = millisUntilFinished / 1000;

						if (ss >= 60) {
							long q = ss / 60;
							long w = ss - (60 * q);
							if (Long.toString(w).length() == 1) {
								btnArrived.setText(q + ":0" + w);
							} else {
								btnArrived.setText(q + ":" + w);
							}
						} else if (ss < 60) {
							if (Long.toString(ss).length() == 1) {
								btnArrived.setText("0:0" + ss);
							} else {
								btnArrived.setText("0:" + ss);
							}
						}

					}

					public void onFinish() {
						btnArrived.setEnabled(true);
						btnArrived.setText("НЕТ КЛИЕНТА");

						// /////////////////
						if (countDownTimer3 != null) {
							countDownTimer3.cancel();
						}

						if (OrderManager.getInstance().getOrder(orderID).dateNoClient == 0) {
							long dateNow = new Date().getTime();
							OrderManager.getInstance().setDateNoClient(orderID,
									(dateNow));
						}

						// Log.d("timer", "btnTest - " + s + " s iss - " + s*1000 +
						// " ms");
						countDownTimer3 = new CountDownTimer(1000000, 1000) {

							public void onTick(long millisUntilFinished) {

								long ss;
								Order ord = OrderManager.getInstance()
										.getOrder(orderID);

								ss = (new Date().getTime() - ord.dateNoClient) / 1000;

								// Log.e("tag", "ct2");

								if (ss >= 60) {
									long q = ss / 60;
									long w = ss - (60 * q);
									if (Long.toString(w).length() == 1) {
										btnArrived.setText("НЕТ КЛИЕНТА (" + q
												+ ":0" + w + ")");
									} else {
										btnArrived.setText("НЕТ КЛИЕНТА (" + q
												+ ":" + w + ")");
									}
								} else if (ss < 60) {
									if (Long.toString(ss).length() == 1) {
										btnArrived.setText("НЕТ КЛИЕНТА (0:0"
												+ ss + ")");
									} else {
										btnArrived.setText("НЕТ КЛИЕНТА (0:"
												+ ss + ")");
									}
								}

							}

							public void onFinish() {
								btnArrived.setEnabled(true);
								btnArrived.setText("НЕТ КЛИЕНТА");

								btnArrived.setOnClickListener(null);
							}
						}.start();
						// ////////////////

						btnArrived.setOnClickListener(noClientListener);
					}
				}.start();

			}
		});
	}

	protected static boolean isBeznalOrKilometrazh(int orderId) {
		boolean res = false;
		Order ord = OrderManager.getInstance().getOrder(orderId);
		if (ord.isNonCashPay()) {
			return true;
		}
		for (DispSubOrder disp : ord.getAddress()) {
			if (disp.tariff.equals("Километраж")) {
				return true;
			}
		}

		return res;
	}

	private static void writeToArch(int orderID) {
		Log.d(LOG_TAG, " writeToArch() Going to write order in DB");
		Order ord = OrderManager.getInstance().getOrder(orderID);
		ContentValues cv = new ContentValues();

		DbArchHelper dbHelper = new DbArchHelper(ContextHelper.getInstance()
				.getCurrentContext());
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String arcive = ord.toArchive();
		cv.put("idorder", orderID);
		cv.put("fulldescline", ord.toArchive());
		cv.put("fulldesc", ord.getOrderFullDesc());
		cv.put("fulldescother", ord.getOrderFullDescOther());

		/*
		 * Calendar c = Calendar.getInstance();
		 * c.setTimeInMillis(ord.getDate()); c.add(Calendar.DATE, 6);
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * System.out.println("111 " + sdf.format(c.getTime())); cv.put("date",
		 * c.getTime().getTime());
		 */

		cv.put("date", ord.getDate());
		// вставляем запись и получаем ее ID

		// check if there is already order with such ID
		// if so delete old one and insert new else just insert new one
		long rowID = -1;
		String[] columns = new String[] { "idorder" };
		String selection = "idorder = ?";
		String[] selectionArgs = new String[] { String.valueOf(orderID) };
		Cursor cr = db.query("order_arch", columns, selection, selectionArgs,
				null, null, null);
		if (cr.moveToFirst()) {
			db.delete("order_arch", selection, selectionArgs);
			rowID = db.insert("order_arch", null, cv);
		} else {
			rowID = db.insert("order_arch", null, cv);
		}

		Log.d(LOG_TAG, " writeToArch() row inserted, ID = " + rowID);

		dbHelper.close();
	}



	private static void imFree() {
		Log.d(LOG_TAG, "imFree() COUNT "
				+ OrderManager.getInstance().getCountOfOrdersByState(Order.STATE_PERFORMING));

		if (OrderManager.getInstance().getCountOfOrdersByState(
				Order.STATE_PERFORMING) == 0) {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {

					// TextView state_driver = (TextView)
					// ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.state_driver);
					// state_driver.setText("СВОБОДЕН");
					// state_driver.setTextColor(Color.parseColor("#009900"));
					StateObserver.getInstance().setDriverState(
							StateObserver.DRIVER_FREE);

					TextView busy2 = (TextView) ContextHelper.getInstance()
							.getCurrentActivity().findViewById(R.id.btn_busy);
					busy2.setText("Занят");
					busy2.setEnabled(true);

					ServerData.getInstance().isFree = true;

					btnConnDrivCl.setEnabled(false);

					byte[] body = RequestBuilder
							.createGetBalanceData(ServerData.getInstance()
									.getPeopleID());
					byte[] data = RequestBuilder.createSrvTransfereData(
							RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
									.getInstance().getSrvID(),
							RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
									.getInstance().getGuid(), true, body);
					ConnectionHelper.getInstance().send(data);

				}
			});
		}
	}

	private static void setDetails(final String fullDescOther) {
		btnDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fullDescOther.equals("")) {
					AlertDHelper.showDialogOk("Детали отсутствуют");
				} else {
					AlertDHelper.showDialogOk(fullDescOther.replaceAll("<br>", "\n"));
				}
			}
		});

	}

	public static class SamplePagerAdapter extends PagerAdapter {
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
		public int getCount() {
			return pages.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}


		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

//	public static class PlaceHolder extends Fragment {
//
//		public PlaceHolder() {
//		}
//
//		public static PlaceHolder newInstance(String commentType) {
//			PlaceHolder fragment = new PlaceHolder();
//			return fragment;
//		}
//
//		@Nullable
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//			View currentView = inflater.inflate(R.layout.order_comment_fragment, container, false);
//
//			return currentView;
//		}
//	}

}