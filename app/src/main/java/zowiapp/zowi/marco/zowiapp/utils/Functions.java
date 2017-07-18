package zowiapp.zowi.marco.zowiapp.utils;

import android.graphics.Point;

public class Functions {

    public static Point[] createEmptyPointArray(int length) {
        Point[] pointArray = new Point[length];
        for (int i=0; i<pointArray.length; i++) pointArray[i] = new Point();

        return pointArray;
    }

}
