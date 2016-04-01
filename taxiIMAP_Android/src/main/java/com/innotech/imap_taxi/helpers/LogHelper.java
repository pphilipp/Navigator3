package com.innotech.imap_taxi.helpers;

import java.io.*;
import java.util.Calendar;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.network.ConnectionHelper;

public class LogHelper {

    public static void w(final String str) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                File sdCardRoot = Environment.getExternalStorageDirectory();
//                File yourDir = new File(sdCardRoot, "imap_android");
//                yourDir.mkdirs();
//
//                FileWriter fWriter;
//                try {
//
//                    Time now = new Time();
//                    now.setToNow();
//                    //now.t
//                    String date = now.monthDay + "." + now.month + " " + now.hour + ":" + now.minute + ":" + now.second + " - " + " socket is connected - " + ConnectionHelper.getInstance().isConnected() + " socket is disconnected - " + ConnectionHelper.getInstance().isDisconnected();
//
//                    String path = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/imap_android/log.txt";
//
//                    fWriter = new FileWriter(path, true);
//                    fWriter.write(date + " " + str + "\n");
//                    fWriter.flush();
//                    fWriter.close();
//                } catch (Exception e) {
//                    Log.d("error", e.getMessage());
//                }
//            }
//        }).start();
    }

    public static void w_gps(final String str) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                File sdCardRoot = Environment.getExternalStorageDirectory();
//                File yourDir = new File(sdCardRoot, "imap_android");
//                yourDir.mkdirs();
//
//                FileWriter fWriter;
//                try {
//
//                    Time now = new Time();
//                    now.setToNow();
//                    String date = now.monthDay + "." + now.month + " " + now.hour + ":" + now.minute + ":" + now.second + " - ";
//
//                    String path = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/imap_android/log_gps.txt";
//
//                    fWriter = new FileWriter(path, true);
//                    fWriter.write(date + " " + str + "\n");
//                    fWriter.flush();
//                    fWriter.close();
//                } catch (Exception e) {
//                    Log.d("error", e.getMessage());
//                }
//            }
//        }).start();
    }

    public static void w_ping_state(final String str) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                File sdCardRoot = Environment.getExternalStorageDirectory();
//                File yourDir = new File(sdCardRoot, "imap_android");
//                yourDir.mkdirs();
//
//                FileWriter fWriter;
//                try {
//
//                    Time now = new Time();
//                    now.setToNow();
//                    String date = now.monthDay + "."
//                            + now.month + " "
//                            + now.hour + ":"
//                            + now.minute + ":"
//                            + now.second + " - "
//                            + " socket is connected - "
//                            + ConnectionHelper.getInstance().isConnected()
//                            + " socket is disconnected - "
//                            + ConnectionHelper.getInstance().isDisconnected();
//                    String path = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/imap_android/log_ping_state.txt";
//
//                    fWriter = new FileWriter(path, true);
//                    fWriter.write(date + " " + str + " " + ServerData.getInstance().getIp() + ":" + ServerData.getInstance().getPort() + "\n");
//                    fWriter.flush();
//                    fWriter.close();
//                } catch (Exception e) {
//                    Log.d("error", e.getMessage());
//                }
//            }
//        }).start();
    }

    public static void w_connection(final String str) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//
//                File sdCardRoot = Environment.getExternalStorageDirectory();
//                File yourDir = new File(sdCardRoot, "imap_android");
//                yourDir.mkdirs();
//
//                FileWriter fWriter;
//                try {
//
//                    Calendar c = Calendar.getInstance();
//                    Time now = new Time();
//                    now.setToNow();
//                    String date = now.monthDay + "." + now.month + " " + now.hour + ":" + now.minute + ":" + now.second + " - " + " socket is connected - " + ConnectionHelper.getInstance().isConnected() + " socket is disconnected - " + ConnectionHelper.getInstance().isDisconnected();
//
//                    String path = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/imap_android/connection.txt";
//
//                    fWriter = new FileWriter(path, true);
//                    fWriter.write(date + " " + str + "\n");
//                    fWriter.flush();
//                    fWriter.close();
//                } catch (Exception e) {
//                    Log.d("error", e.getMessage());
//                }
//            }
//
//        }).start();
    }

    public static void w_log_registr(final String str) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                File sdCardRoot = Environment.getExternalStorageDirectory();
//                File yourDir = new File(sdCardRoot, "imap_android");
//                yourDir.mkdirs();
//
//                FileWriter fWriter;
//                try {
//
//                    Calendar c = Calendar.getInstance();
//                    Time now = new Time();
//                    now.setToNow();
//                    String date = now.monthDay + "." + now.month + " " + now.hour + ":" + now.minute + ":" + now.second + " - ";
//                    String path = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/imap_android/log_registr.txt";
//
//                    fWriter = new FileWriter(path, true);
//                    fWriter.write(date + " " + str + "\n");
//                    fWriter.flush();
//                    fWriter.close();
//                } catch (Exception e) {
//                    Log.d("error", e.getMessage());
//                }
//            }
//
//
//        }).start();
    }

    public static void w_log_packets(final String str) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                File sdCardRoot = Environment.getExternalStorageDirectory();
//                File yourDir = new File(sdCardRoot, "imap_android");
//                yourDir.mkdirs();
//
//                FileWriter fWriter;
//                try {
//
//                    Calendar c = Calendar.getInstance();
//                    Time now = new Time();
//                    now.setToNow();
//                    String date = now.monthDay + "." + now.month + " " + now.hour + ":" + now.minute + ":" + now.second + " - ";
//                    String path = Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/imap_android/log_packets.txt";
//
//                    fWriter = new FileWriter(path, true);
//                    fWriter.write(date + " " + str + "\n");
//                    fWriter.flush();
//                    fWriter.close();
//                } catch (Exception e) {
//                    Log.d("error", e.getMessage());
//                }
//            }
//
//
//        }).start();
    }

    public static void printLog(Context context){
        try{
//            android.os.Process.myPid();
            Time now = new Time();
            now.setToNow();
            Log.d("Play LOG!!! ", now.monthDay+"."+now.month+"."+now.year+" "+now.hour+":"+now.minute);
            File filename = new File(Environment.getExternalStorageDirectory().toString().concat(File.separator).concat("imap_android"));//context.getExternalFilesDir(null).getPath()
            filename.mkdirs();
            String command = "logcat -f "+ filename.getPath().concat(File.separator).concat("my_app.log") + " -v time -d *:D";
            Runtime.getRuntime().exec(command);
            Log.d("Stop LOG!!!", "command: " + command);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void lounchLogging(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File sdCardRoot = Environment.getExternalStorageDirectory();
                File youDir = new File(sdCardRoot, "imap_android");
                File archiveDir = new File(youDir, "log_archive");
                archiveDir.mkdirs();
                File[] files = youDir.listFiles();
                try {
                    Time now = new Time();
                    now.setToNow();
                    String date = now.monthDay + "-"
                            + now.month + "-"
                            + now.hour + "-"
                            + now.minute + "-"
                            + now.second;
                    BufferedInputStream origin = null;
                    FileOutputStream dest = new FileOutputStream(archiveDir.getPath().concat(File.separator).concat(date).concat(".zip"));
                    ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(dest));
                    for(File f : files){
                        byte[] data = new byte[2048];
                        if (!f.isDirectory()){
                            FileInputStream fi = new FileInputStream(f.getPath());
                            origin = new BufferedInputStream(fi, 2048);
                            ZipEntry entry = new ZipEntry(f.getName());
                            zipOutputStream.putNextEntry(entry);
                        }
                        int count;
                        while ((count = origin != null ? origin.read(data, 0, 2048) : -1) != -1){
                            zipOutputStream.write(data,0,count);
                        }
                    }
                    if (origin != null) {
                        origin.close();
                    }
                    zipOutputStream.close();
//                    for(File f : files){
//                        if(!f.isDirectory())
//                            f.delete();
//                    }
//                    printLog(ContextHelper.getInstance().getCurrentContext());
                    Log.d("LogArchive","Create");
                } catch (FileNotFoundException e) {
                    Log.d("LogArchive", e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("LogArchive", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
