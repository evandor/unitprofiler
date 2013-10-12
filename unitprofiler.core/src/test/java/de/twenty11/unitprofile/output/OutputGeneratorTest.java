package de.twenty11.unitprofile.output;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import de.twenty11.unitprofile.domain.MethodInvocation;
import de.twenty11.unitprofiler.annotations.Profile;


public class OutputGeneratorTest {

    @Test
    @Ignore
    //@Profile
    public void aaa() {
        OutputGenerator generator = new OutputGenerator();
        
        MethodInvocation rootInvocation = Mockito.mock(MethodInvocation.class);
        generator.renderFromBootstrapTemplate(rootInvocation);
    }
}
