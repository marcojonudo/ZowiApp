package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;

import java.util.Date;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ZowiEyesConstants;

/**
 * Created by Marco on 03/02/2017.
 */
public class ZowiEyesChecker extends CheckerTemplate{

    private static int[][] imagesCoordinates;
    private boolean firstTime = true;
    private Long referenceTime;

    public static void setImagesCoordinates(int[][] coordinates) {
        imagesCoordinates = coordinates;
    }

    public boolean check(float eventX, float eventY) {
        double distance, distanceToCenter = 2000;
        if (firstTime) {
            referenceTime = new Date().getTime();
            firstTime = false;
        }

        for (int[] image: imagesCoordinates) {
            distance = Math.sqrt(Math.pow(image[0]-eventX, 2) + Math.pow(image[1]-eventY, 2));

            if (distance < distanceToCenter) {
                distanceToCenter = distance;
            }
        }

        for (int i=0; i < ZowiEyesConstants.DISTANCE_LIMITS.length; i++) {
            if (distanceToCenter > ZowiEyesConstants.DISTANCE_LIMITS[i][0]) {
                Log.i("ZowiEyes", String.valueOf(distanceToCenter));
                if (new Date().getTime() - referenceTime >= ZowiEyesConstants.DISTANCE_PERIODS[i]) {
                    sendDataToZowi("S " + String.valueOf(ZowiEyesConstants.DISTANCE_LIMITS[i][1]));
                    referenceTime = new Date().getTime();
                }
                break;
            }
        }
        return true;
    }

}
