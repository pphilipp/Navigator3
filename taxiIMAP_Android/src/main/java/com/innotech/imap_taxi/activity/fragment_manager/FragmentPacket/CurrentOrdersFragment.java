package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.OrdersAdapter;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.PreliminaryOrder;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.datamodel.SettingsFromXml;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.network.packet.PreOrdersResponse;
import com.innotech.imap_taxi.network.packet.RefusePreliminaryOrdeResponce;
import com.innotech.imap_taxi.network.packet.SignPrelimOrdeResponce;
import com.innotech.imap_taxi.utile.AlertDHelper;
import com.innotech.imap_taxi3.R;

public class CurrentOrdersFragment extends FragmentPacket {
	public static final String LOG_TAG = CurrentOrdersFragment.class.getSimpleName();
	public final static int STATE_PERFORMING = 0;
	public final static int STATE_PERFORMED = 1;
	public final static int STATE_PRE = 2;
	public final static int STATE_ARCH = 3;
	public static boolean isUpdate = false;
	private static ListView listView_orders;
	private static int state;

	public static void setState(int state) {
		CurrentOrdersFragment.state = state;
	}

	private Button back;

	private String parkingName, position, all;

	ServerData serv;
	AlertDialog dialog;

	private static List<Order> orders;
	private static List<String> arch_list;
	private static List<String> arch_desc;
	static OrdersAdapter mAdapter;

	public int currentViewState;

	static SharedPreferences sharedPref;

	private final String TAG = "orders";

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
		Log.d(LOG_TAG, "79 onSaveInstanceState");
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		displayOrders(CurrentOrdersFragment.state);
		Log.d(LOG_TAG, "86 onViewStateRestored");
	}

	public CurrentOrdersFragment() {
		super(CURRENTORDERS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.current_orders_new, container,
				false);

		Log.d(LOG_TAG, "96 onCreateView start ----->");

		sharedPref = PreferenceManager
				.getDefaultSharedPreferences(ContextHelper.getInstance()
				.getCurrentContext());

		orders = new ArrayList<Order>();

		Log.d(LOG_TAG, "102 orders size -----> " + orders.size());

		mAdapter = new OrdersAdapter(orders, ContextHelper.getInstance()
				.getCurrentContext());

		listView_orders = (ListView) myView.findViewById(R.id.listView_orders);
		listView_orders.setAdapter(mAdapter);

		Log.d(LOG_TAG, "113 set adapter with orders. orders.size() -----> " + orders.size());

		back = (Button) myView.findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentTransactionManager.getInstance().back();
			}
		});

		//WTF is this object!!!!!!!!!! (запланированный ExecutorService)
		final ScheduledExecutorService scheduler = Executors
				.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {

//				Log.d(LOG_TAG, "130 start ScheduledExecutorService WTF!!!");

				// This section is not working or not understand satiation when it should work.
				if (FragmentTransactionManager.getInstance().getId()
						== FragmentPacket.CURRENTORDERS
						&& isUpdate) {
					// ContextHelper.getInstance().runOnCurrentUIThread(
					// new Runnable() {
					// @Override
					// public void run() {
					// mAdapter.notifyDataSetChanged();
					// Log.w("UPD","ORDRS");
					// }
					// });
					RequestHelper.getOrders();

					Log.d(LOG_TAG, "145 update orders via RequestHelper.getOrders()" + orders.size());

					displayOrders(0);

				}
			}
		}, 0, 5000l, TimeUnit.MILLISECONDS);

		MultiPacketListener.getInstance().addListener(
				Packet.SIGN_PRELIM_ORDER_ANSWER, new OnNetworkPacketListener() {
					@Override
					public void onNetworkPacket(Packet packet) {
						SignPrelimOrdeResponce pack = (SignPrelimOrdeResponce) packet;
						System.out.println("goted SIGN_PRELIM_ORDER_ANSWER");

						// ordermanager setorderpresigned (orderID, true) и
						// убрать update!!!!
					}
				});

		MultiPacketListener.getInstance().addListener(
				Packet.REFUSE_PRELIMINARY_ORDER_ANSWER,
				new OnNetworkPacketListener() {
					@Override
					public void onNetworkPacket(Packet packet) {
						RefusePreliminaryOrdeResponce pack = (RefusePreliminaryOrdeResponce) packet;

						Log.d(LOG_TAG, "173 goted REFUSE_PRELIMINARY_ORDER_ANSWER " + pack.orderId);

						// ordermanager setorderpresigned (orderID, false) и
						// убрать update!!!!
					}
				});

		MultiPacketListener.getInstance().addListener(
				Packet.PREORDERS_RESPONCE, new OnNetworkPacketListener() {
					@Override
					public void onNetworkPacket(Packet packet) {
						PreOrdersResponse pack = (PreOrdersResponse) packet;
						Log.d(LOG_TAG, "185 goted PREORDERS_RESPONCE " + pack.orders.size()); // ok

						List<PreliminaryOrder> ords = pack.orders;
						orders = new ArrayList<Order>();

						for (PreliminaryOrder pro : ords) {
							System.out.println(pro.order.streetName
									+ pro.order.fare
									+ pro.order.orderPrelimFullDesc);
							Order ord = new Order(pro.order);
							ord.setPreliminary(true);
							ord.setPreSigned(pro.signed);
							orders.add(ord);

							Log.d(LOG_TAG, "199 MultiPacketListener Packet.PREORDERS_RESPONCE" + orders.size());
						}

						ContextHelper.getInstance().runOnCurrentUIThread(
								new Runnable() {
									@Override
									public void run() {
										// mAdapter.notifyDataSetChanged();
										mAdapter.updateMyList(orders);
									}
								});

						listView_orders
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											final int arg2, long arg3) {
										Log.i("test", "Open description " +
												arg2 + "");
										//

										// OrderDetails.dispOrderId(orders.get(arg2).getOrderID());
										// FragmentTransactionManager.getInstance().openFragment(FragmentPacket.ORDER_DETAILS);

										AlertDialog.Builder builder = new AlertDialog.Builder(
												ContextHelper.getInstance()
														.getCurrentActivity());

										builder.setMessage(
												"Выберите действие над заказом?")
												.setTitle("Уведомление")
												.setPositiveButton(
														"Подписаться",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																if (orders
																		.get(arg2)
																		.isSigned()) {
																	AlertDHelper
																			.showDialogOk("Вы уже подписаны на этот заказ!");
																	return;
																}
																byte[] body = RequestBuilder
																		.createSignPrelimOrder(
																				orders.get(
																						arg2)
																						.getOrderID(),
																				ServerData
																						.getInstance()
																						.getNick(),
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
																				true,
																				body);
																ConnectionHelper
																		.getInstance()
																		.send(data);

																// orders.get(arg2).addNickToPrePeopList(ServerData.getInstance().getNick());
																// OrderManager.getInstance().signToPreOrder(orders.get(arg2).getOrderID());

																// временно
																update();
																// временно
															}
														})
												.setNeutralButton(
														"Отписаться",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																if (!orders
																		.get(arg2)
																		.isSigned()) {
																	AlertDHelper
																			.showDialogOk("Вы не подписаны на этот заказ!");
																	return;
																}
																byte[] body = RequestBuilder
																		.createRefusePreliminaryOrder(
																				orders.get(
																						arg2)
																						.getOrderID(),
																				ServerData
																						.getInstance()
																						.getNick(),
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
																				true,
																				body);
																ConnectionHelper
																		.getInstance()
																		.send(data);

																// временно
																update();
																// временно
															}
														})
												.setNegativeButton(
														"Отмена",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface arg0,
																	int arg1) {
																dialog.dismiss();
															}
														});

										dialog = builder.create();
										dialog.setCancelable(false);
										dialog.show();
									}

								});
					}
				});

		return myView;
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(LOG_TAG, "357 onResume");

		// update();
		// balance.setText(UIData.getInstance().getBalance());
	}

	private static void update() {
		Log.d(LOG_TAG, "366 update()");
		if (ConnectionHelper.getInstance().isConnected()) {
			byte[] body = RequestBuilder.createReqPreOrders(ServerData
					.getInstance().getPeopleID());
			byte[] data = RequestBuilder.createSrvTransfereData(
					RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
							.getInstance().getSrvID(),
					RequestBuilder.DEFAULT_DESTINATION_ID, ServerData
							.getInstance().getGuid(), true, body);
			ConnectionHelper.getInstance().send(data);
		}
	}

	public static void updateView() {
		Log.d(LOG_TAG, "updateView() List change notify");

		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			@Override
			public void run() {
				mAdapter.updateMyList(orders);
			}
		});
	}

	public static void displayOrders(int state) {
		Log.d(LOG_TAG, "displayOrders()");

		/*switch (FragmentTransactionManager.getInstance().getId()) {
		case FragmentPacket.CURRENTORDERS:
			state = STATE_PERFORMING;
			CurrentOrdersFragment.setState(STATE_PERFORMING);
			break;
		case FragmentPacket.ARCHIV:
			state = STATE_ARCH;
			CurrentOrdersFragment.setState(STATE_PERFORMED);
			break;
		}*/

		Log.w("STATE", String.valueOf(state) + ", size = " + orders.size());

		if (state == STATE_PERFORMED
				&& CurrentOrdersFragment.state == STATE_PERFORMED) {
			isUpdate = true;
			orders = OrderManager.getInstance().getOrdersByState(
					Order.STATE_PERFORMED);
			Log.d("orders", "order.size (performed) = " + orders.size());
			listView_orders.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
										final int arg2, long arg3) {
					Log.i("test", "Open description " + arg2 + "");

					OrderDetails.dispOrderId(orders.get(arg2).getOrderID());
					FragmentTransactionManager.getInstance().openFragment(
							FragmentPacket.ORDER_DETAILS);
				}

			});

			System.out.println("performed - " + orders.size());

		} else if (state == STATE_PERFORMING
				&& CurrentOrdersFragment.state == STATE_PERFORMING) {
			Log.d("STATE", "TEST");
			isUpdate = false;
			orders = OrderManager.getInstance().getOrdersByState(
					Order.STATE_PERFORMING);
			Log.d("orders", "order.size (performing) = " + orders.size());
			System.out.println("COUNT* ________ "
					+ OrderManager.getInstance().getCountOfOrdersByState(
							Order.STATE_PERFORMING));
			System.out.println("COUNT *________ "
					+ OrderManager.getInstance().getCountOfOrdersByState(
							Order.STATE_KRYG_ADA));

			List<Order> orders_krygi = OrderManager.getInstance()
					.getOrdersByState(Order.STATE_KRYG_ADA);
			for (Order ord : orders_krygi) {
				orders.add(ord);
				Log.d("STATE", "added order from lap to current");
			}

			Iterator<Order> ord = orders.iterator();
			while (ord.hasNext()) {
				Order item = ord.next();
				long timeDifference = -1;
				if (item.getStatus() == Order.STATE_KRYG_ADA
						|| item.getStatus() == Order.STATE_MISSED) {
					long firstLapTime = (SettingsFromXml.getInstance()
							.getFirstTimeSearch() != 0) ? SettingsFromXml
							.getInstance().getFirstTimeSearch() : 20000;
					timeDifference = System.currentTimeMillis()
							- item.getReciveTime();
					if (timeDifference > firstLapTime) {
						orders.remove(item);
						Log.d("STATE", "Deleted Lap oreder from current");
					}
				}
			}
			Log.d("STATE", "CurrentOrders fragmentId = "
					+ FragmentTransactionManager.getInstance().getId());

			listView_orders.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
										final int arg2, long arg3) {
					Log.i("test", "Open description " + arg2 + "");

					if (orders.get(arg2).getStatus() == Order.STATE_PERFORMING) {
						OrderDetails.isArch = true;
						OrderDetails.setOrderId(orders.get(arg2).getOrderID());
						FragmentTransactionManager.getInstance().openFragment(
								FragmentPacket.ORDER_DETAILS);
					} else if (orders.get(arg2).getStatus() == Order.STATE_KRYG_ADA) {
						EfirOrder.orderId = -1;
						EfirOrder.setOrderId(orders.get(arg2).getOrderID());
						FragmentTransactionManager.getInstance().openFragment(
								FragmentPacket.ORDER);
					}

				}

			});

			System.out.println("performing + krygi - " + orders.size());
		} else if (state == STATE_PRE) {
			isUpdate = false;
			update();
		}

		ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
			@Override
			public void run() {
				mAdapter.updateMyList(orders);

				Log.d(LOG_TAG, "503 ContextHelper  mAdapter.updateMyList(orders) orders.size()-> " + orders.size());

			}
		});
	} //END displayOrders()

	@Override
	public void onStop() {super.onStop();Log.d(LOG_TAG, "onStop()");}
}
