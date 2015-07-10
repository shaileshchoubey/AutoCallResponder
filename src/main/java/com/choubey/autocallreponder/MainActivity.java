package com.choubey.autocallreponder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.choubey.autocallreponder.db.TemplatesDbDao;
import com.choubey.autocallreponder.db.UserTemplatesData;


public class MainActivity extends ActionBarActivity {
    private static final String N = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, ActionHandlerActivity.class);
        EditText editTextForNumber = (EditText) findViewById(R.id.edit_number);
        EditText editTextForMessage = (EditText) findViewById(R.id.edit_text);

        String numberEntered = editTextForNumber.getText().toString();
        String messageEntered = editTextForMessage.getText().toString();

        intent.putExtra(DisplayKeys.NUMBER_ENTERED_KEY.value, numberEntered);
        intent.putExtra(DisplayKeys.MESSAGE_ENTERED_KEY.value, messageEntered);
        startActivity(intent);
    }

    public void activate(View view) {

    }

    public void save(View view) {
        EditText editTextForNumber = (EditText) findViewById(R.id.edit_number);
        EditText editTextForMessage = (EditText) findViewById(R.id.edit_text);

        String numberEntered = editTextForNumber.getText().toString();
        String messageEntered = editTextForMessage.getText().toString();

        UserTemplatesData userTemplatesData = new UserTemplatesData();
        userTemplatesData.addValueForValue(UserTemplatesData.UserTemplates.COLUMN_NAME_CONTACT_NUMBER, numberEntered);
        userTemplatesData.addValueForValue(UserTemplatesData.UserTemplates.COLUMN_NAME_MESSAGE, messageEntered);
        userTemplatesData.addValueForValue(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE, N);

        TemplatesDbDao.createTemplate(this, userTemplatesData);
        setContentView(R.layout.activity_main);
        Toast.makeText(view.getContext(), "Template saved successfully", Toast.LENGTH_SHORT).show();
    }

    public void manageExistingTemplates(View view) {
        Intent intent = new Intent(this, ManageTemplatesActivity.class);
        startActivity(intent);
    }
}
