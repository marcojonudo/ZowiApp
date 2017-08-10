package zowiapp.zowi.marco.zowiapp.checker;

import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;

public abstract class CheckerTemplate {

    protected void sendDataToZowi(String command) {
        ZowiActions.sendDataToZowi(command);
    }

}
