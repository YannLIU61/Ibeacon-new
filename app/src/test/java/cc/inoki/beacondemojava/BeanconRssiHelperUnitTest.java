package cc.inoki.beacondemojava;

import org.junit.Test;

import cc.inoki.beacondemojava.utils.BeaconRssiHelper;

import static org.junit.Assert.*;

public class BeanconRssiHelperUnitTest {
    private final static double EPSILON = 1e-10;

    @Test
    public void zeroRSSIMean() {
        BeaconRssiHelper helper = new BeaconRssiHelper();
        assertEquals(0 , helper.mean(), BeanconRssiHelperUnitTest.EPSILON);
    }

    @Test
    public void oneRSSIMean() {
        BeaconRssiHelper helper = new BeaconRssiHelper();
        helper.addRssiRecord(-56);
        assertEquals(-56 , helper.mean(), BeanconRssiHelperUnitTest.EPSILON);
    }

    @Test
    public void serveralRSSIMean() {
        BeaconRssiHelper helper = new BeaconRssiHelper();
        helper.addRssiRecord(-56);
        helper.addRssiRecord(-57);
        helper.addRssiRecord(-58);

        assertEquals(-57 , helper.mean(), BeanconRssiHelperUnitTest.EPSILON);
    }
}
