package etfos.catalinac.projekt.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import etfos.catalinac.projekt.R;

public class NumericActivity extends Activity implements View.OnClickListener{
    static Button sensorView;
    private boolean modeClicked;
    private Button liveModeBtn;
    IncomingHandler bluetoothIn;

    final static int handlerState = 0;                        //used to identify handler message
    final Integer DISTANCE_THRESHOLD = 200;
    final Integer DISTANCE_LIMIT = 10;
    final Integer MIN_PERIOD = 100;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private static StringBuilder recDataString;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    private volatile SoundPool soundPool;
    private int soundID;
    private Thread counter;
    boolean loaded = false;
    boolean counterRunning = false;
    private static volatile Integer distance = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_numeric);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Link the buttons and textViews to respective views
        sensorView = (Button) findViewById(R.id.numericBtn);

        liveModeBtn = (Button) findViewById(R.id.liveModeBtn);
        liveModeBtn.setOnClickListener(this);

        soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {loaded = true;
            }
        });

        soundID = soundPool.load(this, R.raw.beep, 1);

        bluetoothIn = new IncomingHandler();

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        switch(v.getId()) {
            case R.id.liveModeBtn:
                if(!modeClicked){
                    i.setClass(this, LiveActivity.class);
                    i.putExtra(MainActivity.EXTRA_DEVICE_ADDRESS, address);
                    liveModeBtn.setBackgroundResource(R.drawable.mode_button_connecting);
                    liveModeBtn.setText(R.string.waitText);
                    liveModeBtn.setTextColor(Color.BLACK);
                    modeClicked = true;
                    this.startActivity(i);
                }
                break;
        }
    }

    private static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == handlerState) {                                     //if message is what we want
                String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                recDataString.append(readMessage);                                      //keep appending to string until ~
                int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                    {
                        String sensor = recDataString.substring(1, endOfLineIndex);             //get sensor value from string between indices 1-5
                        if (!sensor.equals("-1")) {
                            String text = sensor + "cm";
                            sensorView.setText(text);    //update the textviews with sensor values
                            distance = Integer.parseInt(sensor);
                        }

                    }
                    recDataString.delete(0, recDataString.length());
                }
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        counter = new Thread(new Runnable() {
            @Override
            public void run() {
                Long diff, lastTime = System.currentTimeMillis();
                counterRunning = true;

                while (counterRunning) {
                    diff = System.currentTimeMillis() - lastTime;
                    if (distance <= DISTANCE_THRESHOLD && diff >= MIN_PERIOD && diff > distance * DISTANCE_LIMIT) {
                        soundPool.play(soundID, 1, 1, 1, 0, 1f);
                        lastTime = System.currentTimeMillis();
                    }
                }
            }
        });

        modeClicked = false;

        recDataString = new StringBuilder();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try

        {
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");

        counter.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        counterRunning = false;
        try {
            counter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
