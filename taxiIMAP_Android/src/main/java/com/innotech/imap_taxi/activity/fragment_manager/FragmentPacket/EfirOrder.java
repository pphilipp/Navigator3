package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.datamodel.SettingsFromXml;
import com.innotech.imap_taxi.graph_utils.GraphUtils;
import com.innotech.imap_taxi.graph_utils.RouteView;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi.utile.NotificationService;
import com.innotech.imap_taxi.voice.CommandsRecognitionService;
import com.innotech.imap_taxi3.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA. User: u27 Date: 9/23/13 Time: 1:49 PM To change
 * this template use File | Settings | File Templates.
 */
public class EfirOrder extends FragmentPacket {

	public static int orderId = -1;
	private static Button btn5, btn10, btn15, btn20, btn25, btn30, btnAccept;
	private static TextView txtDetails, date, time, region, distance, fromTxt,
			toTxt, costTxt, edditionalTxt, declineTxt, timerTxt, extraTxt,
			autoClassTxt;
	private static ImageView imgSource, commentsImage;
	private static RouteView rw;
	private static boolean cansel = false;
	// private static Button btnCancel;
	private static LinearLayout btnCancel;
	private static RelativeLayout edditionalInfoLayout, extraInfoLayout;
	static LinearLayout colorLayout;
	static ImageView imageFrom;
	static ImageView imageJoker, imageWebOrder, imageNoCash, imageEd,
			extraInfoBtn;
	static CountDown timer;
	protected static String comments;
	private static boolean checkOrderId = false;
	private static int sec;

	

	private boolean voiceEnable = false;

	private static final String[] FIVE_MINUTES_PHRASES = { "5 минут",
			"5 хвилин", "пять минут", "п’ять хвилин" };
	private static final String[] TEN_MINUTES_PHRASES = { "10 минут",
			"10 хвилин", "десять минут", "десять хвилин" };
	private static final String[] FIFTEEN_MINUTES_PHRASES = { "15 минут",
			"15 хвилин", "пятнадцать минут", "п’ятнадцять хвилин" };
	private static final String[] TWENTY_MINUTES_PHRASES = { "20 минут",
			"20 хвилин", "двадцать минут", "двадцять хвилин" };
	private static final String[] TWENTY_FIVE_MINUTES_PHRASES = { "25 минут",
			"25 хвилин", "двадцать пять минут", "двадцять п’ять хвилин" };
	private static final String[] THIRTY_MINUTES_PHRASES = { "30 минут",
			"30 хвилин", "тридцать минут", "тридцять хвилин" };

	private final ArrayList<String[]> commands = new ArrayList<String[]>();

	public final static String BROADCAST_ACTION = "voiceAction";

	BroadcastReceiver voiceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getExtras() != null) {
				ArrayList<String> phraseList = intent
						.getStringArrayListExtra("phraseList");
				final int resIndex = compareCommands(phraseList);
				if (resIndex != -1) {
					Log.d("IMAP_Speech", "Going to perfome command: "
							+ commands.get(resIndex)[0] + ", orderID = "
							+ orderId + ", index = " + resIndex);
					ContextHelper.getInstance().runOnCurrentUIThread(
							new Runnable() {

								@Override
								public void run() {
									switch (resIndex) {
									case 0:
										answerOrder(orderId, 5);
										break;
									case 1:
										answerOrder(orderId, 10);
										break;
									case 2:
										answerOrder(orderId, 15);
										break;
									case 3:
										answerOrder(orderId, 20);
										break;
									case 4:
										answerOrder(orderId, 25);
										break;
									case 5:
										answerOrder(orderId, 30);
										break;
									}
								}
							});
					Toast.makeText(
							ContextHelper.getInstance().getCurrentContext(),
							"Команда принята!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							ContextHelper.getInstance().getCurrentContext(),
							"Команда не распознана!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	};

	/**
	 * Compare recognized phrases with list of commands.
	 * 
	 * @param phraseList
	 *            - list of recognized phrases
	 * @return - onMatch returns index of matched array of commands
	 */
	public int compareCommands(ArrayList<String> phraseList) {

		int resultCommandIndex = -1;
		String resultCommand = "";
		// log all rec
		for (String phrase : phraseList)
			Log.d("IMAP_Speech", "Recognized phrase = " + phrase);
		int index = 0;
		for (String[] setOfCommands : commands) {
			for (String command : setOfCommands)
				for (String phrase : phraseList)
					if (command.toLowerCase().equals(phrase.toLowerCase())) {
						resultCommandIndex = index;
						resultCommand = command;
						break;
					}
			index++;
		}

		return resultCommandIndex;
	}

	public EfirOrder() {
		super(ORDER);
	}

	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	//
	// Log.i("FRAGMENT", "FRAGMENT onSaveInstanceState");
	// }

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

	public static class CountDown extends CountDownTimer {

		public CountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			sec = (int) millisUntilFinished / 1000;
			/*
			 * btnCancel.setText(ContextHelper.getInstance().getCurrentContext()
			 * .getString(R.string.decline) + " " + sec);
			 */
			timerTxt.setText(String.valueOf(sec));
			

		}

	}

	public static void setOrderId(final int orderId2) {

		cansel = false;

		System.out.println("orderid - " + orderId);
		// !!!!orderId < 0
		if (FragmentTransactionManager.getInstance().getId() != FragmentPacket.ORDER) {
			orderId = orderId2;
			checkOrderId = true;
			final Order order = OrderManager.getInstance().getOrder(orderId);

			ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
				@Override
				public void run() {

					// txtDetails.setText(Html.fromHtml(order.getOrderFullDesc()));
					time.setText(Utils.dateToTimeString(order.getDate()));
					// date.setText(Utils.dateToDateString(order.getDate()));
					String regionFromOrder = (order.getRegion() != null && !order
							.getRegion().equals("")) ? order.getRegion()
							: ContextHelper.getInstance().getCurrentContext()
									.getString(R.string.noRegion);
					region.setText(regionFromOrder);
					// get driver time and set it on accept button
					btnAccept.setText(ContextHelper.getInstance()
							.getCurrentContext().getString(R.string.accept)
							+ "["
							+ SettingsFromXml.getInstance().getDriverTime()
							+ "]");
					// set timer on cancel button
					Log.d("XML", "FirstTimeSearch = "
							+ SettingsFromXml.getInstance()
									.getFirstTimeSearch());
					Log.d("XML", "Own mOrders = "
							+ SettingsFromXml.getInstance().isShowYourOrders());
					int millis = (SettingsFromXml.getInstance()
							.getFirstTimeSearch() != 0) ? SettingsFromXml
							.getInstance().getFirstTimeSearch() : 20000;
					long timeDifference = System.currentTimeMillis()
							- order.getReciveTime();
					millis = (int) (((millis - timeDifference) > 0) ? (millis - timeDifference)
							: 0);
					if (timer != null)
						timer.cancel();
					timer = new CountDown(millis, 1000);
					timer.start();
					// setDetailsOther(order.getOrderFullDescOther());
					System.out.println("SOURCE ----------------- SOURCE _"
							+ order.getSourceWhence() + "_");
					int img = getOrderSourceImage(order.getSourceWhence());
					/*
					 * if (img != -1) { imgSource.setImageResource(img); }
					 */
					float distToPoint = (order.distanceToPointOfDelivery != 0) ? order.distanceToPointOfDelivery / 1000
							: 0;
					// TODO get and set distance
					distToPoint = 0.43586f;
					String dist = (distToPoint > 100) ? ">100"
							+ ContextHelper.getInstance().getCurrentContext()
									.getString(R.string.km) : String.format(
							"%.1f", distToPoint)
							+ ContextHelper.getInstance().getCurrentContext()
									.getString(R.string.km);
					SpannableString ss = null;
					ss = new SpannableString(dist);
					ss.setSpan(new RelativeSizeSpan(0.6f), dist.length() - 2,
							dist.length(), 0);
					ss.setSpan(new ForegroundColorSpan(ContextHelper
							.getInstance().getCurrentContext().getResources()
							.getColor(R.color.greyText)), dist.length() - 2,
							dist.length(), 0);
					distance.setText(ss);

					String addressTo = "";
					String addressFrom = (order.getAddress() != null) ? order
							.getAddressFact() : "No addressFrom";

					String street = (order.getStreet() != null) ? order
							.getStreet() : "No adress";
					String adressFact = (order.getAddressFact() != null
							|| !order.getAddressFact().equals("0") || !order
							.getAddressFact().equals("")) ? order
							.getAddressFact() : "";

					addressFrom = street + " " + adressFact;

					if (order.getAddress().size() > 0) {
						addressTo = order.getAddress().get(
								order.getAddress().size() - 1).to;
					}
					fromTxt.setText(addressFrom);
					toTxt.setText(addressTo);

					colorLayout.setBackgroundColor(order.colorClass);
					autoClassTxt.setText(order.autoClass);

					// set images (longView) with drawables
					/*if (order.getAddress().size() == 1
							|| order.getAddress().size() == 0) {
						imageFrom.setImageResource(R.drawable.route_unknown);
					}
					if (order.getAddress().size() == 2) {
						imageFrom.setImageResource(R.drawable.route_2_points);
					}
					if (order.getAddress().size() > 2) {
						imageFrom.setImageResource(R.drawable.route_3_points);
					}*/
					
					// set images with customView
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
					
					
					// set editional inf images
					Boolean joker = (order.agentName != null && !order.agentName
							.equals("")) ? true : false;
					Boolean noCash = order.isNonCashPay();
					Boolean webOrder = (!order.getSourceWhence()
							.equals("Phone")
							&& !order.getSourceWhence().equals("Skype") && !order
							.getSourceWhence().equals("Mail")) ? true : false;
					/*
					 * Boolean edditional = (order.getFeatures() != null &&
					 * order .getFeatures().size() > 0) ? true : false;
					 */

					// set extra info images
					if (joker)
						imageJoker.setVisibility(View.VISIBLE);
					else
						imageJoker.setVisibility(View.GONE);
					if (noCash)
						imageNoCash.setVisibility(View.VISIBLE);
					else
						imageNoCash.setVisibility(View.GONE);
					if (webOrder)
						imageWebOrder.setVisibility(View.VISIBLE);
					else
						imageWebOrder.setVisibility(View.GONE);
					/*
					 * if (edditional) imageEd.setVisibility(View.VISIBLE); else
					 * imageEd.setVisibility(View.INVISIBLE);
					 */

					// set price
					float priceFloat = (order.getFare() == 0) ? 0 : order
							.getFare();
					String price = String.format("%.2f", priceFloat);
					costTxt.setText(getPriceFormat(price));

					// get features of the order
					if (order.getFeatures() != null
							&& order.getFeatures().size() != 0) {
						String featureList = "";
						for (String dop : order.getFeatures())
							featureList += dop + "; ";
						edditionalTxt.setText(featureList);
					} else {
						edditionalTxt.setText(ContextHelper.getInstance()
								.getCurrentContext()
								.getString(R.string.noFeature));
					}

					comments = (!order.getComments().equals("")) ? order
							.getComments() : "nothing";
					extraTxt.setText(comments);
							
							
					if (!comments.equals("nothing")) {
						extraTxt.setVisibility(View.VISIBLE);
						edditionalTxt.setVisibility(View.GONE);
						commentsImage.setImageResource(R.drawable.comment_active);
						imageEd.setImageResource(R.drawable.additional_not_active);
					} else if (order.getFeatures() != null
							&& order.getFeatures().size() != 0) {
						extraTxt.setVisibility(View.GONE);
						edditionalTxt.setVisibility(View.VISIBLE);
						commentsImage.setImageResource(R.drawable.additional_active);
						imageEd.setImageResource(R.drawable.comment_not_active);
					} else {
						extraTxt.setVisibility(View.GONE);
						edditionalTxt.setVisibility(View.GONE);
						commentsImage.setImageResource(R.drawable.comment_not_active);
						imageEd.setImageResource(R.drawable.additional_not_active);
					}
					/*if (comments.equals("nothing"))
						extraInfoBtn.setImageResource(R.drawable.pencil);
					else
						extraInfoBtn
								.setImageResource(R.drawable.pencil_attention);*/
					
					
				}
			});
		}
		
		imageEd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (extraTxt.getVisibility() == View.VISIBLE) {
					extraTxt.setVisibility(View.GONE);
					edditionalTxt.setVisibility(View.VISIBLE);
					commentsImage.setImageResource(R.drawable.additional_active);
					imageEd.setImageResource(R.drawable.comment_not_active);
				} else if (edditionalTxt.getVisibility() == View.VISIBLE) {
					extraTxt.setVisibility(View.VISIBLE);
					edditionalTxt.setVisibility(View.GONE);
					commentsImage.setImageResource(R.drawable.comment_active);
					imageEd.setImageResource(R.drawable.additional_not_active);
				}
				
			}
		});

		btn5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, 5);
			}
		});

		btn10.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, 10);
			}
		});

		btn15.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, 15);
			}
		});

		btn20.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, 20);
			}
		});

		btn25.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, 25);
			}
		});

		btn30.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, 30);
			}
		});

		btnAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				answerOrder(orderId, SettingsFromXml.getInstance()
						.getDriverTime());
				// answerOrder(orderId, 10);
			}
		});

		imageEd.setVisibility(View.VISIBLE);
		
		/*extraInfoBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("myLogs", "Click-Click");
				if (extraTxt.getVisibility() == View.GONE) {
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

				}

			}
		});*/
	}

	/*
	 * protected static void setDetailsOther(final String orderFullDescOther) {
	 * 
	 * btnDetails.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * AlertDHelper.showDialogOk(orderFullDescOther); } }); }
	 */

	protected static int getOrderSourceImage(String sourceWhence) {

		if (sourceWhence.equals("Phone")) {
			return R.drawable.tel_2;
		} else if (sourceWhence.equals("WebOrder")) {
			return R.drawable.web2;
		} else if (sourceWhence.equals("WebOrderAnd")) {
			return R.drawable.and2;
		} else if (sourceWhence.equals("WebOrderiOS")) {
			return R.drawable.and2;
		} else if (sourceWhence.equals("Mail")) {
			return R.drawable.mail2;
		} else if (sourceWhence.equals("Skype")) {
			return R.drawable.skype2;
		}

		return -1;
	}

	public static void startTimer(final long ms) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					final int curId = orderId; // свежие правки
					Log.d("STATE", "Started timer for orderID = " + orderId);
					Thread.sleep(ms);
					System.out.println("close window");
					ContextHelper.getInstance().runOnCurrentUIThread(
							new Runnable() {
								@Override
								public void run() {
									if (!cansel) {
										try {

											if (FragmentTransactionManager
													.getInstance().getId() == FragmentPacket.ORDER) {
												FragmentTransactionManager
														.getInstance().back();
											}
											Log.d("myLogs", "outPoint");
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									if (OrderManager.getInstance()
											.getOrder(curId).getStatus() == Order.STATE_TAKEN) {
										OrderManager.getInstance()
												.changeOrderState(curId,
														Order.STATE_MISSED);
										NotificationService.cancelNotif(curId,
												"f12");
									} else if (OrderManager.getInstance()
											.getOrder(curId).getStatus() == Order.STATE_NEW
											|| OrderManager.getInstance()
													.getOrder(curId)
													.getStatus() == Order.STATE_KRYG_ADA) {
										try {
											OrderManager.getInstance()
													.changeOrderState(curId,
															Order.STATE_MISSED);

											NotificationService.cancelNotif(
													curId, "f12");
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									CurrentOrdersFragment
											.setState(Order.STATE_PERFORMING);
									CurrentOrdersFragment
											.displayOrders(Order.STATE_PERFORMING);
								}

							});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * Log.d("myLogs", "order id = " + orderId + ", Order state = "
				 * + OrderManager.getInstance().getOrder(orderId) .getStatus());
				 */
				// CurrentOrdersFragment.setState(Order.STATE_PERFORMING);
				// CurrentOrdersFragment.displayOrders(Order.STATE_PERFORMING);
				checkOrderId = false;
				orderId = -1;
			}
		}).start();

	}

	private static void answerOrder(int orderId, int interval) {

		OrderManager.getInstance().changeOrderState(orderId, Order.STATE_TAKEN);

		if (OrderManager.getInstance().getOrder(orderId) != null) {
			OrderManager.getInstance().getOrder(orderId).accepted=true;
			OrderManager.getInstance().getOrder(orderId).arrived=false;
		}

		OrderManager.getInstance().setDateTime(orderId, interval);
		cansel = true;

		byte[] body4 = RequestBuilder.createBodyOrderAnswer(orderId,
				"CanGetOrder", "" + interval, ServerData.getInstance()
						.getPeopleID());
		byte[] data4 = RequestBuilder.createSrvTransfereData(
				RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
						.getInstance().getSrvID(),
				RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance()
						.getGuid(), true, body4);
		ConnectionHelper.getInstance().send(data4);

		FragmentTransactionManager.getInstance().openFragment(
				FragmentPacket.SWIPE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
		outState.putInt("orderID", orderId);
	}

	@Override
	public void onPause() {

		// Stop VoiceRecognition Service
		Intent voiceServiceIntent = new Intent(ContextHelper.getInstance()
				.getCurrentContext(), CommandsRecognitionService.class);

		if (voiceEnable) {
			ContextHelper.getInstance().getCurrentContext()
					.stopService(voiceServiceIntent);
			// end
		}
		ContextHelper.getInstance().getCurrentContext()
				.unregisterReceiver(voiceReceiver);

		super.onPause();
		SharedPreferences pref = ContextHelper.getInstance()
				.getCurrentContext()
				.getSharedPreferences("ether", Context.MODE_PRIVATE);
		Calendar c = Calendar.getInstance();
		int seconds = c.get(Calendar.SECOND);
		pref.edit().putInt("timeOnPause", seconds);
		pref.edit().putInt("timeToGo", sec);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myView = inflater.inflate(R.layout.order_fragment_new2, container,
				false);

		LinearLayout ll = (LinearLayout) myView
				.findViewById(R.id.routeInfoLayout);
		PaintDrawable p = (PaintDrawable) GraphUtils.getEtherRouteGradient(ll);
		ll.setBackground((Drawable) p);
		
		rw = (RouteView) myView
				.findViewById(R.id.routeCustom);

		btn5 = (Button) myView.findViewById(R.id.btn_5);
		btn10 = (Button) myView.findViewById(R.id.btn_10);
		btn15 = (Button) myView.findViewById(R.id.btn_15);
		btn20 = (Button) myView.findViewById(R.id.btn_20);
		btn25 = (Button) myView.findViewById(R.id.btn_25);
		btn30 = (Button) myView.findViewById(R.id.btn_30);
		// set buttons Gradient
		p = (PaintDrawable) GraphUtils.getEtherButtonsGradient(btn5);
		btn5.setBackground(p);
		btn10.setBackground(p);
		btn15.setBackground(p);
		btn20.setBackground(p);
		btn25.setBackground(p);
		btn30.setBackground(p);
		btnAccept = (Button) myView.findViewById(R.id.btn_accept);
		// btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel = (LinearLayout) myView.findViewById(R.id.btn_cancel);
		declineTxt = (TextView) myView.findViewById(R.id.declineTxt);
		timerTxt = (TextView) myView.findViewById(R.id.timeTxt);
		// btnDetails = (Button) view.findViewById(R.id.btn_details);
		imgSource = (ImageView) myView.findViewById(R.id.img_source);
		commentsImage = (ImageView) myView.findViewById(R.id.commentImage);
		// ExtraInfo btn (pencil)
		extraInfoBtn = (ImageView) myView.findViewById(R.id.extraInfoBtn);
		// date = (TextView) view.findViewById(R.id.date);
		time = (TextView) myView.findViewById(R.id.time);
		region = (TextView) myView.findViewById(R.id.addressFrom);
		distance = (TextView) myView.findViewById(R.id.distance);
		costTxt = (TextView) myView.findViewById(R.id.costTxt);
		edditionalTxt = (TextView) myView.findViewById(R.id.edditionalTxt);

		// define route info view elements
		colorLayout = (LinearLayout) myView.findViewById(R.id.colorLayout);
		autoClassTxt = (TextView) myView.findViewById(R.id.autoClassTxt);
		imageFrom = (ImageView) myView.findViewById(R.id.imageFrom);
		fromTxt = (TextView) myView.findViewById(R.id.fromTxt);
		toTxt = (TextView) myView.findViewById(R.id.toTxt);

		// editional info images (imageJoker, imageWebOrder, imageNoCash,
		// imageEd;)
		imageJoker = (ImageView) myView.findViewById(R.id.imageJoker);
		imageWebOrder = (ImageView) myView.findViewById(R.id.imageWeb);
		imageNoCash = (ImageView) myView.findViewById(R.id.imageNoCash);
		imageEd = (ImageView) myView.findViewById(R.id.imageEd);

		edditionalInfoLayout = (RelativeLayout) myView
				.findViewById(R.id.edditionalInfoLayout);
		extraInfoLayout = (RelativeLayout) myView
				.findViewById(R.id.extraInfoLayout);
		extraTxt = (TextView) myView.findViewById(R.id.extraTxt);

		// set TypeFace for buttons and date
		Typeface t = Typeface
				.createFromAsset(ContextHelper.getInstance()
						.getCurrentContext().getAssets(),
						"fonts/TickingTimebombBB.ttf");
		btn5.setTypeface(t);
		btn10.setTypeface(t);
		btn15.setTypeface(t);
		btn20.setTypeface(t);
		btn25.setTypeface(t);
		btn30.setTypeface(t);

		time.setTypeface(t);

		t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");

		fromTxt.setTypeface(t);
		toTxt.setTypeface(t);
		distance.setTypeface(t);
		// btnCancel.setTypeface(t);
		declineTxt.setTypeface(t);
		timerTxt.setTypeface(t);
		btnAccept.setTypeface(t);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				cansel = true;
				CurrentOrdersFragment
						.setState(CurrentOrdersFragment.STATE_PERFORMING);
				CurrentOrdersFragment
						.displayOrders(CurrentOrdersFragment.STATE_PERFORMING);
				FragmentTransactionManager.getInstance().back();
			}
		});

		// btnDetails.setEnabled(true);
		if (orderId != -1) {
			setOrderId(orderId);
		}
		return myView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	public void onResume() {

		IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
		ContextHelper.getInstance().getCurrentContext()
				.registerReceiver(voiceReceiver, intFilt);
		// open CommandRecognition Service
		Intent voiceServiceIntent = new Intent(ContextHelper.getInstance()
				.getCurrentContext(), CommandsRecognitionService.class);

		commands.clear();

		commands.add(0, FIVE_MINUTES_PHRASES);
		commands.add(1, TEN_MINUTES_PHRASES);
		commands.add(2, FIFTEEN_MINUTES_PHRASES);
		commands.add(3, TWENTY_MINUTES_PHRASES);
		commands.add(4, TWENTY_FIVE_MINUTES_PHRASES);
		commands.add(5, THIRTY_MINUTES_PHRASES);

		voiceServiceIntent.putExtra("commands", commands);
		if (voiceEnable) {
			ContextHelper.getInstance().getCurrentContext()
					.startService(voiceServiceIntent);
		}
		Log.d("IMAP_Speech", "Effir frag id = "
				+ FragmentTransactionManager.getInstance().getId());
		// end

		
		super.onResume();
		SharedPreferences pref = ContextHelper.getInstance()
				.getCurrentContext()
				.getSharedPreferences("ether", Context.MODE_PRIVATE);
		sec = pref.getInt("timeToGo", 0);
		Calendar c = Calendar.getInstance();
		int secondsNow = c.get(Calendar.SECOND);
		int secondsOnPause = pref.getInt("timeOnPause", 0);
		if ((secondsNow - secondsOnPause) > sec)
			checkOrderId = false;
		/*
		 * if (!checkOrderId) {
		 * FragmentTransactionManager.getInstance().openFragment(7);
		 * Log.d("myLogs", "!!!!!!!"); } else { Log.d("myLogs", "(((((((("); }
		 */
		Log.d("myLogs", "!!!!CHECKORDERID = " + checkOrderId);
		if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
				ContextHelper.getInstance().getCurrentContext()).getString(
				UserSettingActivity.KEY_TEXT_SIZE, "")) != 0) {
			/*
			 * txtDetails .setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer
			 * .parseInt(PreferenceManager .getDefaultSharedPreferences(
			 * ContextHelper.getInstance() .getCurrentContext()) .getString(
			 * UserSettingActivity.KEY_TEXT_SIZE, "")) + 14);
			 */
		}
	}
	
}
