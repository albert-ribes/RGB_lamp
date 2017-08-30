package com.example.rgb;

import android.bluetooth.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Set;

public class RGBActivity extends AppCompatActivity {

    SeekBar seekBarRed, seekBarGreen, seekBarBlue;
    TextView valueRed, valueGreen, valueBlue;
    TextView textViewSend;

    Button buttonSend, buttonBluetooth, buttonDiscoverBluetooth, buttonConnectBluetooth;
    SurfaceView surfaceViewColor;
    SurfaceHolder holder;
    Canvas canvas;
    String stringRed, stringGreen, stringBlue;
    String stringRGB;
    Integer intRed, intGreen, intBlue;

    ListView bluetoothDevicesList;
    BluetoothDevice lampBluetoothDevice;

    //Bluetooth
    BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    final int REQUEST_ENABLE_BT = 1;
    public static String EXTRA_ADDRESS = "device_address";
    private Set<BluetoothDevice> pairedDevices;

    //Handler to check Bluetooth status
    private int myInterval = 5000; // 5 seconds by default, can be changed later
    private Handler myHandler;


    public static final String PREFS_NAME = "MyPrefsFile";
    String  lampBluetoothDeviceMAC;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("RGBActivity starting...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        seekBarRed= (SeekBar) findViewById(R.id.seekBarRed);
        seekBarGreen= (SeekBar) findViewById(R.id.seekBarGreen);
        seekBarBlue= (SeekBar) findViewById(R.id.seekBarBlue);

        surfaceViewColor = (SurfaceView) findViewById(R.id.surfaceViewColor);

        valueRed= (TextView) findViewById(R.id.textViewResultRed);
        valueGreen = (TextView) findViewById(R.id.textViewResultGreen);
        valueBlue= (TextView) findViewById(R.id.textViewResultBlue);

        textViewSend=(TextView) findViewById(R.id.textViewSend);

        buttonBluetooth= (Button) findViewById(R.id.buttonBluetooth);
        buttonSend=(Button)findViewById(R.id.buttonSend);
        buttonSend.setEnabled(false);

        // Restore preferences
        lampBluetoothDeviceMAC = MyApplication.getDefaults("lampBluetoothDeviceMAC", MyApplication.getAppContext());
        if (lampBluetoothDeviceMAC!=null && !lampBluetoothDeviceMAC.contains("none")){
            System.out.println("lampBluetoothDeviceMAC: " + lampBluetoothDeviceMAC);
            lampBluetoothDevice =  myBluetoothAdapter.getRemoteDevice(lampBluetoothDeviceMAC);
            if (BluetoothClass.ConnectBluetooth(lampBluetoothDevice)){
                buttonSend.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        android.content.Context context = getApplicationContext();
                        CharSequence text = "Device successfully connected to "  + lampBluetoothDevice.getName();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
            }
            else{
                buttonSend.setEnabled(false);
            }
        }

        if (myBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        }

        buttonBluetooth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RGBActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
        });

        stringRed=String.valueOf(seekBarRed.getProgress());
        intRed=Integer.valueOf(stringRed);
        valueRed.setText(stringRed);
        stringGreen=String.valueOf(seekBarGreen.getProgress());
        intGreen=Integer.valueOf(stringGreen);
        valueGreen.setText(stringGreen);
        stringBlue=String.valueOf(seekBarBlue.getProgress());
        intBlue=Integer.valueOf(stringBlue);
        valueBlue.setText(stringBlue);

        surfaceViewColor.setBackgroundColor(Color.rgb(intRed, intGreen, intBlue));


        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                holder = surfaceViewColor.getHolder();
                    try {
                        synchronized(holder) {
                            canvas = holder.lockCanvas();
                            if(canvas == null) {
                                System.out.println ("lockCanvas returned null");
                            } else {
                                // paint a background color
                                canvas.drawColor(Color.BLACK);
                                // paint a rectangular shape that fill the surface.
                                int border = 20;
                                RectF r = new RectF(border, border, canvas.getWidth()-20, canvas.getHeight()-20);
                                Paint paint = new Paint();
                                paint.setARGB(200, 135, 135, 135); //paint color GRAY+SEMY TRANSPARENT
                                canvas.drawRect(r , paint );
                                holder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    finally{
                        System.out.println("Finaly...");
                    }
                    /*
                    finally {
                        // do this in a finally so that if an exception is thrown
                        // during the above, we don't leave the Surface in an
                        // inconsistent state
                        if (holder != null) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }*/

                /*
                canvas = holder.lockCanvas();
                boolean variable = holder.getSurface().isValid();
                while (!variable){
                    variable = holder.getSurface().isValid();
                }
                holder.unlockCanvasAndPost(canvas);
                // paint a background color
                canvas.drawColor(Color.BLACK);
                // paint a rectangular shape that fill the surface.
                int border = 20;
                RectF r = new RectF(border, border, canvas.getWidth()-20, canvas.getHeight()-20);
                Paint paint = new Paint();
                paint.setARGB(200, 135, 135, 135); //paint color GRAY+SEMY TRANSPARENT
                canvas.drawRect(r , paint );
                holder.unlockCanvasAndPost(canvas);
                */
            }
        });

        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stringRed=String.valueOf(progress);
                 intRed=Integer.valueOf(stringRed);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        valueRed.setText(stringRed);
                        surfaceViewColor.setBackgroundColor(Color.rgb(intRed, intGreen, intBlue));
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stringGreen=String.valueOf(progress);
                intGreen=Integer.valueOf(stringGreen);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        valueGreen.setText(stringGreen);
                        surfaceViewColor.setBackgroundColor(Color.rgb(intRed, intGreen, intBlue));
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stringBlue=String.valueOf(progress);
                intBlue=Integer.valueOf(stringBlue);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        valueBlue.setText(stringBlue);
                        surfaceViewColor.setBackgroundColor(Color.rgb(intRed, intGreen, intBlue));
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringRGB=stringRed + "," + stringGreen + "," + stringBlue+ ".";
                BluetoothClass.sendData(stringRGB);
                //surfaceViewColor.setBackgroundColor(Integer.valueOf(stringRed.concat(stringGreen.concat(stringBlue))));
                //textViewSend.setText("code sent: \"" + stringRGB + "\"");
            }
        });
        myHandler = new Handler();
        startRepeatingTask();
    }

    Runnable myStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                myHandler.postDelayed(myStatusChecker, myInterval);
            }
        }
    };
    void startRepeatingTask() {
        myStatusChecker.run();
    }
    void updateStatus(){
            if (BluetoothClass.sendData(".")){
                buttonSend.setEnabled(true);
                System.out.println("Updating status: BluetoothClass.mySocket is connected");
            }
            else{
                lampBluetoothDevice =  myBluetoothAdapter.getRemoteDevice(lampBluetoothDeviceMAC);
                if (BluetoothClass.ConnectBluetooth(lampBluetoothDevice)){
                    buttonSend.setEnabled(true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            android.content.Context context = getApplicationContext();
                            CharSequence text = "Device successfully connected to "  + lampBluetoothDevice.getName();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });
                    System.out.println("Updating status: BluetoothClass.mySocket is connected");
                }
                else{
                    buttonSend.setEnabled(false);
                    System.out.println("Updating status: BluetoothClass.mySocket is NOT connected()");
                }

            }
    }
    void stopRepeatingTask() {
        myHandler.removeCallbacks(myStatusChecker);
    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();

            String address = info.substring(info.length() - 17);

            /*
            // Make an intent to start next activity.
            Intent i = new Intent(bluetoothDevicesList, ledControl.class);

            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);
            */
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("RGB Page") // TODO: Define a title for the content shown.
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

        if (BluetoothClass.isBtConnected){
            buttonSend.setEnabled(true);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder = surfaceViewColor.getHolder();
                try {
                    synchronized(holder) {
                        canvas = holder.lockCanvas();
                        if(canvas == null) {
                            System.out.println ("lockCanvas returned null");
                        } else {
                            // paint a background color
                            canvas.drawColor(Color.BLACK);
                            // paint a rectangular shape that fill the surface.
                            int border = 20;
                            RectF r = new RectF(border, border, canvas.getWidth()-20, canvas.getHeight()-20);
                            Paint paint = new Paint();
                            paint.setARGB(200, 135, 135, 135); //paint color GRAY+SEMY TRANSPARENT
                            canvas.drawRect(r , paint );
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
                finally{
                    System.out.println("Finaly...");
                }
                    /*
                    finally {
                        // do this in a finally so that if an exception is thrown
                        // during the above, we don't leave the Surface in an
                        // inconsistent state
                        if (holder != null) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }*/

                /*
                canvas = holder.lockCanvas();
                boolean variable = holder.getSurface().isValid();
                while (!variable){
                    variable = holder.getSurface().isValid();
                }
                holder.unlockCanvasAndPost(canvas);
                // paint a background color
                canvas.drawColor(Color.BLACK);
                // paint a rectangular shape that fill the surface.
                int border = 20;
                RectF r = new RectF(border, border, canvas.getWidth()-20, canvas.getHeight()-20);
                Paint paint = new Paint();
                paint.setARGB(200, 135, 135, 135); //paint color GRAY+SEMY TRANSPARENT
                canvas.drawRect(r , paint );
                holder.unlockCanvasAndPost(canvas);
                */
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void onResume() {
        super.onResume();
        updateStatus();
        startRepeatingTask();
    }
}

