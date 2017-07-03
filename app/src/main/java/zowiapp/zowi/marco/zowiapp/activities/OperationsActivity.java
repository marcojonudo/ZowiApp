package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.OperationsConstants;
import zowiapp.zowi.marco.zowiapp.checker.OperationsChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;

/**
 * Created by Marco on 24/01/2017.
 */
public class OperationsActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private OperationsChecker operationsChecker;
    private int operationsType;
    private String image;
    private String[] operationsImages;
    private int[] operationsResults;
    private String[][] operations;

    public OperationsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        operationsChecker = new OperationsChecker();

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            operationsType = activityDetails.getInt(OperationsConstants.JSON_PARAMETER_OPERATIONSTYPE);
            image = activityDetails.getString(OperationsConstants.JSON_PARAMETER_IMAGE);
            JSONArray jsonOperationsImages = activityDetails.getJSONArray(OperationsConstants.JSON_PARAMETER_OPERATIONSIMAGES);
            operationsImages = new String[jsonOperationsImages.length()];
            operations = new String[OperationsConstants.NUMBER_OF_OPERATIONS][OperationsConstants.NUMBER_OF_OPERATORS];

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
        ConstraintLayout operationsActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.operations_activity_template, contentContainer, false);
        LinearLayout operationsContainer = (LinearLayout) operationsActivityTemplate.findViewById(R.id.operations_container);

        /* Set the resource of the left image */
        ImageView mainImage = (ImageView) operationsActivityTemplate.findViewById(R.id.main_image);
        mainImage.setImageResource(gameParameters.getResources().getIdentifier(image, "drawable", gameParameters.getPackageName()));

        /* Array that will allow the correction of the operations */
        operationsResults = new int[OperationsConstants.NUMBER_OF_OPERATIONS];

        for (int i=0; i<OperationsConstants.NUMBER_OF_OPERATIONS; i++) {
            Random random = new Random();
            int firstNumber = random.nextInt(OperationsConstants.RANDOM_NUMBER_LIMIT);
            int randomOperatorIndex = random.nextInt(OperationsConstants.OPERATORS.length);
            String operator = OperationsConstants.OPERATORS[randomOperatorIndex];

            int secondNumber = 0, operationResult = 0;
            switch (operator) {
                case "+":
                    /* We want the result to be between 0 and 9, so the second randomly generated number must be
                       between 0 and RAMDON_NUMBER_LIMIT-firstNumber-1.
                       Ej. fN = 6, RNL-fN = 10-6 = 4 --> Random number between 0 and 3 */
                    secondNumber = random.nextInt(OperationsConstants.RANDOM_NUMBER_LIMIT-firstNumber);

                    operationResult = firstNumber + secondNumber;
                    break;
                case "-":
                    /* Ej. fN = 6, fN+1 = 7 --> Random number between 0 and 6 */
                    secondNumber = random.nextInt(firstNumber+1);

                    operationResult = firstNumber - secondNumber;
                    break;
                default:
                    break;
            }
            operationsResults[i] = operationResult;
            String[] operation = {String.valueOf(firstNumber), operator, String.valueOf(secondNumber)};
            operations[i] = operation;

            ConstraintLayout operationsTemplate = null;
            ConstraintLayout operationContainer;
            switch (operationsType) {
                case 1:
                    operationsTemplate = (ConstraintLayout) inflater.inflate(R.layout.operation_1_template, operationsContainer, false);
                    operationContainer = (ConstraintLayout) operationsTemplate.findViewById(R.id.operation_container);

                    for (int j=0; j<operation.length; j++) {
                        TextView op = (TextView) operationContainer.getChildAt(j);
                        op.setText(operation[j]);
                    }
                    break;
                case 2:
                    /* Remove the left image as it is not necessary in this mode */
                    mainImage.setVisibility(View.GONE);
                    Guideline guideline = (Guideline) operationsActivityTemplate.findViewById(R.id.operations_template_guideline);
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
                    layoutParams.guidePercent = 0;
                    guideline.setLayoutParams(layoutParams);

                    operationsTemplate = (ConstraintLayout) inflater.inflate(R.layout.operation_2_template, operationsContainer, false);
                    operationContainer = (ConstraintLayout) operationsTemplate.findViewById(R.id.operation_container);
                    for (int j=0; j<operation.length; j++) {
                        /* This operation selects automatically elements 2, 5 and 8, that correspond to the ImageViews */
                        ImageView operationsImage = (ImageView) operationContainer.getChildAt(j+(2*(j+1))-1);
                        operationsImage.setImageResource(gameParameters.getResources().getIdentifier(operationsImages[i], "drawable", gameParameters.getPackageName()));
                    }
                    break;
                default:
                    break;
            }

            if (operationsTemplate != null) {
                ConstraintLayout checkButton = (ConstraintLayout) operationsTemplate.findViewById(R.id.operations_button);
                ConstraintLayout displayButton = (ConstraintLayout) operationsTemplate.findViewById(R.id.display_operation_button);
                checkButton.setTag(i);
                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int)view.getTag();
                        operationsChecker.check(gameParameters, index, operationsResults[index]);
                    }
                });
                if (displayButton != null) {
                    displayButton.setTag(i);
                    displayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = (int)view.getTag();
                            ZowiActions.sendOperation(operations[index]);
                        }
                    });
                }

                operationsContainer.addView(operationsTemplate);
            }
            else {
                new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "operationsTemplate");
            }
        }

        if (contentContainer != null) {
            contentContainer.addView(operationsActivityTemplate);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

}
