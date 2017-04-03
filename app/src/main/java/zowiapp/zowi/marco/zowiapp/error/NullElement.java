package zowiapp.zowi.marco.zowiapp.error;

import android.util.Log;

import zowiapp.zowi.marco.zowiapp.GameParameters;

/**
 * Created by Marco on 03/04/2017.
 */

public class NullElement {

    GameParameters gameParameters;

    public NullElement(GameParameters gameParameters, String className, String methodName, String objectName) {
        this.gameParameters = gameParameters;
        Log.e("NullElement", className + " - " + methodName + " - " + objectName);
    }
}
