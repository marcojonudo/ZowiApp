package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.ZowiActions;
import zowiapp.zowi.marco.zowiapp.ZowiSocket;

/**
 * Created by Marco on 03/02/2017.
 */
public class DragChecker extends CheckerTemplate {

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
