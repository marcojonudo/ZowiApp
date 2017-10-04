package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

public class LogicBlocksChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, String zowiDirection, String correctZowiDirection) {
        if (zowiDirection.equals(correctZowiDirection))
            return true;
        else
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);

        return false;
    }

}
