package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

/**
 * Created by Marco on 24/01/2017.
 */
public abstract class CheckerTemplate {

    protected void sendDataToZowi(String command) {
        ZowiActions.sendDataToZowi(command);
    }

}
