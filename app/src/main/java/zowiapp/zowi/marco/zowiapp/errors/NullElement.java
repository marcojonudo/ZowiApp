package zowiapp.zowi.marco.zowiapp.errors;

import android.util.Log;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class NullElement {

    public NullElement(GameParameters gameParameters, String className, String methodName, String objectName) {
        Log.e("NullElement", className + " - " + methodName + " - " + objectName);

        String text = "¡Vaya! ¡Ha habido un problema!\nAvisa a la maestra y pulsa el botón";
        Layout.showErrorAlertDialog(gameParameters, "¡Solucionar!", text);
    }
}
