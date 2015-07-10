package com.choubey.autocallreponder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by choubey on 6/20/15.
 */
public class CustomBroadcastReceiver extends BroadcastReceiver {
    private TelephonyManager telephonyManager;
    private ITelephony telephonyService;
    private PhoneStateListener callBlockListener;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        Class c = null;
        try {
            c = Class.forName(telephonyManager.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Method m = null;
        try {
            m = c.getDeclaredMethod("getITelephony");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        m.setAccessible(true);

        try {
            Bundle extras = intent.getExtras();
            String incomingNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            telephonyService = (ITelephony)m.invoke(telephonyManager);
            telephonyService.endCall();
            //telephonyManager.getNetworkOperatorName();
            Toast.makeText(context, "Call silenced from incoming number = " + incomingNumber, Toast.LENGTH_LONG).show();
            //callBlockListener = new CustomPhoneStateListener(telephonyService);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //telephonyManager.listen(callBlockListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
