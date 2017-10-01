package zowiapp.zowi.marco.zowiapp.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    public static void scaleAnimation(View view, boolean increaseSize, float scaleFactorToPuzzle, float[] pivots) {
        float scale = increaseSize ? scaleFactorToPuzzle : 1f/scaleFactorToPuzzle;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        scaleX.setDuration(SCALE_ANIMATION_DURATION);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY",scale);
        scaleY.setDuration(SCALE_ANIMATION_DURATION);

        /* Pivots are set to 0 to avoid problems getting x and y position of the views */
        view.setPivotX(0);
        view.setPivotY(0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
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

    public static void rotateAnimation(View view, String actualDirection, String nextDirection) {
        float fromValue = 0, toValue = 0;
        switch (actualDirection) {
            case "TOP":
                fromValue = 0;
                toValue = nextDirection.equals("LEFT") ? -90 : 90;
                break;
            case "LEFT":
                fromValue = -90;
                toValue = nextDirection.equals("BOTTOM") ? -180 : 0;
                break;
            case "BOTTOM":
                fromValue = -180;
                toValue = nextDirection.equals("RIGHT") ? -270 : -90;
                break;
            case "RIGHT":
                fromValue = -270;
                toValue = nextDirection.equals("TOP") ? -360 : -180;
                break;
        }
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", fromValue, toValue);
        rotateAnimation.setDuration(TRANSLATE_ANIMATION_DURATION);
        rotateAnimation.start();
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
