package zowiapp.zowi.marco.zowiapp.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ZowiEyesConstants;
import zowiapp.zowi.marco.zowiapp.checker.ZowiEyesChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.Functions;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class ZowiEyesActivity extends ActivityTemplate {

    private int[] imagesNumber;

    public ZowiEyesActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.ZOWI_EYES);
        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(ZowiEyesConstants.JSON_PARAMETER_IMAGES);
            JSONArray jsonImagesNumber = activityDetails.getJSONArray(ZowiEyesConstants.JSON_PARAMETER_IMAGESNUMBER);
            doubleArrayImages = new String[jsonImages.length()][];
            imagesNumber = new int[jsonImagesNumber.length()];
            containerCoordinates = Functions.createEmptyPointArray(ZowiEyesConstants.LAYOUT_IMAGES);
            for (int i = 0; i< doubleArrayImages.length; i++) {
                JSONArray jsonCategoryImages = jsonImages.getJSONArray(i);
                doubleArrayImages[i] = new String[jsonCategoryImages.length()];
                for (int j = 0; j< doubleArrayImages[i].length; j++) {
                    doubleArrayImages[i][j] = jsonCategoryImages.getString(j);
                }

                imagesNumber[i] = jsonImagesNumber.getInt(i);
            }
            imagesCoordinates = Functions.createEmptyPointArray(imagesNumber[0]+imagesNumber[1]);

            imagesHandler.init(null, doubleArrayImages, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, null);
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
            inflater.inflate(R.layout.guided_zowi_eyes_activity_template, contentContainer, true);

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

                containerCoordinates[i].set(imageView.getLeft() + imageView.getWidth()/2, zowiEyesContainer.getTop() + imageView.getTop() + imageView.getHeight()/2);
            }

            if (contentContainer != null) {
                ConstraintLayout constrainContainer = (ConstraintLayout) contentContainer.getChildAt(0);

                imagesHandler.loadZowiEyesImages(constrainContainer, imagesNumber, ZowiEyesConstants.LAYOUT_IMAGES, imagesCoordinates, containerCoordinates);
            }

            for (int i =0; i<containerCoordinates.length; i++) {
                ConstraintLayout constrainContainer = (ConstraintLayout) contentContainer.getChildAt(0);

                View v = new View(gameParameters);
                ViewGroup.LayoutParams l = new ViewGroup.LayoutParams(5,5);
                v.setLayoutParams(l);
                v.setBackgroundColor(ContextCompat.getColor(gameParameters, R.color.red));
                v.setX(containerCoordinates[i].x);
                v.setY(containerCoordinates[i].y);

                constrainContainer.addView(v);
            }
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "zowiEyesContainer");
        }

        ZowiEyesChecker.setImagesCoordinates(imagesCoordinates);
    }

}
