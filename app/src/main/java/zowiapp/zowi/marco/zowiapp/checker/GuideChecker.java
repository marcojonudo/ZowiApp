package zowiapp.zowi.marco.zowiapp.checker;

import android.support.constraint.ConstraintLayout;
import android.view.View;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.utils.AsyncTaskHandler;
import zowiapp.zowi.marco.zowiapp.utils.Layout;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.GuideActivity;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.utils.Animations;

public class GuideChecker extends CheckerTemplate {

    private GuideActivity guideActivity;
    private GameParameters gameParameters;

    public GuideChecker(GuideActivity guideActivity) {
        this.guideActivity = guideActivity;
    }

    public void check(GameParameters gameParameters) {
        this.gameParameters = gameParameters;
        new AsyncTaskHandler(this, ActivityType.GUIDE).execute(ZowiActions.ZOWI_CHECKS_ANSWERS);
    }

    public void checkAnswers() {
        ConstraintLayout guideImagesContainer = (ConstraintLayout) gameParameters.findViewById(R.id.guide_images_container);

        if (guideImagesContainer != null) {
            View view;
            for (int i=0; i<guideImagesContainer.getChildCount(); i++) {
                view = guideImagesContainer.getChildAt(i);
                if (view.getTag().toString().equals("0"))
                    Animations.shadeAnimation(view, 1.0f, 0.1f);
            }
        }
        else {
            new NullElement(gameParameters, guideActivity.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "guideImagesContainer");
        }

        String text = gameParameters.getResources().getString(R.string.guide_zowi);
        Layout.showGenericAlertDialog(gameParameters, true, text);
        sendDataToZowi(ZowiActions.GUIDE);
    }

}
