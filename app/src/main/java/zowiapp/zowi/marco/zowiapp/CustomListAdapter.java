package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

/**
 * Created by Marco on 09/08/2016.
 */
public class CustomListAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private final int UNITS_NUMBER = 5;
    private final int ELEMENT_TYPES = 2;
    private final int HEADER_TYPE = 0;
    private final int ACTIVITY_TYPE = 1;

    public CustomListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        /* Por cada unidad dos elementos: el header, y el TableLayout con todas las actividades */
        return UNITS_NUMBER*ELEMENT_TYPES;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    private int getViewType(int position) {

        if (position%2 == 0) {
            return HEADER_TYPE;
        }
        else {
            return ACTIVITY_TYPE;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        int viewType = getViewType(position);

        if (view == null) {
            switch (viewType) {
                case HEADER_TYPE:
                    view = inflater.inflate(R.layout.header_type, viewGroup, false);
                    return view;
                case ACTIVITY_TYPE:
                    View tableLayoutView = inflater.inflate(R.layout.activities_table_layout, viewGroup, false);
                    View tableRowView;
                    View gridItemView;

                    TableLayout tableLayout = (TableLayout) tableLayoutView.findViewById(R.id.unit_activities_layout);
                    TableRow tableRow;

                    for (int i=0; i<2; i++) {
                        tableRowView = inflater.inflate(R.layout.activities_table_row, viewGroup, false);
                        tableRow = (TableRow) tableRowView.findViewById(R.id.table_row);

                        for (int j=0; j<4; j++) {
                            gridItemView = inflater.inflate(R.layout.grid_item, viewGroup, false);
                            gridItemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            tableRow.addView(gridItemView);
                        }
                        tableLayout.addView(tableRow);
                    }

                    return tableLayout;
            }
        }
        return view;
    }
}