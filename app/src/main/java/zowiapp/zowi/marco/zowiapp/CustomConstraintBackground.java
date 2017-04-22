package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;

/**
 * Created by Marco on 10/03/2017.
 */
public class CustomConstraintBackground extends ConstraintLayout implements Target {

    public CustomConstraintBackground(Context context) {
        super(context);
    }

    public CustomConstraintBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomConstraintBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {}

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {}
}