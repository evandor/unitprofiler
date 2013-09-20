package de.twenty11.unitprofile;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.twenty11.unitprofile.agent.Agent;
import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.domain.Invocation;
import de.twenty11.unitprofile.helper.TestClass;

public class ProfiledClassWithStaticMethodTest {

    TestClass testClassField = new TestClass(500);
    String testStringField = "testABC";

    @Profile
    @Test
    public void testProfiler () {
        invokeStaticMethod();
        
//        Invocation rootInvocation = Agent.getRootInvocation();
//        assertThat(rootInvocation.getChildren().size(), is(1));
//        
//        assertThat(rootInvocation.getChildren().get(0).getChildren().size(), is(1));
    }
    
    private static void invokeStaticMethod() {
        new TestClass(30);
    }
}
