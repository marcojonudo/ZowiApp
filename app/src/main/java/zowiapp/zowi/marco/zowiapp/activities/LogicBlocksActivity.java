package zowiapp.zowi.marco.zowiapp.activities;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import zowiapp.zowi.marco.zowiapp.utils.Animations;
import zowiapp.zowi.marco.zowiapp.utils.ImagesHandler;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiSocket;

public class LogicBlocksActivity extends ActivityTemplate {

    private int correctImageIndex, state;
    private boolean killThread, checkAnswers, correctAnswer;
    private String zowiDirection;
    private String[] nextZowiDirection;

    private static final int WAITING_ZOWI_MOVES = 0;
    private static final int ZOWI_HAS_MOVED = 1;
    private static final int ZOWI_IMAGE_INDEX = 4;

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
        correctImageIndex = Integer.parseInt(chosenImageView.getTag().toString().split(CommonConstants.TAG_SEPARATOR)[0]);

        TextView description = (TextView) gameParameters.findViewById(R.id.activity_description);
        String descriptionText = "¡Escoge el " + imageName[0] + " " + imageName[2] + " de color " + imageName[1] + "!";

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
                }
            });
            turnRightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String actualDirection = zowiDirection;
                    zowiDirection = getNextZowiDirection(false);
                    Animations.rotateAnimation(zowi, actualDirection, zowiDirection);
                }
            });
        }

        if (contentContainer != null) {
            contentContainer.addView(logicBlocksActivityTemplate);

            //TODO Estudiar implementacion con Zowi
//            new Thread(new Runnable() {
//                public void run() {
//                    while (!Thread.currentThread().isInterrupted() && !killThread) {
//                        int bytesAvailable = ZowiSocket.isInputStreamAvailable();
//                        if (bytesAvailable > 0) {
//                            byte[] packetBytes = new byte[64];
//                            ZowiSocket.readInputStream(packetBytes);
//
//                            String receivedText = new String(packetBytes, 0, bytesAvailable);
//                            /* sendFinalAck from Zowi sends an 'F' as response to ZOWI_CHECKS_ANSWERS */
//                            if (receivedText.contains("F") && state == WAITING_ZOWI_MOVES) {
//                                state = ZOWI_HAS_MOVED;
//                            }
//                            else if (receivedText.contains("F") && state == ZOWI_HAS_MOVED) {
//                                int imageIndex;
//                                if (receivedText.contains("AD")) {
//                                    imageIndex = 1;
//                                    Animations.rotateAndTranslate(zowi, 0);
//                                }
//                                else if (receivedText.contains("IZ")) {
//                                    imageIndex = 3;
//                                    Animations.rotateAndTranslate(zowi, -90);
//                                }
//                                else if (receivedText.contains("DE")) {
//                                    imageIndex = 5;
//                                    Animations.rotateAndTranslate(zowi, 90);
//                                }
//                                else if (receivedText.contains("AT")) {
//                                    imageIndex = 7;
//                                    Animations.rotateAndTranslate(zowi, 180);
//                                }
//                                else
//                                    imageIndex = -1;
//                                killThread = true;
//                                state = WAITING_ZOWI_MOVES;
//
//                                correctAnswer = ((LogicBlocksChecker) checker).check(gameParameters, imageIndex, correctImageIndex);
//
//                                reactToAnswer(correctAnswer);
//                            }
//
//                        }
//                    }
//                    checkAnswers = false;
//                    killThread = false;
//                }
//            }).start();
        }
        else
            new NullElement(gameParameters, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName(), "contentContainer");
    }

    private void reactToAnswer(boolean correctAnswer) {
        /* Get Zowi imageView */
        ImageView zowi = (ImageView) gameParameters.findViewById(R.id.logic_blocks_cell_5);

        /* Load a different image if answer is correct or not */
        if (zowi != null) {
            int resourceId = gameParameters.getResources().getIdentifier(correctAnswer ? LogicBlocksConstants.ZOWI_HAPPY : LogicBlocksConstants.ZOWI_SAD, CommonConstants.DRAWABLE, gameParameters.getPackageName());
            Picasso.with(gameParameters).load(resourceId).into(zowi);
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
