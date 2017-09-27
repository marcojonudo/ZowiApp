package zowiapp.zowi.marco.zowiapp.activities;

public class ActivityConstants {

    public static class CommonConstants {
        public static final String INTENT_PARAMETER_TYPE = "categoryType";
        public static final String INTENT_PARAMETER_TITLE = "activityTitle";
        public static final String INTENT_PARAMETER_NUMBER = "activityNumber";
        public static final String JSON_PARAMETER_TYPE = "type";
        static final String JSON_PARAMETER_DESCRIPTION = "description";
        public static final String GUIDED = "GUIDED";
        static final int AXIS_NUMBER = 2;
        public static final int OVERLAY_HORIZONTAL_RATIO = 8;
        static final int NON_REPEATED_IMAGES_CATEGORY_INDEX = -1;
        static final int DRAG_LIMITS_SIZE = 4;
        static final String TAG_SEPARATOR = "-";
        public static final String DRAWABLE = "drawable";
        static final int RANDOM_CORRECT_RESULTS_SENCENCE_LIMIT = 6;
    }

    static class GridConstants {
        static final ActivityType GRID_TYPE = ActivityType.GRID;
        static final String JSON_PARAMETER_GRIDSIZE = "gridSize";
        static final String JSON_PARAMETER_CELLS = "cells";
        static final String JSON_PARAMETER_IMAGES = "images";
        static final int COORDINATES_3X3_LENGTH = 9;
        static final int COORDINATES_4X4_LENGTH = 16;
        static final float CELL_FILLED_SPACE = 0.85f;
        static final String INNER = "INNER";
    }

    static class ColumnsConstants {
        static final ActivityType COLUMNS_TYPE = ActivityType.COLUMNS;
        static final String JSON_PARAMETER_LEFTTITLE = "leftTitle";
        static final String JSON_PARAMETER_RIGHTTITLE = "rightTitle";
        static final String JSON_PARAMETER_IMAGES = "images";
        static final String JSON_PARAMETER_CORRECTION = "correction";
        static final int NUMBER_OF_IMAGES = 5;
        static final int NUMBER_OF_COLUMNS_CORNERS = 2;
        static final int LEFT_COLUMN_INDEX = 0;
        static final int RIGHT_COLUMN_INDEX = 1;
    }

    public static class OperationsConstants {
        static final String JSON_PARAMETER_OPERATIONSTYPE = "operationsType";
        static final String JSON_PARAMETER_IMAGE = "image";
        static final int NUMBER_OF_OPERATIONS = 6;
        static final int NUMBER_OF_OPERATORS = 3;
        static final String JSON_PARAMETER_OPERATIONSIMAGES = "operationsImages";
        static final int RANDOM_NUMBER_LIMIT = 10;
        static final String[] OPERATORS = {"+", "-"};
        static final String COLUMN_0 = "00000";
        private static final String COLUMN_1 = "01110";
        private static final String COLUMN_2 = "01001";
        private static final String COLUMN_3 = "11111";
        private static final String COLUMN_4 = "00001";
        private static final String COLUMN_5 = "10011";
        private static final String COLUMN_6 = "10101";
        private static final String COLUMN_7 = "01010";
        private static final String COLUMN_8 = "10001";
        private static final String COLUMN_9 = "11100";
        private static final String COLUMN_10 = "00100";
        private static final String COLUMN_11 = "11101";
        private static final String COLUMN_12 = "10010";
        private static final String COLUMN_13 = "00110";
        private static final String COLUMN_14 = "01101";
        private static final String COLUMN_15 = "00010";
        private static final String COLUMN_16 = "10100";
        private static final String COLUMN_17 = "11000";
        private static final String COLUMN_18 = "01000";
        private static final String COLUMN_19 = "01111";
        private static final String COLUMN_20 = "01110";
        public static final String[][] NUMBERS_TO_LED = {
                {COLUMN_1, COLUMN_8, COLUMN_8, COLUMN_1},
                {COLUMN_2, COLUMN_3, COLUMN_4},
                {COLUMN_2, COLUMN_5, COLUMN_6, COLUMN_2},
                {COLUMN_7, COLUMN_8, COLUMN_6, COLUMN_7},
                {COLUMN_9, COLUMN_10, COLUMN_10, COLUMN_3},
                {COLUMN_11, COLUMN_6, COLUMN_6, COLUMN_12},
                {COLUMN_13, COLUMN_14, COLUMN_6, COLUMN_15},
                {COLUMN_8, COLUMN_12, COLUMN_16, COLUMN_17},
                {COLUMN_7, COLUMN_6, COLUMN_6, COLUMN_7},
                {COLUMN_18, COLUMN_6, COLUMN_6, COLUMN_19}
        };
        public static final String[][] OPERATORS_TO_LED = {
                {COLUMN_10, COLUMN_20, COLUMN_10},
                {COLUMN_10, COLUMN_10, COLUMN_10}
        };
        public static final String EMPTY_COLUMN = COLUMN_0;
        public static final int MAX_NUMBER_BLUETOOTH_COLUMNS = 5;
    }
//    {0, {0, {0, 1, 1, 1, 0}}}

    public static class PuzzleConstants {
        static final ActivityType PUZZLE_TYPE = ActivityType.PUZZLE;
        static final String JSON_PARAMETER_IMAGES = "images";
        static final int PIECES_NUMBER = 5;
        //TODO Change puzzle dimensions
        static final double[][][] PIECES_TO_PUZZLE = {
            {{0.5, 0.5}, {0.5, 1}, {0.5, 0.5}, {0.5, 0.5}, {0.5, 0.5}},
            {{0.5, 0.5}, {0.5, 1}, {0.5, 0.5}, {0.5, 0.5}, {0.5, 0.5}},
            {{0.5, 0.5}, {0.5, 1}, {0.5, 0.5}, {0.5, 0.5}, {0.5, 0.5}},
        };
        public static final int DISTANCE_LIMIT = 200;
        static final double[][][] PUZZLE_SHAPES_COORDINATES_FACTORS = {
                {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
                {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
                {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}}
        };
        static final double[][][] CORRECTION_SHAPES_COORDINATES_FACTORS = {
                {{0.25, 0.25}, {0.25, 0.5}, {0.25, 0.25}, {0.25, 0.25}, {0.25, 0.25}},
                {{0.25, 0.25}, {0.25, 0.5}, {0.25, 0.25}, {0.25, 0.25}, {0.25, 0.25}},
                {{0.25, 0.25}, {0.25, 0.5}, {0.25, 0.25}, {0.25, 0.25}, {0.25, 0.25}}
        };
        static final float[][] SCALE_ANIMATION_INCREASE_PIVOTS = {
                {0, 0}, {0, 0.5f}, {0, 1}, {1, 0}, {1, 1}
        };
        static final float[][] SCALE_ANIMATION_DECREASE_PIVOTS = {
                {1, 1}, {1, 0.5f}, {1, 0}, {0, 1}, {0, 0}
        };
        //TODO This factors are correct, but I only use shape 1 for testing
//        protected static final double[][][] SHAPES_COORDINATES_FACTORS = {
//            {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
//            {{0, 0}, {0, 0}, {0.5, 0}, {0, 0.5}, {0.5, 0.5}},
//            {{0, 0}, {0.5, 0}, {0.5, 0}, {0.5, 0.5}, {0, 0.5}}
//        };
    }

    static class GuideConstants {
        static final String JSON_PARAMETER_IMAGES = "images";
        static final String JSON_PARAMETER_CORRECTION = "correction";
        static final int NUMBER_OF_IMAGES = 4;
        /* index of the category of the correct house, only one of which can be displayed */
        static final int CATEGORY_ONLY_ONE_IMAGE = 1;
    }

    public static class ColouredGridConstants {
        static final ActivityType COLOUREDGRID_TYPE = ActivityType.COLOURED_GRID;
        static final String JSON_PARAMETER_CELLS = "cells";
        static final String JSON_PARAMETER_COLOUREDCELLS = "colouredCells";
        static final int NUMBER_OF_COLORS = 3;
        static final String JSON_PARAMETER_IMAGES = "images";
        static final int COORDINATES_4X4_LENGTH = 17;
        public static final float CELL_FILLED_SPACE = 0.85f;
    }

    static class MusicConstants {
        static final ActivityType MUSIC_TYPE = ActivityType.MUSIC;
        static final String JSON_PARAMETER_IMAGES = "images";
        static final int NUMBER_OF_DICTATIONS = 3;
        static final String[] DICTATION_PERIODS = {
                "1 1 0.5 0.5 1 1 0.5 0.5",
                "1 0 2 1 1",
                "2 0 0 0.5 0.5 0.5 0.5",
                "2 0 0 0.5 0.5 0.5 0.5",
                "2 0 0 0.5 0.5 0.5 0.5"
        };
    }

    static class DragConstants {
        static final ActivityType DRAG_TYPE = ActivityType.DRAG;
        static final String JSON_PARAMETER_DRAGIMAGES = "dragImages";
        static final String JSON_PARAMETER_CONTAINERELEMENTS = "containerElements";
        static final String JSON_PARAMETER_DRAGIMAGESNUMBER = "dragImagesNumber";
        static final String JSON_PARAMETER_TEXTS = "texts";
        static final String JSON_PARAMETER_CONTAINERIMAGES = "containerImages";
        static final String JSON_PARAMETER_CORRECTION = "correction";
    }

    static class MemoryConstants {
        static final ActivityType MEMORY_TYPE = ActivityType.MEMORY;
        static final String JSON_PARAMETER_IMAGES = "images";
        static final int NUMBER_OF_IMAGES = 4;
        static final int FLIP_DELAY = 5000;
    }

    static class FoodPyramidConstants {
        static final ActivityType FOODPYRAMID_TYPE = ActivityType.FOODPYRAMID;
        static final String JSON_PARAMETER_IMAGES = "images";
        static final String JSON_PARAMETER_CORRECTION = "correction";
        static final int PYRAMID_STEPS = 4;
        static final int PYRAMID_COORDINATES_LENGTH = 6;
        static final int NUMBER_OF_IMAGES = 7;
        static final int[][] PYRAMID_LIMITS_FACTORS = {
                {0, 2, 1, 1, 1, 1},
                {0, 0, 1, 2, 3, 4}
        };
        protected static final int DISTANCE_LIMIT = 75;
    }

    static class SeedsConstants {
        static final ActivityType SEEDS_TYPE = ActivityType.SEEDS;
        static final String JSON_PARAMETER_SEEDSIMAGES = "seedsImages";
        static final String JSON_PARAMETER_CONTAINERIMAGES = "containerImages";
        static final int NUMBER_OF_SEEDS = 16;
        static final int GUIDELINE_POSITION = 8;
        static final String JSON_PARAMETER_CORRECTION = "correction";
    }

    public static class ZowiEyesConstants {
        static final ActivityType ZOWI_EYES_TYPE = ActivityType.ZOWI_EYES;
        static final String JSON_PARAMETER_IMAGESNUMBER = "imagesNumber";
        static final String JSON_PARAMETER_IMAGES = "images";
        static final int LAYOUT_IMAGES = 12;
        public static final int[][] DISTANCE_LIMITS = {
            {300, 1}, {200, 2}, {0, 3}
        };
        public static final int[] DISTANCE_PERIODS = {
            1000, 500, 200
        };
    }

    public static class LogicBlocksConstants {
        static final String JSON_PARAMETER_IMAGES = "images";
        public static final String ZOWI_POINTER = "zowi_pointer";
        static final String ZOWI_HAPPY = "zowi_happy_open_small";
        static final String ZOWI_SAD = "zowi_happy_sad_small";
    }
}
