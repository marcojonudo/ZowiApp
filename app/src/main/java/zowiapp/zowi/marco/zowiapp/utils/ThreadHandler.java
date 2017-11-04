package zowiapp.zowi.marco.zowiapp.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.MusicActivity;
import zowiapp.zowi.marco.zowiapp.activities.PuzzleActivity;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.zowi.Zowi;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiSocket;

public class ThreadHandler {

    private static boolean killThread;

    public static Thread createThread(final ThreadType threadType) {
        return new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !killThread) {
                    int bytesAvailable = ZowiSocket.isInputStreamAvailable();
                    if (bytesAvailable > 0) {
                        String receivedText = ZowiSocket.readInputStream();

                        switch (threadType) {
                            case ZOWI_CONNECTED:
                                zowiConnected(receivedText);
                                break;
                            case SIMPLE_FEEDBACK:
                                zowiStates(receivedText);
                                break;
                            case ZOWI_MOVES_360:
                                break;
                            case ZOWI_OPERATIONS:
                                zowiOperations(receivedText);
                                break;
                        }
                    }

                }

                killThread = false;
            }
        });
    }

    private static void zowiConnected(String receivedText) {
        /* sendFinalAck from Zowi sends an 'F' */
        if (receivedText.contains(ZowiSocket.ZOWI_PROGRAM_ID)) {
            ZowiSocket.setConnected();
            killThread = true;
        }
    }

    private static void zowiStates(String receivedText) {
        if (receivedText.contains("F")) {
            killThread = true;
        }
    }

    private static void zowiMoves360(byte[] packetBytes, int bytesAvailable) {
//        String receivedText = new String(packetBytes, 0, bytesAvailable);
//                            /* sendFinalAck from Zowi sends an 'F' as response to ZOWI_CHECKS_ANSWERS */
//        if (receivedText.contains("F") && state == WAITING_ZOWI_MOVES) {
//            state = ZOWI_HAS_MOVED;
//        }
//        else if (receivedText.contains("F") && state == ZOWI_HAS_MOVED) {
//            int imageIndex;
//            if (receivedText.contains("AD")) {
//                imageIndex = 1;
//                Animations.rotateAndTranslate(zowi, 0);
//            }
//            else if (receivedText.contains("IZ")) {
//                imageIndex = 3;
//                Animations.rotateAndTranslate(zowi, -90);
//            }
//            else if (receivedText.contains("DE")) {
//                imageIndex = 5;
//                Animations.rotateAndTranslate(zowi, 90);
//            }
//            else if (receivedText.contains("AT")) {
//                imageIndex = 7;
//                Animations.rotateAndTranslate(zowi, 180);
//            }
//            else
//                imageIndex = -1;
//            killThread = true;
//            state = WAITING_ZOWI_MOVES;
//
//            correctAnswer = ((LogicBlocksChecker) checker).check(gameParameters, imageIndex, correctImageIndex);
//
//            reactToAnswer(correctAnswer);
//        }
    }

    private static void zowiOperations(String receivedText) {
        if (receivedText.contains("F")) {
            killThread = true;
        }
    }

    public enum ThreadType {
        ZOWI_CONNECTED, SIMPLE_FEEDBACK, ZOWI_STATES, ZOWI_CHECKS, ZOWI_MOVES_360, ZOWI_OPERATIONS, EMPTY_INPUT_BUFFER
    }

}
