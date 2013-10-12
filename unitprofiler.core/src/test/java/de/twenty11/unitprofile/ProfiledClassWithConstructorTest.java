package de.twenty11.unitprofile;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofile.helper.TestClass;
import de.twenty11.unitprofiler.annotations.Profile;

public class ProfiledClassWithConstructorTest {

    static {
        new TestClass(100);
    }

    @Profile
    @Test
    public void testConstructorProfilerLeer() {
        MethodInvocation rootInvocation = Agent.getRootInvocation();
        // assertThat(rootInvocation, is(not(nullValue())));
        // assertThat(rootInvocation.getChildren().size(), is(1));
        // assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(1));
        // assertThat(rootInvocation.getTime(),
        // is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }

    @Profile
    @Test
    public void testConstructorProfiler10() {

        new TestClass(10);

        MethodInvocation rootInvocation = Agent.getRootInvocation();
        assertThat(rootInvocation, is(not(nullValue())));
        assertThat(rootInvocation.getChildren().size(), is(1));
        assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(1));
        assertThat(rootInvocation.getTime(), is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }

    @Profile
    @Test
    public void testConstructorProfiler20() {

        new TestClass(20);

        MethodInvocation rootInvocation = Agent.getRootInvocation();
        assertThat(rootInvocation, is(not(nullValue())));
        assertThat(rootInvocation.getChildren().size(), is(1));
        assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(1));
        assertThat(rootInvocation.getTime(), is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }
}
