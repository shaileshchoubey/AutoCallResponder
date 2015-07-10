package com.choubey.autocallreponder;

import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

/**
 * Created by choubey on 6/20/15.
 */
public class CustomPhoneStateListener extends PhoneStateListener {
    private ITelephony telephonyService;

    public CustomPhoneStateListener(ITelephony telephonyService)
    {
        this.telephonyService = telephonyService;
        Log.i(this.getClass().getCanonicalName(), "Registering this ");
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber)
    {
        Log.i(this.getClass().getCanonicalName(), "Incoming number=" + incomingNumber);
        if(incomingNumber.equals("+919866785913") && state == TelephonyManager.CALL_STATE_RINGING)
        {
            try
            {
                telephonyService.endCall();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }
}
