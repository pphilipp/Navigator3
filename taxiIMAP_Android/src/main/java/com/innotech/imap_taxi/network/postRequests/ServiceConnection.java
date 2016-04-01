package com.innotech.imap_taxi.network.postRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.innotech.imap_taxi.datamodel.house;
import com.innotech.imap_taxi.datamodel.street;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sergey on 16.11.2015.
 */
public class ServiceConnection {
    private static final String LOG_TAG = ServiceConnection.class.getSimpleName();
    private static final boolean TEST = true;
    private Context ctx;
    SharedPreferences mSettings;
    SharedPreferences mSettings2;
    private final byte COND = 3;
    private final byte POS = 15;
    private final byte ANIMAL = 34;
    private final byte WIFI = 33;
    private final byte NOSMOKE = 35;
    private final byte BAG = 11;

    public ServiceConnection(Context ctx) {
        super();
        this.ctx = ctx;
        mSettings = ctx.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        mSettings2 = ctx.getSharedPreferences("mydata", Context.MODE_PRIVATE);
    }


    /**
     * return url path to host server/ This path is added before files names
     * @return - String url
     */
    private String getHost() {
        String h = "";
        if (mSettings.contains("Server")) {
            h = mSettings.getString("Server", "");
        }
        return h;
    }

    /**
     * Method fetches list of streets from server
     * @param str - query. Part of street name
     * @return - List of streets which meet query request
     */
    public List<street> getStreet(String str) {
        InputStream is = null;
        List<street> mStr = new ArrayList<street>();
        String street = "";
        try {
            street = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // ""
            e1.printStackTrace();
        }

        try {
            //JSONArray jArray = new JSONArray(postRequest("http://194.48.212.13/mobile/ind.php", "term=" + street));
            JSONArray jArray = new JSONArray(postRequest("ind.php", "term=" + street));
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject res = jArray.getJSONObject(i);
                Log.d("PRELIM_CHECK", "street JSON = " + res.toString());
                mStr.add(new street(res.optString("value"),
                        res.optString("id"), res.optString("geox"), res
                        .optString("geoy")));
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.getMessage());
            Log.e("PRELIM_CHECK", "Error parsing data " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return mStr;
    }

    /**
     * Method fetches list of houses by query
     * @param streetId - id of given street
     * @param house - number which is included in potential house number
     * @return - List of houses which meet query
     */
    public List<house> getStreetHouse(String streetId, String house) {

        List<house> mHouse = new ArrayList<house>();
        String value, id, x, y;

        InputStream is = null;
        String hs = "";

        try {
            hs = URLEncoder.encode(house, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            //JSONArray response = new JSONArray(postRequest("http://194.48.212.13/mobile/ind2.php", "idstr=" + streetId + "&term=" + hs));
            JSONArray response = new JSONArray(postRequest("ind2.php", "idstr=" + streetId + "&term=" + hs));
            for (int i = 0; i < response.length(); i++) {
                JSONObject res = response.getJSONObject(i);
                value = res.optString("value");
                id = res.optString("id");
                x = res.optString("geox");
                y = res.optString("geoy");

                mHouse.add(new house(value, id, x, y));
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "result = " + mHouse.get(0).getValue());
        return mHouse;
    }


    /**
     * Method fetches route info from server (points, distance, etc)
     * @param urlParameters -
     *   "streetFromID="
     * + "&BuildFrom="
     * + "&ClassAvto="
     * + "&additionalservices=[]"
     * + "&arrObjectsJSON=" - subOrder JSONArray
     * @return - String price 99.99
     */
    public String getPrice(String urlParameters) {
        Log.d(LOG_TAG, "urlParameters = " + urlParameters);
        String price = "";
        String res = postRequest("getroute.php", urlParameters);

        Log.d(LOG_TAG, "q - " + urlParameters);
        Log.d(LOG_TAG, "q - " + res);

        String res2 = "";
        if (res != null && !res.equals("")) {
            res2 = res.substring(res.indexOf("{"), (res.indexOf("}") + 1));
        }
        JSONObject response = null;
        try {
            response = new JSONObject(res2);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "JSONException problem");
        }
        String distance = response.optString("distance");

        Log.d(LOG_TAG, distance);

        price = response.optString("price");
        return price;
    }


    /**
     * Send request to server
     * @param script2 - page
     * @param urlParameters2 - post fields
     * @return - server response as String
     */
    public String postRequest(String script2, String urlParameters2) {

        String script;

        final String urlParameters = urlParameters2;

        if (mSettings.contains("PHPSESSID") && !mSettings.getString("PHPSESSID", "").equals("")) {
            script = script2 + "?PHPSESSID=" + mSettings.getString("PHPSESSID", "");
        } else {
            script = script2;
        }

        String res="";

        try {
            String request = getHost() + script;
            Log.d("postRequest", request);
            Log.d("postRequest", urlParameters);
            URL url;
            url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(35000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");


            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream in = connection.getInputStream();

            Scanner in1 = new Scanner(in);

            StringBuilder sb = new StringBuilder();

            while (in1.hasNextLine()) {
                sb.append(in1.nextLine() + "\n");
            }

            connection.disconnect();


            Log.d(LOG_TAG, sb.toString());

            res = sb.toString();

        } catch (MalformedURLException e3) {
            e3.printStackTrace();
            Log.d("PostRequest", "error = " + e3.getMessage());
        } catch (ProtocolException e) {
            Log.d("PostRequest", "error = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("PostRequest", "error = " + e.getMessage());
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Log.d("PostRequest", "error = " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("PostRequest", "error = " + e.getMessage());
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Method sends request for order update (like in WebCab createOrder)
     * @param urlParams - list of post fields:
     *   func=UpdateOrder
     *   &nameUser=" + name - Client name
     * + "&fromStr=" + fromStr
     * + "&nameUser2="
     * + "&orderid=" + orderId - MUST BE
     * + "&frontdoor=" + parad - entrance
     * + "&optation=" + opt - comments
     * + "&price=" + price
     * + "&toStr=" + toStr - first address where
     * + "&flat=" + flat
     * + "&streetFromID=" + streetId
     * + "&phone=" + phone - Clients phone
     * + "&phone2=" + ""
     * + "&BuildFrom=" + dom
     * + "&ClassAvto=" + cl
     * + "&additionalservices=[" + ads + "]" - numbered list of additional features
     * + "&arrObjectsJSON=" + suborder.toString() - subOrder addresses
     * @return
     */
    public String upDateOrder(String urlParams) {
        String res = "no result";
        try {

            JSONObject response = new JSONObject(postRequest("create_order.php", urlParams));

            res = response.toString();
        } catch (JSONException e) {
            e.printStackTrace();

            Log.d(LOG_TAG, "request error");

        }

        Log.d(LOG_TAG, "orderUpdate res = " + res);

        return res;
    }

    public String getCode(String numb) {

        String message = "";
        String s = "";
        String urlParameters = null;
        try {
            urlParameters = "func=code&tel=" + URLEncoder.encode(numb, "UTF-8");

            String request = getHost() + "reglogand.php";
            Log.d("getCode", request);
            Log.d("getCode", urlParameters);

            URL url;
            url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream in = connection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                s += current;
            }
            Log.d("getCode", "RESPONSE - " + s);

            connection.disconnect();

            JSONObject result = null;

            result = new JSONObject(s);

            message = result.optString("message");
            Log.v("getCode", " message - " + message);

        } catch (UnsupportedEncodingException e) {
            //Mint.logException(e);
            // ""
            e.printStackTrace();
        } catch (MalformedURLException e) {
            //Mint.logException(e);
            // ""
            e.printStackTrace();
        } catch (IOException e) {
            //Mint.logException(e);
            // ""
            e.printStackTrace();
        } catch (JSONException e) {
            //Mint.logException(e);
            // ""
            e.printStackTrace();
        } catch (NullPointerException e) {
            //Mint.logException(e);
            // ""
            e.printStackTrace();
        }


        return message;

    }

    public String checkCode(String numb, String pass) {

        String message = "";
        String s = "";
        int version = mSettings.getInt("app_version", -1);
        int os = mSettings.getInt("os_version", -1);
        String deviceName = mSettings.getString("device_name", "");

        try {
            final String urlParameters = "func=LoginToken&login="
                    + URLEncoder.encode(numb, "UTF-8") + "&password=" + pass
                    + "&version=" + version
                    + "&os=" + os
                    + "&device=" + deviceName;

            String request = getHost() + "reglogand.php";
            Log.d("host", request);
            Log.d("POST PARAMS", urlParameters);
            URL url;
            url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream in = connection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                s += current;
            }
            Log.d("checkCode", "RESPONSE - " + s);

            connection.disconnect();

            JSONObject result = null;
            result = new JSONObject(s);

            message = result.optString("message");
            Log.v("checkCode", " message - " + message);

            if (message.equals("success")) {
                String sess = result.optString("session");

                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("PHPSESSID", sess);
                editor.commit();

                Log.d("SHARED", "puted PHPSESSID " + mSettings.getString("PHPSESSID", ""));
            }

        } catch (MalformedURLException e3) {
            Log.d(LOG_TAG, "error = " + e3.getMessage());
            e3.printStackTrace();
        } catch (ProtocolException e) {
            Log.d(LOG_TAG, "error = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(LOG_TAG, "error = " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(LOG_TAG, "error = " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.d(LOG_TAG, "error = " + e.getMessage());
            e.printStackTrace();
        }

        return message;
    }

    /**
     * Fetches current actual server url
     */
    public void getServer() {
        Log.d(LOG_TAG, "start getServer()");

        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }

        String script;
        if (TEST) {
            //test server
            script = "http://194.48.212.13/mobile/getURL2.php";
        } else {
            //real server
            script = "http://194.48.212.13/mobile/getURL.php";
        }

        String urlParameters = "json={\"Task\":\"GetURL\",\"version\":\"" + version + "\"}";

        String res = "";

        try {

            Log.d("getURL", script);
            Log.d("getURL", urlParameters);

            URL url;
            url = new URL(script);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            InputStream in = connection.getInputStream();

            Scanner in1 = new Scanner(in);

            StringBuilder sb = new StringBuilder();

            while (in1.hasNextLine()) {
                sb.append(in1.nextLine() + "\n");
            }

            connection.disconnect();

            Log.d("postRequest", sb.toString());

            JSONObject result = null;
            result = new JSONObject(sb.toString());

            String message = result.optString("message");
            Log.v("getURL", " message - " + message);

            if (message.equals("success")) {
                String serv = result.optString("url");

                res = serv;
            }
        } catch (MalformedURLException e3) {
            e3.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = mSettings.edit();
        Log.i("Server_check", res);
        editor.putString("Server", res);
        editor.commit();

        Log.d(LOG_TAG, "puted Server " + mSettings.getString("Server", ""));

    }

}
