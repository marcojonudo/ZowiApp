package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.ZowiActions;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MusicConstants;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;

/**
 * Created by Marco on 24/01/2017.
 */
public class MusicActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private ImagesHandler imagesHandler;
    private String[] dictationsImages;

    public MusicActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        this.gameParameters = gameParameters;
        this.activityTitle = activityTitle;
        this.activityDetails = activityDetails;
        this.inflater = (LayoutInflater) gameParameters.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.MUSIC);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonDictationsImages = activityDetails.getJSONArray(MusicConstants.JSON_PARAMETER_IMAGES);
            dictationsImages = new String[jsonDictationsImages.length()];

            for (int i=0; i<dictationsImages.length; i++) {
                dictationsImages[i] = jsonDictationsImages.getString(i);
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
        LinearLayout musicActivityTemplate = (LinearLayout) inflater.inflate(R.layout.music_activity_template, contentContainer, false);

        for (int i=0; i<MusicConstants.NUMBER_OF_DICTATIONS; i++) {
            inflater.inflate(R.layout.dictation_template, musicActivityTemplate, true);
        }

        if (contentContainer != null) {
            contentContainer.addView(musicActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(MusicConstants.MUSIC_TYPE, contentContainer, this);
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

        imagesHandler.loadMusicSimpleImages(dictationsContainer, dictationsImages, MusicConstants.NUMBER_OF_DICTATIONS, dictationsImages.length);
    }

}
