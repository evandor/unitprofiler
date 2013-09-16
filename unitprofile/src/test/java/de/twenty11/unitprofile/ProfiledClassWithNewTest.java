package de.twenty11.unitprofile;

import org.junit.Test;

import de.twenty11.unitprofile.annotations.Profile;

public class ProfiledClassWithNewTest {

    @Profile
    @Test
    public void testProfiler () {
        new TestClass(1000);
    }
}
