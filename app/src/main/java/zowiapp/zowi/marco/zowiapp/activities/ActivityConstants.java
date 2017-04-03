package zowiapp.zowi.marco.zowiapp.activities;

/**
 * Created by Marco on 23/01/2017.
 */
public class ActivityConstants {

    public static class CommonConstants {
        public static final String INTENT_PARAMETER_TITLE = "activityTitle";
        public static final String INTENT_PARAMETER_NUMBER = "activityNumber";
        public static final String JSON_PARAMETER_TYPE = "type";
        protected static final String JSON_PARAMETER_DESCRIPTION = "description";
        protected static final int AXIS_NUMBER = 2;
    }

    protected static class GridConstants {
        protected static final ActivityType GRID_TYPE = ActivityType.GRID;
        protected static final String JSON_PARAMETER_GRIDSIZE = "gridSize";
        protected static final String JSON_PARAMETER_CELLS = "cells";
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int COORDINATES_3X3_LENGTH = 9;
        protected static final int COORDINATES_4X4_LENGTH = 16;
        protected static final float CELL_FILLED_SPACE = 0.85f;
        protected static final String INNER = "INNER";
    }

    protected static class ColumnsConstants {
        protected static final ActivityType COLUMNS_TYPE = ActivityType.COLUMNS;
        protected static final String JSON_PARAMETER_LEFTTITLE = "leftTitle";
        protected static final String JSON_PARAMETER_RIGHTTITLE = "rightTitle";
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final String JSON_PARAMETER_CORRECTION = "correction";
        protected static final int NUMBER_OF_IMAGES = 5;
        protected static final int NUMBER_OF_COLUMNS_CORNERS = 2;
        protected static final int LEFT_COLUMN_INDEX = 0;
        protected static final int RIGHT_COLUMN_INDEX = 1;
        protected static final float CELL_FILLED_SPACE = 1;
    }

    protected static class OperationsConstants {
        protected static final String JSON_PARAMETER_OPERATIONSTYPE = "operationsType";
        protected static final String JSON_PARAMETER_IMAGE = "image";
        protected static final int NUMBER_OF_OPERATIONS = 6;
        protected static final String JSON_PARAMETER_OPERATIONSIMAGES = "operationsImages";
        protected static final int RANDOM_NUMBER_LIMIT = 10;
        protected static final String[] OPERATORS = {"+", "-"};
    }

    public static class PuzzleConstants {
        protected static final ActivityType PUZZLE_TYPE = ActivityType.PUZZLE;
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int PIECES_NUMBER = 5;
        //TODO Change puzzle dimensions
        protected static final double[][][] PIECES_TO_PUZZLE = {
            {{0.5, 0.5}, {0.5, 1}, {0.5, 0.5}, {0.5, 0.5}, {0.5, 0.5}},
            {{0.5, 0.5}, {0.5, 1}, {0.5, 0.5}, {0.5, 0.5}, {0.5, 0.5}},
            {{0.5, 0.5}, {0.5, 1}, {0.5, 0.5}, {0.5, 0.5}, {0.5, 0.5}},
    };
        public static final int DISTANCE_LIMIT = 100;
        protected static final double[][][] SHAPES_COORDINATES_FACTORS = {
                {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
                {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
                {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}}
        };
        //TODO This factors are correct, but I only use shape 1 for testing
//        protected static final double[][][] SHAPES_COORDINATES_FACTORS = {
//            {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
//            {{0, 0}, {0, 0}, {0.5, 0}, {0, 0.5}, {0.5, 0.5}},
//            {{0, 0}, {0.5, 0}, {0.5, 0}, {0.5, 0.5}, {0, 0.5}}
//        };
    }

    protected static class GuideConstants {
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int IMAGES_MARGIN = 35;
    }

    protected static class ColouredGridConstants {
        protected static final ActivityType COLOUREDGRID_TYPE = ActivityType.COLOURED_GRID;
        protected static final String JSON_PARAMETER_CELLS = "cells";
        protected static final String JSON_PARAMETER_COLOUREDCELLS = "colouredCells";
        protected static final int NUMBER_OF_COLORS = 3;
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int COORDINATES_4X4_LENGTH = 17;
    }

    protected static class MusicConstants {
        protected static final ActivityType MUSIC_TYPE = ActivityType.MUSIC;
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int NUMBER_OF_DICTATIONS = 3;
    }

    protected static class DragConstants {
        protected static final ActivityType DRAG_TYPE = ActivityType.DRAG;
        protected static final String JSON_PARAMETER_DRAGIMAGES = "dragImages";
        protected static final String JSON_PARAMETER_CONTAINERELEMENTS = "containerElements";
        protected static final String JSON_PARAMETER_DRAGIMAGESNUMBER = "dragImagesNumber";
        protected static final String JSON_PARAMETER_TEXTS = "texts";
        protected static final String JSON_PARAMETER_CONTAINERIMAGES = "containerImages";
        protected static final String JSON_PARAMETER_CORRECTION = "correction";
    }

    protected static class MemoryConstants {
        protected static final ActivityType MEMORY_TYPE = ActivityType.MEMORY;
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int NUMBER_OF_IMAGES = 4;
        protected static final int FLIP_DELAY = 5000;
    }

    protected static class FoodPyramidConstants {
        protected static final ActivityType FOODPYRAMID_TYPE = ActivityType.FOODPYRAMID;
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final String JSON_PARAMETER_IMAGESNUMBER = "imagesNumber";
        protected static final String JSON_PARAMETER_CORRECTION = "correction";
        protected static final int PYRAMID_STEPS = 4;
        protected static final int PYRAMID_COORDINATES_LENGTH = 6;
        protected static final int[][] PYRAMID_LIMITS_FACTORS = {
                {0, 2, 1, 1, 1, 1},
                {0, 0, 1, 2, 3, 4}
        };
        protected static final int DISTANCE_LIMIT = 75;
    }

    protected static class SeedsConstants {
        protected static final ActivityType SEEDS_TYPE = ActivityType.SEEDS;
        protected static final String JSON_PARAMETER_SEEDSIMAGES = "seedsImages";
        protected static final String JSON_PARAMETER_CONTAINERIMAGES = "containerImages";
        protected static final int NUMBER_OF_SEEDS = 16;
        protected static final int GUIDELINE_POSITION = 8;
        protected static final String JSON_PARAMETER_CORRECTION = "correction";
    }

    public static class ZowiEyesConstants {
        protected static final ActivityType ZOWI_EYES_TYPE = ActivityType.ZOWI_EYES;
        protected static final String JSON_PARAMETER_IMAGESNUMBER = "imagesNumber";
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int LAYOUT_IMAGES = 12;
        public static final int[][] DISTANCE_LIMITS= {
            {500, 10}, {350, 5}, {200, 3}, {100, 2}, {50, 1}
        };
    }
}
