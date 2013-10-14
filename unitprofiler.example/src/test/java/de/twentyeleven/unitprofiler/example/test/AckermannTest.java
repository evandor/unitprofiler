package de.twentyeleven.unitprofiler.example.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.twenty11.unitprofiler.annotations.Profile;
import de.twentyeleven.unitprofiler.example.Ackermann;

public class AckermannTest {

    @Test
    @Profile
    public void test_with_three_and_two() {
        long result = Ackermann.calc(3, 2);
        assertEquals(29L, result);
    }
}
