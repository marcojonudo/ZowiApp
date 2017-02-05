package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

/**
 * Created by Marco on 03/02/2017.
 */
public class OperationsChecker {

    public void check(GameParameters gameParameters, int index, int correctResult) {
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
