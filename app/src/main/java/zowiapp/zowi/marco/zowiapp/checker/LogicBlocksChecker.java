package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

public class LogicBlocksChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, int chosenImageTag, int correctIndex) {
        if (chosenImageTag == correctIndex)
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
        else
            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);

        return true;
    }

}
