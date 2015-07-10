package com.choubey.autocallreponder;

/**
 * Created by choubey on 6/14/15.
 */
public enum DisplayKeys {
    NUMBER_ENTERED_KEY("autoCallResponder.Number"),
    MESSAGE_ENTERED_KEY("autoCallResponder.Message");

    public final String value;

    private DisplayKeys(String value)
    {
        this.value = value;
    }
}
