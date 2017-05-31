package zowiapp.zowi.marco.zowiapp.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;

/**
 * Created by Marco on 26/05/2017.
 */

public class Animations {

    private static final int TRANSLATE_ANIMATION_DURATION = 1000;
    private static final int SCALE_ANIMATION_DURATION = 500;
    private static final int SHADE_ANIMATION_DURATION = 1000;

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

    public static void shadeAnimation(View view, float from, float to) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setDuration(SHADE_ANIMATION_DURATION);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }

    public static void flip1(Context context, ImageView frontView, View backView) {
        Animator setRightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out);
        Animator setLeftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);

        setRightOut.setTarget(frontView);
        setLeftIn.setTarget(backView);
        setRightOut.start();
        setLeftIn.start();

    }

    public static void flip2WithDelay(Context context, ImageView frontView1, View backView1, ImageView frontView2, View backView2) {
        Animator setLeftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
        Animator setRightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out);

        setLeftOut.setTarget(frontView1);
        setRightIn.setTarget(backView1);
        setLeftOut.start();
        setRightIn.start();

        setLeftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
        setRightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out);

        setLeftOut.setTarget(frontView2);
        setRightIn.setTarget(backView2);
        setLeftOut.start();
        setRightIn.start();
    }

}
