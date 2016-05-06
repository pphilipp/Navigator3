package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.AddressAdapter;
import com.innotech.imap_taxi.datamodel.Address;
import com.innotech.imap_taxi.datamodel.DispOrder;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.house;
import com.innotech.imap_taxi.datamodel.street;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.postRequests.ServiceConnection;
import com.innotech.imap_taxi3.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sergey on 12.11.2015.
 * Fragment provides editing of the current order
 */
public class OwnOrder extends FragmentPacket implements View.OnClickListener{
    public static final String LOG_TAG = OwnOrder.class.getSimpleName();

    //tag for logs
    private static final String TAG = "OWN_ORDER";
    //car class choose button
    @Bind(R.id.class_btn) Button classBtn;
    //cancel edit and quit button
    @Bind(R.id.cancel_btn) Button cancelBtn;
    //store and send new odrder ot server button
    @Bind(R.id.create_btn) Button createBtn;
    //Text field for order cost
    @Bind(R.id.costTxt) TextView costTxt;
    private Context mContext;
    private Typeface mTypeface;

    //Additional features indicator
    @Bind(R.id.features_image) ImageView featuresImage;
    //Comments indicator
    @Bind(R.id.comments_image) ImageView commentsImage;
    //wtf?
    @Bind(R.id.finger_print_image) ImageView fingerPrintImage;

    //listview for addresses in route
    @Bind(R.id.address_listview) ListView addressListView;

    //layout container for additional features. It must be visible if feature list is not blank
    @Bind(R.id.features_layout) LinearLayout featuresLayout;
    //text view for feature list
    @Bind(R.id.features_list_txt) TextView featuresTxt;

    //add/delete address buttons
    @Bind(R.id.add_address_img) ImageView addAddressImage;
    @Bind(R.id.minus_address_img) ImageView minusAddressImage;

    //class auto
    private final int allClass = 4;
    private final int standClass = 2;
    private final int biznClass = 1;
    private final int premClass = 5;

    //AsyncTask to get price from server
    private AsyncGetPrice getPriceFromServer;

    //links code and name of features
    private HashMap<Integer, String> featureLinks = new HashMap<>();

    //list of addresses in this order
    private List<Address> route;

    //list of features in code format
    private List<Integer> featuresList = new ArrayList<>();

    //threads queue
    private Executor executor = Executors.newSingleThreadExecutor();

    //initial order
    private static Order currentOrder;
    //current order price
    private String currentPrice;
    //progressBar which is shown while fetching cost
    @Bind(R.id.cost_progress) ProgressBar costProgress;
    private String defaultComments = "";
    private AddressAdapter mAdapter;

    //set order for this view
    public static void setCurrentOrder(Order mOrder) {
        currentOrder = mOrder;
    }

    public OwnOrder() {
        super(FragmentPacket.OWN_ORDER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ownOrderView = inflater.inflate(R.layout.own_order, container, false);
        ButterKnife.bind(this, ownOrderView);
        mContext = ContextHelper.getInstance().getCurrentContext();

        //get remote server
        final ServiceConnection sc = new ServiceConnection(mContext);
        Thread getServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sc.getServer();
                Log.d(LOG_TAG, "get remote server");
            }
        });
        getServerThread.start();

        //typeFace for text fields
        mTypeface = Typeface.createFromAsset(ContextHelper.getInstance()
                .getCurrentContext().getAssets(), "fonts/Roboto-Condensed.ttf");

        //define view elements and set typeFace
        classBtn.setTypeface(mTypeface);
        cancelBtn.setTypeface(mTypeface);
        createBtn.setTypeface(mTypeface);
        costTxt.setTypeface(mTypeface);
        featuresTxt.setTypeface(mTypeface);

        //setUp onClickListeners
        classBtn.setOnClickListener(this);
        featuresImage.setOnClickListener(this);
        commentsImage.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        //add address to route. new address wil be appended to the end of the current route
        addAddressImage.setOnClickListener(this);
        //delete last address from the route. it is impossible to delete address "from"
        minusAddressImage.setOnClickListener(this);

        //update all fields
        upDateOwnOrderState();
        addressListView.setAdapter(mAdapter);

        return ownOrderView;
    }

    private void sendOrder() {

        //TODO check validity of order (price != null)

        String name = "";
        String fromStr = "";
        String parad = "";
        String orderId = "";
        String opt = currentOrder.getComments();
        String price = "";
        String toStr = "";
        String flat = "";
        String streetId = route.get(0).getStreetID();
        String phone = "";
        String dom = "";
        String ads = "";
        for (int i = 0; i < featuresList.size(); i++) {
            ads += i == 0 ? featuresList.get(i) : "," + featuresList.get(i);
        }
        int cl = currentOrder.getAutoTariffClassUID();
        JSONArray suborder = new JSONArray();
        for (int i = 1; i < route.size(); i++) {
            toStr += i == 0 ? route.get(i).getStreetName() + ", " + route.get(i).getHouse() :
                    "->" + route.get(i).getStreetName() + ", " + route.get(i).getHouse();
            JSONObject point = new JSONObject();
            try {
                point.put("streetid", route.get(i).getStreetID());
                point.put("house", URLEncoder.encode(route.get(i).getHouse(), "UTF-8"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            suborder.put(point);
        }
        try {
            name = URLEncoder.encode(currentOrder.getClientName(), "UTF-8");
            fromStr = URLEncoder.encode(currentOrder.getStreet(), "UTF-8");
            parad = URLEncoder.encode(currentOrder.getParade(), "UTF-8");
            orderId = URLEncoder.encode(String.valueOf(currentOrder.getOrderID()), "UTF-8");
            opt = URLEncoder.encode(opt, "UTF-8");
            price = URLEncoder.encode(currentPrice, "UTF-8");
            toStr = URLEncoder.encode(toStr, "UTF-8");
            flat = URLEncoder.encode(currentOrder.getFlat(), "UTF-8");
            phone = URLEncoder.encode(currentOrder.getPhoneNumber(), "UTF-8");
            dom = URLEncoder.encode(route.get(0).getHouse(), "UTF-8");
            ads = URLEncoder.encode(ads, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String urlParameters = "func=UpdateOrder&nameUser=" + name
                + "&fromStr=" + fromStr
                + "&nameUser2="
                + "&orderid=" + orderId
                + "&frontdoor=" + parad
                + "&optation=" + opt
                + "&price=" + price
                + "&toStr=" + toStr
                + "&flat=" + flat
                + "&streetFromID=" + streetId
                + "&phone=" + phone
                + "&phone2=" + ""
                + "&BuildFrom=" + dom
                + "&ClassAvto=" + cl
                + "&additionalservices=[" + ads + "]"
                + "&arrObjectsJSON=" + suborder.toString();

        final ServiceConnection sc = new ServiceConnection(mContext);
        Thread upDateOrderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String res = sc.upDateOrder(urlParameters);
                Log.d(TAG, "resp = " + res);
            }
        });
        upDateOrderThread.start();
        FragmentTransactionManager.getInstance().back();
    }


    /**
     * fetch order cost from server
     */
    private void upDatePriceFromServer() {

        //run only one request at same instance
        //if there is already running task - cancel it and run another one
        if (getPriceFromServer == null) {
            getPriceFromServer = new AsyncGetPrice();
            getPriceFromServer.execute();
        } else {
            getPriceFromServer.cancel(true);
            getPriceFromServer = new AsyncGetPrice();
            getPriceFromServer.execute();
        }
    }

    /**
     * Shows dialog where one can edit comments of the order. On ok it stores comments in current order
     * @param mContext - current context
     */
    private void showCommentDialog(Context mContext) {

        //this dialog is full screen dialog

        //get screen width
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //create dialog
        final Dialog commentsDialog = new Dialog(mContext);
        commentsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set dialog custom view and set transparent background
        commentsDialog.setContentView(R.layout.comments_dialog);
        commentsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commentsDialog.show();

        //adjust dialog size
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(commentsDialog.getWindow().getAttributes());

        lp.height = height;
        lp.width = (int) (0.8 * width);
        commentsDialog.getWindow().setAttributes(lp);
        commentsDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

        //define view elements and set typeface
        final EditText commentET = (EditText) commentsDialog.findViewById(R.id.comments_edit_text);
        commentET.setTypeface(mTypeface);
        Button okBtn = (Button) commentsDialog.findViewById(R.id.ok_btn);
        okBtn.setTypeface(mTypeface);

        //fill editText if comment already exists
        if (currentOrder != null && !currentOrder.getComments().equals("")) {
            commentET.setText(currentOrder.getComments());
            commentET.setTextColor(mContext.getResources().getColor(R.color.orangeEditText));
        }

        //set comment on button click
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentET.getText() != null && !commentET.getText().toString().equals("")) {
                    currentOrder.setComments(commentET.getText().toString());
                }
                //update comment indicator image
                Log.d(TAG, "comments = " + currentOrder.getComments()
                        + ", default = " + currentOrder.getPhoneNumber() + ", " + currentOrder.getClientName());
                upDateOwnOrderState();
                commentsDialog.dismiss();
            }
        });
        
        commentsDialog.show();
    }

    /**
     * Set active/inactive image resource for imageView
     */
    private void checkCommentsImageState() {
        commentsImage.setImageResource((currentOrder != null && currentOrder.getComments() != null &&
                !currentOrder.getComments().equals("")) ?
                R.drawable.comments_small_active : R.drawable.comments_small_inactive);
    }

    /**
     * Shows dialog for editing of the features list. Stores data in current order on OK
     * @param mContext
     */
    private void showFeaturesDialog(Context mContext) {

        //this dialog is full screen

        //get screen width
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //create dialog
        final Dialog featureDialog = new Dialog(mContext);
        featureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set dialog custom view and transparent background
        featureDialog.setContentView(R.layout.features_dialog);
        featureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        featureDialog.show();

        //adjust dialog size
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(featureDialog.getWindow().getAttributes());

        lp.height = height;
        lp.width = (int) (0.8 * width);
        featureDialog.getWindow().setAttributes(lp);
        featureDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        
        //define all view elements and set typeFace
        TextView coolerTxt = (TextView) featureDialog.findViewById(R.id.cooler_txt);
        coolerTxt.setTypeface(mTypeface);
        TextView taxometerTxt = (TextView) featureDialog.findViewById(R.id.taxometer_txt);
        taxometerTxt.setTypeface(mTypeface);
        TextView petsTxt = (TextView) featureDialog.findViewById(R.id.pets_txt);
        petsTxt.setTypeface(mTypeface);
        TextView posTxt = (TextView) featureDialog.findViewById(R.id.pos_txt);
        posTxt.setTypeface(mTypeface);
        TextView luggageTxt = (TextView) featureDialog.findViewById(R.id.luggage_txt);
        luggageTxt.setTypeface(mTypeface);
        TextView nosmokingTxt = (TextView) featureDialog.findViewById(R.id.nosmoking_txt);
        nosmokingTxt.setTypeface(mTypeface);
        TextView wifiTxt = (TextView) featureDialog.findViewById(R.id.wifi_txt);
        wifiTxt.setTypeface(mTypeface);

        final CheckBox coolerCheckBox = (CheckBox) featureDialog.findViewById(R.id.cooler_check_box);
        final CheckBox taxometerCheckBox = (CheckBox) featureDialog.findViewById(R.id.taxometer_check_box);
        final CheckBox petsCheckBox = (CheckBox) featureDialog.findViewById(R.id.pets_check_box);
        final CheckBox posCheckBox = (CheckBox) featureDialog.findViewById(R.id.pos_check_box);
        final CheckBox luggageCheckBox = (CheckBox) featureDialog.findViewById(R.id.luggage_check_box);
        final CheckBox nosmokingCheckBox = (CheckBox) featureDialog.findViewById(R.id.nosmoking_check_box);
        final CheckBox wifiTxtCheckBox = (CheckBox) featureDialog.findViewById(R.id.wifi_check_box);

        //uncheck all boxes
        coolerCheckBox.setChecked(false);
        taxometerCheckBox.setChecked(false);
        petsCheckBox.setChecked(false);
        posCheckBox.setChecked(false);
        luggageCheckBox.setChecked(false);
        nosmokingCheckBox.setChecked(false);
        wifiTxtCheckBox.setChecked(false);
        //check checkBoxes if features are used in order
        for (int featureCode : featuresList) {
            switch (featureCode) {
                case Order.ADDITIONAL_SERVICES_CONDITIONER:
                    coolerCheckBox.setChecked(true);
                    break;
                case Order.ADDITIONAL_SERVICES_TAXIMETER:
                    taxometerCheckBox.setChecked(true);
                    break;
                case Order.ADDITIONAL_SERVICES_ANIMAL:
                    petsCheckBox.setChecked(true);
                    break;
                case Order.ADDITIONAL_SERVICES_POS_TERMINAL:
                    posCheckBox.setChecked(true);
                    break;
                case Order.ADDITIONAL_SERVICES_A_LOT_OF_BAG:
                    luggageCheckBox.setChecked(true);
                    break;
                case Order.ADDITIONAL_SERVICES_NO_SMOKE:
                    nosmokingCheckBox.setChecked(true);
                    break;
                case Order.ADDITIONAL_SERVICES_WIFI:
                    wifiTxtCheckBox.setChecked(true);
                    break;
            }

        }

        Button okBtn = (Button) featureDialog.findViewById(R.id.ok_btn);
        okBtn.setTypeface(mTypeface);

        //save result on okBtn click
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                featuresList = new ArrayList<Integer>();
                if (coolerCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_CONDITIONER);
                }
                if (taxometerCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_TAXIMETER);
                }
                if (petsCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_ANIMAL);
                }
                if (posCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_POS_TERMINAL);
                }
                if (luggageCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_A_LOT_OF_BAG);
                }
                if (nosmokingCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_NO_SMOKE);
                }
                if (wifiTxtCheckBox.isChecked()) {
                    featuresList.add(Order.ADDITIONAL_SERVICES_WIFI);
                }
                //upDate current order with new features
                currentOrder.setFeatures(getFeaturesList(featuresList));

                //update current fragment state and order cost
                upDateOwnOrderState();
                featureDialog.dismiss();
            }
        });

    }

    /**
     * Method returns list of features in Strings
     * @param featureList - list of features codes
     * @return - List<String>
     */
    private List<String> getFeaturesList(List<Integer> featureList) {
        List<String> featureStringList = new ArrayList<>();
        if (featureLinks != null) {
            for (int key : featureList) {
                featureStringList.add(featureLinks.get(key));
            }
        }
        return featureStringList;
    }

    /**
     * Method returns features in String by coma
     * @param featuresList - list of features codes
     * @return - String
     */
    private String getFeaturesString(List<Integer> featuresList) {
        String resFeatures = "";
        if (featuresList != null && featureLinks != null) {
            for (int i = 0; i < featuresList.size(); i++) {
                if (i == 0) {
                    resFeatures += featureLinks.get(featuresList.get(i));
                } else {
                    resFeatures += ", " + featureLinks.get(featuresList.get(i));
                }
            }
        }
        return resFeatures;
    }

    /**
     * Method gathers all edited fields and upDates fragment
     */
    private void upDateOwnOrderState(){

        //get all features in integer list
        featuresList = getFeatureList(currentOrder);
        //get features as String
        String featureString = getFeaturesString(featuresList);
        //show features if features list not empty
        featuresLayout.setVisibility((!featureString.equals("")) ? View.VISIBLE : View.GONE);
        featuresTxt.setText(featureString);
        //update features indicator
        checkFeatureImageState(featuresList);
        //upDate comments indicator
        checkCommentsImageState();
        getCarClass(currentOrder);
        //fill route by addresses
        getAddressesFromOrder(currentOrder);
        //get order cost from currentOrder
        getOrderCost(currentOrder);
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
        //update order cost from server if route does not have empty fields and is not empty
        if (isRouteReady()) {
            upDatePriceFromServer();
        }

    }


    /**
     * Method fill route list either from initial order or updates it from current changes
     * @param currentOrder
     */
    private void getAddressesFromOrder(Order currentOrder) {

        //initialize routes list
        if (route == null) {
            route = new ArrayList<>();
        }

        if (currentOrder != null && route.size() == 0) {

            //get Address from and setUp it
            final Address fromAddress = new Address();
            fromAddress.setStreetName(currentOrder.getStreet());
            //get house number
            final String addressFact = (currentOrder.getAddressFact() != null
                    || !currentOrder.getAddressFact().equals("0") || !currentOrder
                    .getAddressFact().equals("")) ? currentOrder.getAddressFact()
                    : "";
            Thread getStreetIDThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "from = " + fromAddress.getStreetName());
                    fromAddress.setStreetID(getStreetID(fromAddress.getStreetName()));
                    fromAddress.setHouseID(getHouseID(fromAddress.getStreetID(), addressFact));
                }
            });
            executor.execute(getStreetIDThread);

            fromAddress.setHouse(addressFact);

            //Entrance
            fromAddress.setEntrance((currentOrder.getParade() != null && !currentOrder.getParade().equals("")) ?
                    currentOrder.getParade() : "");
            //flat
            fromAddress.setFlat((currentOrder.getFlat() != null && !currentOrder.getFlat().equals("")) ?
                    currentOrder.getFlat() : "");

            route.add(fromAddress);

            //get subOrders
            for (DispOrder.DispSubOrder subOrder : currentOrder.getAddress()) {
                Log.d(LOG_TAG, "SubOrder");
                final Address toAddress = new Address();
                String[] splitAddr = subOrder.to.split(",");
                final String curStreet = splitAddr[0];
                final String curHouse = splitAddr[1].trim();
                toAddress.setStreetName(curStreet);
                Log.d(LOG_TAG, curStreet);
                Thread getStreetThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "to = " + curStreet);
                        toAddress.setStreetID(getStreetID(curStreet));
                        toAddress.setHouseID(getHouseID(toAddress.getStreetID(), curHouse));
                    }
                });
                executor.execute(getStreetThread);
                //getStreetThread.start();
                toAddress.setHouse(curHouse);

                route.add(toAddress);
            }

        }
    }

    /**
     * Method gets houseID from server. Must be run in Thread or AsyncTask
     * @param streetID
     * @param addressFact
     * @return - houseID
     */
    private String getHouseID(String streetID, String addressFact) {
        String houseID = "";
        final ServiceConnection sc = new ServiceConnection(mContext);
        List<house> streetsTemp = sc.getStreetHouse(streetID, addressFact);
        houseID = (streetsTemp != null && streetsTemp.size() > 0) ? streetsTemp.get(0).getId() : "";
        return houseID;
    }

    /**
     * Method gets streetID from server. Must be run in Thread or AsyncTask
     * @param streetName
     * @return - streetID
     */
    private String getStreetID(final String streetName) {
        String streetID = "";
        Log.d(TAG, "StreetName = " + streetName);
        final ServiceConnection sc = new ServiceConnection(mContext);
        List<street> streetsTemp = sc.getStreet(streetName);
        streetID = (streetsTemp != null && streetsTemp.size() > 0) ? streetsTemp.get(0).getId() : "";
        Log.d(TAG, "streetID = " + streetID);
        return streetID;
    }

    /**
     * Method sets class button text corresponding to class ID
     * @param currentOrder
     */
    private void getCarClass(Order currentOrder) {

        if (currentOrder != null) {
            int classId = currentOrder.getAutoTariffClassUID();
            String className = "";
            switch (classId) {
                case allClass:
                    className = mContext.getResources().getString(R.string.class_no_class);
                    break;
                case standClass:
                    className = mContext.getResources().getString(R.string.class_standard);
                    break;
                case biznClass:
                    className = mContext.getResources().getString(R.string.class_business);
                    break;
                case premClass:
                    className = mContext.getResources().getString(R.string.class_premium);
                    break;
                default:
                    className = mContext.getResources().getString(R.string.class_no_class);
            }
            classBtn.setText(className);
        }
    }

    /**
     * Method gets order price from initial order if route is not built
     * @param currentOrder
     */
    private void getOrderCost(Order currentOrder) {
        if (currentOrder != null && !isRouteReady()) {
            // set price
            float priceFloat = (currentOrder.getFare() == 0) ? 0 : currentOrder.getFare();
            String price = String.format("%.2f", priceFloat);
            costTxt.setText(getPriceFormat(price));
        }
    }

    /**
     * Method sets different size of cost digits
     * @param price -String
     * @return - formatted SpannableString
     */
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

    /**
     * Method returns integer list of additional features
     * @param currentOrder
     * @return Map<key, String>
     */
    private List<Integer> getFeatureList(Order currentOrder){

        //clear feature list
        featuresList = new ArrayList<>();

        //Attention! HARDCODE
        final String COND_SAMPLE = "Кондиционер";
        final String TAXOMETER_SAMPLE = "Таксометр";
        final String PET_SAMPLE = "Животное";
        final String BAG_SAMPLE = "Фургон";
        final String WIFI_SAMPLE = "Wi-Fi";
        final String POS_SAMPLE = "POS терминал";
        final String NOSMOKING_SAMPLE = "Не курить";

        //create Map of features
        featureLinks.put(Order.ADDITIONAL_SERVICES_CONDITIONER, COND_SAMPLE);
        featureLinks.put(Order.ADDITIONAL_SERVICES_TAXIMETER, TAXOMETER_SAMPLE);
        featureLinks.put(Order.ADDITIONAL_SERVICES_ANIMAL, PET_SAMPLE);
        featureLinks.put(Order.ADDITIONAL_SERVICES_A_LOT_OF_BAG, BAG_SAMPLE);
        featureLinks.put(Order.ADDITIONAL_SERVICES_WIFI, WIFI_SAMPLE);
        featureLinks.put(Order.ADDITIONAL_SERVICES_POS_TERMINAL, POS_SAMPLE);
        featureLinks.put(Order.ADDITIONAL_SERVICES_NO_SMOKE, NOSMOKING_SAMPLE);

        //add features to list if they are in current order
        if (currentOrder != null && currentOrder.getFeatures() != null) {
            List<String> currentOrderFeatures = currentOrder.getFeatures();

            if (currentOrderFeatures.contains(COND_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_CONDITIONER);
            }
            if (currentOrderFeatures.contains(TAXOMETER_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_TAXIMETER);
            }
            if (currentOrderFeatures.contains(PET_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_ANIMAL);
            }
            if (currentOrderFeatures.contains(BAG_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_A_LOT_OF_BAG);
            }
            if (currentOrderFeatures.contains(WIFI_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_WIFI);
            }
            if (currentOrderFeatures.contains(POS_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_POS_TERMINAL);
            }
            if (currentOrderFeatures.contains(NOSMOKING_SAMPLE)) {
                featuresList.add(Order.ADDITIONAL_SERVICES_NO_SMOKE);
            }
        }
        return featuresList;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        checkFeatureImageState(featuresList);
        checkCommentsImageState();
        //upDateOwnOrderState();
    }

    @Override
    public void onResume() {
        super.onResume();

        //check if there are new addresses
        Address newAddress = GetAddressFragment.getCurrentAddress();
        if (newAddress != null && route != null) {
            route.add(newAddress);
            if (mAdapter != null) {
                mAdapter.upDateList(route);
                //upDatePriceFromServer();
            }

        }
        //upDate fragment
        upDateOwnOrderState();
    }

    /**
     * Method sets feature indicator recourse active/inactive
     * @param featuresList - integer list
     */
    private void checkFeatureImageState(List<Integer> featuresList) {
        if (featuresList != null && featuresList.size() > 0) {
            //upDatePriceFromServer();
            featuresImage.setImageResource(R.drawable.features_small_active);
        } else {
            featuresImage.setImageResource(R.drawable.features_small_inactive);
        }

    }

    /**
     * Method shows class choose dialog. and upDates order class onClick
     * @param mContext
     */
    private void showClassDialog(Context mContext) {

        //define buttons
        Button standardBtn;
        Button businessBtn;
        Button premiumBtn;
        Button noClassBtn;


        final String STANDARD = mContext.getResources().getString(R.string.class_standard);
        final String BUSINESS = mContext.getResources().getString(R.string.class_business);
        final String PREMIUM = mContext.getResources().getString(R.string.class_premium);
        final String NO_CLASS = mContext.getResources().getString(R.string.class_no_class);

        //create dialog
        final Dialog classDialog = new Dialog(mContext);
        classDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //set custom view and transparent background
        classDialog.setContentView(R.layout.class_dialog);
        classDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        standardBtn = (Button) classDialog.findViewById(R.id.standard);
        standardBtn.setTag(standClass);
        businessBtn = (Button) classDialog.findViewById(R.id.business);
        businessBtn.setTag(biznClass);
        premiumBtn = (Button) classDialog.findViewById(R.id.premium);
        premiumBtn.setTag(premClass);
        noClassBtn = (Button) classDialog.findViewById(R.id.no_class);
        noClassBtn.setTag(allClass);

        standardBtn.setTypeface(mTypeface);
        businessBtn.setTypeface(mTypeface);
        premiumBtn.setTypeface(mTypeface);
        noClassBtn.setTypeface(mTypeface);

        View.OnClickListener chooseClass = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //default values
                String className = NO_CLASS;
                int classID = allClass;
                switch ((int) v.getTag()) {
                    case allClass:
                        className = NO_CLASS;
                        classID = allClass;
                        break;
                    case standClass:
                        className = STANDARD;
                        classID = standClass;
                        break;
                    case biznClass:
                        className = BUSINESS;
                        classID = biznClass;
                        break;
                    case premClass:
                        className = PREMIUM;
                        classID = premClass;
                        break;
                    default:
                        className = NO_CLASS;
                        classID = allClass;
                }
                //upDate button text
                classBtn.setText(className);
                //upDate current order
                currentOrder.setAutoClass(className);
                currentOrder.setAutoTariffClassUID(classID);
                //get price from server
                upDatePriceFromServer();
                classDialog.dismiss();
            }
        };

        //set clickListeners
        standardBtn.setOnClickListener(chooseClass);
        businessBtn.setOnClickListener(chooseClass);
        premiumBtn.setOnClickListener(chooseClass);
        noClassBtn.setOnClickListener(chooseClass);

        classDialog.show();
    }

    /**
     * Check if route list initialized and doesn`t content null fields
     * @return - true if ok
     */
    public boolean isRouteReady() {
        if (route != null && route.size() > 0) {
            for (Address curAddress : route) {
                if (curAddress.getStreetID() == null || curAddress.getStreetID().equals("")
                        || curAddress.getStreetID() == null || curAddress.getStreetID().equals("")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.class_btn :
                //send order to server
                showClassDialog(mContext);
                break;
            case R.id.features_image :
                showFeaturesDialog(mContext);
                break;
            case R.id.comments_image :
                //quit back to order details
                showCommentDialog(mContext);
                break;
            case R.id.cancel_btn :
                FragmentTransactionManager.getInstance().back();
                break;
            case R.id.create_btn:
                //send order to server
               sendOrder();
                break;
            case R.id.add_address_img :
                GetAddressFragment.setCurrentOrder(currentOrder);
                FragmentTransactionManager.getInstance()
                        .openFragment(FragmentPacket.GET_ADDRESS);
                break;

            case R.id.minus_address_img :
                //we can not delete address "from"
                if (route.size() > 1) {
                    route.remove(route.size() - 1);
                    //upDate list
                    mAdapter.upDateList(route);
                    if (route.size() > 1) {
                        //upDate price
                        upDatePriceFromServer();
                    } else {
                        //if there is only one address set 0 price
                        costTxt.setText(getPriceFormat("0.00"));
                    }
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.address_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.address_cannt_be_deleted), Toast.LENGTH_SHORT).show();
                }
                //log list of products
                for (Address mAddress : route) {
                    Log.d(TAG, "minus button check address = " + mAddress.getStreetName());
                }

            break;
        }
    }

    /**
     * Async class to get order price
     */
    private class AsyncGetPrice extends AsyncTask<Void, Void, String> {

        public AsyncGetPrice() {
            Log.d(TAG, "constructor");
        }

        /**
         * set progress bar instead of cost textView while fetching price
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                @Override
                public void run() {
                    costTxt.setVisibility(View.GONE);
                    costProgress.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {


            Log.d(TAG, "update");
            //store all needed info into urlParameters
            String phone2 = "";
            String ads = "";
            for (int i = 0; i < featuresList.size(); i++) {
                ads += i == 0 ? featuresList.get(i) : "," + featuresList.get(i);
            }
            String toStr = "";
            JSONArray suborder = new JSONArray();
            for (int i = 1; i < route.size(); i++) {
                toStr += i == 0 ? route.get(i).getStreetName() + ", " + route.get(i).getHouse() :
                        "->" + route.get(i).getStreetName() + ", " + route.get(i).getHouse();
                JSONObject point = new JSONObject();
                try {
                    point.put("streetid", route.get(i).getStreetID());
                    point.put("house", URLEncoder.encode(route.get(i).getHouse(), "UTF-8"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                suborder.put(point);
            }


            //get price
            String getPriceUrlParameters = null;
            try {
                getPriceUrlParameters = "streetFromID=" + route.get(0).getStreetID()
                        + "&BuildFrom=" + URLEncoder.encode(route.get(0).getHouse(), "UTF-8")
                        + "&ClassAvto=" + currentOrder.getAutoTariffClassUID()
                        + "&additionalservices=[" + ads
                        + "]" + "&arrObjectsJSON=" + suborder.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final ServiceConnection sc = new ServiceConnection(mContext);
            final String urlParams = getPriceUrlParameters;
            currentPrice = sc.getPrice(urlParams);
            Log.d(TAG, "price = " + currentPrice);


            return currentPrice;
        }

        /**
         * Set textView visible and toggleBtnHide progressBar
         * @param s - String
         */
        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            ContextHelper.getInstance().runOnCurrentUIThread(new Runnable() {
                @Override
                public void run() {
                    costTxt.setText(getPriceFormat(s));
                    costTxt.setVisibility(View.VISIBLE);
                    costProgress.setVisibility(View.GONE);
                }
            });
        }
    }

}
