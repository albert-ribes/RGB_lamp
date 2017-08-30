package com.example.rgb;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by albert on 05/12/2016.
 */


/*

What good are static classes? A good use of a static class is in defining one-off, utility and/or library classes where instantiation would not make sense. A great example is the Math class that contains some mathematical constants such as PI and E and simply provides mathematical calculations. Requiring instantiation in such a case would be unnecessary and confusing. See Java's Math class. Notice that it is final and all of its members are static. If Java allowed top-level classes to be declared static then the Math class would indeed be static.

1.- Declare your class final - Prevents extension of the class since extending a static class makes no sense
2.- Make the constructor private - Prevents instantiation by client code as it makes no sense to instantiate a static class
3.- Make all the members and functions of the class static - Since the class cannot be instantiated no instance methods can be called or instance fields accessed
4.- Note that the compiler will not prevent you from declaring an instance (non-static) member. The issue will only show up if you attempt to call the instance member

*/

public final class BluetoothClass {

    // variables
    static boolean isBtConnected;
    static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();                 //El meu dispositiu Bluetooth (mòvil/smartphone)
    static BluetoothDevice lampBluetoothDevice;                                                        //La Làmpara RGB
    static String preferredMAC;
    static BluetoothSocket mySocket;

    // Constructor
    private BluetoothClass(/*String preferredMAC*/) {
        //this.preferredMAC = preferredMAC;
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println("Constructor 'BluetoothClass' executed,");
        /*
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        */
    }

    // Methods

    public static void deletePreferredMAC(){
        preferredMAC="none";
    }

    public static void updatePreferredMAC(String MAC){
        preferredMAC = MAC;
    }

    public static boolean btConnect(int newValue) {
        return true;
    }

    public static boolean isConnected(int newValue) {
        return true;
    }

    public static String getMyMAC(){
        //String myMACAddress= android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address");
        //return myMACAddress;
        return "wer";
    }

    public static String getBluetoothData(){
        //textViewBluetoothData.setText("\nBluetooth_Name: " + myBluetoothAdapter.getName() + "\nBluetooth_Address: " + myMACAddress + "\nPreferred MAC: " + lampBluetoothDeviceMAC);
        String BluetoothData = "Bluetooth_Name: " + myBluetoothAdapter.getName() + "\nPreferred MAC: " + MyApplication.getDefaults("lampBluetoothDeviceMAC", MyApplication.getAppContext());
        return BluetoothData;
    }

    public static String getBluetoothStatus(){
        String BluetoothState="";
        switch (myBluetoothAdapter.getState()){
            case 0x0000000a:
                BluetoothState="STATE_OFF";
                break;
            case 0x0000000b:
                BluetoothState="STATE_TURNING_ON";
                break;
            case 0x0000000c:
                BluetoothState="STATE_ON";
                break;
            case 0x0000000d:
                BluetoothState="STATE_TURNING_OFF";
                break;
        }
        String BluetoothStatus = "Bluetooth_Enabled: " + myBluetoothAdapter.isEnabled() + "\nBluetooth_State: " + BluetoothState + "\nConnection Status: " + isBtConnected;
        return BluetoothStatus;
    }

    public static boolean sendData(String message) {
        boolean result;
        OutputStream btOutStream = null;
        try {
            //System.out.println("Getting OutputStream...");
            btOutStream = mySocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] sendBytes = message.getBytes();

        try {
            //System.out.println("Writing " + sendBytes);
            btOutStream.write(sendBytes);
            //System.out.println("Write successful");
            result=true;
        } catch (IOException e) {
            result=false;
            e.printStackTrace();
        }
           return result;
    }

    public static boolean ConnectBluetooth(BluetoothDevice device) {
        /*
        El mètode es connecta a un dispositiu Bluetooth (BluetoothDevice device).
        Primer crea un socket sobre el dispositiu Bluetooth (mySocket = device.createRfcommSocketToServiceRecord(MY_UUID))
        i després s'hi connecta (mySocket.connect()).
        El mètode retorna la variable "isBtConnected", en la qual s'emmagatzema l'estat de la connexió.
        */
        final BluetoothDevice myDevice;
        BluetoothSocket tmp = null;
        myDevice = device;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            System.out.println("Creating RFCommSocket with UUID " + MY_UUID);
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            System.out.println("Error creating socket!");
        }
        mySocket = tmp;
        System.out.println("BT Socket mySocket created.");

        // Cancel discovery because it will slow down the connection
        myBluetoothAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            System.out.println("Connecting to device" + myDevice.getName() + ", " + myDevice.getAddress());
            mySocket.connect();
            isBtConnected = true;
        } catch (IOException connectException) {
            isBtConnected = false;
            // Unable to connect; close the socket and get out
            try {
                mySocket.close();
            } catch (IOException closeException) {
            }
        }
            if (isBtConnected == true) {

                System.out.println("Connected to remote device: " + mySocket.getRemoteDevice());
                MyApplication.setDefaults("lampBluetoothDeviceMAC", mySocket.getRemoteDevice().toString(), MyApplication.getAppContext());
            } else {
                System.out.println("Connection failed!");
            }
        return isBtConnected;
    }


}


