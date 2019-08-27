package name.cantanima.droidnim;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public class MainActivity
    extends AppCompatActivity
    implements DialogInterface.OnClickListener, BTR_Listener, DialogInterface.OnCancelListener
{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getApplicationContext();
    setContentView(R.layout.activity_main);
    TextView value_view = findViewById(R.id.value_view);
    Nim_Game_View game_view = findViewById(R.id.game_view);
    game_view.set_views(value_view);
  }

  public Context getContext() { return context; }

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
        Intent si = new Intent(this, SettingsActivity.class);
        startActivity(si);
        return true;
      /*case R.id.action_twoplayer:
        if (bluetooth_is_available()) {
          if (bt_adapter.isEnabled()) {
            if (bt_thread != null) {
              bt_thread.disconnect();
              bt_thread = null;
            }
            bt_thread = new BT_Setup_Thread(this);
            bt_thread.start();
            bt_thread.host_or_join();
          } else {
            Intent enable_bt_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enable_bt_intent, REQUEST_ENABLE_BT);
          }
        }
        return true;
      case R.id.action_achievements:
        new AlertDialog.Builder(this).setTitle(R.string.achievement_dialog_title)
            .setMessage(R.string.achievement_dialog_message)
            .setPositiveButton(R.string.understood, this)
            .show();
        return true;*/
      case R.id.action_information:
        Intent ii = new Intent(this, Information_Activity.class);
        startActivity(ii);
        return true;
    }

    return super.onOptionsItemSelected(item);

  }

  public void show_welcome() {
    welcome_dialog = new AlertDialog.Builder(this)
        .setTitle(R.string.app_welcome_title)
        .setMessage(
            getString(R.string.app_welcome_message_start) +
                " (v" + getString(R.string.app_version) + ") " +
                getString(R.string.app_welcome_message_end)
        )
        .setPositiveButton(R.string.understood, this)
        .show();
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {

    if (dialog == welcome_dialog) {
      ((Nim_Game_View) findViewById(R.id.game_view)).start_game(3, 7);
    } else if (dialog == bluetooth_dialog) {
      if (which != DialogInterface.BUTTON_NEGATIVE) {
        // connect to device
        bt_thread.join_game(which);
      }
    } else if (dialog == host_or_join_dialog) {
      if (which == DialogInterface.BUTTON_POSITIVE) {
        (new Host_Thread(this)).execute();
      } else {
        bt_thread.select_paired_device();
      }
    }

  }

  public boolean bluetooth_is_available() {
    bt_adapter = BluetoothAdapter.getDefaultAdapter();
    boolean result = bt_adapter != null;
    if (!result) {
      new AlertDialog.Builder(this)
          .setTitle(R.string.no_bluetooth_title)
          .setMessage(
              getString(R.string.no_bluetooth_message) + " " +
                  getString(R.string.bluetooth_not_available)
          )
          .setPositiveButton(R.string.understood, this)
          .show();
    }
    return result;
  }

  /**
   * Dispatch incoming result to the correct fragment.
   *
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //Log.d(tag, "In activity result with code " + String.valueOf(resultCode));
    if (requestCode == REQUEST_ENABLE_BT) {
      //Log.d(tag, "bluetooth requests enable");
      if (resultCode == RESULT_OK) {
        //Log.d(tag, "bluetooth enable OK");
        if (bt_thread == null)
          bt_thread = new BT_Setup_Thread(this);
        bt_thread.host_or_join();
      } else if (resultCode == RESULT_CANCELED) {
        //Log.d(tag, "canceled, aborting");
        new AlertDialog.Builder(this)
            .setTitle(R.string.no_bluetooth_title)
            .setMessage(getString(R.string.no_bluetooth_message) + " " +
                getString(R.string.bluetooth_canceled)
            )
            .setPositiveButton(R.string.understood, this)
            .show();
      }
    } else if (requestCode == REQUEST_HOST) {
      //Log.d(tag, "bluetooth requests host");
      if (resultCode == RESULT_OK) {
        //Log.d(tag, "bluetooth host ok");
        (new Host_Thread(this)).execute();
      } else {
        //Log.d(tag, "canceled hosting");
        new AlertDialog.Builder(this)
            .setTitle(R.string.no_bluetooth_title)
            .setMessage(getString(R.string.no_bluetooth_message) + " "
                + getString(R.string.bt_no_discoverability)
            )
            .setPositiveButton(R.string.understood, this)
            .show();
      }
    }
  }

  private class BT_Setup_Thread extends Thread {

    BT_Setup_Thread(MainActivity my_context) { context = my_context; }

    void disconnect() {
      try {
        communication_socket.close();
      } catch (Exception e) {
        // I don't care at this point
      }
    }

    void join_game(int which_device) {

      i_am_hosting = false;
      BluetoothDevice desired_device = available_devices.get(which_device);
      BluetoothSocket socket;
      try {
        socket = desired_device.createRfcommSocketToServiceRecord(UUID.fromString(my_uuid));
        communication_socket = socket;
      } catch (IOException e) {
        String message = getString(R.string.bt_unable_to_join) + " " + e.getMessage();
        //Log.d(tag, message, e);
        new AlertDialog.Builder(context).setTitle(R.string.no_bluetooth_title)
            .setMessage(message)
            .setPositiveButton(R.string.understood, context)
            .show();
      }

      if (communication_socket != null) {
        try {
          communication_socket.connect();
          if (communication_socket.isConnected())
            ((Nim_Game_View) findViewById(R.id.game_view)).setup_human_game(communication_socket, false);
          else {
            new AlertDialog.Builder(context).setTitle(R.string.no_bluetooth_title)
                .setMessage(R.string.bt_unable_to_open_stream)
                .setPositiveButton(R.string.understood, context)
                .show();
          }
        } catch (IOException e) {
          String message = getString(R.string.bt_unable_to_connect) + " " + e.getMessage();
          //Log.d(tag, message, e);
          new AlertDialog.Builder(context).setTitle(R.string.no_bluetooth_title)
              .setMessage(message)
              .setPositiveButton(R.string.understood, context)
              .show();
        }
      }

    }

    void host_or_join() {

      host_or_join_dialog = new AlertDialog.Builder(context)
          .setTitle(R.string.bt_host_or_join_title)
          .setMessage(R.string.bt_host_or_join_message)
          .setPositiveButton(R.string.host, context)
          .setNegativeButton(R.string.join, context)
          .show();

    }

    void select_paired_device() {

      i_am_hosting = false;
      Set<BluetoothDevice> known_devices = bt_adapter.getBondedDevices();
      String [] device_names = new String[known_devices.size()];
      available_devices = new Vector<>(known_devices.size());
      int i = 0;
      for (BluetoothDevice device : known_devices) {
        device_names[i] = device.getName();
        available_devices.add(device);
        ++i;
      }
      bluetooth_dialog = new AlertDialog.Builder(context)
          .setTitle(R.string.bt_select_device_title)
          .setNegativeButton(R.string.cancel, context)
          .setItems(device_names, context)
          .show();
      if (available_devices.size() == 0)
        new AlertDialog.Builder(context)
            .setTitle(R.string.bt_no_devices_title)
            .setMessage(R.string.bt_no_devices_message)
            .setPositiveButton(R.string.understood, context)
            .show();

    }

    MainActivity context;

  }

  private static class Host_Thread extends AsyncTask<Object, Integer, Boolean> {

    Host_Thread(MainActivity main) { context = main; }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      context.bt_waiting_to_host_dialog = new ProgressDialog(context);
      context.bt_waiting_to_host_dialog.setTitle(R.string.bt_waiting_to_host_title);
      context.bt_waiting_to_host_dialog.setMessage(context.getString(R.string.bt_waiting_to_host_message));
      context.bt_waiting_to_host_dialog.setIndeterminate(true);
      context.bt_waiting_to_host_dialog.show();
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param aBoolean The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
      super.onPostExecute(aBoolean);
      context.bt_waiting_to_host_dialog.dismiss();
      try {
        context.server_socket.close();
      } catch (IOException e) {
        // don't care now
      }
      //Log.d(tag, "disconnected server socket");
      if (context.communication_socket.isConnected())
        context.start_human_game();
    }

    @Override
    protected Boolean doInBackground(Object[] params) {

      context.i_am_hosting = true;
      context.host_or_join_dialog.dismiss();
      BluetoothServerSocket server;
      try {
        server = context.bt_adapter.listenUsingInsecureRfcommWithServiceRecord(
            "Ideal Nim", UUID.fromString(context.my_uuid)
        );
        context.server_socket = server;
      } catch (IOException e) {
        String message = context.getString(R.string.bt_unable_to_host) + " " + e.getMessage();
        //Log.d(tag, message, e);
        new AlertDialog.Builder(context).setTitle(R.string.no_bluetooth_title)
            .setMessage(message)
            .setPositiveButton(R.string.understood, context)
            .show();
      }

      if (context.server_socket != null) {
        BluetoothSocket socket;
        try {
          socket = context.server_socket.accept();
          context.communication_socket = socket;
        } catch (IOException e) {
          String message = context.getString(R.string.bt_unable_to_host) + " " + e.getMessage();
          //Log.d(tag, message, e);
          new AlertDialog.Builder(context).setTitle(R.string.no_bluetooth_title)
              .setMessage(message)
              .setPositiveButton(R.string.understood, context)
              .show();
        }
        if (!context.communication_socket.isConnected()) {
          new AlertDialog.Builder(context).setTitle(R.string.no_bluetooth_title)
              .setMessage(R.string.bt_unable_to_open_stream)
              .setPositiveButton(R.string.understood, context)
              .show();
        }
      }

      return null;
    }

    private MainActivity context;
  }

  public boolean i_host_the_game() { return i_am_hosting; }

  public void start_human_game() {
    ((Nim_Game_View) findViewById(R.id.game_view)).setup_human_game(communication_socket, true);
  }

  public void two_player_game_ended() {
    if (communication_socket != null && communication_socket.isConnected()) {
      if (i_host_the_game()) {
        BT_Reading_Thread bt_thread = new BT_Reading_Thread(communication_socket, this, false);
        bt_thread.execute();
      } else {
        Byte[] ack = new Byte[2];
        ack[0] = 0; ack[1] = 0;
        BT_Writing_Thread bt_thread = new BT_Writing_Thread(communication_socket);
        bt_thread.execute(ack);
      }
      //Log.d(tag, "closing socket");
    }
  }

  @Override
  public void received_data(int size, byte[] data) {
    if (data[0] == 0 && data[1] == 0)
      try {
        communication_socket.close();
      } catch (IOException e) {
        // I don't care at this point
      }
  }

  /**
   * This method will be invoked when the dialog is canceled.
   *
   * @param dialog The dialog that was canceled will be passed into the
   *               method.
   */
  @Override
  public void onCancel(DialogInterface dialog) {
    if (dialog == bt_waiting_to_host_dialog) {
      bt_waiting_to_host_dialog.dismiss();
      if (communication_socket != null) try {
        communication_socket.close();
        communication_socket = null;
      } catch (IOException e) {
        // I really don't understand the point of this
      }
    }
  }

  // bluetooth services
  private final String my_uuid = "b87859ae-9f11-11e7-814c-0401beb96201";
  private int REQUEST_ENABLE_BT = 32003, REQUEST_HOST = 32004;
  private Vector<BluetoothDevice> available_devices = null;
  private boolean i_am_hosting = false;
  AlertDialog welcome_dialog, bluetooth_dialog, host_or_join_dialog;
  private BT_Setup_Thread bt_thread;
  private BluetoothSocket communication_socket = null;
  private BluetoothServerSocket server_socket = null;
  private BluetoothAdapter bt_adapter = null;
  ProgressDialog bt_waiting_to_host_dialog = null;
  private Context context;

}
