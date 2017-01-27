package zowiapp.zowi.marco.zowiapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
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
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GuideConstants;

/**
 * Created by Marco on 24/01/2017.
 */
public class GuideActivity extends ActivityTemplate {

    private GameParameters gameParameters;
    private LayoutInflater inflater;
    private String activityTitle, activityDescription;
    private JSONObject activityDetails;
    private String[] guideImages;


    public GuideActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
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
            JSONArray jsonGuideImages = activityDetails.getJSONArray(GuideConstants.JSON_PARAMETER_IMAGES);
            guideImages = new String[jsonGuideImages.length()];

            for (int i=0; i<guideImages.length; i++) {
                guideImages[i] = jsonGuideImages.getString(i);
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
        LinearLayout imagesContainer = (LinearLayout) guideActivityTemplate.findViewById(R.id.images_container);

        placeImages(imagesContainer, guideImages);

        if (contentContainer != null) {
            contentContainer.addView(guideActivityTemplate);
        }
    }

    private void placeImages(LinearLayout imagesContainer, String[] images) {
        for (int i=0; i<images.length; i++) {
            placeImage(imagesContainer, images[i], i);
        }
    }

    private void placeImage(LinearLayout container, String imageName, int i) {
        ImageView image = new ImageView(gameParameters);
        image.setImageResource(gameParameters.getResources().getIdentifier(imageName, "drawable", gameParameters.getPackageName()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1.0f;
        layoutParams.setMargins(GuideConstants.IMAGES_MARGIN, GuideConstants.IMAGES_MARGIN, GuideConstants.IMAGES_MARGIN, GuideConstants.IMAGES_MARGIN);
        image.setLayoutParams(layoutParams);
        image.setTag(i);

        container.addView(image);
    }

}
