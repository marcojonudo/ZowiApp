package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

/**
 * Created by Marco on 03/02/2017.
 */
public class MemoryChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, int firstImageId, int secondImageId, int firstPosition, int secondPosition) {
        if ((firstImageId == secondImageId) && (firstPosition != secondPosition)) {
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
            return false;
        }
    }

}
