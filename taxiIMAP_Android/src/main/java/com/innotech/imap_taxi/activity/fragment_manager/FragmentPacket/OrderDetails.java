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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.core.StateObserver;
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

	private static final String TAG = "Order_details";
	private static AlertDialog dialog;
	private static TextView region;
	private static TextView distance;
	private static TextView edditionalTxt;
	protected static String comments;
	private static ImageView extraInfoBtn;
	private static ImageView imageJoker, imageWebOrder, imageNoCash, imageEd;
	// private static RelativeLayout edditionalInfoLayout;
	private static TextView extraTxt;
	private static TextView arriveTimerTxt;
	private static LinearLayout colorLayout;
	private static Timer timerStopWatch;

	private static long orderTime;
	private static ImageView commentsImage;
	private static TextView autoClassTxt;

	private static RelativeLayout tenMinLayout, minLayout, tenSecLayout,
			secLayout;
	private static LinearLayout flipClock;
	
	private static int curTenMin;
	private static int curMin;
	private static int curTenSec;
	private static int curSec;

	private static ViewPager mPager;

	// private static Button btnArrived1;

	public OrderDetails() {
		super(ORDER_DETAILS);
	}

	private static int oldOrderId = -1;
	private static TextView txtDetails, adressFrom1, adressFrom2, adressTo,
			date, time, price;
	public static Button btnArrived, btnDo, btnMap, btnConnDrivCl, btnDetails,
			btnBack, btnCancel, btnAccept/* , btnNoClient, btnWellDone */;
	private static LinearLayout route, routeInfoLayout;
	private static RouteView rw;
	public static boolean isArch;
	private static CountDownTimer ct3;
	private static OnClickListener doListener, doneListener, arrivedListener,
			noClientListener, cancelListner, acceptListner;
	private static long s;
	private static int orderID = -1;
	private static Order archOrder = null;
	private static boolean isArchiveOrder;

	private static View commentsView;
	private static View featuresView;
	private static PageIndicator mIndicator;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (isArchiveOrder && archOrder != null) {
			dispArchOrder(archOrder);
		} else if (OrderManager.getInstance().getOrder(orderID) != null) {
			isArch = true;
			setOrderId(OrderDetails.orderID);

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.activity_order_details_new1,
				container, false);

		// define Layouts for FlipClock
		tenMinLayout = (RelativeLayout) myView.findViewById(R.id.tenMinLayout);
		minLayout = (RelativeLayout) myView.findViewById(R.id.minLayout);
		tenSecLayout = (RelativeLayout) myView.findViewById(R.id.tenSecLayout);
		secLayout = (RelativeLayout) myView.findViewById(R.id.secLayout);

		txtDetails = (TextView) myView.findViewById(R.id.txt_details);
		autoClassTxt = (TextView) myView.findViewById(R.id.autoClassTxt);
		colorLayout = (LinearLayout) myView.findViewById(R.id.colorLayout);
		routeInfoLayout = (LinearLayout) myView
				.findViewById(R.id.routeInfoLayout);
		PaintDrawable p = (PaintDrawable) GraphUtils
				.getEtherRouteGradient(routeInfoLayout);
		routeInfoLayout.setBackground((Drawable) p);

		region = (TextView) myView.findViewById(R.id.regionTxt);
		distance = (TextView) myView.findViewById(R.id.distTxt);
		edditionalTxt = (TextView) myView.findViewById(R.id.edditionalTxt);
		extraInfoBtn = (ImageView) myView.findViewById(R.id.extraInfoBtn);

		arriveTimerTxt = (TextView) myView.findViewById(R.id.timerTxt);
		flipClock = (LinearLayout) myView.findViewById(R.id.flipClockLayout);

		imageJoker = (ImageView) myView.findViewById(R.id.imageJoker);
		imageWebOrder = (ImageView) myView.findViewById(R.id.imageWeb);
		imageNoCash = (ImageView) myView.findViewById(R.id.imageNoCash);

		imageEd = (ImageView) myView.findViewById(R.id.imageEd);
		commentsImage = (ImageView) myView.findViewById(R.id.commentImage);

		rw = (RouteView) myView
				.findViewById(R.id.routeCustom);
		extraTxt = (TextView) myView.findViewById(R.id.extraTxt);

		adressFrom1 = (TextView) myView.findViewById(R.id.adrsFrom1);
		adressFrom2 = (TextView) myView.findViewById(R.id.adrsFrom2);
		adressTo = (TextView) myView.findViewById(R.id.adrsTo);
		date = (TextView) myView.findViewById(R.id.date);
		time = (TextView) myView.findViewById(R.id.time);
		time.setText("");
		price = (TextView) myView.findViewById(R.id.costTxt);
		route = (LinearLayout) myView.findViewById(R.id.routeLL);

		btnArrived = (Button) myView.findViewById(R.id.btn_arrived);
		// btnArrived1 = (Button) myView.findViewById(R.id.btn_arrived1);
		btnDo = (Button) myView.findViewById(R.id.btn_do);
		btnConnDrivCl = (Button) myView
				.findViewById(R.id.btn_connectDriverClient);

		// btnNoClient = (Button) myView.findViewById(R.id.btn_noClient);
		// btnWellDone = (Button) myView.findViewById(R.id.btnWellDone);

		btnDetails = (Button) myView.findViewById(R.id.btn_details);
		// btnMap = (Button) myView.findViewById(R.id.btn_map);
		btnBack = (Button) myView.findViewById(R.id.btn_back);
		btnCancel = (Button) myView.findViewById(R.id.btn_cancel_ord);
		btnAccept = (Button) myView.findViewById(R.id.btn_accept_order);

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

		adressFrom1.setTypeface(t);
		adressTo.setTypeface(t);
		region.setTypeface(t);
		edditionalTxt.setTypeface(t);

		t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/BebasNeueRegular.ttf");
		distance.setTypeface(t);
		price.setTypeface(t);

		t = Typeface
				.createFromAsset(ContextHelper.getInstance()
						.getCurrentContext().getAssets(),
						"fonts/TickingTimebombBB.ttf");
		arriveTimerTxt.setTypeface(t);

		route.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MapFragment.orderId = -1;
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.MAP);
			}
		});

		route.setEnabled(true);
		btnDetails.setEnabled(true);
		btnCancel.setEnabled(true);
		btnAccept.setEnabled(false);

		mPager = (ViewPager) myView.findViewById(R.id.comment_container);
		commentsView = inflater.inflate(R.layout.order_comment_fragment, null);
		featuresView = inflater.inflate(R.layout.order_comment_fragment, null);
		mIndicator = (CirclePageIndicator) myView
				.findViewById(R.id.indicator);

		return myView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
				ContextHelper.getInstance().getCurrentContext()).getString(
				UserSettingActivity.KEY_TEXT_SIZE, "")) != 0) {
			txtDetails
					.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer
							.parseInt(PreferenceManager
									.getDefaultSharedPreferences(
											ContextHelper.getInstance()
													.getCurrentContext())
									.getString(
											UserSettingActivity.KEY_TEXT_SIZE,
											"")) + 14);
		}

	}

	@Override
	public void onStop() {
		super.onStop();

		if (ct3 != null) {
			ct3.cancel();
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

	public static void setOrderId(final int orderID) {
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

		// for btnTest accept order without button
		/*OrderManager.getInstance().getOrder(orderID).accepted = true;
		OrderManager.getInstance().getOrder(orderID).arrived = false;
		btnArrived.setEnabled(true);
		btnAccept.setEnabled(false);*/

		route.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ----
				// RequestHelper.getRoutes(orderID);
				System.out.println("getRoutes" + orderID);
				// ----
				MapFragment.orderId = orderID;
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

				if (ct3 != null) {
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

				if (ct3 != null) {
					ct3.cancel();
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
				Log.d(TAG, "Check status = " + OrderManager.getInstance().getOrder(orderID));
				ord.accepted = true;
				ord.arrived = false;
				setUpButtonsState(ord);
				/*btnArrived.setEnabled(true);
				btnAccept.setEnabled(false);*/
			}
		};

		if (ct3 != null) {
			ct3.cancel();
		}

		btnAccept.setOnClickListener(acceptListner);
		btnArrived.setOnClickListener(arrivedListener);
		btnDo.setOnClickListener(doListener);
		btnCancel.setOnClickListener(cancelListner);
		// btnArrived1.setOnClickListener(arrivedListener);


		System.out.println("f - " + ord.getFolder());

		Log.d(TAG, "Order info: state = " + ord.getStatus() + ", arrived = " + ord.arrived + ", accepted = " + ord.accepted);

		if (ord.getStatus() == Order.STATE_KRYG_ADA) {
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					route.setEnabled(false);

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
											.equals(Order.FOLDER_DOIN)
									|| !OrderManager.getInstance()
											.getOrder(orderID).getOrderType()
											.equals("SendedByDispatcher")) {

								Log.d(TAG, "condition1");

								ContextHelper.getInstance()
										.runOnCurrentUIThread(new Runnable() {
											@Override
											public void run() {

												route.setEnabled(true);

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
											.equals(Order.FOLDER_DOIN)
									|| OrderManager.getInstance()
											.getOrder(orderID).getFolder()
											.equals("ReceiveDriver")// свежие
																	// правки
									|| !OrderManager.getInstance()
											.getOrder(orderID).getOrderType()
											.equals("SendedByDispatcher")) {

								Log.d(TAG, "condition2");
								// btnArrived.setEnabled(false);
								btnArrived.setText("На месте");

								btnDo.setEnabled(true);
								btnDo.setOnClickListener(doListener);
								btnDo.setText("Выполняю");
								btnCancel.setEnabled(true);
								Log.d("STATE", "Order_ID = " + orderID);
								displayTimer(orderID);
							} else {
								Log.d(TAG, "condition3");

								btnAccept.setEnabled(true);
								btnArrived.setEnabled(false);
								btnDo.setEnabled(false);
							}
						}
					});
		}

		// начинается пздц

		if (ord.getFolder().equals(Order.FOLDER_DOIN)) {

			OrderManager.getInstance().changeOrderState(orderID,
					Order.STATE_PERFORMING);
			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					if (!ord.isFromServer() && !isArch) {
						AlertDHelper
								.showDialogOk("Заказ перенесен в папку выполняемые");
					}

					Log.d(TAG, "condition4");


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

			Log.d(TAG, "condition5");
			OrderManager.getInstance().changeOrderState(orderID,
					Order.STATE_PERFORMED);

			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {
					if (!ord.isFromServer() && !isArch) {
						AlertDHelper
								.showDialogOk("Заказ перенесен в папку выполненные");
					}

					if (ct3 != null) {
						ct3.cancel();
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

			Log.d(TAG, "condition5");
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

			Log.d(TAG, "condition6");

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
					if (ct3 != null) {
						ct3.cancel();
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
							.equals(Order.FOLDER_DOIN)
					|| !OrderManager.getInstance().getOrder(orderID)
							.getOrderType().equals("SendedByDispatcher")) {

				Log.d(TAG, "condition7");
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

				Log.d(TAG, "condition8");

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

	}

	private static void setUpButtonsState(Order currentOrder) {
		//hide all buttons and then show only needed ones

		Log.d(TAG, "OrderState = " + getOrderState(currentOrder));
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
				btnArrived.setText("На месте");
				btnArrived.setEnabled(true);
				btnArrived.setOnClickListener(arrivedListener);
				btnConnDrivCl.setVisibility(View.VISIBLE);
				break;
			case ARRIVED:
				hideAllButtons();
				//show do, no Client, connect with Client
				btnDo.setVisibility(View.VISIBLE);
				btnDo.setText("Выполняю");
				btnConnDrivCl.setVisibility(View.VISIBLE);
				btnArrived.setVisibility(View.VISIBLE);
				btnArrived.setText("НЕТ КЛИЕНТА");
				btnArrived.setOnClickListener(noClientListener);
				break;
			case PERFORMING:
				hideAllButtons();
				//show big red button DONE
				btnDo.setVisibility(View.VISIBLE);
				btnDo.setText("Выполнил");
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

	//possible states of order due to view and controls
	private static final int INCOMING = 1;
	private static final int ACCEPTED = 2;
	private static final int ARRIVED = 3;
	private static final int PERFORMING = 4;
	private static final int DONE = 5;
	private static final int UNDEFINED = -1;

	private static int getOrderState(Order currentOrder) {
		int orderState = UNDEFINED;
		//check if order is DONE
		if (currentOrder.getFolder().equals(Order.FOLDER_DONE)) {
			return DONE;
		}
		//check if order is currently performing
		if (currentOrder.getFolder().equals(Order.FOLDER_DOIN)) {
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
		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {

			@Override
			public void run() {
				autoClassTxt.setText(convertToVerticalView(order.autoClass));
				colorLayout.setBackgroundColor(order.colorClass);
				String adressFact = (order.getAddressFact() != null
						|| !order.getAddressFact().equals("0") || !order
						.getAddressFact().equals("")) ? order.getAddressFact()
						: "";
				adressFrom1.setText(order.getStreet() + ", " + adressFact);
				// adressFrom2.setText(OrderManager.getInstance().getOrder(oldOrderId).getAddress().get(0).from);
				String addressTo = "No addressTo";
				if (order.getAddress().size() > 0) {
					addressTo = order.getAddress().get(
							order.getAddress().size() - 1).to;
				}

				adressTo.setText(addressTo);
				String regionFromOrder = (order.getRegion() != null && !order
						.getRegion().equals("")) ? order.getRegion()
						: ContextHelper.getInstance().getCurrentContext()
								.getString(R.string.noRegion);
				region.setText(regionFromOrder);

				// set price
				float priceFloat = (order.getFare() == 0) ? 0 : order.getFare();
				String price = String.format("%.2f", priceFloat);
				OrderDetails.price.setText(getPriceFormat(price));

				// print distance
				distance.setText(order.getDistanceToOrderPlace());

				// print additional and comments
				String featureList = "";
				if (order.getFeatures() != null
						&& order.getFeatures().size() != 0) {

					for (String dop : order.getFeatures())
						featureList += dop + "; ";
					edditionalTxt.setText(featureList);
				} else {
					edditionalTxt.setText(ContextHelper.getInstance()
							.getCurrentContext().getString(R.string.noFeature));
				}
				comments = (!order.getComments().equals("")) ? order
						.getComments() : "nothing";
				extraInfoBtn.setImageResource(R.drawable.pencil);

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
				imageJoker.setVisibility(joker ? View.VISIBLE : View.GONE);
				imageNoCash.setVisibility(noCash ? View.VISIBLE : View.GONE);
				imageWebOrder
						.setVisibility(webOrder ? View.VISIBLE : View.GONE);
				// imageEd.setVisibility(edditional ? View.VISIBLE : View.GONE);
				imageEd.setVisibility(View.VISIBLE);

				//swipe for comments
				edditionalTxt.setVisibility(View.GONE);

				List<View> pages = new ArrayList<View>();
				if (!comments.equals("nothing") && commentsView != null) {
					((TextView) commentsView.findViewById(R.id.commentTxt)).setText(comments);
					pages.add(commentsView);
				}
				if (!featureList.equals("") && featuresView != null) {
					((TextView) featuresView.findViewById(R.id.commentTxt)).setText(featureList);
					pages.add(featuresView);
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
								commentsImage
										.setImageResource(R.drawable.comment_active);
								imageEd.setImageResource(R.drawable.additional_not_active);
								break;
							case 1:
								commentsImage
										.setImageResource(R.drawable.comment_not_active);
								imageEd.setImageResource(R.drawable.additional_active);
								break;
						}
					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});

				//end swipe block

				extraInfoBtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Log.d("myLogs", "Click-Click");
						/*if (extraTxt.getVisibility() == View.GONE) {
							if (!comments.equals("nothing")) {
								extraTxt.setText(comments);
								edditionalTxt.setVisibility(View.GONE);
								extraTxt.setVisibility(View.VISIBLE);
								commentsImage
										.setImageResource(R.drawable.comment_active);
								imageEd.setImageResource(R.drawable.additional_not_active);
							}

						} else {
							edditionalTxt.setVisibility(View.VISIBLE);
							extraTxt.setVisibility(View.GONE);
							commentsImage
									.setImageResource(R.drawable.comment_not_active);
							imageEd.setImageResource(R.drawable.additional_active);

						}*/
						OwnOrder.setCurrentOrder(order);
						FragmentTransactionManager.getInstance().openFragment(FragmentPacket.OWN_ORDER);

					}
				});
				if (!isArchiveOrder) {
					// start timer
					//arriveTimerTxt.setVisibility(View.VISIBLE);
					flipClock.setVisibility(View.VISIBLE);
					orderTime = order.getDate();
					Log.d("myLogs",
							"orderTimer = " + Utils.dateToTimeString(orderTime));
					timerStopWatch = new Timer();
					timerStopWatch.schedule(new OrderDetails.arriveTimer(), 0,
							500);
					date.setText(Utils.dateToDateString(OrderManager
							.getInstance().getOrder(oldOrderId).getDate()));
					arriveTimerTxt.setText(Utils.dateToTimeString(OrderManager
							.getInstance().getOrder(oldOrderId).getDate()));

					setDetails(OrderManager.getInstance().getOrder(orderID)
							.getOrderFullDescOther());
					btnArrived.setVisibility(View.VISIBLE);
					btnConnDrivCl.setVisibility(View.VISIBLE);
					btnDo.setVisibility(View.VISIBLE);
				} else {
					//arriveTimerTxt.setVisibility(View.INVISIBLE);
					flipClock.setVisibility(View.INVISIBLE);
					arriveTimerTxt.setText("00:00");
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
				//btnTest to draw route
				
				rw.setRouteType(routeType);
				//end btnTest
			}
		});
	}

	// arrive timer
	private static class arriveTimer extends TimerTask {

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

					arriveTimerTxt.setText(String.format("%d:%02d", minutes,
							seconds));
					
					try {
						final int tenMin = Math.round(minutes / 10);
						if (tenMin != curTenMin) {
							curTenMin = tenMin;
							flip(tenMin, tenMinLayout);
							}
						int min = minutes - tenMin * 10;
						if (min != curMin) {
							curMin = min;
							flip(min, minLayout);
						}
						int tenSec = Math.round(seconds / 10);
						if (tenSec != curTenSec) {
							curTenSec = tenSec;
							flip(tenSec, tenSecLayout);
						}
						int secForFlip = seconds - tenSec * 10;
						if (secForFlip != curSec) {
							curSec = secForFlip;
							flip(secForFlip, secLayout);
						}
					} catch (Exception e) {
						Log.d("myLogs", "FlipClock error = " + e.getMessage());
						e.printStackTrace();
					}

				}
			});

		}

	}
	
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

	private static void displayTimer(final int orderID) {
		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			@Override
			public void run() {

				if (ct3 != null) {
					ct3.cancel();
				}

				Order order = OrderManager.getInstance().getOrder(orderID);
				long raznica = 0;
				if (order.getDate3() != 0) {
					raznica = order.getDate3() - new Date().getTime();
				} else {
					Log.d("tag-tag-tag",
							"TimeZone.getDefault().getOffset(new Date().getTime()) = "
									+ TimeZone.getDefault().getOffset(
											new Date().getTime()));
					raznica = order.getDate()
							- new Date().getTime()
							- TimeZone.getDefault().getOffset(
									new Date().getTime());// *60*1000;//
															// (2*60*60000);
															// //отнимаем 2
															// часа, для украины
				}

				Log.e("tag", "!!!! raznica " + raznica);

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

					ct3 = new CountDownTimer(1000000, 1000) {

						public void onTick(long millisUntilFinished) {

							long ss;
							Order ord = OrderManager.getInstance().getOrder(
									orderID);

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
				ct3 = new CountDownTimer(s, 1000) {

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
						if (ct3 != null) {
							ct3.cancel();
						}

						if (OrderManager.getInstance().getOrder(orderID).dateNoClient == 0) {
							long dateNow = new Date().getTime();
							OrderManager.getInstance().setDateNoClient(orderID,
									(dateNow));
						}

						// Log.d("timer", "btnTest - " + s + " s iss - " + s*1000 +
						// " ms");
						ct3 = new CountDownTimer(1000000, 1000) {

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

		Log.d("myLogs", "Going to write order in DB");
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

		Log.d("DbTest", "row inserted, ID = " + rowID);

		dbHelper.close();
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

	private static void imFree() {

		System.out.println("COUNT _________ COUNT _ "
				+ OrderManager.getInstance().getCountOfOrdersByState(
						Order.STATE_PERFORMING));

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
					txtDetails.setText(Html.fromHtml(OrderManager.getInstance()
							.getOrder(oldOrderId).getOrderFullDesc()));
					setDetails(OrderManager.getInstance().getOrder(oldOrderId)
							.getOrderFullDescOther());
				}
			});
		}
	}

	public static void dispArchOrder(final Order order) {
		if (order != null) {
			isArchiveOrder = true;
			archOrder = order;
			time.setText("00:00");
			time.setVisibility(View.INVISIBLE);
			btnDo.setVisibility(View.GONE);
			btnArrived.setVisibility(View.GONE);
			btnConnDrivCl.setVisibility(View.GONE);

			if (archOrder != null) {
				fillViewsForOrder(archOrder);
			} else {
				Log.d("myLogs", "ArchDisplay problems");
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

	private static void setDetails(final String fullDescOther) {

		btnDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (fullDescOther.equals("")) {
					AlertDHelper.showDialogOk("Детали отсутствуют");
				} else {
					AlertDHelper.showDialogOk(fullDescOther.replaceAll("<br>",
							"\n"));
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

	public static class PlaceHolder extends Fragment {

		public PlaceHolder() {
		}

		public static PlaceHolder newInstance(String commentType) {
			PlaceHolder fragment = new PlaceHolder();
			return fragment;
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View currentView = inflater.inflate(R.layout.order_comment_fragment, container, false);

			return currentView;
		}
	}

}