package com.example.ameyp.MathPuzzleAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import java.util.Calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import	android.widget.TimePicker;

import com.example.ameyp.MathPuzzleAlarm.R;

public class MainActivity extends AppCompatActivity {

    //the alarm manager instance
    static AlarmManager alarm_Manager;
    TimePicker time_Picker;
    static TextView alarm_Status;
    Context context;
    static PendingIntent pending_intent;
    static public int totalQuestions;

    public static final String EXTRA_MESSAGE = "com.example.alarm1.MESSAGE";
    public static final String QUESTIONS_IN_QUIZ = "pref_numberOfQuestions";

    private boolean preferencesChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(
                        preferencesChangeListener);

        this.context = this;

        //Initialization of the alarm manager instance
        alarm_Manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Initialization of time picker instance
        time_Picker = (TimePicker) findViewById(R.id.timePicker);

        //Initialize Alarm Status Text view
        alarm_Status = (TextView) findViewById(R.id.alarm_status);

        //Constant calendar instance to get the time instance
        final Calendar calendar = Calendar.getInstance();

        //Get the buttons
        Button set_alarm = (Button) findViewById(R.id.set_alarm);
        Button cancel_alarm = (Button) findViewById(R.id.cancel_alarm);

        //Create intent to the alarm receiver class
        final Intent intent_receiver = new Intent(this.context, AlarmReceiver.class);

        //Listener on set alarm button
        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String am_pm;
                Calendar CaL = Calendar.getInstance();

                //Set the hour and minute
                calendar.set(Calendar.HOUR_OF_DAY, time_Picker.getHour());
                calendar.set(Calendar.MINUTE, time_Picker.getMinute());

                //Get the hour and minute
                int hour = time_Picker.getHour();
                int minute = time_Picker.getMinute();

                //Convert to String
                String hour_String = String.valueOf(hour);
                String minute_sting = String.valueOf(minute);

                //set AM or PM for Alarm status string
              if(hour < 12) {
                    am_pm = "AM";
                } else {
                    am_pm = "PM";
                }

                //Change 24 Hour time into 12 hour
                if(hour >12) {
                    hour_String = String.valueOf(hour - 12);
                }

                //Put '0' in minutes if single digit
                if(minute < 10){
                    minute_sting = "0" + String.valueOf(minute);
                }

                //Change alarm status
                set_alarm_status("Alarm set to " + hour_String + ":" + minute_sting + " " + am_pm );

                //Put a string into Intent such that
                // it can be identified that the intent comes from
                // alarm on button. Key is "identifiable"
                intent_receiver.putExtra("identifiable", "yes");

                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0,
                        intent_receiver, PendingIntent.FLAG_UPDATE_CURRENT);

                boolean past = calendar.before(CaL);

                //if alarm set in the past instance of the day
                // schedule it to the next day
                if(past){
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                alarm_Manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);

            }
        });

        //Listener on Cancel Alarm button
        cancel_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent_receiver.putExtra("identifiable", "no");
                sendBroadcast(intent_receiver);
            }
        });

    }

    public static void updateQuestions(SharedPreferences sharedPreferences) {

        String totalNumberQuestions =
                sharedPreferences.getString(MainActivity.QUESTIONS_IN_QUIZ, null);

        totalQuestions = Integer.parseInt(totalNumberQuestions);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {

            this.updateQuestions(
                    PreferenceManager.getDefaultSharedPreferences(this));

            preferencesChanged = false;
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                // called when the user changes the app's preferences
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {

                    preferencesChanged = true;

                    if (key.equals(QUESTIONS_IN_QUIZ)) {
                        MainActivity.updateQuestions(sharedPreferences);
                    }

                }
            };

    public static void set_alarm_status(String alarmStatus) {
        alarm_Status.setText(alarmStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent_setting = new Intent(this.context, SettingsActivity.class);
            startActivity(intent_setting);

         //   return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
