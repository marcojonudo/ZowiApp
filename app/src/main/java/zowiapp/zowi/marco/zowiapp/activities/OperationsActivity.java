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
    private int operationsType;
    private String image;
    private String[][] operations;
    private String[] operationsImages;
    private JSONObject activityDetails;

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
            JSONArray jsonOperationsImages = activityDetails.getJSONArray(OperationsConstants.JSON_PARAMETER_OPERATIONSIMAGES);
            operationsImages = new String[jsonOperationsImages.length()];

            for (int i=0; i<jsonOperations.length(); i++) {
                JSONArray operation = jsonOperations.getJSONArray(i);
                for (int j=0; j<3; j++) {
                    operations[i][j] = operation.getString(j);
                }
            }

            if (jsonOperationsImages.length() != 0) {
                for (int i=0; i<jsonOperationsImages.length(); i++) {
                    operationsImages[i] = jsonOperationsImages.getString(i);
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

        /* Set the resource of the left image */
        ImageView mainImage = (ImageView) operationsActivityTemplate.findViewById(R.id.mainImage);
        mainImage.setImageResource(gameParameters.getResources().getIdentifier(image, "drawable", gameParameters.getPackageName()));

        switch (operationsType) {
            /* Numbers and operations are predefined */
            case 1:
                /* No need to take care about operations images */
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
                   instead of been displayed immediately on screen */
                for (int i=0; i<operations.length; i++) {
                    String[] operation = operations[i];
                    LinearLayout operations2Template = (LinearLayout) inflater.inflate(R.layout.operations_2_template, operationsActivityTemplate, false);

                    for (int j=0; j<operation.length; j++) {
                        // Send to Zowi

                        /* This operation selects automatically elements 2, 5 and 8, that correspond to
                           to the ImageViews*/
                        ImageView operationsImage = (ImageView) operations2Template.getChildAt(j+(2*(j+1))-1);
                        operationsImage.setImageResource(gameParameters.getResources().getIdentifier(operationsImages[i], "drawable", gameParameters.getPackageName()));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(OperationsConstants.OPERATION_IMAGE_WIDTH_PX, OperationsConstants.OPERATION_IMAGE_WIDTH_PX);
                        operationsImage.setLayoutParams(layoutParams);
                    }

                    operationsContainer.addView(operations2Template);
                }
                break;
            default:
                break;
        }

        if (contentContainer != null) {
            contentContainer.addView(operationsActivityTemplate);
        }
    }

}
