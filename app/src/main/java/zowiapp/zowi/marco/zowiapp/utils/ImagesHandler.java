package zowiapp.zowi.marco.zowiapp.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import zowiapp.zowi.marco.zowiapp.R;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.LogicBlocksConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.ColouredGridConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GridConstants;
import zowiapp.zowi.marco.zowiapp.activities.ActivityTemplate;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.activities.MusicActivity;
import zowiapp.zowi.marco.zowiapp.activities.PuzzleActivity;
import zowiapp.zowi.marco.zowiapp.activities.SeedsActivity;
import zowiapp.zowi.marco.zowiapp.listeners.TouchListener;

public class ImagesHandler {

    private Context context;
    private ActivityTemplate activityTemplate;
    private ActivityType activityType;
    private String[] arrayImages, correction;
    private String[][] doubleArrayImages;
    private int oneImageCategoryIndex;

    private static final String SEPARATOR = "-";
    private static final String CONTAINER = "CONTAINER";
    private static final int UNUSED_INDEX = -1;

    // region INITIALIZATION FUNCTIONS

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

    // endregion

    // region RANDOM INDEX GENERATION FUNCTIONS

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

    // endregion

    // region IMAGES LOAD LOGIC FUNCTIONS

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

    public void loadGuideImages(ViewGroup contentContainer, int imagesNumber) {
        ArrayList<ArrayList<Integer>> arrayList = new ArrayList<>();
        for (String[] category: doubleArrayImages)
            arrayList.add(new ArrayList<Integer>());

        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            /* Two random indexes are generated, one for the category and another one for the image */
            int[] indexes = generateCategoriesRandomIndex(arrayList, doubleArrayImages.length, oneImageCategoryIndex, doubleArrayImages[0].length, true);
            imageView = (ImageView) contentContainer.getChildAt(i);
            loadSimpleImage(imageView, doubleArrayImages[indexes[0]][indexes[1]], i, UNUSED_INDEX, indexes[0], correction[indexes[0]]);
        }
    }

    public void loadSimpleImages(ViewGroup contentContainer, int imagesNumber, int imagesLimit) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();
        String[] newCorrection = correction != null ? new String[correction.length] : null;

        int randomImagesIndex;
        ImageView imageView;
        for (int i=0; i<imagesNumber; i++) {
            if (checkCondition(i)) {
                randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

                imageView = getImageView(contentContainer, i);
                customAction(contentContainer, i, randomImagesIndex, newCorrection);
                loadSimpleImage(imageView, arrayImages[randomImagesIndex], i, UNUSED_INDEX, randomImagesIndex, correction != null ? correction[randomImagesIndex] : null);
            }
        }

        finalAction(contentContainer, newCorrection);
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

    private void customAction(ViewGroup contentContainer, int index, int randomImagesIndex, String[] newCorrection) {
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
            case SEEDS:
                newCorrection[index] = correction[randomImagesIndex];
                break;
            default:
                break;
        }
    }

    private void finalAction(ViewGroup contentContainer, String[] newCorrection) {
        switch (activityType) {
            case LOGIC_BLOCKS:
                /* Get the center ImageView for displaying grid_zowi_pointer image */
                ImageView imageView = (ImageView) contentContainer.getChildAt(4);
                loadSimpleImage(imageView, LogicBlocksConstants.ZOWI_POINTER, UNUSED_INDEX, UNUSED_INDEX, UNUSED_INDEX, null);
                break;
            case SEEDS:
                ((SeedsActivity) activityTemplate).setCorrection(newCorrection);
                break;
        }
    }

    public void loadGridImages(ViewGroup contentContainer, Point[] imagesCoordinates, Point dimensions, int[] cells, String[] images) {
        /* 'cells' contains a number between 1 and 9 or 16 that indicates the cells that will contain an image */
        /* 'arrayImages' contains the name of the resources */
        Point[] coordinates = Functions.createEmptyPointArray(images.length);
        for (int i=0; i<coordinates.length; i++) {
            coordinates[i].set(imagesCoordinates[cells[i]-1].x, imagesCoordinates[cells[i]-1].y);
        }
        for (int i=0; i<cells.length; i++) {
            loadImage(contentContainer, images[i], coordinates, dimensions, i, null);
        }
    }

    public void loadZowiEyesImages(ViewGroup contentContainer, int[] imagesNumber, int imagesLimit, Point[] coordinates, Point[] imageViewsCoordinates) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex, index;
        ImageView imageView;
        for (index=0; index<imagesNumber[0]; index++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = getImageView(contentContainer, randomImagesIndex);
            loadSimpleImage(imageView, doubleArrayImages[0][index], index, UNUSED_INDEX, UNUSED_INDEX, "0");

            coordinates[index].set(imageViewsCoordinates[randomImagesIndex].x, imageViewsCoordinates[randomImagesIndex].y);
        }
        for (int j=index; j<index+imagesNumber[1]; j++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, imagesLimit, true);

            imageView = getImageView(contentContainer, randomImagesIndex);
            loadSimpleImage(imageView, doubleArrayImages[1][j-index], imagesNumber[0]+j, UNUSED_INDEX, UNUSED_INDEX, "1");

            coordinates[j].set(imageViewsCoordinates[randomImagesIndex].x, imageViewsCoordinates[randomImagesIndex].y);
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
                loadSimpleImage(imageView, arrayImages[randomImagesIndex], randomImagesIndex, randomPositionIndex, UNUSED_INDEX, null);
            }
        }
    }

    private void fixDimensions(String image, Point[] dimensions, int index) {
        int resourceId = context.getResources().getIdentifier(image, CommonConstants.DRAWABLE, context.getPackageName());

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resourceId, bitmapOptions);
        int bitmapHeight = bitmapOptions.outHeight;
        int bitmapWidth =  bitmapOptions.outWidth;

        float scaleFactor;
        if (dimensions[index].x < dimensions[index].y) {
            if (bitmapHeight < dimensions[index].y)
                scaleFactor = (float)dimensions[index].x/(float)bitmapWidth;
            else
                scaleFactor = (float)dimensions[index].y/(float)bitmapHeight;
        }
        else {
            if (bitmapWidth < dimensions[index].x && (dimensions[index].x - bitmapWidth) > 100)
                scaleFactor = (float)dimensions[index].y/(float)bitmapHeight;
            else
                scaleFactor = (float)dimensions[index].x/(float)bitmapWidth;
        }

        /* ImageView dimensions are scale to fit the content (and to avoid problems with correction later) */
        int width = (int)(bitmapWidth*scaleFactor);
        int height = (int)(bitmapHeight*scaleFactor);

        dimensions[index].set(width, height);
    }

    public void loadPuzzleImages(ViewGroup contentContainer, String[] images, int imagesNumber, Point[] puzzleCoordinates, Point[] coordinates, Point[] dimensions, float[] scaleFactorsToPuzzle, double[][] piecesToPuzzle, double[][] piecesToCorrection, int puzzleContainerSide) {
        ArrayList<Integer> imagesArrayList = new ArrayList<>();

        int randomImagesIndex;
        Point[] correction = Functions.createEmptyPointArray(images.length);
        for (int i=0; i<imagesNumber; i++) {
            randomImagesIndex = generateSimpleRandomIndex(imagesArrayList, images.length, true);

            correction[i] = puzzleCoordinates[randomImagesIndex];

            fixDimensions(images[randomImagesIndex], dimensions, i);
            loadPuzzleImage(contentContainer, images[randomImagesIndex], coordinates, dimensions, scaleFactorsToPuzzle, puzzleContainerSide, piecesToPuzzle, randomImagesIndex, i);
        }

        ((PuzzleActivity) activityTemplate).setCorrection(correction);
    }

    public void loadColouredGridImages(ViewGroup contentContainer, String[] images, Point[] coordinates, Point dimensions) {
        for (int i=0; i<images.length; i++) {
            loadImage(contentContainer, images[i], coordinates, dimensions, i, null);
        }
    }

    // endregion

    // region IMAGES LOAD FUNCTIONS

    private void loadImage(ViewGroup contentContainer, String imageName, Point[] coordinates, Point dimensions, int index, String correction) {
        ImageView imageView = new ImageView(context);
        /* ImageVew is resized to the final size based on the container (grid child) dimensions */
        resizeImageView(imageView, dimensions, coordinates, index);
        /* The resource is loaded into the already resized ImageView */
        int resourceId = context.getResources().getIdentifier(imageName, CommonConstants.DRAWABLE, context.getPackageName());
        Picasso.with(context).load(resourceId).fit().centerInside().memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);

        setTag(imageView, index, UNUSED_INDEX, UNUSED_INDEX, correction, null);
        loadTouchListener(imageView);
        contentContainer.addView(imageView);
    }

    private void loadSimpleImage(ImageView imageView, String imageName, int index, int secondIndex, int randomImageIndex, String correction) {
        int resourceId = context.getResources().getIdentifier(imageName, CommonConstants.DRAWABLE, context.getPackageName());
        Picasso.with(context).load(resourceId).fit().centerInside().memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        setTag(imageView, index, secondIndex, randomImageIndex, correction, imageName);

        loadTouchListener(imageView);
    }

    private void setTag(ImageView imageView, int index, int secondIndex, int randomImageIndex, String correction, String imageName) {
        String tag;

        switch (activityType) {
            case COLUMNS:
                tag = index + SEPARATOR + correction;
                break;
            case MEMORY:
                tag = String.valueOf(index) + SEPARATOR + String.valueOf(secondIndex);
                break;
            case LOGIC_BLOCKS:
                tag = index + SEPARATOR + imageName;
                break;
            case ZOWI_EYES:
                tag = correction;
                break;
            case COLOURED_GRID:
                tag = "";
                break;
            case GRID:
                tag = "zowi" + SEPARATOR + index;
                break;
            case MUSIC:
                tag = String.valueOf(randomImageIndex);
                break;
            case SEEDS:
                if (imageView.getTag() != null && imageView.getTag().equals(CONTAINER))
                    tag = imageView.getTag().toString();
                else
                    tag = index + SEPARATOR + correction;
                break;
            case GUIDE:
                tag = correction;
                break;
            default:
                tag = index + SEPARATOR + correction;
                break;
        }

        imageView.setTag(tag);
    }

    private void loadTouchListener(ImageView imageView) {
        TouchListener touchListener;
        switch (activityType) {
            case MEMORY:
                touchListener = new TouchListener(activityType, activityTemplate);
                imageView.setOnTouchListener(touchListener);
                break;
            case SEEDS:
                if (!imageView.getTag().equals(CONTAINER)) {
                    touchListener = new TouchListener(activityType, activityTemplate);
                    imageView.setOnTouchListener(touchListener);
                }
                break;
            case FOODPYRAMID:
                touchListener = new TouchListener(activityType, activityTemplate);
                imageView.setOnTouchListener(touchListener);
                break;
            case PUZZLE:
                touchListener = new TouchListener(activityType, activityTemplate);
                imageView.setOnTouchListener(touchListener);
                break;
            case COLUMNS:
                touchListener = new TouchListener(activityType, activityTemplate);
                imageView.setOnTouchListener(touchListener);
                break;
            case DRAG:
                touchListener = new TouchListener(activityType, activityTemplate);
                imageView.setOnTouchListener(touchListener);
                break;
            default:
                break;
        }
    }

    private void resizeImageView(ImageView imageView, Point dimensions, Point[] coordinates, int index) {
        int width, height;

        switch (activityType) {
            case GRID:
                width = (int)(dimensions.x * GridConstants.CELL_FILLED_SPACE);
                height = (int)(dimensions.y * GridConstants.CELL_FILLED_SPACE);
                break;
            case COLOURED_GRID:
                width = (int)(dimensions.x * ColouredGridConstants.CELL_FILLED_SPACE);
                height = (int)(dimensions.y * ColouredGridConstants.CELL_FILLED_SPACE);
                break;
            default:
                width = dimensions.x;
                height = dimensions.y;
                break;
        }

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        imageView.setLayoutParams(layoutParams);
        coordinates[index].x = coordinates[index].x - width/2;
        coordinates[index].y = coordinates[index].y - height/2;
        imageView.setX(coordinates[index].x);
        imageView.setY(coordinates[index].y);
    }

    private void loadPuzzleImage(final ViewGroup container, final String imageName, final Point[] coordinates, final Point[] dimensions, final float[] scaleFactorsToPuzzle, final int puzzleContainerSide, final double[][] piecesToPuzzle, final int randomImagesIndex, final int index) {
        final ImageView image = new ImageView(context);

        resizeImageView(image, dimensions[index], coordinates, index);
        int resourceId = context.getResources().getIdentifier(imageName, CommonConstants.DRAWABLE, context.getPackageName());
        Picasso.with(context).load(resourceId).fit().centerInside().memoryPolicy(MemoryPolicy.NO_CACHE).into(image, new Callback() {
            @Override
            public void onSuccess() {
                float scaleFactorToPuzzle = dimensions[index].x > dimensions[index].y ?
                        ((float)puzzleContainerSide*(float)piecesToPuzzle[randomImagesIndex][0])/(float)dimensions[index].x :
                        ((float)puzzleContainerSide*(float)piecesToPuzzle[randomImagesIndex][1])/(float)dimensions[index].y;

                scaleFactorsToPuzzle[index] = scaleFactorToPuzzle;
            }

            @Override
            public void onError() {}
        });
        image.setTag(index);
        loadTouchListener(image);

        container.addView(image);

    }

    // endregion

}
