package zowiapp.zowi.marco.zowiapp.checker;

import android.support.constraint.ConstraintLayout;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;

public class OperationsChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, int index, int correctResult) {
        boolean correctAnswer = false;
        LinearLayout operationsTemplateContainer = (LinearLayout) gameParameters.findViewById(R.id.operations_container);

        if (operationsTemplateContainer != null) {
            ConstraintLayout currentOperation = (ConstraintLayout) operationsTemplateContainer.getChildAt(index);
            ConstraintLayout operationContainer = (ConstraintLayout) currentOperation.getChildAt(0);
            EditText answerEditText = (EditText) operationContainer.getChildAt(operationContainer.getChildCount()-1);

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

}
