package de.twenty11.unitprofile;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofile.helper.TestClass;

public class ProfiledClassInExtendedTestClassTest extends SomeBaseTestClass {

    @Profile
    @Test
    public void should_not_instrument_the_call_to_the_superclasses_constructor() {

        new TestClass(100);

        MethodInvocation rootInvocation = Agent.getRootInvocation();
        assertThat(rootInvocation, is(not(nullValue())));
        assertThat(rootInvocation.getChildren().size(), is(1));
        assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(1));
        assertThat(rootInvocation.getTime(), is(greaterThanOrEqualTo(rootInvocation.getChildren().get(0).getTime())));
    }

}
