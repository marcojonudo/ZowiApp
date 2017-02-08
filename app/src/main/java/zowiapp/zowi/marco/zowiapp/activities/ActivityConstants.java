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
        protected static final int COORDINATES_3X3_LENGTH = 10;
        protected static final int COORDINATES_4X4_LENGTH = 17;
        protected static final double CONTROL_RADIUS_FACTOR = 6.66;
        protected static final int GRID_IMAGE_WIDTH_PX = 150;
        protected static final int GRID_TRANSLATION_TO_CENTER = GRID_IMAGE_WIDTH_PX /2;
    }

    protected static class ColumnsConstants {
        protected static final ActivityType COLUMNS_TYPE = ActivityType.COLUMNS;
        protected static final String JSON_PARAMETER_LEFTTITLE = "leftTitle";
        protected static final String JSON_PARAMETER_RIGHTTITLE = "rightTitle";
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final String JSON_PARAMETER_CORRECTION = "correction";
        protected static final int NUMBER_OF_IMAGES = 5;
        protected static final int NUMBER_OF_COLUMNS_CORNERS = 2;
        protected static final int CIRCUMFERENCE_INITIAL_POS = 45;
        protected static final int CIRCUMFERENCE_INCREMENT = 90;
        protected static final int LEFT_COLUMN_INDEX = 0;
        protected static final int RIGHT_COLUMN_INDEX = 1;
    }

    protected static class OperationsConstants {
        protected static final String JSON_PARAMETER_OPERATIONSTYPE = "operationsType";
        protected static final String JSON_PARAMETER_IMAGE = "image";
        protected static final int NUMBER_OF_OPERATIONS = 6;
        protected static final String JSON_PARAMETER_OPERATIONSIMAGES = "operationsImages";
        protected static final int OPERATION_IMAGE_WIDTH_PX = 125;
        protected static final int RANDOM_NUMBER_LIMIT = 10;
        protected static final String[] OPERATORS = {"+", "-"};
    }

    public static class PuzzleConstants {
        protected static final ActivityType PUZZLE_TYPE = ActivityType.PUZZLE;
        protected static final String JSON_PARAMETER_IMAGE = "image";
        protected static final String JSON_PARAMETER_SHAPE = "shape";
        protected static final String JSON_PARAMETER_PIECESNUMBER = "piecesNumber";
        protected static final int CONTENT_CONTAINER_MARGIN = 100;
        protected static final int[][] PIECES_TO_PUZZLE = {{2, 2}, {2, 1}, {2, 2}, {2, 2}, {2, 2}};
        public static final int DISTANCE_LIMIT = 100;
        protected static final double[][][] SHAPES_COORDINATES_FACTORS = {
            {{0, 0}, {0, 0}, {0, 0.5}, {0.5, 0}, {0.5, 0.5}},
            {{0, 0}, {0, 0}, {0.5, 0}, {0, 0.5}, {0.5, 0.5}},
            {{0, 0}, {0.5, 0}, {0.5, 0}, {0.5, 0.5}, {0, 0.5}}
        };
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
        protected static final int GRID_IMAGE_SIDE_PX = 150;
        protected static final int GRID_TRANSLATION_TO_CENTER = GRID_IMAGE_SIDE_PX /2;
    }

    protected static class MusicConstants {
        protected static final String JSON_PARAMETER_IMAGES = "images";
    }

    protected static class DragConstants {
        protected static final ActivityType DRAG_TYPE = ActivityType.DRAG;
        protected static final String JSON_PARAMETER_DRAGIMAGES = "dragImages";
        protected static final String JSON_PARAMETER_CONTAINERELEMENTS = "containerElements";
        protected static final String JSON_PARAMETER_DRAGIMAGESNUMBER = "dragImagesNumber";
        protected static final String JSON_PARAMETER_TEXTS = "texts";
        protected static final String JSON_PARAMETER_CONTAINERIMAGES = "containerImages";
        protected static final String JSON_PARAMETER_CORRECTION = "correction";
        protected static final int DISTANCE_LIMIT = 100;
    }

    protected static class MemoryConstants {
        protected static final ActivityType MEMORY_TYPE = ActivityType.MEMORY;
        protected static final String JSON_PARAMETER_IMAGES = "images";
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
}
