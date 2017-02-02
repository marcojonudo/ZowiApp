package zowiapp.zowi.marco.zowiapp.activities;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MemoryConstants;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class MemoryActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String[] memoryImages;


    public MemoryActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonMemmoryImages = activityDetails.getJSONArray(MemoryConstants.JSON_PARAMETER_IMAGES);
            memoryImages = new String[jsonMemmoryImages.length()];

            for (int i = 0; i< memoryImages.length; i++) {
                memoryImages[i] = jsonMemmoryImages.getString(i);
            }

            generateLayout();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void generateLayout() {
        setTitleDescription(gameParameters, activityTitle, activityDescription);

        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        RelativeLayout memoryActivityTemplate = (RelativeLayout) inflater.inflate(R.layout.memory_activity_template, contentContainer, false);
        GridLayout imagesGrid = (GridLayout) memoryActivityTemplate.findViewById(R.id.images_grid);

        placeImages(imagesGrid, memoryImages);

        if (contentContainer != null) {
            contentContainer.addView(memoryActivityTemplate);
        }

        new CountDownTimer(2000, 2000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                hideImages();
            }
        }.start();
    }

    private void placeImages(GridLayout imagesContainer, String[] images) {
        int[] ocurrences = {0, 0, 0, 0, 0, 0, 0, 0};
        for (int i=0; i<images.length; i++) {
            int randomIndex = new Random().nextInt(images.length);
            while (ocurrences[randomIndex] == 1) {
                randomIndex = new Random().nextInt(images.length);
            }
            ocurrences[randomIndex] = 1;
            placeImage(imagesContainer, images[randomIndex], i);
        }
    }

    private void placeImage(GridLayout container, String imageName, int i) {
        float width = gameParameters.getResources().getDimension(R.dimen.memory_image_side);

        ImageView image = new ImageView(gameParameters);
        image.setBackground(ContextCompat.getDrawable(gameParameters, gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName())));
        //image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)width, (int)width);
        image.setLayoutParams(layoutParams);
        image.setTag(i);

        container.addView(image);

        TouchListener touchListener = new TouchListener(MemoryConstants.MEMORY_TYPE, this);
        image.setOnTouchListener(touchListener);
    }

    private void hideImages() {
        GridLayout imagesGrid = (GridLayout) gameParameters.findViewById(R.id.images_grid);

        for (int i=0; i<imagesGrid.getChildCount(); i++) {
            ImageView image = (ImageView) imagesGrid.getChildAt(i);
            ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(gameParameters, R.animator.flip);
            anim.setTarget(image);
            anim.setDuration(2000);
            anim.start();
        }
    }

    protected void processTouchEvent(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            default:
                break;
        }
    }

}
