package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

public class PuzzleChecker extends CheckerTemplate {

    public boolean check(GameParameters gameParameters, Point[] piecesCoordinates, Point[] correction) {
        Thread zowiSeeScreenThread = ThreadHandler.createThread(ThreadType.SIMPLE_FEEDBACK);
        zowiSeeScreenThread.start();

        sendDataToZowi(ZowiActions.ZOWI_CHECKS_ANSWERS);

        try {
            zowiSeeScreenThread.join();

            double distance;
            for (int i=0; i<piecesCoordinates.length; i++) {
                distance = Math.sqrt(Math.pow(piecesCoordinates[i].x-correction[i].x, 2) + Math.pow(piecesCoordinates[i].x-correction[i].y, 2));
                if (distance > PuzzleConstants.DISTANCE_LIMIT) {
                    return false;
                }
            }
            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}
