package zowiapp.zowi.marco.zowiapp.layout;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import zowiapp.zowi.marco.zowiapp.R;

/**
 * Created by Marco on 06/10/2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Escolar_N.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
