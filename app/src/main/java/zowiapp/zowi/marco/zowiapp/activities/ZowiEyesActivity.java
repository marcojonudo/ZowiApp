package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ZowiEyesConstants;
import zowiapp.zowi.marco.zowiapp.checker.ZowiEyesChecker;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class ZowiEyesActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String[][] images;
    private int[][] imageViewsCoordinates, imagesCoordinates;
    private int[] imagesNumber;

    public ZowiEyesActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
            JSONArray jsonImages = activityDetails.getJSONArray(ZowiEyesConstants.JSON_PARAMETER_IMAGES);
            images = new String[jsonImages.length()][];
            JSONArray jsonImagesNumber = activityDetails.getJSONArray(ZowiEyesConstants.JSON_PARAMETER_IMAGESNUMBER);
            imagesNumber = new int[jsonImagesNumber.length()];
            imageViewsCoordinates = new int[ZowiEyesConstants.LAYOUT_IMAGES][CommonConstants.AXIS_NUMBER];

            for (int i = 0; i< images.length; i++) {
                JSONArray jsonCategoryImages = jsonImages.getJSONArray(i);
                images[i] = new String[jsonCategoryImages.length()];
                for (int j=0; j<images[i].length; j++) {
                    images[i][j] = jsonCategoryImages.getString(j);
                }

                imagesNumber[i] = jsonImagesNumber.getInt(i);
            }
            imagesCoordinates = new int[imagesNumber[0]+imagesNumber[1]][CommonConstants.AXIS_NUMBER];

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

        if (contentContainer != null) {
            inflater.inflate(R.layout.zowi_eyes_template, contentContainer, true);

            LayoutListener layoutListener = new LayoutListener(ZowiEyesConstants.ZOWI_EYES_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        LinearLayout headerText = (LinearLayout) gameParameters.findViewById(R.id.header_text);

        /* Change header colors to make the activity more immersive */
        if (headerText != null) {
            headerText.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.black));
            TextView title = (TextView) headerText.getChildAt(0);
            title.setTextColor(ContextCompat.getColor(gameParameters, R.color.white));
            TextView description = (TextView) headerText.getChildAt(1);
            description.setTextColor(ContextCompat.getColor(gameParameters, R.color.white));
        }

        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout zowiEyesContainer = (ConstraintLayout) gameParameters.findViewById(R.id.zowi_eyes_template);

        if (zowiEyesContainer != null) {
            ImageView imageView;
            for (int i=0; i<zowiEyesContainer.getChildCount()-1; i++) {
                imageView = (ImageView) zowiEyesContainer.getChildAt(i);

                imageViewsCoordinates[i][0] = imageView.getLeft() + imageView.getWidth()/2;
                imageViewsCoordinates[i][1] = zowiEyesContainer.getTop() + imageView.getTop() + imageView.getHeight()/2;
            }

            placeImages(contentContainer, images);
        }

        ZowiEyesChecker.setImagesCoordinates(imagesCoordinates);
    }


    private void placeImages(RelativeLayout contentContainer, String[][] images) {
        int[] occurrences = new int[ZowiEyesConstants.LAYOUT_IMAGES];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = 0;
        }
        ConstraintLayout constrainContainer = (ConstraintLayout) contentContainer.getChildAt(0);

        for (int i=0; i<imagesNumber[0]; i++) {
            int randomImagesIndex = new Random().nextInt(occurrences.length);

            while (occurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
            }
            occurrences[randomImagesIndex] = 1;

            ImageView imageView = (ImageView) constrainContainer.getChildAt(randomImagesIndex);
            /* 0 is the index for the correct images */
            placeImage(imageView, images[0][i], randomImagesIndex, i, 0);
        }

        for (int i=0; i<imagesNumber[1]; i++) {
            int randomImagesIndex = new Random().nextInt(images[1].length);

            while (occurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
            }
            occurrences[randomImagesIndex] = 1;

            ImageView imageView = (ImageView) constrainContainer.getChildAt(randomImagesIndex);
            /* 0 is the index for the wrong images */
            placeImage(imageView, images[1][i], randomImagesIndex, imagesNumber[0]+i, 1);
        }
    }

    private void placeImage(ImageView imageView, String imageName, int randomImagesIndex, int i, int correction) {
        imageView.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        imageView.setTag(correction);

        imagesCoordinates[i][0] = imageViewsCoordinates[randomImagesIndex][0];
        imagesCoordinates[i][1] = imageViewsCoordinates[randomImagesIndex][1];
    }

}
