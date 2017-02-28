package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
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
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MusicConstants;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;

/**
 * Created by Marco on 24/01/2017.
 */
public class MusicActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String[] dictationsImages;

    public MusicActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
//        LinearLayout dictationsContainer = (LinearLayout) musicActivityTemplate.findViewById(R.id.dictations_container);

        for (int i=0; i<MusicConstants.NUMBER_OF_DICTATIONS; i++) {
            inflater.inflate(R.layout.dictation_template, musicActivityTemplate, true);
        }

//        placeImages(dictationsContainer, dictationsImages);

        if (contentContainer != null) {
            contentContainer.addView(musicActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(MusicConstants.MUSIC_TYPE, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);
        LinearLayout dictationsContainer = (LinearLayout) gameParameters.findViewById(R.id.dictations_container);

        loadImages(dictationsContainer, dictationsImages);
    }

    private void loadImages(LinearLayout dictationsContainer, String[] images) {
        int[] occurrences = new int[MusicConstants.NUMBER_OF_DICTATIONS];
        for (int i=0; i<occurrences.length; i++) {
            occurrences[i] = 0;
        }

        for (int i=0; i<MusicConstants.NUMBER_OF_DICTATIONS; i++) {
            ConstraintLayout dictationContainer = (ConstraintLayout) dictationsContainer.getChildAt(i);
            ConstraintLayout imageContainer = (ConstraintLayout) dictationContainer.getChildAt(0);
            ImageView image = (ImageView) imageContainer.getChildAt(0);

            int randomImagesIndex = new Random().nextInt(occurrences.length);

            while (occurrences[randomImagesIndex] == 1) {
                randomImagesIndex = new Random().nextInt(occurrences.length);
            }
            occurrences[randomImagesIndex] = 1;

            loadImage(image, images[randomImagesIndex], i);
        }
    }

    private void loadImage(ImageView imageView, String imageName, int i) {
        imageView.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
    }

}
