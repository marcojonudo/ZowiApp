package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.ZowiSocket;

/**
 * Created by Marco on 24/01/2017.
 */
public abstract class CheckerTemplate {

    protected static final String CORRECT_ANSWER_COMMAND = "C 1";
    protected static final String WRONG_ANSWER_COMMAND = "C 2";
    protected static final String TIC_ANSWER_COMMAND = "D 1";
    protected static final String X_ANSWER_COMMAND = "D 2";
    protected static final String ZOWI_CHECKS_ANSWERS = "E";

    protected void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

}
