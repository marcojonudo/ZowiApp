package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.ZowiActions;
import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class ColumnsChecker extends CheckerTemplate{

    public boolean check(GameParameters gameParameters, String chosenColumn, String correctColumn) {
        if (chosenColumn.equals(correctColumn)) {
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
            return false;
        }
    }

}
