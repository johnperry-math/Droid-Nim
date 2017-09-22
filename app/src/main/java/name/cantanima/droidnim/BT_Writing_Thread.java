package name.cantanima.droidnim;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cantanima on 9/5/17.
 */

public class BT_Writing_Thread extends AsyncTask<Byte [], Integer, Boolean> {

  private String tag = "BT_Writing_Thread";
  private byte[] data_to_write;

  public BT_Writing_Thread(Context main, BluetoothSocket socket) {
    Log.d(tag, "creating");
    context = main;
    bt_socket = socket;
  }

  @Override
  protected Boolean doInBackground(Byte[] ...params) {
    Log.d(tag, "doing");
    try {
      bt_output_stream = bt_socket.getOutputStream();
      int n = params[0][0] + 1;
      Log.d(tag, String.valueOf(n));
      byte [] info = new byte [n];
      for (int i = 0; i < n; ++i) {
        Log.d(tag, String.valueOf(params[0][i]));
        info[i] = params[0][i];
      }
      bt_output_stream.write(info);
      success = true;
      failure_message = "";
    } catch (IOException e) {
      success = false;
      failure_message = e.getMessage();
      Log.d(tag, failure_message);
    }
    return success;
  }

  @Override
  public void onPostExecute(Boolean success) {
    if (!success) {
      String message = context.getString(R.string.bt_failed_to_write) + " " + failure_message;
      new AlertDialog.Builder(context).setTitle(context.getString(R.string.no_bluetooth_title))
          .setMessage(message)
          .setPositiveButton(context.getString(R.string.understood), null)
          .show();
    }
  }

  OutputStream bt_output_stream;
  BluetoothSocket bt_socket;
  Context context;
  boolean success;
  String failure_message;

}

