package etfos.catalinac.projekt.activities;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;
import etfos.catalinac.projekt.R;
import etfos.catalinac.projekt.utility.BluetoothConnector;

public class NumericActivity extends AppCompatActivity {
    Button connectionButton;

    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothConnector bluetoothConnector;

    private ProgressDialog progress;
    private boolean isBtConnected = false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra("address");

        setContentView(R.layout.activity_numeric);

        connectionButton = (Button) findViewById(R.id.numericBtn);

        new ConnectBT().execute(); //Call the class to connect
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = false; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(NumericActivity.this, "Connecting...", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            // TODO attempt connection
            progress.dismiss();
            System.out.println(address);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
