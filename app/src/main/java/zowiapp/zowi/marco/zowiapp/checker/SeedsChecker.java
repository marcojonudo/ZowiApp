package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

public class SeedsChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, String imageCategory, String containerCategory) {
        if (imageCategory.equals(containerCategory)) {
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
            return false;
        }
    }

}
