package com.choubey.autocallreponder.db;

import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by choubey on 6/28/15.
 */
public final class UserTemplatesData {
    private Map<String, String> templateDataMap = new HashMap<>();
    public UserTemplatesData() {}

    public void addValueForValue(String columnName, String columnValue)
    {
        templateDataMap.put(columnName, columnValue);
    }

    public Map<String, String> getTemplateData()
    {
        return templateDataMap;
    }

    public String getValueForColumn(String columnName)
    {
        return templateDataMap.get(columnName);
    }

    public static class UserTemplates implements BaseColumns
    {
        public static final String TABLE_NAME = "USER_TEMPLATES";
        public static final String COLUMN_NAME_TEMPLATE_ID = "TEMPLATE_ID";
        public static final String COLUMN_NAME_CONTACT_NUMBER = "CONTACT_NUMBER";
        public static final String COLUMN_NAME_MESSAGE = "MESSAGE";
        public static final String COLUMN_NAME_ACTIVE = "ACTIVE";
    }
}
