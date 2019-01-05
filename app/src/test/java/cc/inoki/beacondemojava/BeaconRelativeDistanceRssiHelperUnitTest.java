package cc.inoki.beacondemojava;

import org.junit.Test;

import cc.inoki.beacondemojava.utils.BeaconRelativeDistanceRssiHelper;

import static org.junit.Assert.*;

public class BeaconRelativeDistanceRssiHelperUnitTest {
    private final static double EPSILON = 1e-10;

    @Test
    public void noRangeNoData() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        assertEquals(beaconRelativeDistanceRssiHelper.getRelativeDistance(),
                BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA);
    }

    @Test
    public void noRangeOneData() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRssi(-28);

        assertEquals(beaconRelativeDistanceRssiHelper.getRelativeDistance(),
                BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA);
    }

    @Test
    public void oneRangeNoData() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  0,3, 1);

        assertEquals(beaconRelativeDistanceRssiHelper.getRelativeDistance(),
                BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA);
    }

    @Test
    public void oneRangeOneData() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  0,3, 1);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(beaconRelativeDistanceRssiHelper.getRelativeDistance(),
                BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA);
    }

    @Test
    public void oneRangeManyDataNoNewGroupFar() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  0,5, 1);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-57);

        assertEquals(BeaconRelativeDistanceRssiHelper.FAR,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());
    }

    @Test
    public void oneRangeManyDataNoNewGroupNear() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  0,5, 1);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-49);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEAR,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());
    }

    @Test
    public void oneRangeManyDataNewGroupFar() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  0,5, 3);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-55);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-54);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-100);

        assertEquals(BeaconRelativeDistanceRssiHelper.FAR,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-98);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());
    }

    @Test
    public void oneRangeManyDataNewGroupNear() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  0,5, 3);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-55);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-30);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEAR,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-32);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());
    }

    @Test
    public void manyRangeManyDataNewGroupNear() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  -100,20, 10).
                addRangeToleranceEpsilon(-100, -60, 20, 8).
                addRangeToleranceEpsilon(-60, -40, 10, 5).
                addRangeToleranceEpsilon(-40, 1, 5, 3);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-55);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-30);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEAR,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-32);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());
    }

    @Test
    public void manyRangeManyDataNewGroupFar() {
        BeaconRelativeDistanceRssiHelper beaconRelativeDistanceRssiHelper =
                BeaconRelativeDistanceRssiHelper.getInstance().clear();
        beaconRelativeDistanceRssiHelper.addRangeToleranceEpsilon(-20000,  -100,20, 10).
                addRangeToleranceEpsilon(-100, -60, 20, 8).
                addRangeToleranceEpsilon(-60, -40, 10, 5).
                addRangeToleranceEpsilon(-40, 1, 5, 3);

        beaconRelativeDistanceRssiHelper.addRssi(-53);

        assertEquals(BeaconRelativeDistanceRssiHelper.NEED_MORE_DATA,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-55);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-80);

        assertEquals(BeaconRelativeDistanceRssiHelper.FAR,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());

        beaconRelativeDistanceRssiHelper.addRssi(-85);

        assertEquals(BeaconRelativeDistanceRssiHelper.NO_CHANGE,
                beaconRelativeDistanceRssiHelper.getRelativeDistance());
    }
}
