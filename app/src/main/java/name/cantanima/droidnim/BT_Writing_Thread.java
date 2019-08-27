package name.cantanima.droidnim;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
//import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cantanima on 9/5/17.
 */

public class BT_Writing_Thread extends AsyncTask<Byte [], Integer, Boolean> {

  //private String tag = "BT_Writing_Thread";

  public BT_Writing_Thread(BluetoothSocket socket) {
    bt_socket = socket;
  }

  @Override
  protected Boolean doInBackground(Byte[] ...params) {
    boolean success;
    OutputStream bt_output_stream;
    try {
      bt_output_stream = bt_socket.getOutputStream();
      int n = params[0][0] + 2;
      byte [] info = new byte [n];
      for (int i = 0; i < n; ++i) {
        info[i] = params[0][i];
      }
      bt_output_stream.write(info);
      if (info[0] == 0 && info[1] == 0) {
        try {
          Thread.sleep(1000);
        } catch (Exception e) {
          // don't care
        }
        bt_output_stream.close();
        bt_socket.close();
      }
      success = true;
      failure_message = "";
    } catch (IOException e) {
      success = false;
      failure_message = e.getMessage();
    }
    return success;
  }

  @Override
  public void onPostExecute(Boolean success) {
    if (!success) {
      Context context = MainActivity.getContext();
      String message = context.getString(R.string.bt_failed_to_write) + " " + failure_message;
      new AlertDialog.Builder(context).setTitle(context.getString(R.string.no_bluetooth_title))
          .setMessage(message)
          .setPositiveButton(context.getString(R.string.understood), null)
          .show();
    }
  }

  private BluetoothSocket bt_socket;
  private String failure_message;

}

