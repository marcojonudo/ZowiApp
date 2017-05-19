package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class FoodPyramidChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, String imageCategory, String containerCategory) {
        if (imageCategory.equals(containerCategory)) {
            sendDataToZowi(CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
            sendDataToZowi(WRONG_ANSWER_COMMAND);
            return false;
        }
    }

}
