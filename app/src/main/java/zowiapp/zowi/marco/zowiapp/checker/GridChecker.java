package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GridConstants;
import zowiapp.zowi.marco.zowiapp.utils.Layout;

public class GridChecker extends CheckerTemplate {

    private int lastCell = 0;

    public int checkMovement(GameParameters gameParameters, int gridSize, String nextDirection, int zowiCell, int[] obstacles) {
        if (lastCell == 0)
            lastCell = zowiCell;

        boolean correctDirection = checkCorrectDirection(gameParameters, gridSize, nextDirection);

        if (correctDirection) {
            int nextCell = getNextCell(zowiCell, nextDirection, gridSize);

            if (nextCell != 0) {
                boolean nextObstacle = checkObstacle(obstacles, nextCell);

                if (!nextObstacle) {
                    lastCell = nextCell;
                    return nextCell;
                }
                else {
                    String text = "¡Oh, no! ¡Zowi se ha encontrado un obstáculo y no puede avanzar!";
                    Layout.showAlertDialog(gameParameters, true, false, text);
                }
            }
        }

        return 0;
    }

    private boolean checkCorrectDirection(GameParameters gameParameters, int gridSize, String newDirection) {
        boolean correctDirection = true;

        int cellsNumber = gridSize == 1 ? 9 : 16;
        int cellsStep = gridSize == 1 ? 3 : 4;
        switch (newDirection) {
            case "UP":
                int rightUpLimit = gridSize == 1 ? 4 : 5;
                if (lastCell > 0 && lastCell < rightUpLimit)
                    correctDirection = false;
                break;
            case "LEFT":
                int moduleCorrection = gridSize == 1 ? 3 : 4;
                for (int i=moduleCorrection; i<cellsNumber; i=i+cellsStep) {
                    if ((lastCell+moduleCorrection-1) % i == 0)
                        correctDirection = false;
                }
                break;
            case "RIGHT":
                int checkStart = gridSize == 1 ? 3 : 4;
                for (int i=checkStart; i<cellsNumber; i=i+cellsStep) {
                    if (lastCell % i == 0)
                        correctDirection = false;
                }
                break;
            case "DOWN":
                int leftDownLimit = gridSize == 1 ? 6 : 12;
                int rightDownLimit = gridSize == 1 ? 10 : 17;
                if (lastCell > leftDownLimit && lastCell < rightDownLimit)
                    correctDirection = false;
                break;
        }

        if (!correctDirection) {
            String text = "¡Vaya! ¡Zowi ha estado a punto de salirse de la cuadrícula!";
            Layout.showAlertDialog(gameParameters, true, false, text);
        }

        return correctDirection;
    }

    private int getNextCell(int lastCell, String nextDirection, int gridSize) {
        boolean smallGrid = gridSize == 1;
        int nextCell = 0;
        switch (nextDirection) {
            case "UP":
                nextCell = lastCell - (smallGrid ? 3 : 4);
                break;
            case "LEFT":
                nextCell = lastCell - 1;
                break;
            case "RIGHT":
                nextCell = lastCell + 1;
                break;
            case "DOWN":
                nextCell = lastCell + (smallGrid ? 3 : 4);
                break;
        }

        return nextCell;
    }

    private boolean checkObstacle(int[] obstacles, int nextCell) {
        boolean nextObstacle = false;
        for (int obstacle: obstacles) {
            if (nextCell == obstacle)
                nextObstacle = true;
        }

        return nextObstacle;
    }

//    public boolean check(GameParameters gameParameters, String[] directions, int[] nextCells, int[] obstacles, int[] ) {
//        boolean nextObstacle
//        for (int i=0; i<nextCells.length; i++) {
//            for (int j=0; j<obstacles.length; j++) {
//                if (nextCells[i] == obstacles[j]) {
//
//                }
//            }
//        }
//        if (imageCategory.equals(containerCategory)) {
//            sendDataToZowi(ZowiActions.CORRECT_ANSWER_COMMAND);
//            return true;
//        }
//        else {
//            sendDataToZowi(ZowiActions.WRONG_ANSWER_COMMAND);
//            return false;
//        }
//    }

}
