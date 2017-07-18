package zowiapp.zowi.marco.zowiapp.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Random;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.MusicActivity;
import zowiapp.zowi.marco.zowiapp.activities.PuzzleActivity;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

/**
 * Created by Marco on 26/05/2017.
 */

public class ImagesHandler {

    private Context context;
    private ActivityTemplate activityTemplate;
    private ActivityType activityType;

    public ImagesHandler(Context context, ActivityTemplate activityTemplate, ActivityType activityType) {
        this.context = context;
        this.activityTemplate = activityTemplate;
        this.activityType = activityType;
    }

    public int generateSimpleRandomIndex(ArrayList<Integer> arrayList, int limit, boolean insert) {
        int n = new Random().nextInt(limit);

        n = arrayList.contains(n) ? generateSimpleRandomIndex(arrayList, limit, false) : n;
        if (insert)
            arrayList.add(n);

        return n;
    }

    private int[] generateCategoriesRandomIndex(ArrayList<ArrayList<Integer>> arrayList, int categoryLimit, int individualCategoryLimit, int imageLimit, boolean insert) {
        Random random = new Random();
        int categoryIndex = random.nextInt(categoryLimit);
        int imageIndex = random.nextInt(imageLimit);

        if (categoryIndex == individualCategoryLimit && arrayList.get(individualCategoryLimit).size() > 0)
            generateCategoriesRandomIndex(arrayList, categoryLimit, individualCategoryLimit, imageLimit, false);

        /* The total number of elements is calculated to determine if at least one image of each category has been displayed */
        int totalElements = 0;
        for (ArrayList<Integer> category: arrayList)
            totalElements += category.size();

        /* If a category is not empty and total elements is lower than the total number of images, we ensure that other empty category images
           are displayed before it */
        if (!arrayList.get(categoryIndex).isEmpty() && totalElements < arrayList.size()) {
            int[] indexes = generateCategoriesRandomIndex(arrayList, categoryLimit, individualCategoryLimit, imageLimit, false);
            categoryIndex = indexes[0];
            imageIndex = indexes[1];
        }

        if (arrayList.get(categoryIndex).contains(imageIndex)) {
            int[] indexes = generateCategoriesRandomIndex(arrayList, categoryLimit, individualCategoryLimit, imageLimit, false);
            categoryIndex = indexes[0];
            imageIndex = indexes[1];
        }

        if (insert)
            arrayList.get(categoryIndex).add(imageIndex);

        return new int[]{categoryIndex, imageIndex};
    }

    public void loadPuzzleImages(ViewGroup contentContainer, String[] images, int imagesNumber, Point[] coordinates, Point[] dimensions, float[][] scaleFactorsToPuzzle, int randomShapeIndex, int puzzleContainerSide) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        Point[] correction = Functions.createEmptyPointArray(images.length);
        for (int i=0; i<imagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, images.length, true);

            correction[i] = coordinates[randomImagesIndex];
            loadPuzzleImage(contentContainer, images[randomImagesIndex], coordinates, dimensions, scaleFactorsToPuzzle, puzzleContainerSide, randomShapeIndex, randomImagesIndex, i);
        }

        ((PuzzleActivity) activityTemplate).setCorrection(correction);
    }

    public void loadCategoriesImages(ViewGroup contentContainer, String[][] images, int imagesNumber, int categoryOneImageIndex, Point[] coordinates, Point dimensions, String[] correction) {
        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();
        for (String[] category: images)
            arrayList.add(new ArrayList<Integer>());

        for (int i=0; i<imagesNumber; i++) {
            int[] indexes = generateCategoriesRandomIndex(arrayList, images.length, categoryOneImageIndex, images[0].length, true);
            loadImage(contentContainer, images[indexes[0]][indexes[1]], coordinates, dimensions, i, correction[indexes[0]]);
        }
    }

    public void loadSimpleImages(ViewGroup contentContainer, String[] images, int imagesNumber, int imagesLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = (ImageView) contentContainer.getChildAt(i);
            loadSimpleContainerImageView(imageView, images[randomImagesIndex], i);
        }
    }

    public void loadMusicSimpleImages(ViewGroup contentContainer, String[] images, int imagesNumber, int imagesLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        ImageView imageView;
        ConstraintLayout button;
        for (int i=0; i<imagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = (ImageView) ((ConstraintLayout)((ConstraintLayout) contentContainer.getChildAt(i)).getChildAt(0)).getChildAt(0);
            button = (ConstraintLayout) ((ConstraintLayout) contentContainer.getChildAt(i)).getChildAt(2);

            button.setTag(randomImagesIndex);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MusicActivity) activityTemplate).music(view);
                }
            });
            loadMusicSimpleImageView(imageView, images[randomImagesIndex]);
        }
    }

    public void loadLogicBlocksImages(ViewGroup contentContainer, String[] images, int imagesNumber, int imagesLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int imagesIndex = 0;
        int randomImagesIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            if (i%2 != 0 && i != 4) {
                randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

                imageView = (ImageView) contentContainer.getChildAt(i);
                loadSimpleLogicBlocksImageView(imageView, images[randomImagesIndex], imagesIndex);

                imagesIndex++;
            }
        }

        /* Get the center ImageView for displaying zowi_pointer image */
        imageView = (ImageView) contentContainer.getChildAt(4);
        loadMusicSimpleImageView(imageView, "zowi_pointer");
    }


    public void loadZowiEyesImages(ViewGroup contentContainer, String[][] images, int correctImagesNumber, int wrongImagesNumber, int imagesLimit, Point[] coordinates, Point[] imageViesCoordinates) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        ImageView imageView;
        for (int i=0; i<correctImagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = (ImageView) contentContainer.getChildAt(randomImagesIndex);
            loadZowiEyesImageView(imageView, images[0][i], i, randomImagesIndex, coordinates, imageViesCoordinates, 0);
        }
        for (int i=0; i<wrongImagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = (ImageView) contentContainer.getChildAt(randomImagesIndex);
            loadZowiEyesImageView(imageView, images[1][i], correctImagesNumber+i, randomImagesIndex, coordinates, imageViesCoordinates, 1);
        }
    }

    public void loadSimpleDoubleImages(ViewGroup imagesContainer, String[] images, int imagesNumber, int imagesLimit, int positionLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();
        ArrayList<Integer> positionArrayList = new ArrayList<>();

        int randomImagesIndex, randomPositionIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);
            for (int j=0; j<2; j++) {
                randomPositionIndex = generateSimpleRandomIndex(positionArrayList, positionLimit, true);

                imageView = (ImageView) ((FrameLayout) imagesContainer.getChildAt(randomPositionIndex)).getChildAt(1);
                loadSimpleContainerImageView(imageView, images[randomImagesIndex], i);
            }
        }
    }

    private void loadSimpleContainerImageView(ImageView imageView, String imageName, int i) {
        imageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
        imageView.setTag(i);
    }

    private void loadSimpleLogicBlocksImageView(ImageView imageView, String imageName, int imagesIndex) {
        imageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
        String tag = imagesIndex + "-" + imageName;
        imageView.setTag(tag);
    }

    private void loadMusicSimpleImageView(ImageView imageView, String imageName) {
        imageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
    }

    private void loadZowiEyesImageView(ImageView imageView, String imageName, int i, int randomImagesIndex, Point[] coordinates, Point[] imageViewsCoordinates, int correction) {
        imageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
        imageView.setTag(correction);

        coordinates[i].set(imageViewsCoordinates[randomImagesIndex].x, imageViewsCoordinates[randomImagesIndex].y);
    }

    private void loadImage(ViewGroup contentContainer, String imageName, Point[] coordinates, Point dimensions, int i, String correction) {
        ImageView image = new ImageView(context);
        image.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));

        /* 'scaleFactor' is used to set the exact width and height to the ImageView, the same as the resource it will contain */
        Drawable drawable = image.getDrawable();
        float xDimension = dimensions.x, yDimension = dimensions.y;
        float scaleFactor = xDimension < yDimension ? xDimension/(float)drawable.getIntrinsicWidth() : yDimension/(float)drawable.getIntrinsicHeight();

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        coordinates[i].x = coordinates[i].x - width/2;
        coordinates[i].y = coordinates[i].y - height/2;
        image.setX(coordinates[i].x);
        image.setY(coordinates[i].y);
        String tag = i + "-" + correction;
        image.setTag(tag);

        TouchListener touchListener = new TouchListener(activityType, activityTemplate);
        image.setOnTouchListener(touchListener);

        contentContainer.addView(image);
    }

    private void loadPuzzleImage(ViewGroup container, String imageName, Point[] coordinates, Point[] dimensions, float[][] scaleFactorsToPuzzle, int puzzleContainerSide, int randomShapeIndex, int randomIndex, int i) {
        ImageView image = new ImageView(context);
        image.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
//        image.setBackgroundColor(ContextCompat.getColor(context, R.color.red));

        Drawable drawable = image.getDrawable();
        float scaleFactor, scaleFactorToPuzzle;
        int width, height;
        if (dimensions[i].x < dimensions[i].y) {
            scaleFactor = (float)dimensions[i].x/(float)drawable.getIntrinsicWidth();
            width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
            height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
            scaleFactorToPuzzle = ((float)puzzleContainerSide*(float)PuzzleConstants.PIECES_TO_PUZZLE[randomShapeIndex][randomIndex][0])/(float)width;
            scaleFactorsToPuzzle[i][0] = scaleFactorToPuzzle;
        }
        else {
            scaleFactor = (float)dimensions[i].x/(float)drawable.getIntrinsicHeight();
            width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
            height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
            scaleFactorToPuzzle = ((float)puzzleContainerSide*(float) ActivityConstants.PuzzleConstants.PIECES_TO_PUZZLE[randomShapeIndex][randomIndex][1])/(float)height;
            scaleFactorsToPuzzle[i][1] = scaleFactorToPuzzle;
        }

        dimensions[i].x = width;
        dimensions[i].y = height;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        image.setLayoutParams(layoutParams);
        coordinates[i].x = coordinates[i].x - width/2;
        coordinates[i].y = coordinates[i].y - height/2;
        image.setX(coordinates[i].x);
        image.setY(coordinates[i].y);
        image.setTag(i);

        container.addView(image);

        TouchListener touchListener = new TouchListener(activityType, activityTemplate);
        image.setOnTouchListener(touchListener);
    }

}
