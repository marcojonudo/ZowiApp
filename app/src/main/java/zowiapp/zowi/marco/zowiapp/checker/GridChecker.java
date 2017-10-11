package zowiapp.zowi.marco.zowiapp.checker;

import android.util.Log;
import android.widget.Toast;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.GridConstants;

public class GridChecker extends CheckerTemplate {

    private static int lastCell = 0;

    public int checkMovement(GameParameters gameParameters, int gridSize, int initialCell, String nextDirection, int lastCell, int[] obstacles) {
        boolean correctDirection = checkCorrectDirection(gameParameters, gridSize, initialCell, nextDirection);

        if (correctDirection) {
            int nextCell = getNextCell(lastCell, nextDirection);

            if (nextCell != 0) {
                boolean nextObstacle = checkObstacle(obstacles, nextCell);

                if (!nextObstacle)
                    return nextCell;
            }
        }

        return 0;
    }

    private boolean checkCorrectDirection(GameParameters gameParameters, int gridSize, int initialCell, String newDirection) {
        boolean correctDirection = true;
        lastCell = lastCell == 0 ? initialCell : getNextCell(lastCell, newDirection);
        Log.i("cell", String.valueOf(lastCell));
        int cellsNumber = gridSize == 1 ? 9 : 16;
        int cellsStep = gridSize == 1 ? 3 : 4;
        switch (newDirection) {
            case "UP":
                int rightUpLimit = gridSize == 1 ? 4 : 5;
                if (lastCell > 0 && lastCell < rightUpLimit)
                    correctDirection = false;
                break;
            case "LEFT":
                for (int i=1; i<cellsNumber; i=i+cellsStep) {
                    if (lastCell % i == 0)
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

        if (!correctDirection)
            Toast.makeText(gameParameters, "¡Cuidado, que sacas a Zowi fuera de la cuadrícula!", Toast.LENGTH_LONG).show();

        return correctDirection;
    }

    private int getNextCell(int lastCell, String nextDirection) {
        int nextCell = 0;
        switch (nextDirection) {
            case "UP":
                nextCell = lastCell - 3;
                break;
            case "LEFT":
                nextCell = lastCell - 1;
                break;
            case "RIGHT":
                nextCell = lastCell + 1;
                break;
            case "DOWN":
                nextCell = lastCell + 3;
                break;
        }

//        nextCells.add(nextCell);
//            nextCells.set(movementsNumber, nextCell);
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
