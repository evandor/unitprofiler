package de.twenty11.unitprofile.callback;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.twenty11.unitprofile.domain.MethodInvocation;


public class ProfilerCallbackTest {
    
    @Before
    public void setUp() {
        
    }
    
    @Test
    @Ignore

    public void method_start_start_profiling_and_method_stop_stops_profiling() {
        //assertThat(ProfilerCallback.isProfiling(), is(false));
        ProfilerCallback.start("object", "method", 11);
        //assertThat(ProfilerCallback.isProfiling(), is(true));
        ProfilerCallback.stop("object", "method");
        //assertThat(ProfilerCallback.isProfiling(), is(false));
    }

    @Test
    @Ignore
    public void each_subsequent_start_of_profiledMethod_adds_to_invocation_list() {
        ProfilerCallback.start("object", "method", 11);
        ProfilerCallback.stop("object", "method");
        
        assertThat(ProfilerCallback.getInvocations().size(), is(1));
        
        ProfilerCallback.start("object", "method", 11);
        ProfilerCallback.stop("object", "method");
        
        assertThat(ProfilerCallback.getInvocations().size(), is(2));
    }
    
    @Test
    @Ignore
    public void submethod_is_child_of_parent_invocation() throws InterruptedException {
        MethodInvocation rootInvocation = ProfilerCallback.start("object", "m1", 11);

        Thread.sleep(20);
        ProfilerCallback.before("object", "m2", 11);

        Thread.sleep(20);
        ProfilerCallback.before("object", "m3", 11);

        Thread.sleep(20);
        ProfilerCallback.after("object", "m3");

        Thread.sleep(20);
        ProfilerCallback.after("object", "m2");
        
        Thread.sleep(20);
        ProfilerCallback.before("object", "m2", 11);
        
        Thread.sleep(20);
        ProfilerCallback.after("object", "m2");
        
        Thread.sleep(20);
        ProfilerCallback.stop("object", "m1");

        //assertThat(rootInvocation.getStart(), is(lessThanOrEqualTo(rootInvocation.getEnd())));
        System.out.println("========================");
        System.out.println(rootInvocation.dump());
    }

}
