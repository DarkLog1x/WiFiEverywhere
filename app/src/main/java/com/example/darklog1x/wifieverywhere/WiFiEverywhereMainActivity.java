package com.example.darklog1x.wifieverywhere;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class WiFiEverywhereMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_everywhere_main);


        final Button StartCaptiveService = (Button) findViewById(R.id.StartCaptiveService);
        StartCaptiveService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isMyServiceRunning(CaptivePortalByPass.class) && !isMyServiceRunning(WifiSharing.class)) {
                    startService(new Intent(getBaseContext(), CaptivePortalByPass.class));
                    startService(new Intent(getBaseContext(), WifiSharing.class));

                } else {
                    Toast.makeText(getApplicationContext(), "Services already Started", Toast.LENGTH_SHORT).show();
                }


            }
        });

        final Button StopCaptiveService = (Button) findViewById(R.id.StopCaptiveService);
        StopCaptiveService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isMyServiceRunning(CaptivePortalByPass.class) && isMyServiceRunning(WifiSharing.class)) {
                    stopService(new Intent(getBaseContext(), CaptivePortalByPass.class));
                    stopService(new Intent(getBaseContext(), WifiSharing.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Services already Stopped", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

