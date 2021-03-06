package zowiapp.zowi.marco.zowiapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import zowiapp.zowi.marco.zowiapp.activities.ActivityConstants.CommonConstants;
import zowiapp.zowi.marco.zowiapp.layout.CustomConstraintBackground;
import zowiapp.zowi.marco.zowiapp.GameParameters;
import zowiapp.zowi.marco.zowiapp.R;

public class FreeGameListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] activitiesTitles, activitiesImages;
    private int currentActivity = 0;
    private ConstraintLayout[] activitiesGroup;

    private static int activitiesNumber, groupsNumber;
    private static final int ACTIVITIES_GROUP = 8;
    private static final String CATEGORY_TYPE = "FREE";
    private static final String CATEGORY_TYPE_KEY = "categoryType";
    private static final String ACTIVITY_TITLE_KEY = "activityTitle";
    private static final String ACTIVITY_NUMBER_KEY = "activityNumber";

    public FreeGameListAdapter(Context context, String[] titles, String[] images) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activitiesTitles = titles;
        this.activitiesImages = images;
        activitiesNumber = titles.length;
        activitiesGroup = new ConstraintLayout[activitiesNumber];
        groupsNumber = (activitiesNumber-1)/ACTIVITIES_GROUP + 1;
    }

    @Override
    public int getCount() {
        return groupsNumber;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            if (activitiesGroup[position] == null) {
                final CustomConstraintBackground groupFreeActivities = (CustomConstraintBackground) inflater.inflate(R.layout.menu_free_activities_container, viewGroup, false);
                ViewGroup.LayoutParams l = groupFreeActivities.getLayoutParams();
                l.height = context.getResources().getDisplayMetrics().heightPixels;//- statusBarHeight;
                groupFreeActivities.setBackgroundResource((position >= activitiesNumber/ACTIVITIES_GROUP) ? R.drawable.menu_footprint_background_final : (position % 2 == 0 && groupsNumber > 2) ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom);
//                Picasso.with(context).load(position % 2 == 0 ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom).fit().centerInside().into(unitContainer);

                loadContent(groupFreeActivities);

                activitiesGroup[position] = groupFreeActivities;

                return groupFreeActivities;
            }
            else {
                return activitiesGroup[position];
            }
        }
        else {
            if (activitiesGroup[position] == null) {
                final CustomConstraintBackground groupFreeActivities = (CustomConstraintBackground) inflater.inflate(R.layout.menu_free_activities_container, viewGroup, false);
                groupFreeActivities.setLayoutParams(view.getLayoutParams());
                groupFreeActivities.setBackgroundResource((position >= activitiesNumber/ACTIVITIES_GROUP) ? R.drawable.menu_footprint_background_final : (position % 2 == 0 && groupsNumber > 2) ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom);
//                Picasso.with(context).load((position == UNITS_NUMBER-1) ? R.drawable.menu_footprint_background_final : (position % 2 == 0) ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom).fit().centerInside().into(groupFreeActivities);

                loadContent(groupFreeActivities);
                activitiesGroup[position] = groupFreeActivities;

                return groupFreeActivities;
            }
            else {
                return activitiesGroup[position];
            }
        }
    }

    private void loadContent(ConstraintLayout unitContainer) {
        for (int j = 0; j< ACTIVITIES_GROUP; j++) {
            ConstraintLayout activityContainer = (ConstraintLayout) unitContainer.getChildAt(j);
            if (currentActivity < activitiesNumber) {
                TextView activityTitle = (TextView) activityContainer.getChildAt(0);
                ImageView activityImage = (ImageView) activityContainer.getChildAt(1);

                activityContainer.setTag(currentActivity);
                activityTitle.setText(activitiesTitles[currentActivity]);
                int resourceId = context.getResources().getIdentifier(activitiesImages[currentActivity], CommonConstants.DRAWABLE, context.getPackageName());
                Picasso.with(context).load(resourceId).fit().centerInside().into(activityImage);

                activityContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int activityTag = (int) view.getTag();
                        String activityTitle = activitiesTitles[activityTag];

                        Intent intent = new Intent(context, GameParameters.class);
                        intent.putExtra(CATEGORY_TYPE_KEY, CATEGORY_TYPE);
                        intent.putExtra(ACTIVITY_TITLE_KEY, activityTitle);
                        intent.putExtra(ACTIVITY_NUMBER_KEY, activityTag);

                        context.startActivity(intent);
                    }
                });
            }
            else {
                activityContainer.setVisibility(View.INVISIBLE);
            }

            currentActivity++;
        }
    }

}