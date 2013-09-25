package de.twenty11.unitprofile;

import org.junit.Ignore;
import org.junit.Test;

import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.helper.TestClass;

public class ProfiledClassWithNewTest {

    //@Profile
    @Test
    @Ignore
    public void testProfiler () {
        new TestClass(60);
    }
}
