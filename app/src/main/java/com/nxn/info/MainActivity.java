package com.nxn.info;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends Activity {
    Process su;
    BufferedReader mReader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            su = Runtime.getRuntime().exec("/system/bin/su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mReader = new BufferedReader(new InputStreamReader(su.getInputStream()));
        setContentView(R.layout.main);
        EditText editor = (EditText)findViewById(R.id.editText);
        editor.setText(
                "Build.DEVICE="+Build.DEVICE+
                        "\nBuild.MODEL="+Build.MODEL+
                        "\nBuild.BOARD="+Build.BOARD);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.layout.main,new Notification.Builder(MainActivity.this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Info")
                .setContentIntent(PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, MainActivity.class)
                        .setAction("com.nxn.info")
                        .addCategory(Intent.CATEGORY_LAUNCHER), 0))
                .getNotification());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        EditText editor = (EditText)findViewById(R.id.editText);
        if(editor == null) return true;
        int id = item.getItemId();
        if (id == R.id.reset) {
            editor.setText(
                    "Build.DEVICE=" + Build.DEVICE +
                            "\nBuild.MODEL=" + Build.MODEL +
                            "\nBuild.BOARD=" + Build.BOARD
            );
            return true;
        }
        if(id == R.id.cat){
            try {
                String cmd = "cat /sys/class/misc/mtgpio/pin | grep \": 0 0 1 1\"\necho \"----------------\"\n";
                su.getOutputStream().write(cmd.getBytes());
                while (!mReader.ready());
                while (mReader.ready()){
                    editor.getEditableText().append("\n\t"+mReader.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(id == R.id.find){
            try {
                String cmd = "find -P /sys/class | grep gpio\necho \"----------------\"\n";
                su.getOutputStream().write(cmd.getBytes());
                while (!mReader.ready());
                while (mReader.ready()){
                    editor.getEditableText().append("\n\t"+mReader.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
