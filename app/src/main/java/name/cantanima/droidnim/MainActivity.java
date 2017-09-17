package name.cantanima.droidnim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
}
