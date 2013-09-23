package de.twenty11.unitprofile;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.domain.Instrumentation;
import de.twenty11.unitprofile.domain.Invocation;

public class ProfiledClassWithMethodTest {

    @Profile
    @Test
    public void testProfiler () {
        sleep20();
        sleep20();
        sleep40();
        
        List<Instrumentation> instrumentations = Agent.getInstrumentations();
        assertThat(instrumentations, is(not(nullValue())));
        
        Invocation rootInvocation = Agent.getRootInvocation();
        assertThat(rootInvocation, is(not(nullValue())));
        assertThat(rootInvocation.getChildren().size(), is(2));
        assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(0));
        assertThat(rootInvocation.getTime(),is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }
    
    private void sleep20() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {}
    }

    private void sleep40() {
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {}
    }

}
