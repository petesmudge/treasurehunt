package com.example.petersmith.treasurehunt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;
import android.view.View;

import static android.util.Xml.parse;

/**
 * Created by peter.smith on 27/01/2017.
 *
 * class to handle BT LE scanning and results
 *
 * Need to define accessor methods needed - suggesting:
 *          Start()  <-- parameters for scan
 *          callback()  <-- scan results to caller.
 *          Stop() <-- stop scan
 */
/*
public class BeaconScan {

    private BluetoothLeScanner mScanner;
    private ScanSettings mScanSettings;



    private void initBT(){
        final BluetoothManager bluetoothManager =  (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();


        //Create the scan settings
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        //Set scan latency mode. Lower latency, faster device detection/more battery and resources consumption
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //Wrap settings together and save on a settings var (declared globally).
        mScanSettings = scanSettingsBuilder.build();
        //Get the BLE scanner from the BT adapter (var declared globally)
        mScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void startLeScan(boolean start) {
        if (start) {
            //********************
            //START THE BLE SCAN
            //********************
            //Scanning parameters FILTER / SETTINGS / RESULT CALLBACK. Filter are used to define a particular
            //device to scan for. The Callback is defined above as a method.
            mScanner.startScan(null, mScanSettings, mScanCallback);
        }else{
            //Stop scan
            mScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            //Convert advertising bytes to string for a easier parsing. GetBytes may return a NullPointerException. Treat it right(try/catch).
            String advertisingString = byteArrayToHex(result.getScanRecord().getBytes());
            //Print the advertising String in the LOG with other device info (ADDRESS - RSSI - ADVERTISING - NAME)
            Log.i(TAG, result.getDevice().getAddress()+" - RSSI: "+result.getRssi()+"\t - "+advertisingString+" - "+result.getDevice().getName());

            Log.i(TAG,"UID = "+ mUuid);
            if (advertisingString.contains(mUuid.replace("-",""))) {
                if (result.getRssi() > dist) {
                    if(counter == 0){
                        counter = 5;
                        startAnim.interrupt();
                        wave.clearAnimation();
                        wave.setVisibility(View.INVISIBLE);
                        showDialog();
                    }
                }
            }
        }
    };


    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
*/