package de.twenty11.unitprofile.agent;

import org.junit.Ignore;
import org.junit.Test;



public class AgentTest {
    @Test
    @Ignore
    public void shouldInstantiateSleepingInstance() throws InterruptedException {
        InstrumentationTest sleeping = new InstrumentationTest();
        sleeping.somethingElse();
        System.out.println("profiling done...");
    }
}
