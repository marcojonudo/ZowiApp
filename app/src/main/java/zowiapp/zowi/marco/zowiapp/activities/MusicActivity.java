package zowiapp.zowi.marco.zowiapp.activities;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MusicConstants;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

public class MusicActivity extends ActivityTemplate {

    public MusicActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        this.imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.MUSIC);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonDictationsImages = activityDetails.getJSONArray(MusicConstants.JSON_PARAMETER_IMAGES);
            arrayImages = new String[jsonDictationsImages.length()];

            for (int i = 0; i< arrayImages.length; i++) {
                arrayImages[i] = jsonDictationsImages.getString(i);
            }

            imagesHandler.init(arrayImages, null, CommonConstants.NON_REPEATED_IMAGES_CATEGORY_INDEX, null);
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
        LinearLayout musicActivityTemplate = (LinearLayout) inflater.inflate(R.layout.guided_music_activity_template, contentContainer, false);

        for (int i=0; i<MusicConstants.NUMBER_OF_DICTATIONS; i++) {
            inflater.inflate(R.layout.guided_music_dictation_template, musicActivityTemplate, true);
        }

        if (contentContainer != null) {
            contentContainer.addView(musicActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ActivityType.MUSIC, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
        else {
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
        }
    }

    public void music(View v) {
        int dictationNumber = Integer.parseInt(v.getTag().toString());
        String command = "M " + MusicConstants.DICTATION_PERIODS[dictationNumber];

        ZowiActions.sendDataToZowi(command);
    }

    protected void getElementsCoordinates() {
        LinearLayout dictationsContainer = (LinearLayout) gameParameters.findViewById(R.id.dictations_container);

        imagesHandler.loadSimpleImages(dictationsContainer, MusicConstants.NUMBER_OF_DICTATIONS, arrayImages.length);

        /* Images tags are loaded into buttons for identifying dictation when clicking */
        if (dictationsContainer != null) {
            for (int i=0; i<dictationsContainer.getChildCount(); i++) {
                ImageView imageView = (ImageView)((ConstraintLayout)((ConstraintLayout) dictationsContainer.getChildAt(0)).getChildAt(0)).getChildAt(0);
                dictationsContainer.getChildAt(2).setTag(imageView.getTag());
            }
        }
    }

}
