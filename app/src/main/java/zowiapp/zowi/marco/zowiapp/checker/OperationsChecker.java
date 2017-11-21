package zowiapp.zowi.marco.zowiapp.checker;

import android.support.constraint.ConstraintLayout;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.utils.AsyncTaskHandler;
import zowiapp.zowi.marco.zowiapp.zowi.Zowi;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiSocket;

public class OperationsChecker extends CheckerTemplate {

    private ArrayList<String> matrixCodeArray;

    public boolean check(GameParameters gameParameters, int index, int correctResult, int operationsType) {
        boolean correctAnswer = false;
        LinearLayout operationsTemplateContainer = (LinearLayout) gameParameters.findViewById(R.id.operations_container);

        if (operationsTemplateContainer != null) {
            ConstraintLayout currentOperation = (ConstraintLayout) operationsTemplateContainer.getChildAt(index);
            ConstraintLayout operationContainer = operationsType == 1 ?
                                                    (ConstraintLayout) currentOperation.getChildAt(0) :
                                                    (ConstraintLayout) currentOperation.getChildAt(2);
            EditText answerEditText = operationsType == 1 ?
                                                    (EditText) operationContainer.getChildAt(operationContainer.getChildCount()-1) :
                                                    (EditText) ((ConstraintLayout)operationContainer.getChildAt(operationContainer.getChildCount()-2)).getChildAt(0);

            String answer = answerEditText.getText().toString();

            if (answer.equals("")) {
                Toast.makeText(gameParameters, "¡Escribe un resultado!", Toast.LENGTH_SHORT).show();
            }
            else {
                int answerNumber = Integer.valueOf(answer);
                if (answerNumber == correctResult) {
                    sendDataToZowi(ZowiActions.TIC_ANSWER_COMMAND);
                    correctAnswer = true;
                }
                else {
                    sendDataToZowi(ZowiActions.X_ANSWER_COMMAND);
                    correctAnswer = false;
                }
            }
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "operationsTemplateContainer");
        }

        return correctAnswer;
    }

    public void sendOperation(String[] operation) {
        if (operation != null) {
            matrixCodeArray = new ArrayList<>();
            for (int i=0; i<operation.length; i++) {
                String[] operator = (i == 1) ? ((operation[i].equals("+")) ? ActivityConstants.OperationsConstants.OPERATORS_TO_LED[0] : ActivityConstants.OperationsConstants.OPERATORS_TO_LED[1]) : ActivityConstants.OperationsConstants.NUMBERS_TO_LED[Integer.parseInt(operation[i])];
                for (String numberColumn: operator) {
                    matrixCodeArray.add(numberColumn);
                }
                /* Add empty column between operators */
                matrixCodeArray.add(ActivityConstants.OperationsConstants.EMPTY_COLUMN);
            }
            /* Add empty columns until the last number disappears */
            for (int i = 0; i< ActivityConstants.OperationsConstants.MAX_NUMBER_BLUETOOTH_COLUMNS; i++) {
                matrixCodeArray.add(ActivityConstants.OperationsConstants.EMPTY_COLUMN);
            }
        }

        StringBuilder matrixCommand = new StringBuilder();
        matrixCommand.append("O ");

        boolean waitForAck = false;
        for (int i=0; i<matrixCodeArray.size() && !waitForAck; i++) {
            matrixCommand.append(matrixCodeArray.get(i)).append(" ");
            /* Send only 5 bits columns to avoid filling the buffer */
            if ((i+1) % 5 == 0) {
                matrixCommand.deleteCharAt(matrixCommand.length()-1);

                new AsyncTaskHandler(this, ActivityType.OPERATIONS).execute(matrixCommand.toString());

                waitForAck = true;
                for (int j=0; j<i+1; j++)
                    matrixCodeArray.remove(0);
//                try {
//                    zowiProcessOperation.join();
//
//                    zowiProcessOperation = ThreadHandler.createThread(ThreadType.SIMPLE_FEEDBACK);
//                    zowiProcessOperation.start();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
        /* If size is > 2 means that here is still content to send to Zowi */
        if (!waitForAck & matrixCommand.length() > 2) {
            new AsyncTaskHandler(this, ActivityType.OPERATIONS).execute(matrixCommand.toString());
            matrixCodeArray.clear();
        }
    }

}
