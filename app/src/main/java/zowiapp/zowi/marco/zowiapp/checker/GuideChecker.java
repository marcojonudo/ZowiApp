package zowiapp.zowi.marco.zowiapp.checker;

import android.widget.ImageView;
import android.widget.LinearLayout;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.GuideActivity;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;

public class GuideChecker extends CheckerTemplate {

    private GuideActivity guideActivity;

    public GuideChecker(GuideActivity guideActivity) {
        this.guideActivity = guideActivity;
    }

    public boolean check(GameParameters gameParameters) {
        Thread zowiSeeScreenThread = ThreadHandler.createThread(ThreadType.SIMPLE_FEEDBACK);
        zowiSeeScreenThread.start();

        sendDataToZowi(ZowiActions.ZOWI_CHECKS_ANSWERS);

        try {
            zowiSeeScreenThread.join();

            LinearLayout guideImagesContainer = (LinearLayout) gameParameters.findViewById(R.id.guide_images_container);

            if (guideImagesContainer != null) {
                ImageView imageView;
                for (int i=0; i<guideImagesContainer.getChildCount(); i++) {
                    // TODO Escoger la imagen de la casa de Zowi
                    imageView = (ImageView) guideImagesContainer.getChildAt(i);
                    Animations.shadeAnimation(imageView, 1.0f, 2.0f);
                }
            }
            else {
                new NullElement(gameParameters, guideActivity.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "guideImagesContainer");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

}
