package zowiapp.zowi.marco.zowiapp;

/**
 * Created by Marco on 23/01/2017.
 */
public class ActivityConstants {

    protected static class CommonConstants {
        protected static final String INTENT_PARAMETER_TITLE = "activityTitle";
        protected static final String INTENT_PARAMETER_NUMBER = "activityNumber";
        protected static final String JSON_PARAMETER_DESCRIPTION = "description";
        protected static final String JSON_PARAMETER_TYPE = "TYPE";
        protected static final int AXIS_NUMBER = 2;
    }

    protected static class GridConstants {
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
        protected static final String JSON_PARAMETER_LEFTTITLE = "leftTitle";
        protected static final String JSON_PARAMETER_RIGHTTITLE = "rightTitle";
        protected static final String JSON_PARAMETER_IMAGES = "images";
        protected static final int COLUMNS_INCREMENT = 2;
        protected static final int CIRCUMFERENCE_INITIAL_POS = 45;
        protected static final int CIRCUMFERENCE_INCREMENT = 90;
        protected static final int LEFT_COL_INDEX_ADJUSTMENT = 2;
        protected static final int RIGHT_COL_INDEX_ADJUSTMENT = 1;
        protected static final int COLUMNS_IMAGE_WIDTH_PX = 150;
        protected static final int COLUMNS_TRANSLATION_TO_CENTER = COLUMNS_IMAGE_WIDTH_PX /2;
    }


}