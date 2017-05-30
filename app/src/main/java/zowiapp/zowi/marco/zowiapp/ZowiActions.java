package zowiapp.zowi.marco.zowiapp;

public class ZowiActions {

    public static final String CORRECT_ANSWER_COMMAND = "C 1";
    public static final String WRONG_ANSWER_COMMAND = "C 2";
    public static final String TIC_ANSWER_COMMAND = "D 1";
    public static final String X_ANSWER_COMMAND = "D 2";
    public static final String ZOWI_CHECKS_ANSWERS = "E";

    public static void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

}
