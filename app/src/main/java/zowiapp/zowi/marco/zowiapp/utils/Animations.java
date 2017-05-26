package zowiapp.zowi.marco.zowiapp.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.ScaleAnimation;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;

/**
 * Created by Marco on 26/05/2017.
 */

public class Animations {

    private static final int TRANSLATE_ANIMATION_DURATION = 1000;
    private static final int SCALE_ANIMATION_DURATION = 500;

    public static void translateAnimation(View view, int[][] coordinates, int index) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), coordinates[index][0]);
        animX.setDuration(TRANSLATE_ANIMATION_DURATION);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), coordinates[index][1]);
        animY.setDuration(TRANSLATE_ANIMATION_DURATION);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animX).with(animY);
        animatorSet.start();
    }

    public static void scaleAnimation(View view, float scaleFactorToPuzzle, float[][] pivots, int index) {
        ScaleAnimation anim = new ScaleAnimation(
                scaleFactorToPuzzle, 1f,
                scaleFactorToPuzzle, 1f,
                ScaleAnimation.RELATIVE_TO_PARENT, pivots[index][0], // Pivot point of X scaling
                ScaleAnimation.RELATIVE_TO_PARENT, pivots[index][1]); // Pivot point of Y scaling
        anim.setFillAfter(true);
        anim.setDuration(SCALE_ANIMATION_DURATION);
        view.startAnimation(anim);
    }

}
