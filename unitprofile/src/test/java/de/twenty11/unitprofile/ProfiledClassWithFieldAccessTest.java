package de.twenty11.unitprofile;

import org.junit.Test;

import de.twenty11.unitprofile.annotations.Profile;

public class ProfiledClassWithFieldAccessTest {

    TestClass testClassField = new TestClass(500);
    String testStringField = "testABC";

    @Profile
    @Test
    public void testProfiler () {
        testClassField.sleep(500);
        testStringField.replaceAll("A", "B");
    }
}
