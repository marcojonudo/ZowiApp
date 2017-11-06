package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.GameParameters;

public class ColumnsChecker extends CheckerTemplate{

    public boolean check(GameParameters gameParameters, String chosenColumn, String correctColumn) {
        if (chosenColumn.equals(correctColumn)) {
//            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        }
        else {
//            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
            return false;
        }
    }

}
