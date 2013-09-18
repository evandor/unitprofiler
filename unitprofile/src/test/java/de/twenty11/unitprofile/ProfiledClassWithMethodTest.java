package de.twenty11.unitprofile;

import org.junit.Test;

import de.twenty11.unitprofile.annotations.Profile;

public class ProfiledClassWithMethodTest {

    @Profile
    @Test
    public void testProfiler () {
        sleep100();
        sleep100();
        sleep200();
    }
    
    private void sleep100() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
    }

    private void sleep200() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}
    }

}
