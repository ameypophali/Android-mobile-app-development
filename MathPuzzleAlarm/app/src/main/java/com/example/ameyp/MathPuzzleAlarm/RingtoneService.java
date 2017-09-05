package com.example.ameyp.MathPuzzleAlarm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ameyp.MathPuzzleAlarm.R;

/**
 * Created by ameyp on 4/10/2017.
 */

public class RingtoneService extends Service {

    static MediaPlayer mediaPlayer;
    int start_id;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int start_Id) {

        String action_String = intent.getExtras().getString("identifiable");

        assert action_String != null;
        if(action_String.equals("yes")){
            start_id = 1;
        }
        else if(action_String.equals("no")){
            start_id = 0;
        }

        //Alarm not running and is to be started
        if(!this.isRunning && start_id == 1){

            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(50,50);
            mediaPlayer.start();

            this.isRunning = true;
            this.start_id = 0;

            //Set up notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent intent_main = new Intent(this.getApplicationContext(), MainActivity.class);

            PendingIntent pending_intent_main = PendingIntent.getActivity(this,0, intent_main, 0);

            Notification notification_popup = new Notification.Builder(this)
                    .setContentTitle("Alarm Ringing")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Click here to shut the alarm")
                    .setContentIntent(pending_intent_main)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(0, notification_popup);
        }
        //Alarm running and has to be stopped
        else if(this.isRunning && start_id == 0){
            Intent intent_puzzle = new Intent(RingtoneService.this, MathPuzzle.class);

            String message = "StartPuzzleActivity";
            intent_puzzle.putExtra(MainActivity.EXTRA_MESSAGE, message);
            startActivity(intent_puzzle);

            this.isRunning = false;
            this.start_id = 0;
        }
        //Nothing to be done cases - bug proofing the app
        else if(!this.isRunning && start_id == 0){

            MainActivity.set_alarm_status("Alarm Off!");
            MainActivity.alarm_Manager.cancel(MainActivity.pending_intent);

            this.isRunning = false;
            this.start_id = 0;
        }
        //Nothing to be done cases - bug proofing the app
        else if(this.isRunning && start_id == 1){
            this.isRunning = true;
            this.start_id = 1;

        }
        else {
            Log.e("Error! Unexpected event"," encountered!" );
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("OnDestroy "," called" );
        this.isRunning = false;
    }

}
