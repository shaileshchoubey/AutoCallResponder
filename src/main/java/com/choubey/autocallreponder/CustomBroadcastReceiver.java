package com.choubey.autocallreponder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.choubey.autocallreponder.messagesender.MessageDetails;
import com.choubey.autocallreponder.messagesender.MessageSender;
import com.choubey.autocallreponder.messagesender.SmsMessageSender;

import java.lang.reflect.Method;

/**
 * Created by choubey on 6/20/15.
 */
public class CustomBroadcastReceiver extends BroadcastReceiver {
    private TelephonyManager telephonyManager;
    private ITelephony telephonyService;
    private MessageSender messageSender = new SmsMessageSender();
    private static final long DURATION_OF_RING_REQUIRED = 2000;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        Class c = null;
        try {
            c = Class.forName(telephonyManager.getClass().getName());
        } catch (ClassNotFoundException e) {
            Log.e(this.getClass().getSimpleName(), "Error loading class TelephonyManager", e);
        }

        Method m = null;
        try {
            m = c.getDeclaredMethod("getITelephony");
        } catch (SecurityException| NoSuchMethodException  e) {
            Log.e(this.getClass().getSimpleName(), "Error loading method getITelephony for class TelephonyManager", e);
        }

        m.setAccessible(true);
        try {
            Bundle extras = intent.getExtras();
            String incomingNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            telephonyService = (ITelephony)m.invoke(telephonyManager);
            telephonyService.silenceRinger();
            Thread.sleep(DURATION_OF_RING_REQUIRED);
            telephonyService.endCall();
            Log.i(this.getClass().getSimpleName(), "Incoming call from number = " + incomingNumber + " ended. Sending a message now...");

            MessageDetails messageDetails = new MessageDetails();
            messageDetails.setMessage("");
            messageDetails.setPhoneNumber("");
            //messageSender.sendMessage(messageDetails);
            Log.i(this.getClass().getSimpleName(), "Message successfully sent with details = " + String.valueOf(messageDetails));
            Toast.makeText(context, "Incoming call from number = " + incomingNumber + " ended and message sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error invoking getITelephony method", e);
        }
    }
}
