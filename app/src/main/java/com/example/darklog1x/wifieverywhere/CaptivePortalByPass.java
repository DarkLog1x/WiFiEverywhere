package com.example.darklog1x.wifieverywhere;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class CaptivePortalByPass extends Service {

    private static final String TAG = "Captive_Portal";


    public CaptivePortalByPass() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Your logic that service will perform will be placed here

                CaptivePortalBypassStart();

                //Stop service once it finishes its task
                //Might not be used
                //CaptivePortalBypassStop();
            }
        }).start();

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    //See if the network has changed
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                CaptivePortalBypassStart();
            }
        }
    };

    private void CaptivePortalBypassStart() {
        Log.w(TAG, "started task to see if captive protal is present");

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            boolean returnvalue = CheackInternetAccess();
            Log.w(TAG, "return value of url =" + returnvalue);

            if (isWiFi && !returnvalue) {
                Log.w(TAG, "Network with captive portal");
            } else {
                Log.w(TAG, "Network with internet access");
            }
        }
    }


    private void GetCaptivePortal() {
        try {
            Document doc = Jsoup.connect("http://google.com").get();
            String title = doc.title();
            Log.w(TAG, "title of page" + title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This will test if we are able to ping outside!
    private boolean CheackInternetAccess() {
        System.out.println("executeCommand");
        Runtime runtime = Runtime.getRuntime();
        try {
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 www.google.com");
            int mExitValue = mIpAddrProcess.waitFor();
            System.out.println(" mExitValue " + mExitValue);
            if (mExitValue == 0) {
                Log.w("Connection", "Success !");
                return true;
            } else {
                Log.w("Connection", "Failed !");
                return false;
            }
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
            System.out.println(" Exception:" + ignore);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }
        return false;
    }


    //This might not be used!
    private void CaptivePortalBypassStop() {
        stopSelf();
        Log.w(TAG, "The Captive was stopped");

    }
}
