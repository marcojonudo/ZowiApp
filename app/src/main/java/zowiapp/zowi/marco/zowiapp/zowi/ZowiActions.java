package zowiapp.zowi.marco.zowiapp.zowi;

public class ZowiActions {

    public static final String CORRECT_ANSWER_COMMAND = "C 1";
    public static final String WRONG_ANSWER_COMMAND = "C 0";
    public static final String TIC_ANSWER_COMMAND = "D 1";
    public static final String X_ANSWER_COMMAND = "D 0";
    public static final String ZOWI_CHECKS_ANSWERS = "E";
    public static final String ZOWI_WALKS_FORWARD = "W";
    public static final String TURN_LEFT = "T 1";
    public static final String TURN_RIGHT = "T 0";
    public static final String GUIDE = "G";

    public static void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

}
