package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GuideConstants;
import zowiapp.zowi.marco.zowiapp.checker.GuideChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class GuideActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private GuideChecker guideChecker;
    private ImagesHandler imagesHandler;
    private String[][] images;
    private int[][] imagesCoordinates;
    private int[] imagesDimensions;
    private String[] correction;

    public GuideActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        guideChecker = new GuideChecker(this);
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.GUIDE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(GuideConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(GuideConstants.JSON_PARAMETER_CORRECTION);
            images = new String[jsonImages.length()][];
            correction = new String[jsonCorrection.length()];

            for (int i = 0; i< images.length; i++) {
                JSONArray jsonCategoryImages = jsonImages.getJSONArray(i);
                images[i] = new String[jsonCategoryImages.length()];
                for (int j=0; j<images[i].length; j++) {
                    images[i][j] = jsonCategoryImages.getString(j);
                }
            }
            for (int i=0; i<correction.length; i++) {
                correction[i] = jsonCorrection.getString(i);
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
        LinearLayout guideActivityTemplate = (LinearLayout) inflater.inflate(R.layout.guide_activity_template, contentContainer, false);

        if (contentContainer != null) {
            contentContainer.addView(guideActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ActivityType.GUIDE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout constraintImages = (ConstraintLayout) gameParameters.findViewById(R.id.guide_images_container);

        if (constraintImages != null) {
            imagesCoordinates = new int[GuideConstants.NUMBER_OF_IMAGES][CommonConstants.AXIS_NUMBER];
            imagesDimensions = new int[CommonConstants.AXIS_NUMBER];

            View constraintView;
            for (int i=0; i<constraintImages.getChildCount(); i++) {
                constraintView = constraintImages.getChildAt(i);

                imagesCoordinates[i][0] = (int)constraintView.getX() + constraintView.getWidth()/2;
                imagesCoordinates[i][1] = (int)constraintView.getY() + constraintView.getHeight()/2;
                imagesDimensions[0] = constraintView.getWidth();
                imagesDimensions[1] = constraintView.getHeight();
            }

            imagesHandler.loadCategoriesImages(contentContainer, images, GuideConstants.NUMBER_OF_IMAGES, GuideConstants.CATEGORY_ONLY_ONE_IMAGE, imagesCoordinates, imagesDimensions, correction);
        }

        guideChecker.check(gameParameters);
    }

}
