package cc.inoki.beacondemojava.utils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The data processing class
 */
public class BeaconRelativeDistanceRssiHelper {
    /**
     * Implementation of a singleton design patter to provide the possibility of load the same instance
     * no matter where we want to use it
     *
     * As a platform with lots of threads, we need to guarantee thread safe in getInstance() method
     */
    private static BeaconRelativeDistanceRssiHelper instance;

    private BeaconRelativeDistanceRssiHelper() {
        this.clear();
    }

    synchronized public static BeaconRelativeDistanceRssiHelper getInstance() {
        if (BeaconRelativeDistanceRssiHelper.instance == null) {
            // if instance is null, initialize
            BeaconRelativeDistanceRssiHelper.instance = new BeaconRelativeDistanceRssiHelper();
        }

        return instance;
    }

    private BeaconRssiHelper beaconRssiHelper;

    /**
     * RSSI, a integer <= 0
     */
    private int rssi;       // Last added RSSI
    private int lastRssi;   // Last added rssi

    private Map<Map.Entry<Integer, Integer>, Map.Entry<Float, Float>> toleranceEpsilonInRangeItems;

    private float tolerance;    // Tolerance to detect a balance point in such situation
    private float epsilon;      // Tolerance to detect a small movement in such situation

    public void addRssi(int rssi) {
        // System.out.println("Adding " + rssi + " current mean " + this.beaconRssiHelper.mean());
        if (Math.abs(rssi - this.beaconRssiHelper.mean()) > this.tolerance){
            if (this.rssi != BeaconRelativeDistanceRssiHelper.RSSI_NOT_DEFINED)
                this.lastRssi = Math.round(this.beaconRssiHelper.mean());
            else
                this.lastRssi = BeaconRelativeDistanceRssiHelper.RSSI_NOT_DEFINED;

            // Clear old data in old situation because of a possibly great move
            this.beaconRssiHelper = new BeaconRssiHelper();
            this.beaconRssiHelper.addRssiRecord(rssi);
        } else {
            this.beaconRssiHelper.addRssiRecord(rssi);

            this.lastRssi = this.rssi;
        }

        float _mean = this.beaconRssiHelper.mean();

        // Refresh tolerance and epsilon according to mean
        Set<Map.Entry<Map.Entry<Integer, Integer>, Map.Entry<Float, Float>>> toleranceEpsilonInRangeItemKeys =
                this.toleranceEpsilonInRangeItems.entrySet();
        for (Iterator<Map.Entry<Map.Entry<Integer, Integer>, Map.Entry<Float, Float>>> iterator =
                toleranceEpsilonInRangeItemKeys.iterator(); iterator.hasNext();) {
            Map.Entry<Map.Entry<Integer, Integer>, Map.Entry<Float, Float>> range = iterator.next();

            if (range.getKey().getKey() < _mean && range.getKey().getValue() >= _mean){
                this.tolerance = range.getValue().getKey();    // Set tolerance
                this.epsilon = range.getValue().getValue();     // Set epsilon
                break;
            }
        }

        this.rssi = rssi;
    }

    public int getRelativeDistance() {
        // If rssi or lastRssi is not defined,
        // we have not enough data to determine whether we are approaching or far away
        if (this.rssi == BeaconRelativeDistanceRssiHelper.RSSI_NOT_DEFINED ||
                this.lastRssi == BeaconRelativeDistanceRssiHelper.RSSI_NOT_DEFINED)
        {
            return NEED_MORE_DATA;
        }

        if (Math.abs(this.rssi - this.lastRssi) > this.epsilon) {
            if (this.rssi > this.lastRssi)
                return BeaconRelativeDistanceRssiHelper.NEAR;
            else
                return BeaconRelativeDistanceRssiHelper.FAR;
        } else {
            return BeaconRelativeDistanceRssiHelper.NO_CHANGE;
        }
    }

    /**
     *
     * @param min min RSSI
     * @param max max RSSI
     * @param tolerance tolerance (min criteria to check great moving) will change to
     * @param epsilon epsilon (min criteria to check small moving) will change to
     * @return instance
     */
    public BeaconRelativeDistanceRssiHelper addRangeToleranceEpsilon(int min, int max, float tolerance, float epsilon) {
        // Data normalize
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        if (min < RSSI_MIN) min = RSSI_MIN;
        if (max > RSSI_MAX) max = RSSI_MAX;

        // Add it directly to items, we will take the first one to which we match
        this.toleranceEpsilonInRangeItems.put(
                new AbstractMap.SimpleEntry<>(min, max),
                new AbstractMap.SimpleEntry<>(tolerance, epsilon));
        return this;
    }

    public BeaconRelativeDistanceRssiHelper clear() {
        this.beaconRssiHelper = new BeaconRssiHelper();
        this.rssi = BeaconRelativeDistanceRssiHelper.RSSI_NOT_DEFINED;
        this.lastRssi = BeaconRelativeDistanceRssiHelper.RSSI_NOT_DEFINED;
        this.tolerance = 0;
        this.epsilon = 0;

        return this.clearRange();
    }

    public BeaconRelativeDistanceRssiHelper clearRange() {
        this.toleranceEpsilonInRangeItems = new HashMap<>();

        return this;
    }

    public BeaconRssiHelper getBeaconRssiHelper(){
        return  this.beaconRssiHelper;
    }

    // Constants
    public static final int RSSI_NOT_DEFINED = 1;

    public static final int FAR = -1;
    public static final int NO_CHANGE = 0;
    public static final int NEAR = 1;
    public static final int NEED_MORE_DATA = 100;

    public static final int RSSI_MIN = -10000;
    public static final int RSSI_MAX = 1;
}
