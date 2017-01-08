package zowiapp.zowi.marco.zowiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private ViewGroup rootView;
    private View redButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*rootView = (ViewGroup) findViewById(R.id.placeholder);
        redButton = findViewById(R.id.guidedGameButton);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder, new MainActivityFragment(), "MainActivityFragment");
        ft.commit();*/
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
