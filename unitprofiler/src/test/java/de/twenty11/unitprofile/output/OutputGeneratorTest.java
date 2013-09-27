package de.twenty11.unitprofile.output;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import de.twenty11.unitprofile.annotations.Profile;
import de.twenty11.unitprofile.domain.Invocation;


public class OutputGeneratorTest {

    @Test
    @Ignore
    //@Profile
    public void aaa() {
        OutputGenerator generator = new OutputGenerator();
        
        Invocation rootInvocation = Mockito.mock(Invocation.class);
        generator.renderFromBootstrapTemplate(rootInvocation);
    }
}
