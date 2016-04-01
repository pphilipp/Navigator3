package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.util.*;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.UserSettingActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.core.StateObserver;
import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.datamodel.SettingsFromXml;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.helpers.RequestHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.packet.CallSignChangedResponce;
import com.innotech.imap_taxi.network.packet.DispOrderResponse4;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.network.packet.SearchInEtherOrderOverResponse;
import com.innotech.imap_taxi.network.packet.TCPMessageResponce;
import com.innotech.imap_taxi.utile.AlertDHelper;
import com.innotech.imap_taxi.utile.NotificationService;
import com.innotech.imap_taxi.utile.PlaySound;
import com.innotech.imap_taxi3.R;


;

/**
 * Created with IntelliJ IDEA.
 * User: u27
 * Date: 9/24/13
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class EthearFragment extends FragmentPacket {

    List<DispOrder4> orders;
    List<Order> etherOrders;
    
    private static class ViewHolder{
        TextView text;
    }
    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public void notifyDataSetChanged() {
                Log.d("myLogs", "notifyAdapter!!!");
                if (sharedPrefs.getBoolean("prefIsAutoSearch", false))// autosearch 1 2
                    Collections.sort(orders, new ListViewComparator());
                super.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ether_item, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.txt_details);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            String txt = "";
            if (orders.get(position).canFirstForAnyParking) {
                txt += "<b><font color='red'> Первым </font></b>";
            }

            if (orders.get(position).concessional) {
                txt += "<b><font color='green'> Льгота </font></b>";
            }
            txt += " " + orders.get(position).orderFullDesc;
            if (orders.get(position).orderCostForDriver > 0.0) {
                convertView.setBackgroundColor(Color.parseColor("#33FF0000"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
            holder.text.setText(Html.fromHtml(txt));
            if (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext()).getString(UserSettingActivity.KEY_TEXT_SIZE, "")) != 0) {
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext()).getString(UserSettingActivity.KEY_TEXT_SIZE, "")) + 14);
            }
            Log.w("fd", orders.get(position).orderType);
            if (orders.get(position).orderType.equals("FindDriver1")) {// autosearch 1 2
                convertView.setBackgroundColor(Color.parseColor("#FFCCCC"));
            } else if (orders.get(position).orderType.equals("FindDriver2")) {// autosearch 1 2
                convertView.setBackgroundColor(Color.parseColor("#9193FF"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
            return convertView;

        }
    };
    AlertDialog alertDialog;
    SharedPreferences sharedPrefs;
    ServerData sData;
    OrderManager orderManager;
    int i;
    PlaySound play;
    private boolean isOrderYours = false;
    private Button back, refresh;
    private ListView orders_listView;

    public static boolean isVisible = false;

    public EthearFragment() {
        super(ETHEAR);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.w("ether","onSaveInstanceState");

        setUserVisibleHint(true);
        OrderManager.getInstance().etherOrders = orders;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.w("ether","onViewStateRestored");

        orders = OrderManager.getInstance().etherOrders;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w("ether","onCreateView");
        View myView = inflater.inflate(R.layout.ethear_fragment_new, container, false);
        play = PlaySound.getInstance();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextHelper.getInstance().getCurrentContext());
        //list = new ArrayList<String>();
        orders = new ArrayList<DispOrder4>();
        
        etherOrders = new ArrayList<Order>();
        etherOrders = OrderManager.getInstance().getEfirOrders();
        Log.d("myLogs", "ether orders = " + etherOrders.size());

        orders_listView = (ListView) myView.findViewById(R.id.listview_orders);
        orders_listView.setAdapter(mAdapter);

        MultiPacketListener.getInstance().addListener(Packet.ETHEAR_ORDER_OVER_ANSWER, new OnNetworkPacketListener() {

            @Override
            public void onNetworkPacket(Packet packet) {
                final SearchInEtherOrderOverResponse pack = (SearchInEtherOrderOverResponse) packet;

                Log.i("IMAP", "goted SearchInEtherOrderOverResponse " + pack.orderID);

                ContextHelper.getInstance().runOnCurrentUIThread(
                        new Runnable() {
                            @Override
                            public void run() {

                                for (DispOrder4 dispord : orders) {
                                    if (dispord.orderID == pack.orderID) {
                                        Log.w("ETHER ORDER ANSWER", String.valueOf(pack.orderID));
                                        orders.remove(dispord);
                                        break;
                                    }
                                }

                                mAdapter.notifyDataSetChanged();

                                System.out.println("state " + orderManager.getOrder(pack.orderID).getStatus());
                                orderManager.changeOrderState(pack.orderID, Order.STATE_MISSED);

                                if (sharedPrefs.getBoolean("prefIsAutoSearch", false)) // autosearch 1 2
                                    SwipeFragment.ethear.setText("ЭФИР("
                                            + String.valueOf(orderManager.getCountOfOrdersByState(Order.STATE_NEW)
                                                            + orderManager.getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                            + ")");
                                else
                                    SwipeFragment.ethear.setText("ЭФИР("
                                            + orderManager.getCountOfEfirOrders()
                                            + ")");


                                if (EfirOrder.orderId == pack.orderID) {
                                    EfirOrder.startTimer(0);
                                }
                            }
                        });
            }

        });

        MultiPacketListener.getInstance().addListener(Packet.TCPMESSAGE_RESPONCE, new OnNetworkPacketListener() {

            @Override
            public void onNetworkPacket(Packet packet) {
                final TCPMessageResponce pack = (TCPMessageResponce) packet;

                Log.i("IMAP", "goted TCPMESSAGE_RESPONCE " + pack.orderID);
                ContextHelper.getInstance().runOnCurrentUIThread(
                        new Runnable() {
                            @Override
                            public void run() {

                                if (pack.orderID != -1) {
                                    if (!NotificationService.sendNotific("notif", pack.message, "")) {
                                        AlertDHelper.showDialogOk(pack.message);
                                    }
                                } else {
                                    if (!NotificationService.sendNotific("notifSMS", pack.message, "")) {
                                        AlertDHelper.showDialogOk(pack.message);
                                    }
                                }
                                if (!pack.className.equals("IMAP.Net.OrderIsNotYours_mesbindata")) {
                                    if (pack.className.equals("IMAP.Net.OrderIsYours_mesbindata")) {
                                        isOrderYours = true;
//                                        OrderManager.getInstance().changeOrderState(pack.orderID, 2);
                                    }
                                    return;
                                }

                                List<Order> taken = OrderManager.getInstance().getOrdersByState(Order.STATE_TAKEN);
                                List<Order> perf = OrderManager.getInstance().getOrdersByState(Order.STATE_PERFORMING);

                                System.out.println("1 " + taken.size());
                                if ((taken.size() == 1) && (perf.size() == 0)) {
                                    System.out.println("2 " + pack.orderID + " " + taken.get(0).getOrderID());
                                    if (pack.orderID == taken.get(0).getOrderID()) {
                                        System.out.println("3 ok");

                                        StateObserver.getInstance().setDriverState(StateObserver.DRIVER_FREE);
                                        TextView busy2 = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.btn_busy);
                                        busy2.setText("Занят");
                                        busy2.setEnabled(true);

                                        ServerData.getInstance().isFree = true;
                                    }
                                }

                                //временно !!!!!!!!!!!!!!!!!

                                if (orderManager.getOrder(pack.orderID).getStatus() == Order.STATE_TAKEN) {
                                    orderManager.changeOrderState(pack.orderID, Order.STATE_MISSED);
                                    NotificationService.cancelNotif(pack.orderID, "f3");
                                }

                                if (EfirOrder.orderId == pack.orderID) {
                                    EfirOrder.startTimer(0);
                                }
                            }
                        });
            }

        });

        MultiPacketListener.getInstance().addListener(Packet.ORDER_RESPONCE4, new OnNetworkPacketListener() {
            @Override
            public void onNetworkPacket(Packet packet) {
                DispOrderResponse4 pack = (DispOrderResponse4) packet;

                final DispOrder4 mOrder = pack.getOrder();

                Log.i("IMAP", "goted ORDER_RESPONCE4 " + mOrder.orderID + " " + mOrder.orderType + " " + mOrder.streetName + " !!!! " + mOrder.fare);

                Order ord = new Order(mOrder);
                orderManager = OrderManager.getInstance();

                if (mOrder.orderType.equals("SendedByDispatcher") || mOrder.orderType.equals("SendedByDispatcherFromGetFolder")) {

                    play.play(R.raw.snd_msg);

                    //order add
                    ord.setStatus(Order.STATE_PERFORMING);
                    orderManager.addOrder(ord);
                    //orderManager.getOrder(mOrder.orderID).setStatus(Order.STATE_PERFORMING);

                    //open description window
                    OrderDetails.setOrderId(mOrder.orderID);

                    if (NotificationService.sendNotific("SendedByDispatcher", mOrder.streetName + " " + mOrder.house, "")) {
                        return;
                    }
                    if (!mOrder.folder.equals(Order.FOLDER_NOT_DONE) && !mOrder.folder.equals(Order.FOLDER_TRASH) && !mOrder.folder.equals(Order.FOLDER_DONE)) {
                        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                            @Override
                            public void run() {

                                StateObserver.getInstance().setDriverState(StateObserver.DRIVER_BUSY);

                                TextView busy2 = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.btn_busy);
                                busy2.setEnabled(false);

                                ServerData.getInstance().isFree = false;

                                if (isOrderYours) {
                                    OrderManager.getInstance().getOrder(mOrder.orderID).accepted = true;
                                    OrderManager.getInstance().getOrder(mOrder.orderID).arrived = false;
                                    OrderDetails.btnArrived.setEnabled(true);
                                    OrderDetails.btnAccept.setEnabled(false);
                                    OrderDetails.btnDo.setText("Выполняю");
                                    isOrderYours = false;
                                }
                            }
                        });
                    }
                    for (DispOrder4 orde : orders) {
                        if (orde.orderID == mOrder.orderID) {
                            orders.remove(orde);
                            ContextHelper.getInstance().runOnCurrentUIThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.notifyDataSetChanged();
                                            if (sharedPrefs.getBoolean("prefIsAutoSearch", false))// autosearch 1 2
                                                SwipeFragment.ethear.setText("ЭФИР("
                                                        + String.valueOf(orderManager.getCountOfOrdersByState(Order.STATE_NEW)
                                                                        + orderManager.getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                        + ")");
                                            else
                                                SwipeFragment.ethear.setText("ЭФИР("
                                                        + orderManager.getCountOfEfirOrders()
                                                        + ")");

                                        }
                                    });
                            break;
                        }
                    }

                    if (FragmentTransactionManager.getInstance().getId() != FragmentPacket.ORDER_DETAILS) {
                        FragmentTransactionManager.getInstance().openFragment(FragmentPacket.ORDER_DETAILS);
                    } else {
                        System.out.println("!!!!!!!!! OK !!!!!!!!");
                    }

                } else if (mOrder.orderType.equals("FindDriver1") || mOrder.orderType.equals("FindDriver2")) {

                    play.play(R.raw.snd_msg);


                    ord.setStatus(Order.STATE_KRYG_ADA);
                    orderManager.addOrder(ord);

                    EfirOrder.setOrderId(ord.getOrderID());

                    if (sharedPrefs.getBoolean("prefIsAutoSearch", false)) {// autosearch 1 2
                        //isEixist
                        boolean found = false;
                        for (DispOrder4 item : orders) {
                            if (item.orderID == mOrder.orderID) {
                                orders.remove(item);
                                orders.add(0, mOrder);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            orders.add(0, mOrder);
                        }
                        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                SwipeFragment.ethear.setText("ЭФИР("
                                        + String.valueOf(orderManager.getCountOfOrdersByState(Order.STATE_NEW)
                                        + orderManager.getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                        + ")");
                            }
                        });
                    }

                    if (mOrder.orderType.equals("FindDriver1")) {
                        EfirOrder.startTimer(SettingsFromXml.getInstance().getFirstTimeSearch());
                        startTimerForNotification(SettingsFromXml.getInstance().getFirstTimeSearch(), mOrder.orderID, "f12");
                        if (sharedPrefs.getBoolean("prefAutoSearch1", true)) {
                            NotificationService.sendNotific("FindDriver1", mOrder.streetName + " " + mOrder.house, mOrder.orderID + "");
                        }
                    } else if (mOrder.orderType.equals("FindDriver2")) {
                        EfirOrder.startTimer(SettingsFromXml.getInstance().getSecondTimeSearch());
                        startTimerForNotification(SettingsFromXml.getInstance().getSecondTimeSearch(), mOrder.orderID, "f12");
                        if (sharedPrefs.getBoolean("prefAutoSearch2", true)) {
                            NotificationService.sendNotific("FindDriver2", mOrder.streetName + " " + mOrder.house, mOrder.orderID + "");
                        }
                    }

                    FragmentTransactionManager.getInstance().openFragment(FragmentPacket.ORDER);


                } else if (mOrder.orderType.equals("FindDriver3")) {
                    if (sharedPrefs.getBoolean("prefAutoSearchEfir", true)) {
                        NotificationService.sendNotific("FindDriver3", mOrder.streetName + " " + mOrder.house, mOrder.orderID + "");
                    }
                    play.play(R.raw.msg_new_ether_order);
                    Log.d("myLogs", "from etherFragment");
                    //order add
                    orderManager.addOrder(ord);

                    //isEixist
                    boolean found = false;
                    for (DispOrder4 item : orders) {
                        if (item.orderID == mOrder.orderID) {
                            orders.remove(item);
                            orders.add(0, mOrder);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        orders.add(0, mOrder);
                    }

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(SettingsFromXml.getInstance().getThirdTimeSearch());
                                System.out.println("upd ethear adapter");
                                ContextHelper.getInstance().runOnCurrentUIThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                NotificationService.cancelNotif(mOrder.orderID, "f3");
                                                orders.remove(mOrder);
                                                mAdapter.notifyDataSetChanged();

                                                if (orderManager.getOrder(mOrder.orderID).getStatus() == Order.STATE_TAKEN
                                                        || orderManager.getOrder(mOrder.orderID).getStatus() == Order.STATE_NEW)
                                                {
                                                    orderManager.changeOrderState(mOrder.orderID, Order.STATE_MISSED);
                                                }
                                                if (sharedPrefs.getBoolean("prefIsAutoSearch", false))// autosearch 1 2
                                                    SwipeFragment.ethear.setText("ЭФИР("
                                                            + String.valueOf(orderManager.getCountOfOrdersByState(Order.STATE_NEW)
                                                                            + orderManager.getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                            + ")");
                                                else
                                                    SwipeFragment.ethear.setText("ЭФИР("
                                                            + orderManager.getCountOfEfirOrders()
                                                            + ")");

                                                if (EfirOrder.orderId == mOrder.orderID) {
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

                    //notify
                    ContextHelper.getInstance().runOnCurrentUIThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    if (sharedPrefs.getBoolean("prefIsAutoSearch", false)) // autosearch 1 2
                                        SwipeFragment.ethear.setText("ЭФИР("
                                                + String.valueOf(orderManager.getCountOfOrdersByState(Order.STATE_NEW)
                                                                + orderManager.getCountOfOrdersByState(Order.STATE_KRYG_ADA))
                                                + ")");
                                    else
                                        SwipeFragment.ethear.setText("ЭФИР("
                                                + orderManager.getCountOfEfirOrders()
                                                + ")");
                                }
                            });
                }


            }

        });

        MultiPacketListener.getInstance().addListener(Packet.CALL_SIGNCHANGED_RESPONCE, new OnNetworkPacketListener() {

            @Override
            public void onNetworkPacket(Packet packet) {
                CallSignChangedResponce responce = (CallSignChangedResponce) packet;

                //Просто отменяем заказ
                if (responce.getOrderID() != -1) {
                    cancelOrder(responce.getOrderID());
                }
            }

            private void cancelOrder(int orderID) {
                for (DispOrder4 ord : orders) {
                    if (ord.orderID == orderID) {
                        OrderManager.getInstance().changeOrderState(orderID, Order.STATE_CANCELLED);
                        FragmentTransactionManager.getInstance().openFragment(FragmentPacket.ORDER_DETAILS);
                        OrderDetails.btnCancel.setEnabled(false);
                        OrderDetails.btnAccept.setEnabled(false);
                        OrderDetails.btnDo.setEnabled(false);
                        OrderDetails.btnArrived.setEnabled(false);
                        if (OrderManager.getInstance().getCountOfOrdersByState(Order.STATE_PERFORMING) == 0) {
                            AlertDHelper.showDialogOk("Заказ снят диспетчером");
                            PlaySound.getInstance().play(R.raw.msg_warn);
                        }
                        break;
                    }
                }
                List<Order> ord = OrderManager.getInstance().getOrdersByState(Order.STATE_PERFORMING);
                for (Order or : ord) {
                    if (or.getOrderID() == orderID) {
                        OrderDetails.setOrderId(orderID);

                        OrderManager.getInstance().changeOrderState(orderID, Order.STATE_CANCELLED);

                        FragmentTransactionManager.getInstance().openFragment(FragmentPacket.ORDER_DETAILS);
                        ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {

                            @Override
                            public void run() {
                                OrderDetails.btnCancel.setEnabled(false);
                                OrderDetails.btnDo.setEnabled(false);
                                OrderDetails.btnAccept.setEnabled(false);
                                PlaySound.getInstance().play(R.raw.msg_warn);
                                OrderDetails.btnArrived.setEnabled(false);
                                AlertDHelper.showDialogOk("Заказ снят диспетчером");
                            }
                        });
                        break;
                    }
                }
                //Свежие правки
                if (OrderManager.getInstance().getCountOfOrdersByState(Order.STATE_PERFORMING) < 1) {
                    ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                        @Override
                        public void run() {

                            StateObserver.getInstance().setDriverState(StateObserver.DRIVER_FREE);
                            TextView busy2 = (TextView) ContextHelper.getInstance().getCurrentActivity().findViewById(R.id.btn_busy);
                            busy2.setEnabled(true);
                            busy2.setText("Занят");

                            ServerData.getInstance().isFree = true;
                            if (ServerData.getInstance().doOwn) {
                                StateObserver.getInstance().setDriverState(StateObserver.DRIVER_BUSY);
                                busy2.setEnabled(false);
                            }
                        }

                    });
                }

                //Генерируем событие изменения состояния
            }

        });

        back = (Button) myView.findViewById(R.id.back_to_main_menu);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransactionManager.getInstance().back();
            }
        });

        orders_listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                Log.i("test", "Open description " + arg2 + "");
                EfirOrder.setOrderId(orders.get(arg2).orderID);
                FragmentTransactionManager.getInstance().openFragment(FragmentPacket.ORDER);
            }

        });

        refresh = (Button) myView.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestHelper.requestAir();
            }
        });

        return myView;
    }

    private void startTimerForNotification(final long time, final int orderID, final String string) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    ContextHelper.getInstance().runOnCurrentUIThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    NotificationService.cancelNotif(orderID, string);
                                }
                            });
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private DispOrder4 getOrder(int orderid) {
        for (DispOrder4 ord : orders) {
            if (ord.orderID == orderid) {
                return ord;
            }
        }

        return null;
    }

    public static class ListViewComparator implements Comparator<DispOrder4> {// autosearch 1 2

        @Override
        public int compare(DispOrder4 dispOrder4, DispOrder4 dispOrder42) {
            if (dispOrder4.orderType.equals(dispOrder42.orderType))
                return 0;
            else if (dispOrder4.orderType.equals("FindDriver1"))
                return -1;
            else if (dispOrder4.orderType.equals("FindDriver2") && dispOrder42.orderType.equals("FindDriver3"))
                return -1;
            else
                return 1;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.w("ether","onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("myLogs", "EthearFragment onResume");
        Log.d("catchOnResume", "fragId = " + this.getId());

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w("ether","onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w("ether","onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
