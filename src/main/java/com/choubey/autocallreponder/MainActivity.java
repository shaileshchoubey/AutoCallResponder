package com.choubey.autocallreponder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.choubey.autocallreponder.db.InmemoryCacheFreshness;
import com.choubey.autocallreponder.db.TemplatesDbDao;
import com.choubey.autocallreponder.db.UserTemplatesData;
import com.choubey.autocallreponder.fetchcontacts.FetchContactsMainActivity;

public class MainActivity extends ActionBarActivity {
    private static final int GET_CONTACT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private UserTemplatesData constructDataFromInput(UserTemplatesData.ActiveStatus active)
    {
        EditText editTextForNumber = (EditText) findViewById(R.id.edit_number);
        EditText editTextForMessage = (EditText) findViewById(R.id.edit_text);

        String numberEntered = editTextForNumber.getText().toString();
        String messageEntered = editTextForMessage.getText().toString();

        UserTemplatesData userTemplatesData = new UserTemplatesData();
        userTemplatesData.setContactNumber(numberEntered);
        userTemplatesData.setMessage(messageEntered);
        userTemplatesData.setStatus(active);
        return userTemplatesData;
    }

    public void activate(View view) {
        UserTemplatesData userTemplatesData = constructDataFromInput(UserTemplatesData.ActiveStatus.Y);
        TemplatesDbDao.createTemplate(this, userTemplatesData);
        setContentView(R.layout.activity_main);
        InmemoryCacheFreshness.setCacheFresh(false);
        Toast.makeText(view.getContext(), "Template activated", Toast.LENGTH_SHORT).show();
    }

    public void save(View view) {
        UserTemplatesData userTemplatesData = constructDataFromInput(UserTemplatesData.ActiveStatus.N);
        TemplatesDbDao.createTemplate(this, userTemplatesData);
        setContentView(R.layout.activity_main);
        Toast.makeText(view.getContext(), "Template saved successfully", Toast.LENGTH_SHORT).show();
    }

    public void manageExistingTemplates(View view) {
        Intent intent = new Intent(this, ManageTemplatesActivity.class);
        startActivity(intent);
    }

    public void showContacts(View view) {
        //Toast.makeText(view.getContext(), "Fetching contacts", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FetchContactsMainActivity.class);
        startActivityForResult(intent, GET_CONTACT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == GET_CONTACT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                EditText editTextForNumber = (EditText) findViewById(R.id.edit_number);
                String number = data.getStringExtra("number");
                editTextForNumber.setText(number);
            } else {
                Toast.makeText(getBaseContext(), "Error while fetching contact", Toast.LENGTH_SHORT);
            }
        }
        else
        {
            Toast.makeText(getBaseContext(), "Unknown request code = " + requestCode, Toast.LENGTH_SHORT);
        }
    }

    private String getFormattedNumber(String selectedNumber)
    {
        if(selectedNumber.startsWith(Constants.COUNTRY_CODE_STARTING_CHARACTER))
        {
            return selectedNumber;
        }
        else if(selectedNumber.startsWith(Constants.STD_CODE_STARTING_CHARACTER))
        {
            return Constants.DEFAULT_COUNTRY_CODE + selectedNumber.substring(1, Constants.PHONE_NUMBER_LENGTH_IN_INDIA);
        }
        else
        {
            return Constants.DEFAULT_COUNTRY_CODE + selectedNumber;
        }
    }
}
