package zowiapp.zowi.marco.zowiapp.checker;

import android.support.constraint.ConstraintLayout;
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
public class ColouredGridChecker {

    public void check(GameParameters gameParameters, int index, int correctResult) {
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
                    Toast.makeText(gameParameters, "¡Bien!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(gameParameters, "¡Mal!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
