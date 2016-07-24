package etfos.catalinac.projekt.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

import etfos.catalinac.projekt.R;
import etfos.catalinac.projekt.adapters.DeviceAdapter;
import etfos.catalinac.projekt.models.BtDevice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button startButton;
    Button settingsButton;
    Button deviceListBtn;
    TextView tvSelectedDevice;
    ListView deviceList;

    private final String SELECTED_PREFIX = "Selected device: ";

    private boolean devicesLoaded = false;
    private BluetoothAdapter myBluetooth = null;
    private ArrayList<BtDevice> devices = new ArrayList<>();
    private BtDevice selectedDevice = null;
    private DeviceAdapter deviceAdapter;

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int position, long id)
        {
            selectedDevice = devices.get(position-1);
            deviceList.setVisibility(View.GONE);
            String text = SELECTED_PREFIX + selectedDevice.getName();
            tvSelectedDevice.setText(text);
            startButton.setBackgroundResource(R.drawable.start_button_enabled);
        }
    };

    private void loadDevices(){
        devices.clear();
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices) {
                devices.add(new BtDevice(bt.getName(), bt.getAddress(), R.drawable.device_mobile_phone_icon));
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedDevice = (TextView) findViewById(R.id.tvSelectedDevice);

        startButton = (Button) findViewById(R.id.startBtn);
        settingsButton = (Button) findViewById(R.id.settingsBtn);
        deviceListBtn = (Button)findViewById(R.id.deviceListBtn);

        startButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        deviceListBtn.setOnClickListener(this);

        deviceList = (ListView)findViewById(R.id.deviceListLV);

        TextView textView = new TextView(this);
        textView.setText(R.string.deviceListTitle);

        deviceList.addHeaderView(textView);

        assert deviceList != null;
        deviceList.setVisibility(View.GONE);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (!myBluetooth.isEnabled())
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }

            devicesLoaded = true;
            deviceAdapter = new DeviceAdapter(this, devices);
            deviceList.setAdapter(deviceAdapter);
            deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        }

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        switch(v.getId()){
            case R.id.startBtn:
                if(selectedDevice != null){
                    i.setClass(this, NumericActivity.class);
                    i.putExtra("address", selectedDevice.getAddress());
                    this.startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(), "Please choose a device first", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.settingsBtn:
                i.setClass(this, SettingsActivity.class);
                this.startActivity(i);
                break;
            case R.id.deviceListBtn:
                if(deviceList.getVisibility() == View.GONE && devicesLoaded) {
                    loadDevices();
                    deviceList.setVisibility(View.VISIBLE);
                }else{
                    deviceList.setVisibility(View.GONE);
                    selectedDevice = null;
                    String text = SELECTED_PREFIX + "none";
                    tvSelectedDevice.setText(text);
                    startButton.setBackgroundResource(R.drawable.start_button_disabled);
                }
                break;
        }
    }
}
