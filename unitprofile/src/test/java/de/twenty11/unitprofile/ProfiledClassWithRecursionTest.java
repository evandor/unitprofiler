package de.twenty11.unitprofile;

import org.junit.Test;

import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.helper.Ackermann;

public class ProfiledClassWithRecursionTest {

    @Profile
    @Test
    public void testProfiler () {
        Ackermann.calc(1, 1);
    }
}
