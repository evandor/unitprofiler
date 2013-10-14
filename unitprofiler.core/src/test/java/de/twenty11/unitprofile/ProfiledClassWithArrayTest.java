package de.twenty11.unitprofile;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofile.helper.TestClass;
import de.twenty11.unitprofiler.annotations.Profile;

public class ProfiledClassWithArrayTest {

    @Profile
    @Test
    public void testArrayProfiler() {

        TestClass[] testClasses = new TestClass[2];
        testClasses[0] = new TestClass(10);
        testClasses[1] = new TestClass(10);

        testClasses[0].sleep(10);
        testClasses[1].sleep(20);

        MethodInvocation rootInvocation = Agent.getRootInvocation();
        assertThat(rootInvocation, is(not(nullValue())));
        assertThat(rootInvocation.getChildren().size(), is(2));
        // assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(0));
        // assertThat(rootInvocation.getTime(),is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }
}
