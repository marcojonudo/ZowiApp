package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private int[][] coordinates = new int[10][2];
    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_grid_3x3);

        /*rootView = (ViewGroup) findViewById(R.id.placeholder);
        redButton = findViewById(R.id.guidedGameButton);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, new MainActivityFragment(), "MainActivityFragment");
        ft.commit();*/
        this.context = this;
        GridLayout gameGrid = (GridLayout) findViewById(R.id.game_grid);

        gameGrid.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    GridLayout gameGrid = (GridLayout) findViewById(R.id.game_grid);

                    coordinates[0][0] = gameGrid.getLeft();
                    coordinates[0][1] = gameGrid.getTop();
                    // 492,36

                    int halfCell = gameGrid.getWidth()/6;

                    for (int i=0; i<gameGrid.getChildCount(); i++) {
                        View gameGridChild = gameGrid.getChildAt(i);

                        coordinates[i+1][0] = gameGridChild.getLeft() + gameGridChild.getWidth()/2;
                        coordinates[i+1][1] = gameGridChild.getTop() + gameGridChild.getHeight()/2;

                        coordinates[i+1][0] = coordinates[0][0] + (((i%3)*2 + 1)*halfCell) - 75;
                        coordinates[i+1][1] = coordinates[0][1] + (((i/3)*2 + 1)*halfCell) - 75;
                    }
                    insertZowi();
                    gameGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
        });


    }

    private void insertZowi() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_grid_3x3);

        ImageView zowiImage = new ImageView(this);
        zowiImage.setImageResource(R.drawable.zowi_pointer);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        zowiImage.setLayoutParams(layoutParams);
        zowiImage.setX(coordinates[7][0]);
        zowiImage.setY(coordinates[7][1]);
        zowiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                TranslateAnimation animation = new TranslateAnimation(0, coordinates[8][0]-coordinates[7][0], 0, 0);
                animation.setDuration(1000);
                animation.setFillAfter(true);

                v.startAnimation(animation);
            }
        });

        relativeLayout.addView(zowiImage);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toGuidedGame(View v) {
        Toast.makeText(getApplicationContext(), "Hola", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), GuidedGameActivity.class);
        startActivity(intent);
    }
}
