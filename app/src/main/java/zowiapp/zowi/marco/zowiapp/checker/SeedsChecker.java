package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.MainActivity;
import zowiapp.zowi.marco.zowiapp.activities.CheckerTemplate;

/**
 * Created by Marco on 03/02/2017.
 */
public class SeedsChecker extends CheckerTemplate {

    public SeedsChecker() {
        super(MainActivity.getOutputStream());
    }

    public boolean check(GameParameters gameParameters, String imageCategory, String containerCategory) {
        if (imageCategory.equals(containerCategory)) {
            Toast.makeText(gameParameters, "Bien", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(gameParameters, "Mal", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void sendDataToZowi() {

    }

}
