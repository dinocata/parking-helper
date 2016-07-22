package etfos.catalinac.projekt.activities;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;
import etfos.catalinac.projekt.R;
import etfos.catalinac.projekt.utility.BluetoothConnector;

public class NumericActivity extends AppCompatActivity {

    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothConnector bluetoothConnector;

    private ProgressDialog progress;
    private BluetoothConnector.BluetoothSocketWrapper bluetoothSocket;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra("address");

        setContentView(R.layout.activity_numeric);

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
            if (!isBtConnected) {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice bluetoothDevice = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                bluetoothConnector = new BluetoothConnector(bluetoothDevice, true, myBluetooth, null);

                try {
                    bluetoothSocket = bluetoothConnector.connect();
                } catch (IOException e) {
                    System.out.println("FAIL BRO!!");
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
