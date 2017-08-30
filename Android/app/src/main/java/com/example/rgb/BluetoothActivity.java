package com.example.rgb;

import android.content.*;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.widget.Toast;

import java.util.Set;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.logging.Handler;

import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import android.support.v7.app.AppCompatActivity
        ;
import android.os.Bundle;

import com.example.rgb.BluetoothClass;

import static com.example.rgb.R.id.buttonSend;

public class BluetoothActivity extends AppCompatActivity {

    /*
    SharedPreferences.Editor editor = getSharedPreferences(myPreferredBluetoothAdapter, MODE_PRIVATE).edit();
    editor.putString("name", "Elena");
    editor.commit();
    */
    //Bluetooth bluetooth;//  = new Bluetooth();
    //Layout
    Button buttonEnableBluetooth, buttonDiscoverBluetooth, buttonConnectBluetooth, buttonSend, buttonDeletePreferences;
    TextView textViewBluetoothData, textViewBluetoothStatus, textViewAction;
    ListView bluetoothDevicesList;

    //Bluetooth
    BluetoothSocket btSocket;
    BluetoothSocket mmSocket;

    boolean isBtConnected;
    BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();                     //El meu dispositiu Bluetooth (mòvil/smartphone)
    BluetoothDevice lampBluetoothDevice;// =  myBluetoothAdapter.getRemoteDevice("80:19:34:8E:DA:4F"); //La Làmpara RGB
    String  lampBluetoothDeviceMAC;


    final int REQUEST_ENABLE_BT = 13;
    String myMACAddress;
    public static String EXTRA_ADDRESS = "device_address";
    private Set<BluetoothDevice> pairedDevices;
    UUID MY_UUID;

    //Preferences
    public static final String PREFS_NAME = "MyPrefsFile";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public BluetoothActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonEnableBluetooth = (Button) findViewById(R.id.buttonEnableBluetooth);
        buttonConnectBluetooth = (Button) findViewById(R.id.buttonConnectBluetooth);
        buttonDiscoverBluetooth = (Button) findViewById(R.id.buttonDiscoverBluetooth);
        buttonDeletePreferences = (Button) findViewById(R.id.buttonDeletePreferences);
        textViewBluetoothStatus = (TextView) findViewById(R.id.textViewBluetoothStatus);
        textViewBluetoothData = (TextView) findViewById(R.id.textViewBluetoothData);
        textViewAction = (TextView) findViewById(R.id.textViewAction);
        bluetoothDevicesList = (ListView) findViewById(R.id.listViewDevices);

        isBtConnected = false;
        btSocket = null;
        myMACAddress= android.provider.Settings.Secure.getString(this.getContentResolver(), "bluetooth_address");
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        lampBluetoothDeviceMAC = settings.getString("lampBluetoothDeviceMAC", null);
        System.out.println("lampBluetoothDeviceMAC: " + lampBluetoothDeviceMAC);

        if (lampBluetoothDeviceMAC!= null && !lampBluetoothDeviceMAC.equals("none")){
            System.out.println("Creating lampBluetoothDevice");
            lampBluetoothDevice =  myBluetoothAdapter.getRemoteDevice(lampBluetoothDeviceMAC);
        }

        else{
            lampBluetoothDeviceMAC = "none";
            buttonConnectBluetooth.setEnabled(false);
            buttonSend.setEnabled(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                     textViewAction.setText("SysMessage: select a device which to connect!");
                }
            });
        }

        //bluetooth = new Bluetooth(lampBluetoothDeviceMAC);

        if (myBluetoothAdapter.isEnabled()) {
            pairedDevicesList();
        }
        textViewBluetoothData.setText(BluetoothClass.getBluetoothData());
        textViewBluetoothStatus.setText(BluetoothClass.getBluetoothStatus());
        buttonEnableBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
                }
                if (!myBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    //onActivityResult();
                }
            }
        });

        buttonDeletePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.setDefaults("lampBluetoothDeviceMAC", null, MyApplication.getAppContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewBluetoothData.setText(BluetoothClass.getBluetoothData());
                        textViewBluetoothStatus.setText(BluetoothClass.getBluetoothStatus());
                        textViewAction.setText("SysMessage: preferences deleted.");
                    }
                });

            }
        });

        buttonConnectBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    //onActivityResult();
                    new Thread(new Runnable() {
                        public void run() {
                            System.out.println("Calling bluetooth.ConnectBluetooth to connect to " + lampBluetoothDevice);
                            BluetoothClass.ConnectBluetooth(lampBluetoothDevice);
                        }
                    }).start();

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewAction.setText("SysMessage: connecting to device " + lampBluetoothDevice.getName() + "...");
                        }
                    });
                    new Thread(new Runnable() {
                        public void run() {
                            System.out.println("Calling bluetooth.ConnectBluetooth to connect to " + lampBluetoothDevice);
                            if(BluetoothClass.ConnectBluetooth(lampBluetoothDevice)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewBluetoothData.setText(BluetoothClass.getBluetoothData());
                                        textViewBluetoothStatus.setText(BluetoothClass.getBluetoothStatus());
                                        textViewAction.setText("SysMessage: Device connected to "  + lampBluetoothDevice.getName());
                                        buttonSend.setEnabled(true);
                                        buttonConnectBluetooth.setEnabled(false);

                                        android.content.Context context = getApplicationContext();
                                        CharSequence text = "Device successfully connected to "  + lampBluetoothDevice.getName();
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewBluetoothData.setText(BluetoothClass.getBluetoothData());
                                        textViewBluetoothStatus.setText(BluetoothClass.getBluetoothStatus());
                                        textViewAction.setText("SysMessage: Could NOT connect to " + lampBluetoothDevice.getName());
                                        buttonSend.setEnabled(true);
                                        buttonConnectBluetooth.setEnabled(false);

                                        android.content.Context context = getApplicationContext();
                                        CharSequence text = "Could NOT connect to "  + lampBluetoothDevice.getName();
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });

        buttonDiscoverBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter.isDiscovering()) {
                    myBluetoothAdapter.cancelDiscovery();
                }
                myBluetoothAdapter.startDiscovery();
                pairedDevicesList();
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothClass.sendData("128.128.128,");
             }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int RESULT_OK = 0xffffffff;
        int RESULT_CANCELED = 0x00000000;

        if (requestCode == 13) {
            if(resultCode == RESULT_OK){
                //String result=data.getStringExtra("result");
                System.out.println("Bluetooth succesfully enabled.");
                pairedDevicesList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewBluetoothData.setText(BluetoothClass.getBluetoothData());
                        textViewBluetoothStatus.setText(BluetoothClass.getBluetoothStatus());
                        textViewAction.setText("SysMessage: Bluetooth succesfully enabled.");
                        android.content.Context context = getApplicationContext();
                        CharSequence text = "Bluetooth succesfully enabled";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                System.out.println("Bluetooth NOT enabled.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewBluetoothData.setText(BluetoothClass.getBluetoothData());
                        textViewBluetoothStatus.setText(BluetoothClass.getBluetoothStatus());
                        textViewAction.setText("SysMessage: Bluetooth NOT enabled.");
                        android.content.Context context = getApplicationContext();
                        CharSequence text = "Bluetooth NOT succesfully enabled";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
            }
        }
    }//onActivityResult

    private Button findViewById(Button buttonSend) {
        return buttonSend;
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Bluetooth Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
    }

    private void pairedDevicesList() {

        pairedDevices = myBluetoothAdapter.getBondedDevices();
        final ArrayList list = new ArrayList();
        // If there are paired devices
        int i=0;
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice bt : pairedDevices) {

                if (i==0){
                    lampBluetoothDevice = myBluetoothAdapter.getRemoteDevice(bt.getAddress());
                    //MY_UUID=bt.getUuids()[0].getUuid();
                    System.out.println("lampBluetoothDevice: " + lampBluetoothDevice);// + " MY_UUID:" + bt.getUuids());
                }
                i++;
                //Add the name and address to an array adapter to show in a ListView
                //String uuids = bt.getUuids().toString();//bt.getUuids()[0].getUuid()
                list.add(bt.getName() + "\n" + bt.getAddress());// + uuids); //Get the device's name, address and UUID
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        bluetoothDevicesList.setAdapter(adapter);
        bluetoothDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lines [] = list.get(position).toString().split("\\r?\\n");
                System.out.println("Selecting Device to connect: " + lines[0] + ", MAC: " + lines[1]);

                lampBluetoothDevice = myBluetoothAdapter.getRemoteDevice(lines[1]);
                buttonConnectBluetooth.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewAction.setText("SysMessage: " + lampBluetoothDevice.getName() +  " selected to connect.");// + ", " + mmDevice.getAddress());
                    }
                });
            }
        });
    }
}