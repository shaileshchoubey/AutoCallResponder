package com.choubey.autocallreponder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.choubey.autocallreponder.db.TemplatesDbDao;
import com.choubey.autocallreponder.db.UserTemplatesData;


public class TemplateDetailsDisplayManager {
    private static final int POPUP_WINDOW_WIDTH_IN_DP = 250;
    private static final int POPUP_WINDOW_HEIGHT_IN_DP = 200;
    private static final String Y = "Y";
    private static final String N = "N";
    private static final String ACTIVATE = "ACTIVATE";
    private static final String DEACTIVATE  = "DEACTIVATE";
    private static final String YES = "Yes";
    private static final String NO = "No";

    public void showDetailsAsPopup(View parentView, final Activity context, final UserTemplatesData userTemplatesData)
    {
        ScrollView outerContainer = new ScrollView(context);

        LinearLayout detailsLinearLayout = new LinearLayout(context);
        detailsLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView contactNumberTextView = createCustomTextView(context, "# - " + userTemplatesData.getValueForColumn(UserTemplatesData.UserTemplates.COLUMN_NAME_CONTACT_NUMBER));
        contactNumberTextView.setPadding(Utils.convertDpToPixel(8, context).intValue(), Utils.convertDpToPixel(8, context).intValue(), 0, 0);

        TextView messageTextView = createCustomTextView(context, userTemplatesData.getValueForColumn(UserTemplatesData.UserTemplates.COLUMN_NAME_MESSAGE));
        messageTextView.setGravity(Gravity.CENTER);
        messageTextView.setPadding(Utils.convertDpToPixel(8, context).intValue(), Utils.convertDpToPixel(20, context).intValue(),
                                            Utils.convertDpToPixel(8, context).intValue(), Utils.convertDpToPixel(20, context).intValue());

        TextView activeTextView = createCustomTextView(context, "Active?? - " + Utils.convertBoolCharToString(userTemplatesData.getValueForColumn(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE)));
        activeTextView.setPadding(Utils.convertDpToPixel(8, context).intValue(), 0, 0, 0);

        Button closePopupButton = createCustomButton(context, "CLOSE");
        Button switchActiveButton = createCustomButton(context,
                getSwitchActivateButtonText(userTemplatesData.getValueForColumn(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE)));
        Button deleteTemplateButton = createCustomButton(context, "DELETE TEMPLATE");

        LinearLayout buttonsLayoutFirstLine = new LinearLayout(context);
        buttonsLayoutFirstLine.setGravity(Gravity.CENTER);
        buttonsLayoutFirstLine.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayoutFirstLine.setPadding(0, Utils.convertDpToPixel(15, context).intValue(), 0, 0);

        LinearLayout buttonsLayoutSecondLine = new LinearLayout(context);
        buttonsLayoutSecondLine.setGravity(Gravity.CENTER);
        buttonsLayoutSecondLine.setOrientation(LinearLayout.HORIZONTAL);

        buttonsLayoutFirstLine.addView(switchActiveButton);
        buttonsLayoutFirstLine.addView(deleteTemplateButton);
        buttonsLayoutSecondLine.addView(closePopupButton);

        detailsLinearLayout.addView(contactNumberTextView);
        detailsLinearLayout.addView(messageTextView);
        detailsLinearLayout.addView(activeTextView);
        detailsLinearLayout.addView(buttonsLayoutFirstLine);
        detailsLinearLayout.addView(buttonsLayoutSecondLine);

        outerContainer.addView(detailsLinearLayout);

        final PopupWindow detailsPopupWindow = new PopupWindow(context);
        detailsPopupWindow.setContentView(outerContainer);
        detailsPopupWindow.setFocusable(true);
        detailsPopupWindow.setWidth(Utils.convertDpToPixel(POPUP_WINDOW_WIDTH_IN_DP, context).intValue());
        detailsPopupWindow.setHeight(Utils.convertDpToPixel(POPUP_WINDOW_HEIGHT_IN_DP, context).intValue());
        detailsPopupWindow.showAtLocation(parentView, Gravity.CENTER, 10, 10);

        closePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsPopupWindow.dismiss();
            }
        });

        switchActiveButton.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                flipActivate(userTemplatesData, context);
                detailsPopupWindow.dismiss();
                refreshPage(context);
            }
        }));
    }

    private TextView createCustomTextView(Context context, String text)
    {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(Utils.convertDpToPixel(8, context));
        return textView;
    }

    private Button createCustomButton(Context context, String text)
    {
        Button button = new Button(context);
        button.setText(text);
        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setTextSize(Utils.convertDpToPixel(6, context));
        return button;
    }

    private String getSwitchActivateButtonText(String active)
    {
        if(Y.equals(active))
        {
            return DEACTIVATE;
        }
        else
        {
            return ACTIVATE;
        }
    }

    /**
     * If active = 'Y', then deactivate the template, else activate.
     */
    private void flipActivate(UserTemplatesData userTemplatesData, Context context)
    {
        String currentStatus = userTemplatesData.getValueForColumn(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE);
        String newStatus = null;
        String message = null;
        if(Y.equals(currentStatus))
        {
            newStatus = N;
            message = "Template successfully deactivated.";
        }
        else
        {
            newStatus = Y;
            message = "Template successfully activated.";
        }
        userTemplatesData.addValueForValue(UserTemplatesData.UserTemplates.COLUMN_NAME_ACTIVE, newStatus);

        try {
            TemplatesDbDao.updateRecord(userTemplatesData, context);
        }
        catch(Exception e)
        {
            Log.e(this.getClass().getSimpleName(), "Error while updating record in DB", e);
            message = "Error updating template. Please try again later.";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void refreshPage(Activity context)
    {
        Intent intent = context.getIntent();
        context.finish();
        context.startActivity(intent);
    }
}
