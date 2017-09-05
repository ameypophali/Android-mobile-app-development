package com.example.ameyp.MathPuzzleAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ameyp on 4/10/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //fetch extra string from intent
        String intent_string = intent.getExtras().getString("identifiable");

        //create an intent to ringtone service
        Intent service_intent = new Intent(context, RingtoneService.class);

        service_intent.putExtra("identifiable", intent_string);

        //Start the ringtone service
        context.startService(service_intent);

    }
}
