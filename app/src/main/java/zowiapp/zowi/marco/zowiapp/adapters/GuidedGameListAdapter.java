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

public class GuidedGameListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] unitsTitles, activitiesTitles, activitiesImages;
    private int statusBarHeight, unitsSeparation, currentActivity = 0;
    private ConstraintLayout[] units;

    private static int unitsNumber;
    private static final int ACTIVITIES_NUMBER = 6;
    private static final String CATEGORY_TYPE = "GUIDED";
    private static final String CATEGORY_TYPE_KEY = "categoryType";
    private static final String ACTIVITY_TITLE_KEY = "activityTitle";
    private static final String ACTIVITY_NUMBER_KEY = "activityNumber";

    public GuidedGameListAdapter(Context context, String[] unitsTitles, String[] titles, String[] images) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.unitsTitles = unitsTitles;
        this.activitiesTitles = titles;
        this.activitiesImages = images;
        unitsNumber = unitsTitles.length;
        units = new ConstraintLayout[unitsNumber];

        int statusBarId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(statusBarId);
        }
        /* Padding that separates the units */
        int unitsSeparationId = context.getResources().getIdentifier("units_separation_vertical_margin", "dimen", context.getPackageName());
        if (unitsSeparationId > 0) {
            unitsSeparation = context.getResources().getDimensionPixelSize(unitsSeparationId);
        }
    }

    @Override
    public int getCount() {
        return unitsNumber;
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
            if (units[position] == null) {
                final CustomConstraintBackground unitContainer = (CustomConstraintBackground) inflater.inflate(R.layout.menu_guided_unit_container, viewGroup, false);
                ViewGroup.LayoutParams l = unitContainer.getLayoutParams();
                l.height = context.getResources().getDisplayMetrics().heightPixels - statusBarHeight + unitsSeparation;
                unitContainer.setBackgroundResource(position % 2 == 0 ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom);
//                Glide.with(context).load(position % 2 == 0 ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom).fit().centerInside().into(unitContainer);
//                Picasso.with(context).load(position % 2 == 0 ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom).fit().centerInside().into(unitContainer);
                loadContent(unitContainer, position);

                units[position] = unitContainer;

                return unitContainer;
            }
            else {
                return units[position];
            }
        }
        else {
            if (units[position] == null) {
                final CustomConstraintBackground unitContainer = (CustomConstraintBackground) inflater.inflate(R.layout.menu_guided_unit_container, viewGroup, false);
                unitContainer.setLayoutParams(view.getLayoutParams());
                unitContainer.setBackgroundResource((position == unitsNumber -1) ? R.drawable.menu_footprint_background_final : (position % 2 == 0) ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom);
//                Glide.with(context).load((position == unitsNumber-1) ? R.drawable.menu_footprint_background_final : (position % 2 == 0) ? R.drawable.menu_footprint_background_top : R.drawable.menu_footprint_background_bottom).fit().centerInside().into(unitContainer);

                loadContent(unitContainer, position);
                units[position] = unitContainer;

                return unitContainer;
            }
            else {
                return units[position];
            }
        }
    }

    private void loadContent(ConstraintLayout unitContainer, int position) {
        TextView unitTitle = (TextView) unitContainer.getChildAt(0);
        unitTitle.setText(unitsTitles[position]);
        for (int i=0; i<ACTIVITIES_NUMBER; i++) {
            ConstraintLayout activityContainer = (ConstraintLayout) unitContainer.getChildAt(i+1);
            TextView activityTitle = (TextView) activityContainer.getChildAt(0);
            ImageView activityImage = (ImageView) activityContainer.getChildAt(1);

            activityContainer.setTag(currentActivity);
            activityTitle.setText(activitiesTitles[currentActivity].split(":")[0]);
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
            currentActivity++;
        }
    }

}