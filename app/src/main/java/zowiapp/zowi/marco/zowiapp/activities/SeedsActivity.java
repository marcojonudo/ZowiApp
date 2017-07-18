package zowiapp.zowi.marco.zowiapp.activities;

import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
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
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class SeedsActivity extends ActivityTemplate {

    private String[] containerImages, correction;
    private Point[] seedsCoordinates, containerCoordinates;
    private Point seedsDimensions, containerDimensions;
    private float distanceToLeft, distanceToTop;

    public SeedsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new SeedsChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.SEEDS);

        getParameters();
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
            seedsCoordinates = Functions.createEmptyPointArray(SeedsConstants.NUMBER_OF_SEEDS);
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

        ConstraintLayout seedsActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.seeds_activity_template, contentContainer, false);
        ConstraintLayout constraintContainer = (ConstraintLayout) seedsActivityTemplate.findViewById(R.id.constraint_container);

        inflater.inflate(R.layout.seeds_template, constraintContainer, true);
        inflater.inflate(R.layout.seeds_container_1x4_template, constraintContainer, true);

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
        if (contentContainer != null) {
            dragLimits[0] = 0;
            dragLimits[1] = 0;
            dragLimits[2] = contentContainer.getRight();
            dragLimits[3] = contentContainer.getBottom();
        }

        if (seedsImagesContainer != null) {
            View constraintView;
            int guidelineOmiter = 0;
            for (int i=0; i<SeedsConstants.NUMBER_OF_SEEDS; i++) {
                /* To omit the guideline child*/
                if (i == SeedsConstants.GUIDELINE_POSITION) {
                    guidelineOmiter += 1;
                }
                constraintView = seedsImagesContainer.getChildAt(i+guidelineOmiter);

                seedsCoordinates[i].set(seedsImagesContainer.getLeft() + constraintView.getLeft() + constraintView.getWidth()/2,
                        seedsImagesContainer.getTop() + constraintView.getTop() + constraintView.getHeight()/2);
                seedsDimensions.set(constraintView.getWidth(), constraintView.getHeight());
            }

            imagesHandler.loadCategoriesImages(contentContainer, doubleArrayImages, SeedsConstants.NUMBER_OF_SEEDS, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, seedsCoordinates, seedsDimensions, correction);
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

            imagesHandler.loadSimpleImages(seedsFinalContainer, containerImages, containerImages.length, containerImages.length);
        }
    }

    boolean processTouchEvent(View view, MotionEvent event) {
        float left, right, top, bottom;

        LinearLayout headerText = (LinearLayout) gameParameters.findViewById(R.id.header_text);
        int headerTextHeight = 0;
        if (headerText != null) {
            headerTextHeight = headerText.getHeight();
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "headerText");
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                distanceToLeft = view.getX() - event.getRawX();
                distanceToTop = headerTextHeight + view.getY() - event.getRawY();

                view.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                left = event.getRawX() + distanceToLeft;
                right = event.getRawX() + (view.getWidth()+ distanceToLeft);
                top = event.getRawY() + distanceToTop - headerTextHeight;
                bottom = event.getRawY() + (view.getHeight()+ distanceToTop);

                /* Mechanism to avoid the element to move outside the container.
                   It is only moved when it is in 'contentContainer' */
                if ((left <= dragLimits[0] || right >= dragLimits[2])) {
                    if ((top > dragLimits[1]) && (bottom < dragLimits[3])) {
                        view.setY(top);
                    }
                }
                else if ((top <= dragLimits[1]) || (bottom >= dragLimits[3])) {
                    if ((left > dragLimits[0] && right < dragLimits[2])) {
                        view.setX(left);
                    }
                }
                else {
                    view.setX(left);
                    view.setY(top);
                }
                break;
            case MotionEvent.ACTION_UP:
                float viewCenterX = view.getX() + view.getWidth()/2;
                float viewCenterY = view.getY() + view.getHeight()/2;

                int index = Integer.parseInt(view.getTag().toString().split("-")[0]);

                boolean correctAnswer = false;
                int insideColumnIndex = -1;
                /* We loop through the number of containers */
                for (int i=0; i<containerCoordinates.length; i++) {
                    if ((viewCenterX > containerCoordinates[i].x)&&(viewCenterX < (containerCoordinates[i].x+containerDimensions.x))) {
                        if (viewCenterY > containerCoordinates[i].y) {
                            insideColumnIndex = i;

                            String imageCategory = view.getTag().toString().split("-")[1];
                            correctAnswer = ((SeedsChecker) checker).check(gameParameters, imageCategory, correction[i]);

                            break;
                        }
                    }
                }

                if (correctAnswer) {
                    /* If the view is not completely inside the box, we move it */
                    if (view.getX() < containerCoordinates[insideColumnIndex].x) {
                        view.setX(containerCoordinates[insideColumnIndex].x);
                    }
                    else if ((view.getX()+view.getWidth()) > (containerCoordinates[insideColumnIndex].x+containerDimensions.x)) {
                        view.setX(containerCoordinates[insideColumnIndex].x+containerDimensions.x-view.getWidth());
                    }

                    if (view.getY() < containerCoordinates[insideColumnIndex].y) {
                        view.setY(containerCoordinates[insideColumnIndex].y);
                    }
                    else if ((view.getY()+view.getHeight()) > (containerCoordinates[insideColumnIndex].y+containerDimensions.y)) {
                        view.setY(containerCoordinates[insideColumnIndex].y+containerDimensions.y-view.getHeight());
                    }
                }
                else {
                    Animations.translateAnimation(view, seedsCoordinates, index);
                }
                break;
            default:
                break;
        }

        return true;
    }

}
