package de.twenty11.unitprofile;

import org.junit.Ignore;
import org.junit.Test;

import de.twenty11.unitprofile.helper.Ackermann;
import de.twenty11.unitprofiler.annotations.Profile;

public class ProfiledClassWithRecursionTest {

    //@Profile
    @Test
    @Ignore

    public void testProfiler () {
        Ackermann.calc(1, 1);
    }
}
