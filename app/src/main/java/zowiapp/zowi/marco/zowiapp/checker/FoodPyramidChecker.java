package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.ZowiSocket;
import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/02/2017.
 */
public class FoodPyramidChecker extends CheckerTemplate {

    private boolean checkAnswers;
    private boolean killThread;

    public FoodPyramidChecker() {
        checkAnswers = false;
        killThread = false;
    }

    public boolean check(GameParameters gameParameters, String[][] correctionArray) {
        sendDataToZowi(ZOWI_CHECKS_ANSWERS);

        new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !killThread) {
                    int bytesAvailable = ZowiSocket.isInputStreamAvailable();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        ZowiSocket.readInputStream(packetBytes);

                        String receivedText = new String(packetBytes, 0, bytesAvailable);
                        /* sendFinalAck from Zowi sends an 'F' */
                        if (receivedText.contains("F")) {
                            checkAnswers = true;
                            killThread = true;
                        }
                    }
                }
            }
        }).start();

        while (!checkAnswers) {}

        for (String[] correctionStep: correctionArray) {
            if (!correctionStep[0].equals(correctionStep[1])) {
                sendDataToZowi(WRONG_ANSWER_COMMAND);
                return false;
            }
        }
        sendDataToZowi(CORRECT_ANSWER_COMMAND);
        return true;
    }

}
