package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.SeedsConstants;
import zowiapp.zowi.marco.zowiapp.checker.SeedsChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class SeedsActivity extends ActivityTemplate {

    private String[] containerImages;
    private Point seedsDimensions, containerDimensions;

    public SeedsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new SeedsChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.SEEDS);

        getParameters();
    }

    public void setCorrection(String[] correction) {
        this.correction = correction;
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonSeedsImages = activityDetails.getJSONArray(SeedsConstants.JSON_PARAMETER_SEEDSIMAGES);
            JSONArray jsonContainerImages = activityDetails.getJSONArray(SeedsConstants.JSON_PARAMETER_CONTAINERIMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(SeedsConstants.JSON_PARAMETER_CORRECTION);

            doubleArrayImages = new String[jsonSeedsImages.length()][];
            containerImages = new String[jsonContainerImages.length()];
            correction = new String[jsonCorrection.length()];
            imagesCoordinates = Functions.createEmptyPointArray(SeedsConstants.NUMBER_OF_SEEDS);
            seedsDimensions = new Point();
            containerCoordinates = Functions.createEmptyPointArray(containerImages.length);
            containerDimensions = new Point();
            dragLimits = new int[CommonConstants.DRAG_LIMITS_SIZE];

            for (int i = 0; i< doubleArrayImages.length; i++) {
                JSONArray jsonCategoryImages = jsonSeedsImages.getJSONArray(i);
                doubleArrayImages[i] = new String[jsonCategoryImages.length()];
                for (int j = 0; j< doubleArrayImages[i].length; j++) {
                    doubleArrayImages[i][j] = jsonCategoryImages.getString(j);
                }

                containerImages[i] = jsonContainerImages.getString(i);
                correction[i] = jsonCorrection.getString(i);
            }

            imagesHandler.init(containerImages, doubleArrayImages, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, correction);
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

        ConstraintLayout seedsActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.guided_seeds_activity_template, contentContainer, false);
        ConstraintLayout constraintContainer = (ConstraintLayout) seedsActivityTemplate.findViewById(R.id.constraint_container);

        inflater.inflate(R.layout.guided_seeds_template, constraintContainer, true);
        inflater.inflate(R.layout.guided_seeds_container_1x4_template, constraintContainer, true);

        if (contentContainer != null) {
            contentContainer.addView(seedsActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(SeedsConstants.SEEDS_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        ConstraintLayout seedsImagesContainer = (ConstraintLayout) gameParameters.findViewById(R.id.seeds_images_container);

        /* Store limits for dragging vies */
        if (contentContainer != null)
            setDragLimits(contentContainer);
        else
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");

        if (seedsImagesContainer != null) {
            View constraintView;
            int guidelineOmiter = 0;
            for (int i=0; i<SeedsConstants.NUMBER_OF_SEEDS; i++) {
                /* To omit the guideline child*/
                if (i == SeedsConstants.GUIDELINE_POSITION) {
                    guidelineOmiter += 1;
                }
                constraintView = seedsImagesContainer.getChildAt(i+guidelineOmiter);

                imagesCoordinates[i].set(seedsImagesContainer.getLeft() + constraintView.getLeft() + constraintView.getWidth()/2,
                        seedsImagesContainer.getTop() + constraintView.getTop() + constraintView.getHeight()/2);
                seedsDimensions.set(constraintView.getWidth(), constraintView.getHeight());
            }

            imagesHandler.loadCategoriesImages(contentContainer, SeedsConstants.NUMBER_OF_SEEDS, imagesCoordinates, seedsDimensions);
        }

        ConstraintLayout seedsFinalContainer = (ConstraintLayout) gameParameters.findViewById(R.id.seeds_final_container);

        if (seedsFinalContainer != null) {
            View containerElement;
            Guideline seedsGuideline = (Guideline) gameParameters.findViewById(R.id.seeds_guideline);

            if (seedsGuideline != null) {
                for (int i=0; i<seedsFinalContainer.getChildCount(); i++) {
                    containerElement = seedsFinalContainer.getChildAt(i);

                    containerCoordinates[i].set(containerElement.getLeft(), seedsGuideline.getTop() + containerElement.getTop());
                    containerDimensions.set(containerElement.getWidth(), containerElement.getHeight());
                }
            }

            imagesHandler.loadSimpleImages(seedsFinalContainer, containerImages.length, containerImages.length);
        }
    }

    void processTouchEvent(View view, MotionEvent event) {
        String[] eventsResult = handleEvents(ActivityType.SEEDS, view, event, containerDimensions, null);
        if (eventsResult != null) {
            boolean correctAnswer = ((SeedsChecker) checker).check(gameParameters, eventsResult[1], eventsResult[2]);
            lastImageMovement(ActivityType.SEEDS, view, containerDimensions, null, Integer.parseInt(eventsResult[0]), correctAnswer);

            checkFinishActivity(ActivityType.SEEDS, correctAnswer, SeedsConstants.NUMBER_OF_SEEDS, true);
        }
    }

}
