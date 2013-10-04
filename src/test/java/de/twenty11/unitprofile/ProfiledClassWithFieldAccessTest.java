package de.twenty11.unitprofile;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofile.helper.TestClass;

public class ProfiledClassWithFieldAccessTest {

    TestClass testClassField = new TestClass(500);
    String testStringField = "testABC";

    //@Profile
    @Test
    @Ignore
    public void testProfiler() {
        testClassField.sleep(50);
        testStringField.replaceAll("A", "B");

        MethodInvocation rootInvocation = Agent.getRootInvocation();
        assertThat(rootInvocation, is(not(nullValue())));
        assertThat(rootInvocation.getChildren().size(), is(1));
        // assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(0));
        // assertThat(rootInvocation.getTime(),is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }
}
