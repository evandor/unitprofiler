package de.twenty11.unitprofile;

import org.junit.Test;

import de.twenty11.unitprofile.annotations.Profile;

public class ProfiledClassWithMethodTest {

    @Profile
    @Test
    public void testProfiler () {
        sleep100();
    }
    
    private void sleep100() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }

}
