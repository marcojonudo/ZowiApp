package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.OperationsConstants;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class OperationsActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private int operationsType, operat;
    private String image;
    private String[][] operations;
    private JSONObject activityDetails;
    private int[][] coordinates;

    public OperationsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            operationsType = activityDetails.getInt(OperationsConstants.JSON_PARAMETER_OPERATIONSTYPE);
            image = activityDetails.getString(OperationsConstants.JSON_PARAMETER_IMAGE);
            JSONArray jsonOperations = activityDetails.getJSONArray(OperationsConstants.JSON_PARAMETER_OPERATIONS);
            operations = new String[jsonOperations.length()][3];

            for (int i=0; i<jsonOperations.length(); i++) {
                JSONArray operation = jsonOperations.getJSONArray(i);
                for (int j=0; j<3; j++) {
                    operations[i][j] = operation.getString(j);
                }
            }

            generateLayout();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void generateLayout() {
        setTitleDescription(gameParameters, activityTitle, activityDescription);

        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        LinearLayout operationsActivityTemplate = (LinearLayout) inflater.inflate(R.layout.operations_activity_template, contentContainer, false);
        LinearLayout operationsContainer = (LinearLayout) operationsActivityTemplate.findViewById(R.id.operations_container);

        ImageView mainImage = (ImageView) operationsActivityTemplate.findViewById(R.id.mainImage);
        mainImage.setImageResource(gameParameters.getResources().getIdentifier(image, "drawable", gameParameters.getPackageName()));

        switch (operationsType) {
            /* Numbers and operations are predefined */
            case 1:
                for (String[] operation: operations) {
                    LinearLayout operations1Template = (LinearLayout) inflater.inflate(R.layout.operations_1_template, operationsActivityTemplate, false);

                    for (int i=0; i<operation.length; i++) {
                        TextView op = (TextView) operations1Template.getChildAt(i);
                        op.setText(operation[i]);
                    }

                    operationsContainer.addView(operations1Template);
                }
                break;
            /* Zowi gives the numbers and operation */
            case 2:
                /* In this case, the numbers and operation must be sent to Zowi,
                   instead of been displayd inmediatly on screen */
                break;
            default:
                break;
        }

        if (contentContainer != null) {
            contentContainer.addView(operationsActivityTemplate);
        }
    }

}
