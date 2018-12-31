package cc.inoki.beacondemojava;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cc.inoki.beacondemojava.utils.BeaconRssiHelper;
import cc.inoki.beacondemojava.utils.PermissionManager;

public class ScanActivity extends Activity implements Runnable{

    private static final String LOG_TAG = "ScanActivity";

    // Components
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private BeaconRssiHelper beaconRssiHelper;

    // Views
    private FrameLayout layout;

    // Values
    private boolean isScanning = false;

    private String selectedDeviceMacAddress;
    private String selectedDeviceUUID;


    private double avgDis = 0.0;
    private double dis = 0.0;
    private double lastdis = 0.0;
    private double varRssi = 0.0;
    private int rssiTolerance = 2;
    private ArrayList<Double> dislist = new ArrayList<Double>();


    protected static double calculateAccuracy(double rssi) {
        DecimalFormat df = new DecimalFormat("#.0");
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        Log.i(LOG_TAG, "calculating accuracy based on rssi and txPower of "+rssi+" "+ "-65");


        double ratio = rssi*1.0/(-65);
        if (ratio < 1.0) {
            return Double.parseDouble(df.format(Math.pow(ratio,10)));
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return Double.parseDouble(df.format(accuracy));
        }


    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(LOG_TAG, "Failed:" + errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(LOG_TAG, "Result length:" + results.size());
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // After the filter, the result will be only our device

            ScanActivity.this.dis = calculateAccuracy(result.getRssi());

            Log.i(LOG_TAG,"Calcule distance: "+ ScanActivity.this.dis);

            if (beaconRssiHelper == null){
                beaconRssiHelper = new BeaconRssiHelper();
                beaconRssiHelper.addRssiRecord(result.getRssi());
                Log.i(LOG_TAG, "RSSI: " + result.getRssi());
            }
            else {
                beaconRssiHelper.addRssiRecord(result.getRssi());
                Log.i(LOG_TAG,"Calcule mean "+ beaconRssiHelper.mean());
                if (Math.abs(result.getRssi() - beaconRssiHelper.mean()) > 3){

                    if(ScanActivity.this.dis<1.0){

                        if (ScanActivity.this.lastdis > ScanActivity.this.dis + 0.1) {
                            // Near
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorHot));
                            Log.i(LOG_TAG, "Closing in 1m......... ");
                        } else if (ScanActivity.this.lastdis < ScanActivity.this.dis -0.1) {
                            // far
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorCold));
                            Log.i(LOG_TAG, "Going far still in 1m......... ");
                        } else {
                            layout.setBackgroundColor(Color.WHITE);
                            Log.i(LOG_TAG, "No change <1........");
                        }

                    }else if(1.0<=ScanActivity.this.dis && ScanActivity.this.dis<10.0){
                        if (ScanActivity.this.lastdis > ScanActivity.this.dis + 1) {
                            // Near
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorHot));
                            Log.i(LOG_TAG, "Closing in 1 and 10m......... ");
                        } else if (ScanActivity.this.lastdis < ScanActivity.this.dis -1) {
                            // far
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorCold));
                            Log.i(LOG_TAG, "Going far between 1 and 10 m......... ");
                        } else {
                            layout.setBackgroundColor(Color.WHITE);
                            Log.i(LOG_TAG, "No change 1~10........");
                        }
                    }
                    else if(10.0<=ScanActivity.this.dis && ScanActivity.this.dis<20.0){
                        if (ScanActivity.this.lastdis > ScanActivity.this.dis + 2.0) {
                            // Near
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorHot));
                            Log.i(LOG_TAG, "Closing in 10 and 20......... ");
                        } else if (ScanActivity.this.lastdis < ScanActivity.this.dis -2.0) {
                            // far
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorCold));
                            Log.i(LOG_TAG, "Going far between 10 and 20 m......... ");
                        } else {
                            layout.setBackgroundColor(Color.WHITE);
                            Log.i(LOG_TAG, "No change 10~20........");
                        }
                    } else{
                        if (ScanActivity.this.lastdis > ScanActivity.this.dis + 5.0) {
                            // Near
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorHot));
                            Log.i(LOG_TAG, "Closing >20......... ");
                        } else if (ScanActivity.this.lastdis < ScanActivity.this.dis -5.0) {
                            // far
                            ScanActivity.this.layout.setBackgroundColor(getColor(R.color.colorCold));
                            Log.i(LOG_TAG, "Going far >20......... ");
                        } else {
                            layout.setBackgroundColor(Color.WHITE);
                            Log.i(LOG_TAG, "No change >20........");
                        }
                    }
                    beaconRssiHelper = new BeaconRssiHelper();
                    beaconRssiHelper.addRssiRecord(result.getRssi());
                    ScanActivity.this.lastdis = ScanActivity.this.dis;

//                    TextView distance = findViewById(R.id.device_distance);
//                    distance.setText(String.valueOf( ScanActivity.this.lastdis));

                }
                else {
                    Log.i(LOG_TAG, "RSSI: " + result.getRssi() + " Mean: " + beaconRssiHelper.mean());
                    beaconRssiHelper.addRssiRecord(result.getRssi());
                    layout.setBackgroundColor(Color.WHITE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        // Get Selected Device
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();


        this.selectedDeviceMacAddress = bundle.getString("mac_address", "123");
        this.selectedDeviceUUID = bundle.getString("uuid", "456");



        // Get views
        this.layout = findViewById(R.id.scan_background);
        TextView mac = findViewById(R.id.device_mac);
        TextView uuid = findViewById(R.id.device_uuid);

        mac.setText(this.selectedDeviceMacAddress);
        uuid.setText(this.selectedDeviceUUID);


        // Get adapter
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionManager.PERMISSION_REQUEST_COARSE_LOCATION);
        }
        else {
            scanHandler.post(this);
        }

        Log.i(LOG_TAG, "Test3");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // When we can access location information
                    scanHandler.post(this);
                }
                break;
        }
    }

    @Override
    public void run() {
        if (isScanning) {
            if (btAdapter != null) {
                btAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            }
        } else {
            if (btAdapter != null) {
                Log.i(LOG_TAG, "Scan begin for " + this.selectedDeviceMacAddress);
                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                List<ScanFilter> filters = new ArrayList<>();
                filters.add(
                        new ScanFilter.Builder()
                                .setDeviceAddress(this.selectedDeviceMacAddress)
                                .build());
                btAdapter.getBluetoothLeScanner().startScan(filters, settings, scanCallback);
            }
        }

        isScanning = !isScanning;
    }
}
