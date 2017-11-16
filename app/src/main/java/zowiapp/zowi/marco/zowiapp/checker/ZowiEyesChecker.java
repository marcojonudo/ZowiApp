package zowiapp.zowi.marco.zowiapp.checker;

import android.graphics.Point;
import android.util.Log;

import java.util.Date;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ZowiEyesConstants;

public class ZowiEyesChecker extends CheckerTemplate{

    private static Point[] imagesCoordinates;
    private boolean firstTime = true;
    private Long referenceTime;

    public static void setImagesCoordinates(Point[] coordinates) {
        imagesCoordinates = coordinates;
    }

    public boolean check(float eventX, float eventY) {
        double distance, distanceToCenter = 2000;
        if (firstTime) {
            referenceTime = new Date().getTime();
            firstTime = false;
        }

        for (Point image: imagesCoordinates) {
            distance = Math.sqrt(Math.pow(image.x-eventX, 2) + Math.pow(image.y-eventY, 2));

            if (distance < distanceToCenter) {
                distanceToCenter = distance;
            }
        }

        for (int i=0; i < ZowiEyesConstants.DISTANCE_LIMITS.length; i++) {
            if (distanceToCenter > ZowiEyesConstants.DISTANCE_LIMITS[i]) {
                Log.i("ZowiEyes", eventX + ", " + eventY + ":   " + distanceToCenter);
                if (new Date().getTime() - referenceTime >= ZowiEyesConstants.DISTANCE_PERIODS[i]) {
                    sendDataToZowi("S " + String.valueOf(ZowiEyesConstants.FREQUENCY_LIST[i]));
                    referenceTime = new Date().getTime();
                }
                break;
            }
        }
        return true;
    }

}
