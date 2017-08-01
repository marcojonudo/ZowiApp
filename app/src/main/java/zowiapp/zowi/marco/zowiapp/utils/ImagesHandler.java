package zowiapp.zowi.marco.zowiapp.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
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
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.PuzzleConstants;

public class ImagesHandler {

    private Context context;
    private ActivityTemplate activityTemplate;
    private ActivityType activityType;
    private String[] arrayImages, correction;
    private String[][] doubleArrayImages;
    private int oneImageCategoryIndex;

    private static final String SEPARATOR = "-";

    /*---------- INITIALIZATION FUNCTIONS ----------*/

    public ImagesHandler(Context context, ActivityTemplate activityTemplate, ActivityType activityType) {
        this.context = context;
        this.activityTemplate = activityTemplate;
        this.activityType = activityType;
    }

    public void init(String[] arrayImages, String[][] doubleArrayImages, int oneImageCategoryIndex, String[] correction) {
        this.arrayImages = arrayImages;
        this.doubleArrayImages = doubleArrayImages;
        this.oneImageCategoryIndex = oneImageCategoryIndex;
        this.correction = correction;
    }

    /*---------- RANDOM INDEX GENERATION FUNCTIONS ----------*/

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

    /*---------- IMAGES LOAD LOGIC FUNCTIONS ----------*/

    public void loadCategoriesImages(ViewGroup contentContainer, int imagesNumber, Point[] coordinates, Point dimensions) {
        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();
        for (String[] category: doubleArrayImages)
            arrayList.add(new ArrayList<Integer>());

        for (int i=0; i<imagesNumber; i++) {
            /* Two random indexes are generated, one for the category and another one for the image */
            int[] indexes = generateCategoriesRandomIndex(arrayList, doubleArrayImages.length, oneImageCategoryIndex, doubleArrayImages[0].length, true);
            loadImage(contentContainer, doubleArrayImages[indexes[0]][indexes[1]], coordinates, dimensions, i, correction[indexes[0]]);
        }
    }

    private ImageView getImageView(ViewGroup contentContainer, int index) {
        ImageView imageView;
        switch (activityType) {
            case MUSIC:
                imageView = (ImageView) ((ConstraintLayout)((ConstraintLayout) contentContainer.getChildAt(index)).getChildAt(0)).getChildAt(0);
                break;
            case MEMORY:
                imageView = (ImageView) ((FrameLayout) contentContainer.getChildAt(index)).getChildAt(1);
                break;
            default:
                imageView = (ImageView) contentContainer.getChildAt(index);
                break;
        }

        return imageView;
    }

    public void loadSimpleImages(ViewGroup contentContainer, int imagesNumber, int imagesLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            if (checkCondition(i)) {
                randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

                imageView = getImageView(contentContainer, i);
                customAction(contentContainer, i, randomImagesIndex);
                loadSimpleImage(imageView, arrayImages[randomImagesIndex], i, correction != null ? correction[i] : null);
            }
        }

        finalAction(contentContainer);
    }

    private boolean checkCondition(int index) {
        boolean passedCondition = false;

        switch (activityType) {
            case LOGIC_BLOCKS:
                if (index%2 != 0 && index != 4)
                    passedCondition = true;
                break;
            default:
                passedCondition = true;
        }

        return passedCondition;
    }

    private void customAction(ViewGroup contentContainer, int index, int randomImagesIndex) {
        switch (activityType) {
            case MUSIC:
                ConstraintLayout button = (ConstraintLayout) ((ConstraintLayout) contentContainer.getChildAt(index)).getChildAt(2);

                button.setTag(randomImagesIndex);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MusicActivity) activityTemplate).music(view);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void finalAction(ViewGroup contentContainer) {
        switch (activityType) {
            case LOGIC_BLOCKS:
                /* Get the center ImageView for displaying zowi_pointer image */
                ImageView imageView = (ImageView) contentContainer.getChildAt(4);
                loadSimpleImage(imageView, "zowi_pointer", -1, null);
                break;
        }
    }

    public void loadZowiEyesImages(ViewGroup contentContainer, int[] imagesNumber, int imagesLimit, Point[] coordinates, Point[] imageViewsCoordinates) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber[0]; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = getImageView(contentContainer, i);
            loadSimpleImage(imageView, doubleArrayImages[0][i], i, "0");

            coordinates[i].set(imageViewsCoordinates[randomImagesIndex].x, imageViewsCoordinates[randomImagesIndex].y);
        }
        for (int i=0; i<imagesNumber[1]; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = getImageView(contentContainer, i);
            loadSimpleImage(imageView, doubleArrayImages[1][i], imagesNumber[0]+i, "1");

            coordinates[i].set(imageViewsCoordinates[randomImagesIndex].x, imageViewsCoordinates[randomImagesIndex].y);
        }
    }

    public void loadMemoryImages(ViewGroup imagesContainer, int imagesNumber, int imagesLimit, int positionLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();
        ArrayList<Integer> positionArrayList = new ArrayList<>();

        int randomImagesIndex, randomPositionIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);
            for (int j=0; j<2; j++) {
                randomPositionIndex = generateSimpleRandomIndex(positionArrayList, positionLimit, true);

                imageView = getImageView(imagesContainer, randomPositionIndex);
                loadSimpleImage(imageView, arrayImages[randomImagesIndex], i, null);
            }
        }
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

    /*---------- IMAGES LOAD FUNCTIONS ----------*/

    private void setTag(ImageView imageView, int index, String correction, String imageName) {
        String tag;

        switch (activityType) {
            case COLUMNS:
                tag = index + SEPARATOR + correction;
                break;
            case MEMORY:
                tag = String.valueOf(index);
                break;
            case LOGIC_BLOCKS:
                tag = index + SEPARATOR + imageName;
                break;
            case ZOWI_EYES:
                tag = correction;
                break;
            default:
                tag = index + SEPARATOR + correction;
                break;
        }

        imageView.setTag(tag);
    }

    private void loadImage(ViewGroup contentContainer, String imageName, Point[] coordinates, Point dimensions, int index, String correction) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));

        resizeImageView(imageView, dimensions, coordinates, index);
        setTag(imageView, index, correction, null);

        TouchListener touchListener = new TouchListener(activityType, activityTemplate);
        imageView.setOnTouchListener(touchListener);

        contentContainer.addView(imageView);
    }

    private void loadSimpleImage(ImageView imageView, String imageName, int index, String correction) {
        imageView.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
        setTag(imageView, index, correction, imageName);
    }

    private void resizeImageView(ImageView imageView, Point dimensions, Point[] coordinates, int index) {
        /* 'scaleFactor' is used to set the exact width and height to the ImageView, the same as the resource it will contain */
        Drawable drawable = imageView.getDrawable();
        float xDimension = dimensions.x, yDimension = dimensions.y;
        float scaleFactor = xDimension < yDimension ? xDimension/(float)drawable.getIntrinsicWidth() : yDimension/(float)drawable.getIntrinsicHeight();

        int width = (int)(drawable.getIntrinsicWidth() * scaleFactor);
        int height = (int)(drawable.getIntrinsicHeight() * scaleFactor);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        imageView.setLayoutParams(layoutParams);
        coordinates[index].x = coordinates[index].x - width/2;
        coordinates[index].y = coordinates[index].y - height/2;
        imageView.setX(coordinates[index].x);
        imageView.setY(coordinates[index].y);
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
