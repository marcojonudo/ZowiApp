package zowiapp.zowi.marco.zowiapp;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.OperationsConstants;

public class ZowiActions {

    private static boolean killThread = false;
    private static boolean sendNewInfo = false;

    public static final String CORRECT_ANSWER_COMMAND = "C 1";
    public static final String WRONG_ANSWER_COMMAND = "C 2";
    public static final String TIC_ANSWER_COMMAND = "D 1";
    public static final String X_ANSWER_COMMAND = "D 2";
    public static final String ZOWI_CHECKS_ANSWERS = "E";

    public static void sendDataToZowi(String command) {
        ZowiSocket.sendCommand(command);
    }

    public static void sendOperation(String[] operation) {
        ArrayList<String> matrixCodeArray = new ArrayList<>();
        for (int i=0; i<operation.length; i++) {
            String[] operator = (i == 1) ? ((operation[i].equals("+")) ? OperationsConstants.OPERATORS_TO_LED[0] : OperationsConstants.OPERATORS_TO_LED[1]) : OperationsConstants.NUMBERS_TO_LED[Integer.parseInt(operation[i])];
            for (String numberColumn: operator) {
                matrixCodeArray.add(numberColumn);
            }
            /* Add empty column between operators */
            matrixCodeArray.add(OperationsConstants.EMPTY_COLUMN);
        }
        /* Add empty columns until the last number disappears */
        for (int i=0; i<OperationsConstants.MAX_NUMBER_BLUETOOTH_COLUMNS; i++) {
            matrixCodeArray.add(OperationsConstants.EMPTY_COLUMN);
        }

        StringBuilder matrixCommand = new StringBuilder();
        matrixCommand.append("O ");

        /* New thread for receiving final ack's from Zowi */
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
                            sendNewInfo = true;
                        }
                    }
                }
            }
        }).start();

        for (int i=0; i<matrixCodeArray.size(); i++) {
            matrixCommand.append(matrixCodeArray.get(i)).append(" ");
            /* Send only 5 bits columns to avoid filling the buffer */
            if ((i+1) % 5 == 0) {
                matrixCommand.deleteCharAt(matrixCommand.length()-1);
                ZowiSocket.sendCommand(matrixCommand.toString());
                matrixCommand = new StringBuilder();
                matrixCommand.append("O ");

                while (!sendNewInfo) {}
                sendNewInfo = false;
            }
        }
        ZowiSocket.sendCommand(matrixCommand.toString());
        killThread = true;
    }

}
