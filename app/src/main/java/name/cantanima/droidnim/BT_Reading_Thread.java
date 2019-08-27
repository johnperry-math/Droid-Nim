package name.cantanima.droidnim;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cantanima on 9/5/17.
 */

public class BT_Reading_Thread extends AsyncTask<Object, Integer, Boolean> {

  public BT_Reading_Thread(
      BluetoothSocket socket, BTR_Listener listener, boolean show_dialog
  ) {
    bt_socket = socket;
    notify = listener;
    show_progress_dialog = show_dialog;
  }

  @Override
  protected Boolean doInBackground(Object ...params) {
    boolean success;
    InputStream bt_input_stream;
    try {
      bt_input_stream = bt_socket.getInputStream();
      size = bt_input_stream.read(info);
      success = true;
      failure_message = "";
      if (info[0] == 0 && info[1] == 0) bt_socket.close();
    } catch (IOException e) {
      success = false;
      failure_message = e.getMessage();
    }
    return success;
  }

  @Override
  public void onPreExecute() {
    if (show_progress_dialog) {
      Context context = MainActivity.getContext();
      progress_dialog = new ProgressDialog(context);
      progress_dialog.setTitle(context.getString(R.string.bt_progress_title));
      progress_dialog.setMessage(context.getString(R.string.bt_progress_message));
      progress_dialog.setIndeterminate(true);
      progress_dialog.setCancelable(false);
      progress_dialog.show();
    }
  }

  @Override
  public void onPostExecute(Boolean success) {
    if (show_progress_dialog)
      progress_dialog.dismiss();
    if (success)
      notify.received_data(size, info);
    else {
      Context context = MainActivity.getContext();
      String message = context.getString(R.string.bt_failed_to_write) + " " + failure_message;
      new AlertDialog.Builder(context).setTitle(context.getString(R.string.no_bluetooth_title))
          .setMessage(message)
          .setPositiveButton(context.getString(R.string.understood), null)
          .show();
      Nim_Game_View view = (((MainActivity) context).findViewById(R.id.game_view));
      view.emergency_start_game();
    }
  }

  private ProgressDialog progress_dialog;
  private BluetoothSocket bt_socket;
  private BTR_Listener notify;
  private boolean show_progress_dialog;
  private final byte [] info = new byte[21];
  private int size;
  private String failure_message;

}


