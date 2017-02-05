package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.OperationsConstants;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class OperationsActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private int operationsType;
    private String image;
    private String[] operationsImages;
    private int[] operationsResults;

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
            JSONArray jsonOperationsImages = activityDetails.getJSONArray(OperationsConstants.JSON_PARAMETER_OPERATIONSIMAGES);
            operationsImages = new String[jsonOperationsImages.length()];

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
        ImageView mainImage = (ImageView) operationsActivityTemplate.findViewById(R.id.main_image);
        mainImage.setImageResource(gameParameters.getResources().getIdentifier(image, "drawable", gameParameters.getPackageName()));

        /* Array that will allow the correction of the operations */
        operationsResults = new int[OperationsConstants.NUMBER_OF_OPERATIONS];

        for (int i=0; i<OperationsConstants.NUMBER_OF_OPERATIONS; i++) {
            int firstNumber = new Random().nextInt(OperationsConstants.RANDOM_NUMBER_LIMIT);
            int randomOperatorIndex = new Random().nextInt(OperationsConstants.OPERATORS.length);
            String operator = OperationsConstants.OPERATORS[randomOperatorIndex];

            int secondNumber = 0, operationResult = 0;
            switch (operator) {
                case "+":
                    /* We want the result to be between 0 and 9, so the second randomly generated number must be
                       between 0 and RAMDON_NUMBER_LIMIT-firstNumber-1.
                       Ej. fN = 6, RNL-fN = 10-6 = 4 --> Random number between 0 and 3 */
                    secondNumber = new Random().nextInt(OperationsConstants.RANDOM_NUMBER_LIMIT-firstNumber);

                    operationResult = firstNumber + secondNumber;
                    break;
                case "-":
                    /* Ej. fN = 6, fN+1 = 7 --> Random number between 0 and 6 */
                    secondNumber = new Random().nextInt(firstNumber+1);

                    operationResult = firstNumber - secondNumber;
                    break;
                default:
                    break;
            }
            operationsResults[i] = operationResult;
            String[] operation = {String.valueOf(firstNumber), operator, String.valueOf(secondNumber)};

            LinearLayout operationsTemplate = null;
            switch (operationsType) {
                case 1:
                    operationsTemplate = (LinearLayout) inflater.inflate(R.layout.operations_1_template, operationsActivityTemplate, false);

                    for (int j=0; j<operation.length; j++) {
                        TextView op = (TextView) operationsTemplate.getChildAt(j);
                        op.setText(operation[j]);
                    }
                    break;
                case 2:
                    operationsTemplate = (LinearLayout) inflater.inflate(R.layout.operations_2_template, operationsActivityTemplate, false);

                    for (int j=0; j<operation.length; j++) {
                        // Send to Zowi

                        /* This operation selects automatically elements 2, 5 and 8, that correspond to the ImageViews*/
                        ImageView operationsImage = (ImageView) operationsTemplate.getChildAt(j+(2*(j+1))-1);
                        operationsImage.setImageResource(gameParameters.getResources().getIdentifier(operationsImages[i], "drawable", gameParameters.getPackageName()));
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(OperationsConstants.OPERATION_IMAGE_WIDTH_PX, OperationsConstants.OPERATION_IMAGE_WIDTH_PX);
//                        operationsImage.setLayoutParams(layoutParams);
                    }
                    break;
                default:
                    break;
            }

            if (operationsTemplate != null) {
                LinearLayout operationsTemplateContainer = (LinearLayout) operationsTemplate.findViewById(R.id.operations_template_container);
                Button checkButton = (Button) operationsTemplateContainer.getChildAt(operationsTemplateContainer.getChildCount()-1);
                checkButton.setTag(i);
                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int)view.getTag();
                        checkAnswer(index, operationsResults[index]);
                    }
                });

                operationsContainer.addView(operationsTemplate);
            }
        }

        if (contentContainer != null) {
            contentContainer.addView(operationsActivityTemplate);
        }
    }

    public void checkAnswer(int index, int correctResult) {
        LinearLayout operationsTemplateContainer = (LinearLayout) gameParameters.findViewById(R.id.operations_container);
        if (operationsTemplateContainer != null) {
            LinearLayout currentOperation = (LinearLayout) operationsTemplateContainer.getChildAt(index);
            EditText answerEditText = (EditText) currentOperation.getChildAt(currentOperation.getChildCount()-2);

            String answer = answerEditText.getText().toString();

            if (answer.equals("")) {
                Toast.makeText(gameParameters, "¡Escribe un resultado!", Toast.LENGTH_SHORT).show();
            }
            else {
                int answerNumber = Integer.valueOf(answer);
                if (answerNumber == correctResult) {
                    // Send OK To Zowi
                    Toast.makeText(gameParameters, "¡Bien!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
