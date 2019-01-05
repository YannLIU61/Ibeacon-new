package cc.inoki.beacondemojava.utils;

import java.util.ArrayList;

public class BeaconRssiHelper {

    private ArrayList<Integer> rssiRecords;

    private float _mean;

    public BeaconRssiHelper(){
        this.rssiRecords = new ArrayList<>();
        this._mean = 0;
    }

    public synchronized void addRssiRecord(int rssi) {
        this.rssiRecords.add(rssi);

        if (this.rssiRecords.size() == 1) this._mean = rssi;
        else {
            this._mean = (this._mean * (this.rssiRecords.size() - 1) + rssi ) / this.rssiRecords.size();
        }
    }

    public float mean(){
        return this._mean;
    }

}
