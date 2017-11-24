package zowiapp.zowi.marco.zowiapp.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import zowiapp.zowi.marco.zowiapp.R;

public class Animations {

    private static final int TRANSLATE_ANIMATION_DURATION = 1000;
    private static final int GRID_ROTATE_ANIMATION_DURATION = 6000;
    private static final int GRID_TRANSLATE_ANIMATION_DURATION = 3500;
    private static final int SCALE_ANIMATION_DURATION = 500;
    private static final int SHADE_ANIMATION_DURATION = 1000;
    private static final int CERO_DURATION = 0;

    public static void translateAnimation(View view, Point[] coordinates, int index) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), coordinates[index].x);
        animX.setDuration(TRANSLATE_ANIMATION_DURATION);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), coordinates[index].y);
        animY.setDuration(TRANSLATE_ANIMATION_DURATION);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animX).with(animY);
        animatorSet.start();
    }

    public static void scaleAnimation(View view, boolean increaseSize, float scaleFactorToPuzzle) {
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

    public static AnimatorSet rotateAndTranslate(View view, String actualDirection, String nextDirection, Point[] coordinates, int index) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), coordinates[index].x);
        animX.setDuration(GRID_TRANSLATE_ANIMATION_DURATION);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), coordinates[index].y);
        animY.setDuration(GRID_TRANSLATE_ANIMATION_DURATION);

        float fromValue = 0, toValue = 0;
        switch (actualDirection) {
            case "UP":
                fromValue = 0;
                toValue = nextDirection.equals(actualDirection) ? 0 : (nextDirection.equals("LEFT") ? -90 : 90);
                break;
            case "LEFT":
                fromValue = -90;
                toValue = nextDirection.equals(actualDirection) ? -90 : (nextDirection.equals("DOWN") ? -180 : 0);
                break;
            case "DOWN":
                fromValue = -180;
                toValue = nextDirection.equals(actualDirection) ? -180 : (nextDirection.equals("RIGHT") ? -270 : -90);
                break;
            case "RIGHT":
                fromValue = nextDirection.equals(actualDirection) ? 90 : -270;
                toValue = nextDirection.equals(actualDirection) ? 90 : (nextDirection.equals("UP") ? -360 : -180);
                break;
        }

        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(view, "rotation", fromValue, toValue);
        rotateAnimation.setDuration(actualDirection.equals(nextDirection) ? CERO_DURATION : GRID_ROTATE_ANIMATION_DURATION);
        rotateAnimation.start();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(rotateAnimation);
        animatorSet.play(animX).with(animY).after(actualDirection.equals(nextDirection) ? CERO_DURATION : GRID_ROTATE_ANIMATION_DURATION);
        animatorSet.start();

        return animatorSet;
    }

}
