package de.twenty11.unitprofile.agent;

import org.junit.Test;



public class AgentTest {
    @Test
    public void shouldInstantiateSleepingInstance() throws InterruptedException {

        InstrumentationTest sleeping = new InstrumentationTest();
        //sleeping.randomSleep();
        sleeping.somethingElse();
        System.out.println("hier");
    }
}
