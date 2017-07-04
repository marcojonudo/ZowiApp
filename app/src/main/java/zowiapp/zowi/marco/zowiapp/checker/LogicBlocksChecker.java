package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.ImageView;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.FoodPyramidActivity;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiSocket;

/**
 * Created by Marco on 03/02/2017.
 */
public class LogicBlocksChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, int chosenImageTag, int correctIndex) {
        if (chosenImageTag == correctIndex)
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
        else
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);

        return true;
    }

}
