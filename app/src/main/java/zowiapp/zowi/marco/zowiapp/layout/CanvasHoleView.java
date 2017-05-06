package zowiapp.zowi.marco.zowiapp.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.Date;

import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.checker.ZowiEyesChecker;

public class CanvasHoleView extends FrameLayout {
    private static float RADIUS;

    private Paint backgroundPaint;
    boolean firstLoad = true;
    private float eventX, eventY;
    private float lastEventX, lastEventY;
    private float screenWidth, screenHeight;
    private ZowiEyesChecker zowiEyesChecker;
    private Long startTime;
    private int mode;

    public CanvasHoleView(Context context) {
        super(context);
        init();
    }

    public CanvasHoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasHoleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        RADIUS = (getResources().getDimension(R.dimen.zowi_eyes_hole_radius) / getResources().getDisplayMetrics().density);
        eventX = -1;
        eventY = -1;
        lastEventX = 0;
        lastEventY = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int navBarHeight = 0;
        if (resourceId > 0) {
            navBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels - navBarHeight;

        zowiEyesChecker = new ZowiEyesChecker();

        backgroundPaint = new Paint();
        backgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        startTime = new Date().getTime();
        mode = 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        eventX = event.getX();
        eventY = event.getY();
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (firstLoad) {
            canvas.drawCircle(RADIUS/2, RADIUS/2, RADIUS, backgroundPaint);
            firstLoad = false;
        }

        if ((eventX <= 0) || (eventX >= screenWidth)) {
            if ((eventY > 0) && (eventY < screenHeight)) {
                canvas.drawCircle(lastEventX, eventY, RADIUS, backgroundPaint);
                lastEventY = eventY;
            }
            else {
                canvas.drawCircle(lastEventX, lastEventY, RADIUS, backgroundPaint);
            }
        }
        else if ((eventY <= 0) || (eventY >= screenHeight)) {
            if ((eventX > 0) && ( eventX < screenWidth)) {
                canvas.drawCircle(eventX, lastEventY, RADIUS, backgroundPaint);
                lastEventX = eventX;
            }
        }
        else {
            canvas.drawCircle(eventX, eventY, RADIUS, backgroundPaint);
            lastEventX = eventX;
            lastEventY = eventY;
        }

        zowiEyesChecker.check(eventX, eventY);
    }
}