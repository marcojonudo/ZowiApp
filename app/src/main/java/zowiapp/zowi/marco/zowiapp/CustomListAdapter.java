package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

/**
 * Created by Marco on 09/08/2016.
 */
public class CustomListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] unitsTitles;
    private String[][] activitiesTitles, activitiesImages;

    private final int UNITS_NUMBER = 5;

    public CustomListAdapter(Context context, String[] unitsTitles, String[][] titles, String[][] pictures) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.unitsTitles = unitsTitles;
        this.activitiesTitles = titles;
        this.activitiesImages = pictures;
    }

    @Override
    public int getCount() {
        return UNITS_NUMBER;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            View containerView = inflater.inflate(R.layout.guided_game_layout, viewGroup, false);
            LinearLayout container = (LinearLayout) containerView.findViewById(R.id.guided_game_layout);

            View unitTitleView = generateTitleLayout(viewGroup, position);
            View tableLayoutView = generateActivitiesLayout(viewGroup, position);

            container.addView(unitTitleView);
            container.addView(tableLayoutView);

            return containerView;
        }
        else {
            updateView(view, position);
        }

        return view;
    }

    private View generateTitleLayout(ViewGroup viewGroup, int position) {
        View unitTitleView = inflater.inflate(R.layout.unit_title, viewGroup, false);
        TextView unitTitle = (TextView) unitTitleView.findViewById(R.id.unit_title);
        unitTitle.setText(unitsTitles[position]);

        return unitTitleView;
    }

    private View generateActivitiesLayout(ViewGroup viewGroup, int position) {
        View tableLayoutView = inflater.inflate(R.layout.activities_table_layout, viewGroup, false);
        TableLayout tableLayout = (TableLayout) tableLayoutView.findViewById(R.id.unit_activities_layout);

        int elementsPerRow = 4;
        int currentActivity = 0;
        for (int i=0; i<2; i++) {
            View tableRowView = inflater.inflate(R.layout.activities_table_row, viewGroup, false);
            TableRow tableRow = (TableRow) tableRowView.findViewById(R.id.table_row);

            for (int j=0; j<elementsPerRow; j++) {
                View gridItemView = inflater.inflate(R.layout.grid_item, viewGroup, false);
                TextView title = (TextView) gridItemView.findViewById(R.id.activity_title);
                ImageView image = (ImageView) gridItemView.findViewById(R.id.activity_image);

                title.setText(activitiesTitles[position][currentActivity]);
                image.setImageResource(context.getResources().getIdentifier(activitiesImages[position][currentActivity], "drawable", context.getPackageName()));

                gridItemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                tableRow.addView(gridItemView);

                currentActivity++;
            }
            elementsPerRow = 2;
            tableLayout.addView(tableRowView);
        }

        return tableLayoutView;
    }

    private void updateView(View view, int position) {
        TextView unitTitle = (TextView) view.findViewById(R.id.unit_title);
        unitTitle.setText(unitsTitles[position]);

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.unit_activities_layout);
        int currentActivity = 0;
        for (int i=0; i<tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);

            for (int j=0; j<tableRow.getChildCount(); j++) {
                View gridItemView = tableRow.getChildAt(j);
                TextView activityTitle = (TextView) gridItemView.findViewById(R.id.activity_title);
                activityTitle.setText(activitiesTitles[position][currentActivity]);
                ImageView activityImage = (ImageView) gridItemView.findViewById(R.id.activity_image);
                activityImage.setImageResource(context.getResources().getIdentifier(activitiesImages[position][currentActivity], "drawable", context.getPackageName()));

                currentActivity++;
            }
        }
    }
}