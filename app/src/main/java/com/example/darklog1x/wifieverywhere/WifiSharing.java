package com.example.darklog1x.wifieverywhere;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class WifiSharing extends Service {
    public WifiSharing() {
    }

    List<ScanResult> mScanResults;
    List<WifiConfiguration> KnownNetworks;
    private View mView;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    private static final String TAG = "WiFi_Sharing";
    WifiManager mWifiManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResults = mWifiManager.getScanResults();

                for (int i = 0; i < mScanResults.size(); i++) {
                    Log.w("Wifi is", mScanResults.get(i).toString());
                }
                //Savednetworks();
                displayNetworks();
            }
        }

    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                onCreate();
            }
        }).start();

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mWifiScanReceiver);
        super.onDestroy();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(mView);
        mView = null;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    private void Savednetworks() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        KnownNetworks = mWifiManager.getConfiguredNetworks();
        //getPrivilegedConfiguredNetworks

        for (int i = 0; i < KnownNetworks.size(); i++) {
            Log.w("Wifi is", KnownNetworks.get(i).toString());
        }
        ///data/misc/wifi

//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process mIpAddrProcess = runtime.exec("/system/bin/cat /date/misc/wifi/wpa_suppliciant.conf");
//            int mExitValue = mIpAddrProcess.waitFor();
//            System.out.println(" mExitValue " + mExitValue);
//
//        } catch (InterruptedException ignore) {
//            ignore.printStackTrace();
//            System.out.println(" Exception:" + ignore);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println(" Exception:" + e);
//        }

    }

    private void connetctToAndSetWifFiNetwork(String SSID, String Password) {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
// This is must be quoted according to the documentation
// http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#SSID
        wc.SSID = '"' + SSID + '"';
        wc.preSharedKey = '"' + Password + '"';
        wc.hiddenSSID = false;
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        int res = wifi.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean b = wifi.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b);
    }

    private void connetctToAndSetWifFiNetwork(String SSID) {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
// This is must be quoted according to the documentation
// http://developer.android.com/reference/android/net/wifi/WifiConfiguration.html#SSID
        wc.SSID = '"' + SSID + '"';
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.hiddenSSID = false;
        wc.status = WifiConfiguration.Status.ENABLED;
        int res = wifi.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean b = wifi.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b);
    }


    private void displayNetworks() {
        mView = new MyLoadView(this);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, 150, 10, 10,
                WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG, //TYPE_SYSTEM_OVERLAY,
                        WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.CENTER;
        mParams.setTitle("Window test");


        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mView, mParams);
    }

    public class MyLoadView extends View {

        private Paint mPaint;

        public MyLoadView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setTextSize(50);
            mPaint.setARGB(200, 200, 200, 200);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < mScanResults.size(); i++) {
                canvas.drawText(mScanResults.get(i).toString(), 0, 100, mPaint);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
