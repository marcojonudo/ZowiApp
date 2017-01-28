package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.MusicConstants;

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
        LinearLayout dictationsContainer = (LinearLayout) musicActivityTemplate.findViewById(R.id.dictations_container);

        placeImages(dictationsContainer, dictationsImages);

        if (contentContainer != null) {
            contentContainer.addView(musicActivityTemplate);
        }
    }

    private void placeImages(LinearLayout imagesContainer, String[] images) {
        for (String image: images) {
            placeImage(imagesContainer, image);
        }
    }

    private void placeImage(LinearLayout container, String imageName) {
        LinearLayout dictationTemplate = (LinearLayout) inflater.inflate(R.layout.dictation_template, container, false);

        ImageView image = (ImageView) dictationTemplate.findViewById(R.id.dictation_image);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));

        container.addView(dictationTemplate);
    }

}
