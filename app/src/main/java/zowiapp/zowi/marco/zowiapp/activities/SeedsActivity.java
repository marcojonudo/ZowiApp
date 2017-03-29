package zowiapp.zowi.marco.zowiapp.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.SeedsConstants;
import zowiapp.zowi.marco.zowiapp.checker.SeedsChecker;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class SeedsActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private SeedsChecker seedsChecker;
    private String[][] seedsImages;
    private String[] containerImages, correction;
    private int[][] seedsCoordinates, seedsDimensions, containerCoordinates, containerDimensions;
    private float distanceToLeft, distanceToTop;
    private int[] dragLimits;

    public SeedsActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        seedsChecker = new SeedsChecker();

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonSeedsImages = activityDetails.getJSONArray(SeedsConstants.JSON_PARAMETER_SEEDSIMAGES);
            JSONArray jsonContainerImages = activityDetails.getJSONArray(SeedsConstants.JSON_PARAMETER_CONTAINERIMAGES);
            JSONArray jsonCorrection = activityDetails.getJSONArray(SeedsConstants.JSON_PARAMETER_CORRECTION);
            seedsImages = new String[jsonSeedsImages.length()][];
            containerImages = new String[jsonContainerImages.length()];
            correction = new String[jsonCorrection.length()];
            seedsCoordinates = new int[SeedsConstants.NUMBER_OF_SEEDS][CommonConstants.AXIS_NUMBER];
            seedsDimensions = new int[SeedsConstants.NUMBER_OF_SEEDS][CommonConstants.AXIS_NUMBER];
            containerCoordinates = new int[containerImages.length][CommonConstants.AXIS_NUMBER];
            containerDimensions = new int[containerImages.length][CommonConstants.AXIS_NUMBER];
            dragLimits = new int[4];

            for (int i=0; i<seedsImages.length; i++) {
                JSONArray jsonCategoryImages = jsonSeedsImages.getJSONArray(i);
                seedsImages[i] = new String[jsonCategoryImages.length()];
                for (int j=0; j<seedsImages[i].length; j++) {
                    seedsImages[i][j] = jsonCategoryImages.getString(j);
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

                seedsCoordinates[i][0] = seedsImagesContainer.getLeft() + constraintView.getLeft() + constraintView.getWidth()/2;
                seedsCoordinates[i][1] = seedsImagesContainer.getTop() + constraintView.getTop() + constraintView.getHeight()/2;
                seedsDimensions[i][0] = constraintView.getWidth();
                seedsDimensions[i][1] = constraintView.getHeight();

            }

            loadImages(contentContainer, seedsImages);
        }

        ConstraintLayout seedsFinalContainer = (ConstraintLayout) gameParameters.findViewById(R.id.seeds_final_container);

        if (seedsFinalContainer != null) {
            View containerElement;
            Guideline seedsGuideline = (Guideline) gameParameters.findViewById(R.id.seeds_guideline);

            if (seedsGuideline != null) {
                for (int i=0; i<seedsFinalContainer.getChildCount(); i++) {
                    containerElement = seedsFinalContainer.getChildAt(i);

                    containerCoordinates[i][0] = containerElement.getLeft();
                    containerCoordinates[i][1] = seedsGuideline.getTop() + containerElement.getTop();
                    containerDimensions[i][0] = containerElement.getWidth();
                    containerDimensions[i][1] = containerElement.getHeight();
                }
            }

            loadContainerImages(seedsFinalContainer, containerImages);
        }
    }

    private void loadImages(RelativeLayout contentContainer, String[][] images) {
        /* Ocurrences array for generating random index, so the images have not the same order
           every time the child enter */
        int[][] occurrences = new int[images.length][];
        int[] categoryOccurrences = new int[images.length];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = new int[images[i].length];
            categoryOccurrences[i] = 0;
            for (int j=0; j<occurrences[i].length; j++) {
                occurrences[i][j] = 0;
            }
        }

        /* We ensure that at least one image of each category is displayed */
        for (int i=0; i<images.length; i++) {
            int randomImagesIndex = new Random().nextInt(images.length);
            int randomCategoryIndex = new Random().nextInt(images[0].length);

            while (categoryOccurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
            }
            categoryOccurrences[randomImagesIndex] = 1;
            occurrences[randomImagesIndex][randomCategoryIndex] = 1;

            occurrences[randomImagesIndex][randomCategoryIndex] = 1;
            loadImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }

        /* As images are chosen randomly between all the available ones, we cannot take the length of the
           images array as reference. Instead of that, we use a number defined in activities_details */
        for (int i=images.length; i<SeedsConstants.NUMBER_OF_SEEDS; i++) {
            int randomImagesIndex = new Random().nextInt(images.length);
            /* images index can be 0, as all the subarrays have always the same number of images */
            int randomCategoryIndex = new Random().nextInt(images[0].length);

            while (occurrences[randomImagesIndex][randomCategoryIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
                randomCategoryIndex = new Random().nextInt(images[0].length);
            }
            occurrences[randomImagesIndex][randomCategoryIndex] = 1;

            loadImage(contentContainer, images[randomImagesIndex][randomCategoryIndex], i, correction[randomImagesIndex]);
        }
    }

    private void loadContainerImages(ConstraintLayout contentContainer, String[] images) {
        int[] occurrences = new int[images.length];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = 0;
        }

        for (int i=0; i<images.length; i++) {
            int randomImagesIndex = new Random().nextInt(images.length);

            while (occurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(images.length);
            }
            occurrences[randomImagesIndex] = 1;

            ImageView imageView = (ImageView) contentContainer.getChildAt(i);
            loadContainerImage(imageView, images[randomImagesIndex], i);
        }
    }

    private void loadContainerImage(ImageView imageView, String imageName, int i) {
        imageView.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        String tag = String.valueOf(i);
        imageView.setTag(tag);
    }

    private void loadImage(RelativeLayout contentContainer, String imageName, int i, String correction) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));

        /* 'scaleFactor' is used to set the exact width and height to the ImageView, the same as the resource it will contain */
        Drawable drawable = image.getDrawable();
        float scaleFactor;
        if (seedsDimensions[i][0] < seedsDimensions[i][1]) {
            scaleFactor = (float)seedsDimensions[i][0]/(float)drawable.getIntrinsicWidth();
        }
        else {
            scaleFactor = (float)seedsDimensions[i][1]/(float)drawable.getIntrinsicHeight();
        }

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        seedsCoordinates[i][0] = seedsCoordinates[i][0] - width/2;
        seedsCoordinates[i][1] = seedsCoordinates[i][1] - height/2;
        image.setX(seedsCoordinates[i][0]);
        image.setY(seedsCoordinates[i][1]);
        String tag = i + "-" + correction;
        image.setTag(tag);

        TouchListener touchListener = new TouchListener(SeedsConstants.SEEDS_TYPE, this);
        image.setOnTouchListener(touchListener);

        contentContainer.addView(image);
    }

    protected boolean processTouchEvent(View view, MotionEvent event) {
        float left, right, top, bottom;

        LinearLayout headerText = (LinearLayout) gameParameters.findViewById(R.id.header_text);
        int headerTextHeight = 0;
        if (headerText != null)
            headerTextHeight = headerText.getHeight();

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
                /* We loop trought the number of containers */
                for (int i=0; i<containerCoordinates.length; i++) {
                    if ((viewCenterX > containerCoordinates[i][0])&&(viewCenterX < (containerCoordinates[i][0]+containerDimensions[i][0]))) {
                        if (viewCenterY > containerCoordinates[i][1]) {
                            insideColumnIndex = i;

                            String imageCategory = view.getTag().toString().split("-")[1];
                            correctAnswer = seedsChecker.check(gameParameters, imageCategory, correction[i]);

                            break;
                        }
                    }
                }

                if (correctAnswer) {
                    /* If the view is not completely inside the box, we move it */
                    if (view.getX() < containerCoordinates[insideColumnIndex][0]) {
                        view.setX(containerCoordinates[insideColumnIndex][0]);
                    }
                    else if ((view.getX()+view.getWidth()) > (containerCoordinates[insideColumnIndex][0]+containerDimensions[insideColumnIndex][0])) {
                        view.setX(containerCoordinates[insideColumnIndex][0]+containerDimensions[insideColumnIndex][0]-view.getWidth());
                    }

                    if (view.getY() < containerCoordinates[insideColumnIndex][1]) {
                        view.setY(containerCoordinates[insideColumnIndex][1]);
                    }
                    else if ((view.getY()+view.getHeight()) > (containerCoordinates[insideColumnIndex][1]+containerDimensions[insideColumnIndex][1])) {
                        view.setY(containerCoordinates[insideColumnIndex][1]+containerDimensions[insideColumnIndex][1]-view.getHeight());
                    }
                }
                else {
                    ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", view.getX(), seedsCoordinates[index][0]);
                    animX.setDuration(1000);
                    ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", view.getY(), seedsCoordinates[index][1]);
                    animY.setDuration(1000);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(animX).with(animY);
                    animatorSet.start();
                }
                break;
            default:
                break;
        }

        return true;
    }

}
