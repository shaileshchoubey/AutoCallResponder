package com.choubey.autocallreponder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.choubey.autocallreponder.action.ActionManager;
import com.choubey.autocallreponder.action.CallActionManager;
import com.choubey.autocallreponder.action.MessageActionManager;
import com.choubey.autocallreponder.db.InmemoryCacheFreshness;
import com.choubey.autocallreponder.db.TemplatesDbDao;
import com.choubey.autocallreponder.db.UserTemplatesData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by choubey on 6/20/15.
 */
public class CustomBroadcastReceiver extends BroadcastReceiver {
    public CustomBroadcastReceiver() {}
    private List<UserTemplatesData> userTemplatesDataList = null;
    private Map<String, UserTemplatesData> numberToActiveTemplateMap = new HashMap<>();
    private ActionManager callActionManager = new CallActionManager();
    private ActionManager messageActionManager = new MessageActionManager();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(userTemplatesDataList == null || !InmemoryCacheFreshness.isCacheFresh())
        {
            Log.i(this.getClass().getSimpleName(), "Refreshing inmemory cache");
            InmemoryCacheFreshness.setCacheFresh(true);
            userTemplatesDataList = TemplatesDbDao.queryAndGetTemplatesDataForActiveUsers(context);
            numberToActiveTemplateMap = constructMap(userTemplatesDataList);
            Log.i(this.getClass().getSimpleName(), "Inmemory cache refreshed");
        }
        Log.i(this.getClass().getSimpleName(), "Hashcode is:" + String.valueOf(intent.hashCode()));
        Bundle extras = intent.getExtras();
        if(extras == null)
        {
            Log.w(this.getClass().getSimpleName(), "Null extras found in intent. Skipping this broadcast.");
            return;
        }

        String state = extras.getString(TelephonyManager.EXTRA_STATE);
        Log.i(this.getClass().getSimpleName(), "The state is " + state);
        if(!TelephonyManager.EXTRA_STATE_RINGING.equals(state))
        {
            Log.i(this.getClass().getSimpleName(), "The state is not RINGING. Skipping this broadcast.");
            return;
        }

        String incomingNumberWithCountryCode = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        if(incomingNumberWithCountryCode == null)
        {
            Log.w(this.getClass().getSimpleName(), "No incoming number found in intent. Skipping this broadcast.");
            return;
        }
        Log.i(this.getClass().getSimpleName(), "Incoming number with country code = " + incomingNumberWithCountryCode);
        String incomingNumber = incomingNumberWithCountryCode.substring(3); // Assuming first 3 chars are country codes.
        Log.i(this.getClass().getSimpleName(), "Incoming number = " + incomingNumber);
        if(numberToActiveTemplateMap.get(incomingNumber) != null)
        {
            Log.i(this.getClass().getSimpleName(), "Template found for incoming number = " + incomingNumber + ", running defined actions");
            UserContext userContext = new UserContext();
            UserTemplatesData userTemplatesData = numberToActiveTemplateMap.get(incomingNumber);
            userContext.setMessage(userTemplatesData.getMessage());
            userContext.setNumber(incomingNumber);

            callActionManager.takeAction(userContext, context);
            messageActionManager.takeAction(userContext, context);
            Toast.makeText(context, "Incoming call from number = " + incomingNumber + " ended and message sent", Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.i(this.getClass().getSimpleName(), "No active template found for this number: " + incomingNumber);
        }
    }

    private Map<String, UserTemplatesData> constructMap(List<UserTemplatesData> userTemplatesDataList)
    {
        Map<String, UserTemplatesData> numberToActiveTemplateMap = new HashMap<>();
        for(UserTemplatesData userTemplatesData: userTemplatesDataList)
        {
            if(numberToActiveTemplateMap.get(userTemplatesData.getContactNumber()) != null)
            {
                throw new IllegalArgumentException("More than 1 active template found for number = " + userTemplatesData.getContactNumber());
            }
            numberToActiveTemplateMap.put(userTemplatesData.getContactNumber(), userTemplatesData);
        }
        return numberToActiveTemplateMap;
    }
}