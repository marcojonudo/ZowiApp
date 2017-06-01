package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
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
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class ZowiEyesActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private ImagesHandler imagesHandler;
    private String[][] images;
    private int[][] imageViewsCoordinates, imagesCoordinates;
    private int[] imagesNumber;

    public ZowiEyesActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.ZOWI_EYES);
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
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
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
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "headerText");
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

            if (contentContainer != null) {
                ConstraintLayout constrainContainer = (ConstraintLayout) contentContainer.getChildAt(0);

                imagesHandler.loadZowiEyesImages(constrainContainer, images, imagesNumber[0], imagesNumber[1], ZowiEyesConstants.LAYOUT_IMAGES, imagesCoordinates, imageViewsCoordinates);
            }
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "zowiEyesContainer");
        }

        ZowiEyesChecker.setImagesCoordinates(imagesCoordinates);
    }

}
