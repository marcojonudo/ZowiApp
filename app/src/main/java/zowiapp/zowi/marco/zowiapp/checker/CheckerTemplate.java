package zowiapp.zowi.marco.zowiapp.checker;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.OutputStream;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;

/**
 * Created by Marco on 24/01/2017.
 */
public abstract class CheckerTemplate {

    protected OutputStream outputStream;
    protected static final String CORRECT_ANSWER_COMMAND = "C 1";
    protected static final String WRONG_ANSWER_COMMAND = "C 2";

    public CheckerTemplate(){}

    public CheckerTemplate(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    protected abstract void sendDataToZowi(String command);

}
