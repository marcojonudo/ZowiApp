package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Toast;

public class GameParameters extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_parameters);

        Bundle b = getIntent().getExtras();
        Toast.makeText(this, String.valueOf(b.getInt("grid")), Toast.LENGTH_SHORT).show();

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.game_parameters_container);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_elements, viewGroup, false);

        GridLayout grid = (GridLayout) view.findViewById(R.id.game_grid_3x3);

        Log.i("d","dd");
    }
}
