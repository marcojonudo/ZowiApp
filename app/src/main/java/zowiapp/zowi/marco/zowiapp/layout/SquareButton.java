package zowiapp.zowi.marco.zowiapp.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Marco on 01/08/2016.
 */
public class SquareButton extends Button {

    public SquareButton(Context context) {
        super(context);
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size;
        if (widthMeasureSpec > heightMeasureSpec) {
            size = widthMeasureSpec;
        }
        else {
            size = heightMeasureSpec;
        }
        super.onMeasure(size, size);
    }

}
