package name.cantanima.droidnim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    TextView value_view = (TextView) findViewById(R.id.value_view);
    Nim_Game_View game_view = (Nim_Game_View) findViewById(R.id.game_view);
    game_view.set_views(value_view);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int id = item.getItemId();
    switch (id) {
      case R.id.action_settings:
      case R.id.action_twoplayer:
      case R.id.action_achievements:
        return true;
      case R.id.action_information:
        Intent i = new Intent(this, Information_Activity.class);
        startActivity(i);
        return true;
    }

    return super.onOptionsItemSelected(item);

  }
}
