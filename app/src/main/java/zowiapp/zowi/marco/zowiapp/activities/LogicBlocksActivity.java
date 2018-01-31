package zowiapp.zowi.marco.zowiapp.activities;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.LogicBlocksConstants;
import zowiapp.zowi.marco.zowiapp.checker.LogicBlocksChecker;
import zowiapp.zowi.marco.zowiapp.errors.NullElement;
import zowiapp.zowi.marco.zowiapp.listeners.LayoutListener;
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;
import zowiapp.zowi.marco.zowiapp.utils.Layout;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;

public class LogicBlocksActivity extends ActivityTemplate {

    private String correctZowiDirection, zowiDirection;
    private String[] nextZowiDirection;

    private static final int ZOWI_IMAGE_INDEX = 4;
    private static String SMALL_SHAPE_WITH_N = "pequeno";
    private static String SMALL_SHAPE_WITHOUT_N = "pequeño";

    public LogicBlocksActivity(GameParameters gameParameters, String activityTitle, JSONObject activityDetails) {
        initialiseCommonConstants(gameParameters, activityTitle, activityDetails);
        checker = new LogicBlocksChecker();
        imagesHandler = new ImagesHandler(gameParameters, this, ActivityType.LOGIC_BLOCKS);

        getParameters();
    }

    @Override
    protected void getParameters() {
        try {
            activityDescription = activityDetails.getString(CommonConstants.JSON_PARAMETER_DESCRIPTION);
            JSONArray jsonImages = activityDetails.getJSONArray(LogicBlocksConstants.JSON_PARAMETER_IMAGES);
            arrayImages = new String[jsonImages.length()];

            zowiDirection = "TOP";
            nextZowiDirection = new String[]{"LEFT", "TOP", "RIGHT", "BOTTOM"};

            for (int i = 0; i<arrayImages.length; i++) { arrayImages[i] = jsonImages.getString(i); }

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
        ConstraintLayout logicBlocksActivityTemplate = (ConstraintLayout) inflater.inflate(R.layout.free_logic_blocks_activity_template, contentContainer, false);
        ConstraintLayout grid = (ConstraintLayout) logicBlocksActivityTemplate.getChildAt(0);

        imagesHandler.loadSimpleImages(grid, grid.getChildCount(), arrayImages.length);

        int imageViewIndex = imagesHandler.generateSimpleRandomIndex(new ArrayList<Integer>(), grid.getChildCount(), true);
        while (imageViewIndex % 2 == 0)
            imageViewIndex = imagesHandler.generateSimpleRandomIndex(new ArrayList<Integer>(), grid.getChildCount(), true);

        ImageView chosenImageView = (ImageView) grid.getChildAt(imageViewIndex);
        String[] imageName = chosenImageView.getTag().toString().split(CommonConstants.TAG_SEPARATOR)[1].split("_");
        int correctImageIndex = Integer.parseInt(chosenImageView.getTag().toString().split(CommonConstants.TAG_SEPARATOR)[0]);

        /* Correct direction is defined for checking the activity later */
        switch (correctImageIndex) {
            case 1:
                correctZowiDirection = "TOP";
                break;
            case 3:
                correctZowiDirection = "LEFT";
                break;
            case 5:
                correctZowiDirection = "RIGHT";
                break;
            case 7:
                correctZowiDirection = "BOTTOM";
                break;
        }

        TextView description = (TextView) gameParameters.findViewById(R.id.activity_description);
        if (imageName[2].equals(SMALL_SHAPE_WITH_N))
            imageName[2] = SMALL_SHAPE_WITHOUT_N;

        String descriptionText = "¡Ayuda a Zowi a encontrar el " + imageName[0] + " " + imageName[2] + " " + imageName[1] + "!";

        final ImageView zowi = (ImageView) grid.getChildAt(ZOWI_IMAGE_INDEX);

        if (description != null)
            description.setText(descriptionText);

        Button turnLeftButton = (Button) logicBlocksActivityTemplate.findViewById(R.id.logic_blocks_left_button);
        Button turnRightButton = (Button) logicBlocksActivityTemplate.findViewById(R.id.logic_blocks_right_button);
        if (turnLeftButton != null && turnRightButton != null) {
            turnLeftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String actualDirection = zowiDirection;
                    zowiDirection = getNextZowiDirection(true);
                    Animations.rotateAnimation(zowi, actualDirection, zowiDirection);

                    ZowiActions.sendDataToZowi(ZowiActions.TURN_LEFT);
                }
            });
            turnRightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String actualDirection = zowiDirection;
                    zowiDirection = getNextZowiDirection(false);
                    Animations.rotateAnimation(zowi, actualDirection, zowiDirection);

                    ZowiActions.sendDataToZowi(ZowiActions.TURN_RIGHT);
                }
            });
        }

        if (contentContainer != null) {
            contentContainer.addView(logicBlocksActivityTemplate);

            LayoutListener layoutListener = new LayoutListener(ActivityType.LOGIC_BLOCKS, contentContainer, this);
            contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }
        else
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
    }

    protected void getElementsCoordinates() {
        RelativeLayout contentContainer = (RelativeLayout) gameParameters.findViewById(R.id.content_container);

        createCheckButton(contentContainer, false);
    }

    private void createCheckButton(ViewGroup contentContainer, boolean guidedActivity) {
        Button checkButton = Layout.createFloatingCheckButton(gameParameters, inflater, contentContainer, guidedActivity);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctAnswer = ((LogicBlocksChecker) checker).check(gameParameters, zowiDirection, correctZowiDirection);
                if (correctAnswer) {
                    ZowiActions.sendDataToZowi(ZowiActions.ZOWI_WALKS_FORWARD);

                    ZowiActions.sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
                    finishActivity(ActivityType.LOGIC_BLOCKS, false);
                }
            }
        });
    }

    private void reactToAnswer(boolean correctAnswer) {
        /* Get Zowi imageView */
        ImageView zowi = (ImageView) gameParameters.findViewById(R.id.logic_blocks_cell_5);

        /* Load a different image if answer is correct or not */
        if (zowi != null) {
            int resourceId = gameParameters.getResources().getIdentifier(correctAnswer ? LogicBlocksConstants.ZOWI_HAPPY : LogicBlocksConstants.ZOWI_SAD, CommonConstants.DRAWABLE, gameParameters.getPackageName());
            Picasso.with(gameParameters).load(resourceId).fit().centerInside().memoryPolicy(MemoryPolicy.NO_CACHE).into(zowi);
        }
    }

    private String getNextZowiDirection(boolean turnLeft) {
        int currentDirectionIndex = 1;
        for (int i=0; i<nextZowiDirection.length; i++) {
            if (nextZowiDirection[i].equals(zowiDirection))
                currentDirectionIndex = i;
        }

        currentDirectionIndex = currentDirectionIndex == 0 ? (currentDirectionIndex + nextZowiDirection.length + (turnLeft ? -1 : 1))%4 : (currentDirectionIndex + (turnLeft ? -1 : 1))%4;
        return nextZowiDirection[currentDirectionIndex];
    }

}
