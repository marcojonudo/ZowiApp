package zowiapp.zowi.marco.zowiapp.checker;

import android.support.constraint.ConstraintLayout;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

public class ColouredGridChecker extends CheckerTemplate{

    public boolean check(GameParameters gameParameters, int index, int correctResult) {
        LinearLayout answersContainer = (LinearLayout) gameParameters.findViewById(R.id.answers_container);

        if (answersContainer != null) {
            ConstraintLayout colorContainer = (ConstraintLayout) answersContainer.getChildAt(index);
            EditText colorEditText = (EditText) colorContainer.getChildAt(colorContainer.getChildCount()-3);

            String answer = colorEditText.getText().toString();

            if (answer.equals("")) {
                Toast.makeText(gameParameters, "¡Escribe un resultado!", Toast.LENGTH_SHORT).show();
            }
            else {
                int answerNumber = Integer.parseInt(answer);

                if (answerNumber == correctResult) {
                    sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
                    return true;
                }
            }
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "answersContainer");
        }

        sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
        return false;
    }

}
