package s3542977.com.tqr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private boolean isSearching = false;
    LinearLayout linearLayout;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> discoveredDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Switch bluetoothSwitch = findViewById(R.id.blueToothSwitch);

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                    }
                } else {
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                    }
                }
            }
        });

        discoveredDevices = new ArrayList<>();

        linearLayout = findViewById(R.id.layoutBluetooth);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d("Bluetooth", "Not supported");
        }

        if (mBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
        } else {
            bluetoothSwitch.setChecked(false);
        }
    }

    public void buttonClicked(View view) {
        switch (view.getId()) {
            case R.id.discoverableButton:
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                break;
            case R.id.searchButton:
                Button button = findViewById(R.id.searchButton);
                if (isSearching) {
                    mBluetoothAdapter.cancelDiscovery();
                    button.setText(R.string.search_for_devices);
                    isSearching = false;
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }

                    mBluetoothAdapter.startDiscovery();

                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                    button.setText(R.string.Stop_Searching);
                    isSearching = true;
                }
                break;
        }
    }

    private void addDeviceButton(BluetoothDevice device) {
        discoveredDevices.add(device);
        int index = discoveredDevices.size() - 1;

        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button = new Button(this);
        button.setId(index);
        final int id_ = button.getId();
        final String text = "Device name: " + deviceName + "\nDevice MAC address: " + deviceHardwareAddress;

        button.setText(text);
        button.setTextSize(18);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("Button", text);
                connect(id_);
            }
        });

        linearLayout.addView(button, params);
    }

    private void connect(int index) {
        ConnectBluetoothThread connect = new ConnectBluetoothThread(discoveredDevices.get(index), mBluetoothAdapter);
        connect.run();

        if(connect.isConnected()){
            Toast.makeText(this, "Successfully Connected", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Failed to connect", Toast.LENGTH_LONG).show();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                addDeviceButton(device);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
