package de.twenty11.unitprofile;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.domain.Invocation;
import de.twenty11.unitprofile.helper.TestClass;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ProfiledClassWithConstructorTest {

    @Profile
    @Test
    public void testConstructorProfiler() {

        new TestClass(20);

        System.out.println(Agent.getRootInvocation() != null ? Agent.getRootInvocation().dump() : "---");
        //Invocation rootInvocation = Agent.getRootInvocation();
       // assertThat(rootInvocation, is(not(nullValue())));
        //assertThat(rootInvocation.getChildren().size(), is(1));
        // assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(0));
        // assertThat(rootInvocation.getTime(),is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }
}
