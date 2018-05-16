package s3542977.com.tqr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ConnectBluetoothThread extends Thread{
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isConnected = false;

    ConnectBluetoothThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e("Fail", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.d("Yes", "yo");
            isConnected = true;
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e("Nah", String.valueOf(connectException));
            try {
                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                mmSocket.connect();
                isConnected = true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("Success", "It worked");

//        beginListenForData();
    }

    public boolean isConnected(){
        return isConnected;
    }

    private void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        final InputStream mmInStream;
        final OutputStream mmOutStream;

        try {
            mmInStream = mmSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        byte[] mmBuffer = new byte[1024];

        int numBytes;

        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                Message readMsg = handler.obtainMessage(
                        0, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d("fail", "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Fail", "Could not close the client socket", e);
        }
    }
}