package com.choubey.autocallreponder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by choubey on 7/11/15.
 */
public class ActionHandler {
    public static Activity parentActivity = null;
    private BroadcastReceiver callBlocker = new CustomBroadcastReceiver();
    private static ActionHandler actionHandler = null;
    private IntentFilter filter = null;

    private ActionHandler(Activity parentActivity)
    {
        this.parentActivity = parentActivity;
    }

    public static ActionHandler createNewInstance(Activity parentActivity)
    {
        if(actionHandler == null) {
            synchronized (ActionHandler.class) {
                if(actionHandler == null) {
                    actionHandler = new ActionHandler(parentActivity);
                }
            }
        }
        return actionHandler;
    }

    public static ActionHandler getSingletonInstance()
    {
        if(actionHandler == null)
        {
            throw new IllegalStateException("ActionHandler instance is not created yet! Please create an instance first.");
        }
        return actionHandler;
    }

    public void registerBroadcastReceiver()
    {
        if(parentActivity != null) {
            filter = new IntentFilter("android.intent.action.PHONE_STATE");
            parentActivity.registerReceiver(callBlocker, filter);
        }
        else
        {
            throw new IllegalStateException("Parent activity is not set");
        }
    }

    public void unregisterBroadcastReceiver()
    {
        if (callBlocker != null && parentActivity != null)
        {
            parentActivity.unregisterReceiver(callBlocker);
            callBlocker = null;
        }
    }
}
