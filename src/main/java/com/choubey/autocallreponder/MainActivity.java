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


public class MainActivity extends ActionBarActivity {
    private static final String N = "N";
    private static final String Y = "Y";

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
}
