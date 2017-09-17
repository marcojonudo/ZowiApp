package zowiapp.zowi.marco.zowiapp.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;

public class Animations {

    private static final int TRANSLATE_ANIMATION_DURATION = 1000;
    private static final int SCALE_ANIMATION_DURATION = 500;
    private static final int SHADE_ANIMATION_DURATION = 1000;

    public static void translateAnimation(View view, Point[] coordinates, int index) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), coordinates[index].x);
        animX.setDuration(TRANSLATE_ANIMATION_DURATION);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), coordinates[index].y);
        animY.setDuration(TRANSLATE_ANIMATION_DURATION);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animX).with(animY);
        animatorSet.start();
    }

    public static void scaleAnimation(View view, float scaleFactorToPuzzle, float[] pivots) {
        int drawableWidth = ((ImageView)view).getDrawable().getIntrinsicWidth();
        int drawableHeight = ((ImageView)view).getDrawable().getIntrinsicHeight();
        Log.i("scaleAnimation", "scaleFactorToPuzzle: " + scaleFactorToPuzzle);

        ScaleAnimation anim = new ScaleAnimation(
                1f, scaleFactorToPuzzle,
                1f, scaleFactorToPuzzle,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
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

    public static void rotateAndTranslate(View v, int degrees) {
        AnimationSet animationSet = new AnimationSet(true);
        RotateAnimation rotateAnimation = new RotateAnimation(0, degrees, v.getWidth()/2, v.getHeight()/2);
        rotateAnimation.setDuration(TRANSLATE_ANIMATION_DURATION);

        int[] translation = null;
        switch (degrees) {
            case 0:
                translation = new int[]{0, -v.getHeight()};
                break;
            case 90:
                translation = new int[]{-v.getWidth(), 0};
                break;
            case -90:
                translation = new int[]{v.getWidth(), 0};
                break;
            case 180:
                translation = new int[]{0, v.getHeight()};
                break;
            default:
                translation = new int[]{0, 0};
                break;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(0, translation[0], 0, translation[1]);
        translateAnimation.setDuration(TRANSLATE_ANIMATION_DURATION);
        translateAnimation.setFillAfter(true);

        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(translateAnimation);

        v.startAnimation(animationSet);
    }

}
