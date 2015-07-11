package com.choubey.autocallreponder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by choubey on 7/4/15.
 */
public class TemplatesDbDao {

    public static List<UserTemplatesData> queryAndGetTemplatesData(Context context) {
        TemplatesDbHelper dbHelper = new TemplatesDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UserTemplatesData.UserTemplates.COLUMN_NAME_TEMPLATE_ID,
                UserTemplatesData.UserTemplates.COLUMN_NAME_CONTACT_NUMBER,
                UserTemplatesData.UserTemplates.COLUMN_NAME_MESSAGE,
                UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE
        };

        String sortOrder =
                UserTemplatesData.UserTemplates.COLUMN_NAME_TEMPLATE_ID;

        Cursor c = db.query(
                UserTemplatesData.UserTemplates.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        int numberOfEntriesInDb = c.getCount();
        int currentIndex = 0;
        List<UserTemplatesData> userTemplatesDataList = new LinkedList<>();

        while(currentIndex < numberOfEntriesInDb)
        {
            UserTemplatesData userTemplatesData = new UserTemplatesData();

            int id = c.getInt(c.getColumnIndexOrThrow(UserTemplatesData.UserTemplates.COLUMN_NAME_TEMPLATE_ID));
            String number = c.getString(c.getColumnIndexOrThrow(UserTemplatesData.UserTemplates.COLUMN_NAME_CONTACT_NUMBER));
            String message = c.getString(c.getColumnIndexOrThrow(UserTemplatesData.UserTemplates.COLUMN_NAME_MESSAGE));
            String active = c.getString(c.getColumnIndexOrThrow(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE));
            Log.i(TemplatesDbDao.class.getSimpleName(), "Id:" + id + ", number:" + number + ", message:" + message + ", active:" + active);
            c.moveToNext();
            currentIndex++;

            userTemplatesData.setTemplateId(String.valueOf(id));
            userTemplatesData.setContactNumber(number);
            userTemplatesData.setMessage(message);
            userTemplatesData.setStatus(UserTemplatesData.ActiveStatus.valueOf(active));

            userTemplatesDataList.add(userTemplatesData);
        }
        return userTemplatesDataList;
    }

    public static void updateRecord(UserTemplatesData userTemplatesData, Context context)
    {
        TemplatesDbHelper dbHelper = new TemplatesDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        UserTemplatesData.ActiveStatus newStatus = userTemplatesData.getStatus();
        ContentValues cv = new ContentValues();
        cv.put(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE, newStatus.name());

        String id = userTemplatesData.getTemplateId();
        db.update(UserTemplatesData.UserTemplates.TABLE_NAME, cv,
                                    UserTemplatesData.UserTemplates.COLUMN_NAME_TEMPLATE_ID + " = " + id, null);
    }

    public static void createTemplate(Context context, UserTemplatesData userTemplatesData)
    {
        TemplatesDbHelper dbHelper = new TemplatesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(db == null)
        {
            Log.e(TemplatesDbDao.class.getSimpleName(), "Error occured while opening writable database.");
            Toast.makeText(context, "Some error occured. Try again after some time", Toast.LENGTH_LONG);
            return;
        }

        String numberEntered = userTemplatesData.getContactNumber();
        String messageEntered = userTemplatesData.getMessage();
        UserTemplatesData.ActiveStatus active = userTemplatesData.getStatus();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserTemplatesData.UserTemplates.COLUMN_NAME_CONTACT_NUMBER, numberEntered);
        contentValues.put(UserTemplatesData.UserTemplates.COLUMN_NAME_MESSAGE, messageEntered);
        contentValues.put(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE, active.name());

        long newRowId = db.insert(UserTemplatesData.UserTemplates.TABLE_NAME, null, contentValues);
        db.close();
        Log.i(TemplatesDbDao.class.getSimpleName(), "Value succesfully saved. New row id = " + newRowId);
    }

    public static void deleteTemplate(Context context, String id)
    {
        TemplatesDbHelper dbHelper = new TemplatesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(db == null)
        {
            Log.e(TemplatesDbDao.class.getSimpleName(), "Error occured while opening writable database.");
            Toast.makeText(context, "Some error occured. Try again after some time", Toast.LENGTH_LONG);
            return;
        }

        long numberOfRowsDeleted = db.delete(UserTemplatesData.UserTemplates.TABLE_NAME,
                                    UserTemplatesData.UserTemplates.COLUMN_NAME_TEMPLATE_ID + " = " + id, null);
        if(numberOfRowsDeleted == 1) {
            Log.i(TemplatesDbDao.class.getSimpleName(), "Template deleted. Id = " + id);
        }
        else {
            throw new RuntimeException("Error deleting data for row id = " + id);
        }
        db.close();
    }
}