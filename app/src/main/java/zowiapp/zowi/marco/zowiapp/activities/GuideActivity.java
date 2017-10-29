package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GuideConstants;
import zowiapp.zowi.marco.zowiapp.checker.GuideChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class GuideActivity extends ActivityTemplate {

    private Point imagesDimensions;

    public GuideActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new GuideChecker(this);
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.GUIDE);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(GuideConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(GuideConstants.JSON_PARAMETER_CORRECTION);

            doubleArrayImages = new String[jsonImages.length()][];
            correction = new String[jsonCorrection.length()];
            imagesCoordinates = Functions.createEmptyPointArray(GuideConstants.NUMBER_OF_IMAGES);
            imagesDimensions = new Point();

            for (int i = 0; i< doubleArrayImages.length; i++) {
                JSONArray jsonCategoryImages = jsonImages.getJSONArray(i);
                doubleArrayImages[i] = new String[jsonCategoryImages.length()];
                for (int j = 0; j< doubleArrayImages[i].length; j++) {
                    doubleArrayImages[i][j] = jsonCategoryImages.getString(j);
                }
            }
            for (int i=0; i<correction.length; i++) {
                correction[i] = jsonCorrection.getString(i);
            }

            imagesHandler.init(null, doubleArrayImages, GuideConstants.CATEGORY_ONLY_ONE_IMAGE, correction);
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
        LinearLayout guideActivityTemplate = (LinearLayout) inflater.inflate(R.layout.guided_guide_activity_template, contentContainer, false);

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
        ConstraintLayout constraintImages = (ConstraintLayout) gameParameters.findViewById(R.id.guide_images_container);

        if (constraintImages != null) {
            View constraintView;
            for (int i=0; i<constraintImages.getChildCount(); i++) {
                constraintView = constraintImages.getChildAt(i);

                imagesCoordinates[i].set((int)constraintView.getX() + constraintView.getWidth()/2, (int)constraintView.getY() + constraintView.getHeight()/2);
                imagesDimensions.set(constraintView.getWidth(), constraintView.getHeight());
            }

            // TODO Revisar bucle infinito imágenes
            imagesHandler.loadGuideImages(constraintImages, GuideConstants.NUMBER_OF_IMAGES, imagesCoordinates, imagesDimensions);
        }
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                ((GuideChecker) checker).check(gameParameters);
            }
        }.start();
    }

}
