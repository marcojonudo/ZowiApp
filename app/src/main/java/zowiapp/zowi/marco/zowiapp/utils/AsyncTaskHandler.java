package zowiapp.zowi.marco.zowiapp.utils;

import android.os.AsyncTask;

import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.activities.ActivityType;
import zowiapp.zowi.marco.zowiapp.checker.CheckerTemplate;
import zowiapp.zowi.marco.zowiapp.checker.FoodPyramidChecker;
import zowiapp.zowi.marco.zowiapp.checker.GuideChecker;
import zowiapp.zowi.marco.zowiapp.checker.OperationsChecker;
import zowiapp.zowi.marco.zowiapp.checker.PuzzleChecker;
import zowiapp.zowi.marco.zowiapp.zowi.ZowiActions;
import zowiapp.zowi.marco.zowiapp.utils.ThreadHandler.ThreadType;

public class AsyncTaskHandler extends AsyncTask<String, Integer, Boolean> {

    private GameParameters gameParameters;
    private ActivityType activityType;
    private CheckerTemplate checker;

    public AsyncTaskHandler(CheckerTemplate checker, ActivityType activityType) {
        this.checker = checker;
        this.activityType = activityType;
    }

    @Override
    protected Boolean doInBackground(String... commands) {
        try {
            Thread zowiSeeScreenThread = ThreadHandler.createThread(ThreadType.SIMPLE_FEEDBACK);
            zowiSeeScreenThread.start();

            ZowiActions.sendDataToZowi(commands[0]);

            zowiSeeScreenThread.join();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean connected) {
        Layout.closeAlertDialog();

        switch (activityType) {
            case FOODPYRAMID:
                ((FoodPyramidChecker)checker).checkAnswers();
                break;
            case GUIDE:
                ((GuideChecker)checker).checkAnswers();
                break;
            case PUZZLE:
                ((PuzzleChecker)checker).checkAnsweres();
                break;
            case OPERATIONS:
                ((OperationsChecker)checker).sendOperation(null);
                break;
        }
    }

}