package zowiapp.zowi.marco.zowiapp.errors;

import android.util.Log;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class NullElement {

    public NullElement(GameParameters gameParameters, String className, String methodName, String objectName) {
        Log.e("NullElement", className + " - " + methodName + " - " + objectName);

        String text = "¡Ha habido un error, avisa a tu profe!";
        Layout.showErrorAlertDialog(gameParameters, "¡Solucionar!", text);
    }
}
