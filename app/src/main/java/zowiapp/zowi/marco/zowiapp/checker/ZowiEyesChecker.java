package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ZowiEyesConstants;

/**
 * Created by Marco on 03/02/2017.
 */
public class ZowiEyesChecker {

    private static int[][] imagesCoordinates;

    public static void setImagesCoordinates(int[][] coordinates) {
        imagesCoordinates = coordinates;
    }

    public boolean check(float eventX, float eventY) {
        double distance, distanceToCenter = 2000;
        for (int[] image: imagesCoordinates) {
            distance = Math.sqrt(Math.pow(image[0]-eventX, 2) + Math.pow(image[1]-eventY, 2));

            if (distance < distanceToCenter) {
                distanceToCenter = distance;
            }

        }

        for (int i=0; i< ZowiEyesConstants.DISTANCE_LIMITS.length; i++) {
            if (distanceToCenter > ZowiEyesConstants.DISTANCE_LIMITS[i][0]) {
                //TODO Send frequency to Zowi
                break;
            }
        }
        return true;
    }

}
