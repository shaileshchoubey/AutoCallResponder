package com.choubey.autocallreponder;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class ActionHandlerActivity extends ActionBarActivity {
    private BroadcastReceiver callBlocker = new CustomBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        endCall();
    }

    private void endCall()
    {
        IntentFilter filter= new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(callBlocker, filter);

        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText("End call enabled for the given number.");
        setContentView(textView);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (callBlocker != null)
        {
            unregisterReceiver(callBlocker);
            callBlocker = null;
        }
    }

    private void sendMessage(Intent intent)
    {
        String numberEntered = intent.getStringExtra(DisplayKeys.NUMBER_ENTERED_KEY.value);
        String messageEnetered = intent.getStringExtra(DisplayKeys.MESSAGE_ENTERED_KEY.value);

        TextView textView = new TextView(this);
        textView.setTextSize(20);

        try {
            //SmsManager smsManager = SmsManager.getDefault();
            //smsManager.sendTextMessage(numberEntered, null, messageEnetered, null, null);
            textView.setText("Number entered is :" + numberEntered + "\nMessage entered:" + messageEnetered);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            textView.setText("There was a problem sending the message. Please check logs.");
        }
        setContentView(textView);
    }
}
